package net.ajsdev.cobblemonteampreview.logic

import java.util.*

object TeamPreviewTracker {
    private val previewedPairs = mutableSetOf<Set<UUID>>()

    fun hasPreviewed(playerUUIDs: Set<UUID>): Boolean {
        return previewedPairs.contains(playerUUIDs)
    }

    fun markPreviewed(playerUUIDs: Set<UUID>) {
        previewedPairs.add(playerUUIDs)
    }

    fun clearPreviewed(playerUUIDs: Set<UUID>) {
        previewedPairs.remove(playerUUIDs)
    }
}