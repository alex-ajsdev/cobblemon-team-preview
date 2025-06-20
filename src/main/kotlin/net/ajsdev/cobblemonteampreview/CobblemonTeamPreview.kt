package net.ajsdev.cobblemonteampreview

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.ajsdev.cobblemonteampreview.config.TeamPreviewSettings
import net.ajsdev.cobblemonteampreview.event.PreBattleStarted
import net.ajsdev.cobblemonteampreview.scheduler.Scheduler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component


object CobblemonTeamPreview : ModInitializer {
    override fun onInitialize() {
        Scheduler.init()
        TeamPreviewSettings.load()
        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.NORMAL, PreBattleStarted::handle)
        registerTeamPreviewCommand()
    }

    private fun registerTeamPreviewCommand() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            literal<CommandSourceStack>("teampreview").then(
                literal<CommandSourceStack>("toggle").executes { ctx ->
                    val player = ctx.source.playerOrException
                    val resultString = if (TeamPreviewSettings.togglePlayer(player.uuid)) "disabled" else "enabled"
                    ctx.source.sendSystemMessage(Component.literal("Team preview has been $resultString"))
                    1
                }.then(
                    literal<CommandSourceStack>("global")
                        .requires { it.hasPermission(4) }
                        .executes { ctx ->
                            val resultString = if (TeamPreviewSettings.toggleGlobal()) "disabled" else "enabled"
                            ctx.source.sendSystemMessage(Component.literal("Team preview has been $resultString globally"))
                            1
                        }
                )
            ).register(dispatcher)
        }
    }
}
fun LiteralArgumentBuilder<CommandSourceStack>.register(dispatcher: CommandDispatcher<CommandSourceStack>) {
    dispatcher.register(this)
}
