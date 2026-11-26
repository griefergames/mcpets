package net.griefergames.mcpets

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.`object`.ObjectContents

fun petSpriteObject(): Component {
    return Component.`object`(
        ObjectContents.sprite(
            Key.key("minecraft:gui"),
            Key.key("griefergames", "icons/pet_paw")
        )
    )
}