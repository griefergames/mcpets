package net.griefergames.mcpets.ui

import fr.nocsy.mcpets.data.Pet
import net.griefergames.singularity.ui.framework.component.UIComponent
import net.griefergames.singularity.ui.library.standard.ListInventoryUI
import net.kyori.adventure.text.i18n
import org.bukkit.entity.Player

class PetsMenu(forPlayer: Player) : ListInventoryUI<Pet>(
    i18n("mcpets.inventory.pets.title"),
    6,
    Pet.getAvailablePets(forPlayer).asSequence()
) {

    override fun renderEntry(item: Pet): UIComponent {
        return PetUIItem(item, this)
    }

}