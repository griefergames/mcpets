package net.griefergames.mcpets.ui

import kotlinx.coroutines.launch
import net.griefergames.mcpets.mcPetsScope
import org.bukkit.entity.Player

object UIAccessor {

    fun openPetsMenu(forPlayer: Player) = mcPetsScope.launch {
        PetsMenu(forPlayer).open(forPlayer)
    }

}