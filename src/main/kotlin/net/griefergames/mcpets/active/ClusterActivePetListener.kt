package net.griefergames.mcpets.active

import fr.nocsy.mcpets.MCPets
import fr.nocsy.mcpets.data.Pet
import fr.nocsy.mcpets.data.PetDespawnReason
import fr.nocsy.mcpets.data.PetSkin
import fr.nocsy.mcpets.events.PetDespawnEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.logging.Level

/**
 * GG cluster handoff for active pets. Independent of upstream PetListener Velocity/MySQL path.
 * Keep `Velocity.Enabled=false` on GG servers so both paths do not fight.
 */
class ClusterActivePetListener(
    private val plugin: MCPets,
    private val store: ClusterActivePetStore,
) : Listener {

    // HIGH: before upstream PetListener (HIGHEST) despawns pets on quit.
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val active = ArrayList(Pet.getActivePetsForOwner(uuid))
        val ids = ArrayList<String>()
        val skins = HashMap<String, String>()
        for (pet in active) {
            // Match upstream Velocity quit filter: only pets the player may use
            if (!player.hasPermission(PetAccess.permission(pet))) continue
            val petId = PetAccess.id(pet)
            ids.add(petId)
            pet.activeSkin?.let { skin ->
                PetAccess.skinPathId(skin)?.let { skins[petId] = it }
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                if (ids.isEmpty()) store.clear(uuid) else store.save(uuid, ids, skins)
            } catch (ex: RuntimeException) {
                plugin.logger.log(Level.WARNING, "[MCPets] Cluster active-pet save failed for $uuid", ex)
            }
        })
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId
        // After upstream PetListener reconnection (20L).
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (!player.isOnline) return@Runnable
            // Local reconnect already spawned via reconnectionPets — drop stale handoff.
            if (Pet.getActivePetsForOwner(uuid).isNotEmpty()) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable { store.clear(uuid) })
                return@Runnable
            }
            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                val handoff = try {
                    store.consume(uuid)
                } catch (ex: RuntimeException) {
                    plugin.logger.log(Level.WARNING, "[MCPets] Cluster active-pet consume failed for $uuid", ex)
                    return@Runnable
                }
                if (handoff == null || handoff.petIds.isEmpty()) return@Runnable
                // Same cluster member wrote this → local reconnect / restart, not a server switch.
                if (handoff.sourceMemberId != null && handoff.sourceMemberId == store.localMemberId()) {
                    return@Runnable
                }
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    if (!player.isOnline) return@Runnable
                    if (Pet.getActivePetsForOwner(uuid).isNotEmpty()) return@Runnable
                    spawnHandoff(player, uuid, handoff)
                })
            })
        }, JOIN_DELAY_TICKS)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDespawn(event: PetDespawnEvent) {
        val reason = PetAccess.despawnReason(event)
        if (reason == PetDespawnReason.DISCONNECTION || reason == PetDespawnReason.TELEPORT) return
        val owner = PetAccess.owner(PetAccess.despawnPet(event)) ?: return
        // Event fires before removeActivePet — hop a tick so the active list is up to date.
        Bukkit.getScheduler().runTask(plugin, Runnable {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                try {
                    val remaining = Pet.getActivePetsForOwner(owner)
                    if (remaining.isEmpty()) {
                        store.clear(owner)
                        return@Runnable
                    }
                    val ids = ArrayList<String>(remaining.size)
                    val skins = HashMap<String, String>()
                    for (pet in remaining) {
                        val petId = PetAccess.id(pet)
                        ids.add(petId)
                        pet.activeSkin?.let { skin ->
                            PetAccess.skinPathId(skin)?.let { skins[petId] = it }
                        }
                    }
                    store.save(owner, ids, skins)
                } catch (ex: RuntimeException) {
                    plugin.logger.log(Level.WARNING, "[MCPets] Cluster active-pet despawn sync failed for $owner", ex)
                }
            })
        })
    }

    fun saveAllActiveOnDisable() {
        for ((uuid, pets) in PetAccess.activePets()) {
            if (pets.isNullOrEmpty()) continue
            val ids = ArrayList<String>()
            val skins = HashMap<String, String>()
            for (pet in pets) {
                if (pet == null) continue
                val petId = PetAccess.id(pet)
                ids.add(petId)
                pet.activeSkin?.let { skin ->
                    PetAccess.skinPathId(skin)?.let { skins[petId] = it }
                }
            }
            if (ids.isEmpty()) continue
            store.save(uuid, ids, skins)
        }
    }

    private fun spawnHandoff(player: Player, uuid: UUID, handoff: ActivePetHandoff) {
        for (petId in handoff.petIds) {
            val template = Pet.getFromId(petId) ?: continue
            val pet = template.copy()
            PetAccess.setCheckPermission(pet, false)
            PetAccess.setOwner(pet, uuid)
            restoreSkin(player, pet, handoff.skinId(petId))
            pet.spawn(player.location, true)
        }
    }

    private fun restoreSkin(player: Player, pet: Pet, skinPathId: String?) {
        if (skinPathId.isNullOrEmpty()) return
        for (skin in PetSkin.getSkins(pet)) {
            if (skinPathId != PetAccess.skinPathId(skin)) continue
            val perm = PetAccess.skinPermission(skin)
            if (perm.isNullOrEmpty() || player.hasPermission(perm)) {
                pet.activeSkin = skin
            }
            return
        }
    }

    companion object {
        private const val JOIN_DELAY_TICKS = 25L
    }
}
