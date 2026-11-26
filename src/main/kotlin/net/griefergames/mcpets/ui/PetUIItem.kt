package net.griefergames.mcpets.ui

import fr.nocsy.mcpets.data.Pet
import net.griefergames.mcpets.petSpriteObject
import net.griefergames.singularity.ui.framework.UIClickEvent
import net.griefergames.singularity.ui.framework.component.UIItem
import net.griefergames.singularity.ui.library.standard.InventoryUI
import net.kyori.adventure.text.i18n
import org.bukkit.inventory.ItemStack

class PetUIItem(val pet: Pet, val context: InventoryUI) : UIItem() {

    override suspend fun render(previous: ItemStack?): ItemStack? {
        val petItemStack = pet.buildItem(pet.icon,true)
        petItemStack.editMeta {
            it.displayName(petSpriteObject().append(i18n("mcpets.pet.${pet.id}.name")))
        }
        return petItemStack
    }

    override suspend fun onInteract(event: UIClickEvent) {

    }

}