package net.griefergames.mcpets.plotsquared

import com.plotsquared.bukkit.util.BukkitUtil
import com.plotsquared.core.plot.Plot
import com.plotsquared.core.plot.flag.GlobalFlagContainer
import org.bukkit.Location

object PlotSquaredPets {

    fun registerFlags() {
        GlobalFlagContainer.preAddFlag(PetsFlag.TRUE)
        GlobalFlagContainer.preAddFlag(PetDamageFlag.TRUE)
    }

    fun plotAt(location: Location): Plot? = BukkitUtil.adapt(location).plot

    /** Wilderness / no plot → allowed (same idea as WG default). */
    fun petsAllowed(location: Location): Boolean {
        val plot = plotAt(location) ?: return true
        return plot.getFlag(PetsFlag::class.java)
    }

    /** Wilderness / no plot → damage allowed. */
    fun petDamageAllowed(location: Location): Boolean {
        val plot = plotAt(location) ?: return true
        return plot.getFlag(PetDamageFlag::class.java)
    }
}
