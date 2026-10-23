package net.griefergames.mcpets.plotsquared

import com.plotsquared.core.configuration.caption.StaticCaption
import com.plotsquared.core.plot.flag.types.BooleanFlag

/**
 * PlotSquared flag `pets` — whether pets are allowed on the plot.
 * Default: true (allowed). When false: spawn is cancelled and active pets are despawned.
 */
class PetsFlag private constructor(value: Boolean) : BooleanFlag<PetsFlag>(
    value,
    StaticCaption.of("Whether MCPets are allowed on this plot"),
) {
    override fun flagOf(value: Boolean): PetsFlag = if (value) TRUE else FALSE

    companion object {
        @JvmField
        val TRUE: PetsFlag = PetsFlag(true)

        @JvmField
        val FALSE: PetsFlag = PetsFlag(false)
    }
}
