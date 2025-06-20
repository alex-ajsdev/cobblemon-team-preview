package net.ajsdev.cobblemonteampreview.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

data class TeamPreviewConfig(
    val timeout: Int = 60,
    val enabled: Boolean = true,
    val blacklistedPlayers: MutableSet<UUID> = mutableSetOf()
)

object TeamPreviewSettings {
    private val configFile = Path.of("config/cobblemon-team-preview.json")
    var config = TeamPreviewConfig()

    fun load() {
        if (Files.exists(configFile)) {
            val reader = Files.newBufferedReader(configFile)
            config = Gson().fromJson(reader, TeamPreviewConfig::class.java)
            reader.close()
        } else {
            save()
        }
    }

    private fun save() {
        val writer = Files.newBufferedWriter(configFile)
        GsonBuilder().setPrettyPrinting().create().toJson(config, writer)
        writer.close()
    }

    fun isDisabledFor(uuid: UUID): Boolean {
        return !config.enabled || uuid in config.blacklistedPlayers
    }

    fun togglePlayer(uuid: UUID): Boolean {
        val set = config.blacklistedPlayers
        val added = if (set.contains(uuid)) {
            set.remove(uuid); false
        } else {
            set.add(uuid); true
        }
        save()
        return added
    }

    fun toggleGlobal(): Boolean {
        config = config.copy(enabled = !config.enabled)
        save()
        return !config.enabled
    }
}