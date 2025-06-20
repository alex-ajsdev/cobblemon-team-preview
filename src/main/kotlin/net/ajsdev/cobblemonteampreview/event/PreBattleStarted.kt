package net.ajsdev.cobblemonteampreview.event


import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.util.getPlayer
import net.ajsdev.cobblemonteampreview.config.TeamPreviewSettings
import net.ajsdev.cobblemonteampreview.gui.TeamPreviewGui
import net.ajsdev.cobblemonteampreview.logic.TeamPreviewSession
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

object PreBattleStarted {
    fun handle(event: BattleStartedPreEvent) {
        val battle = event.battle
        if (battle.format.battleType.actorsPerSide != 1 || !battle.isPvP) return

        val skipForAll = !TeamPreviewSettings.config.enabled
        val skipForPlayer = battle.playerUUIDs.any { TeamPreviewSettings.isDisabledFor(it) }

        if (skipForAll || skipForPlayer) return

        event.reason = Component.literal("Starting team preview...").withStyle(ChatFormatting.WHITE)
        event.cancel()
        beginTeamPreview(battle)
    }


    private fun beginTeamPreview(battle: PokemonBattle) {
        val session = TeamPreviewSession(battle)

        for (actor in battle.actors) {
            val currentPlayer = actor.getPlayerUUIDs().firstOrNull()?.getPlayer()
            val opponentActor = actor.getSide().getOppositeSide().actors.firstOrNull()
            val opponentPlayer = opponentActor?.getPlayerUUIDs()?.firstOrNull()?.getPlayer()

            val playerTeam = actor.pokemonList
            val opponentTeam = opponentActor?.pokemonList ?: emptyList()

            if (currentPlayer != null) {
                val gui = TeamPreviewGui(
                    player = currentPlayer,
                    opponent = opponentPlayer,
                    playerTeam = playerTeam,
                    opponentTeam = opponentTeam,
                    onConfirm = { selectedLead ->
                        session.lockIn(actor, selectedLead)
                    },
                    onCloseWithoutSelection = {
                        session.cancel()
                    }).open()
                session.trackGui(currentPlayer.uuid, gui)
            }
        }

        session.subscribe {
            if (session.isReady()) {
                BattleRegistry.startBattle(battle.format, battle.side1, battle.side2, false)
            }
        }
    }
}
