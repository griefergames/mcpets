package net.griefergames.mcpets.active

import java.util.UUID

data class ActivePetHandoff(
    val petIds: List<String>,
    val skinIds: Map<String, String>,
    val updatedAt: Long,
    val sourceMemberId: UUID?,
) {
    fun skinId(petId: String): String? = skinIds[petId]
}
