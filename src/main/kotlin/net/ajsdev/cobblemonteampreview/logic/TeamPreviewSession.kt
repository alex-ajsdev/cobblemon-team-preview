package net.ajsdev.cobblemonteampreview.logic

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore
import com.cobblemon.mod.common.battles.ActiveBattlePokemon
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.pokemon.Pokemon
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.network.chat.Component
import java.util.*

class TeamPreviewSession(private val battle: PokemonBattle) {
    private val activeGuis = mutableMapOf<UUID, SimpleGui>()
    private val subscribers = mutableListOf<() -> Unit>()
    private val lockedTeams = mutableMapOf<UUID, List<BattlePokemon>>()

    fun lockIn(actor: BattleActor, team: List<BattlePokemon>) {
        actor.pokemonList.clear()
        actor.pokemonList.addAll(team)
        lockedTeams[actor.uuid] = team
        notifySubscribers()
    }

    fun trackGui(playerId: UUID, gui: SimpleGui) {
        activeGuis[playerId] = gui
    }

    fun cancel(reason: String = "Team preview cancelled.") {
        for ((_, gui) in activeGuis) {
            gui.player.sendSystemMessage(Component.literal(reason))
            gui.close()
        }

        activeGuis.clear()
        lockedTeams.clear()
        subscribers.clear()
    }

    fun isReady(): Boolean {
        return battle.players.all { player -> lockedTeams.containsKey(player.uuid) }
    }

    fun subscribe(onUpdate: () -> Unit) {
        subscribers += onUpdate
    }

    private fun notifySubscribers() {
        subscribers.forEach { it() }
    }
}
