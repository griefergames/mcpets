package net.griefergames.mcpets

import fr.nocsy.mcpets.MCPets
import net.griefergames.cluster.primitive.cluster.ClusterManager
import net.griefergames.mcpets.active.ClusterActivePetListener
import net.griefergames.mcpets.active.ClusterActivePetStore
import net.griefergames.mcpets.plotsquared.PlotSquaredPets
import net.griefergames.mcpets.plotsquared.PlotSquaredPetsListener
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList

class MCPetsPluginInjector(
    private val plugin: MCPets
) {

    private var activePetListener: ClusterActivePetListener? = null
    private var plotSquaredPetsListener: PlotSquaredPetsListener? = null

    fun onLoad() {
        if (Bukkit.getPluginManager().getPlugin("PlotSquared") != null) {
            try {
                PlotSquaredPets.registerFlags()
                plugin.logger.info("[MCPets] Registered PlotSquared flags: pets, pet-damage")
            } catch (throwable: Throwable) {
                plugin.logger.warning("[MCPets] PlotSquared flag registration failed: ${throwable.message}")
            }
        }

    }

    fun onEnable() {
        enableClusterHandoff()
        enablePlotSquaredPets()
    }

    fun onDisable() {
        plotSquaredPetsListener?.stop()
        plotSquaredPetsListener = null

        val listener = activePetListener ?: return
        try {
            listener.saveAllActiveOnDisable()
        } catch (throwable: Throwable) {
            plugin.logger.warning("[MCPets] Failed to flush active pets to cluster: ${throwable.message}")
        }
        HandlerList.unregisterAll(listener)
        activePetListener = null
    }

    private fun enableClusterHandoff() {
        if (Bukkit.getPluginManager().getPlugin("ClusterConnect") == null) {
            plugin.logger.info("[MCPets] ClusterConnect not present — skipping cluster active-pet handoff.")
            return
        }
        // Next tick: ClusterConnect / Hazelcast may not be fully ready during onEnable.
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (activePetListener != null) return@Runnable
            try {
                ClusterManager.getInstance()
                val store = ClusterActivePetStore()
                val listener = ClusterActivePetListener(plugin, store)
                Bukkit.getPluginManager().registerEvents(listener, plugin)
                activePetListener = listener
                plugin.logger.info("[MCPets] Cluster active-pet handoff enabled (map=${ClusterActivePetStore.MAP_NAME}).")
            } catch (throwable: Throwable) {
                activePetListener = null
                plugin.logger.warning("[MCPets] Cluster active-pet handoff unavailable: ${throwable.message}")
            }
        })
    }

    private fun enablePlotSquaredPets() {
        if (Bukkit.getPluginManager().getPlugin("PlotSquared") == null) {
            return
        }
        try {
            val listener = PlotSquaredPetsListener(plugin)
            listener.start()
            plotSquaredPetsListener = listener
        } catch (throwable: Throwable) {
            plotSquaredPetsListener = null
            plugin.logger.warning("[MCPets] PlotSquared pets enforcement unavailable: ${throwable.message}")
        }
    }
}
