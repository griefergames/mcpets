package net.griefergames.mcpets.plotsquared

import com.plotsquared.core.configuration.caption.StaticCaption
import com.plotsquared.core.plot.flag.types.BooleanFlag

/**
 * PlotSquared flag `pet-damage` — whether players can damage pets on the plot.
 * Default: true (damage allowed). When false: player damage to pets is cancelled
 * (same effect as WorldGuard `mcpets-pet-player-damage` ALLOW).
 */
class PetDamageFlag private constructor(value: Boolean) : BooleanFlag<PetDamageFlag>(
    value,
    StaticCaption.of("Whether players can damage MCPets on this plot"),
) {
    override fun flagOf(value: Boolean): PetDamageFlag = if (value) TRUE else FALSE

    companion object {
        @JvmField
        val TRUE: PetDamageFlag = PetDamageFlag(true)

        @JvmField
        val FALSE: PetDamageFlag = PetDamageFlag(false)
    }
}
