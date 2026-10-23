package net.griefergames.mcpets.plotsquared

import fr.nocsy.mcpets.MCPets
import fr.nocsy.mcpets.data.Pet
import fr.nocsy.mcpets.data.PetDespawnReason
import fr.nocsy.mcpets.data.config.Language
import fr.nocsy.mcpets.events.PetDamagedByEntityEvent
import fr.nocsy.mcpets.events.PetSpawnEvent
import fr.nocsy.mcpets.utils.debug.Debugger
import io.lumine.mythic.bukkit.BukkitAdapter
import net.griefergames.mcpets.active.PetAccess
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import java.util.ArrayList
import java.util.UUID

/**
 * Enforces PlotSquared `pets` / `pet-damage` flags (GG parallel to WorldGuard flags).
 */
class PlotSquaredPetsListener(
    private val plugin: MCPets,
) : Listener {

    private var despawnTaskId: Int = -1

    fun start() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        despawnTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if (MCPets.getMythicMobs() == null) return@scheduleSyncRepeatingTask
            for (owner in ArrayList(PetAccess.activePets().keys)) {
                val player = Bukkit.getPlayer(owner) ?: continue
                if (PlotSquaredPets.petsAllowed(player.location)) continue
                for (pet in ArrayList(Pet.getActivePetsForOwner(owner))) {
                    pet.despawn(PetDespawnReason.FLAG)
                }
                Language.CANT_FOLLOW_HERE.sendMessage(player)
            }
        }, 0L, 20L)
        plugin.logger.info("[MCPets] PlotSquared pets flags enabled (pets, pet-damage).")
    }

    fun stop() {
        if (despawnTaskId != -1) {
            Bukkit.getScheduler().cancelTask(despawnTaskId)
            despawnTaskId = -1
        }
        HandlerList.unregisterAll(this)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSpawn(event: PetSpawnEvent) {
        val where = PetAccess.spawnWhere(event) ?: return
        if (PlotSquaredPets.petsAllowed(where)) return
        event.isCancelled = true
        val pet = PetAccess.spawnPet(event)
        val owner: UUID? = PetAccess.owner(pet)
        val player = owner?.let { Bukkit.getPlayer(it) }
        if (player != null) {
            Language.CANT_FOLLOW_HERE.sendMessage(player)
        }
        Debugger.send(
            "§7Pet §6${PetAccess.id(pet)}§7 spawn cancelled by PlotSquared flag §6pets=false§7.",
        )
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onDamaged(event: PetDamagedByEntityEvent) {
        var player: Player? = null
        val damager = PetAccess.damager(event)
        if (damager is Player) {
            player = damager
        } else if (damager is Projectile) {
            val shooter = damager.shooter
            if (shooter is Player) player = shooter
        }
        if (player == null) return

        val pet = PetAccess.damagedPet(event)
        val activeMob = PetAccess.activeMob(pet) ?: return
        val location = BukkitAdapter.adapt(activeMob.location)
        if (PlotSquaredPets.petDamageAllowed(location)) return

        event.isCancelled = true
        event.setOriginalDamages(0.0)
        Debugger.send(
            "§7The pet §6${PetAccess.id(pet)}§7 didn't receive damages from §6${player.name}§7 " +
                "because of PlotSquared flag §6pet-damage=false§7.",
        )
    }
}
