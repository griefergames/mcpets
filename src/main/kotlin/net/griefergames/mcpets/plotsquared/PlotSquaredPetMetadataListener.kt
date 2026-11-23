package net.griefergames.mcpets.plotsquared

import fr.nocsy.mcpets.MCPets
import fr.nocsy.mcpets.events.PetSpawnedEvent
import fr.nocsy.mcpets.utils.debug.Debugger
import net.griefergames.mcpets.active.PetAccess
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.metadata.FixedMetadataValue

/**
 * Marks MCPets entities so Distributed PlotSquared lets them move plot ↔ road
 * (`everywhere`, `plotsquared:ignore`, `keep` / kill-road-mobs).
 *
 * Hooks [PetSpawnedEvent]: fired from [fr.nocsy.mcpets.data.Pet.spawn] after
 * `AlmPet` metadata and the ActiveMob are set, so every normal (re)spawn path
 * is covered without a delayed EntitySpawn race.
 */
class PlotSquaredPetMetadataListener(
    private val plugin: MCPets,
) : Listener {

    fun start() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
        plugin.logger.info(
            "[MCPets] PlotSquared pet road metadata enabled " +
                "($META_EVERYWHERE, $META_PLOTSQUARED_IGNORE, $META_KEEP).",
        )
    }

    fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPetSpawned(event: PetSpawnedEvent) {
        val pet = PetAccess.spawnedPet(event)
        val entity = PetAccess.entity(pet) ?: return
        applyRoadMetadata(entity)
        Debugger.send(
            "§7Applied PlotSquared road metadata to pet §6${PetAccess.id(pet)}§7 " +
                "(entity ${entity.uniqueId}).",
        )
    }

    private fun applyRoadMetadata(entity: Entity) {
        if (!entity.isValid) return
        entity.setMetadata(META_EVERYWHERE, FixedMetadataValue(plugin, true))
        entity.setMetadata(META_PLOTSQUARED_IGNORE, FixedMetadataValue(plugin, true))
        entity.setMetadata(META_KEEP, FixedMetadataValue(plugin, true))
    }

    companion object {
        const val META_EVERYWHERE = "everywhere"
        const val META_PLOTSQUARED_IGNORE = "plotsquared:ignore"
        const val META_KEEP = "keep"
    }
}
