package net.griefergames.mcpets.active

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap
import java.util.UUID

/**
 * Hazelcast-serializable handoff payload for active pets across cluster members.
 */
class ClusterActivePetPayload(
    var petIds: ArrayList<String> = ArrayList(),
    var skinIds: HashMap<String, String> = HashMap(),
    var updatedAt: Long = System.currentTimeMillis(),
    /** Cluster member UUID that wrote this handoff (same member = local reconnect). */
    var sourceMemberId: UUID? = null,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 2L
    }
}
