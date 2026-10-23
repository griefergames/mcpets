package net.griefergames.mcpets.active

import com.hazelcast.map.IMap
import net.griefergames.cluster.primitive.cluster.ClusterManager
import java.util.ArrayList
import java.util.HashMap
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Short-lived active-pet handoff via cluster-connect IMap (TTL).
 * Parallel GG path — does not touch upstream MySQL / Velocity code.
 */
class ClusterActivePetStore(
    private val mapName: String = MAP_NAME,
    private val ttlSeconds: Long = DEFAULT_TTL_SECONDS,
) {

    private val map: IMap<UUID, ClusterActivePetPayload>
        get() = ClusterManager.getInstance().getMap(mapName)

    fun save(uuid: UUID, petIds: List<String>, skinIds: Map<String, String> = emptyMap()) {
        val payload = ClusterActivePetPayload(
            petIds = ArrayList(petIds),
            skinIds = HashMap(skinIds),
            updatedAt = System.currentTimeMillis(),
            sourceMemberId = localMemberId(),
        )
        map.put(uuid, payload, ttlSeconds, TimeUnit.SECONDS)
    }

    /** Get and delete (consume handoff on destination). */
    fun consume(uuid: UUID): ActivePetHandoff? {
        val payload = map.remove(uuid) ?: return null
        return payload.toHandoff()
    }

    fun clear(uuid: UUID) {
        map.delete(uuid)
    }

    fun localMemberId(): UUID = ClusterManager.getInstance().uuid

    private fun ClusterActivePetPayload.toHandoff() = ActivePetHandoff(
        petIds = petIds,
        skinIds = skinIds,
        updatedAt = updatedAt,
        sourceMemberId = sourceMemberId,
    )

    companion object {
        const val MAP_NAME = "mcpets:active-pet"
        private const val DEFAULT_TTL_SECONDS = 60L
    }
}
