package net.ajsdev.cobblemonteampreview.gui

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.item.PokemonItem
import eu.pb4.sgui.api.elements.GuiElement
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import net.ajsdev.cobblemonteampreview.config.TeamPreviewSettings
import net.ajsdev.cobblemonteampreview.scheduler.Scheduler
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile

class TeamPreviewGui(
    private val player: ServerPlayer,
    private val opponent: ServerPlayer?,
    private val playerTeam: List<BattlePokemon>,
    private val opponentTeam: List<BattlePokemon>,
    private val onConfirm: (List<BattlePokemon>) -> Unit,
    private val onCloseWithoutSelection: () -> Unit = {}
) {
    private val selectedOrder = mutableListOf<BattlePokemon>()

    fun open(): SimpleGui {
        val gui = object : SimpleGui(MenuType.GENERIC_9x4, player, false) {
            override fun onClose() {
                super.onClose()
                if (selectedOrder.isEmpty()) {
                    onCloseWithoutSelection()
                }
            }
        }

        gui.title = Component.literal("Choose Team Order")

        // Player and opponent heads
        gui.setSlot(1, GuiElementBuilder(getPlayerHead(player, "You")))
        gui.setSlot(19, GuiElementBuilder(getPlayerHead(opponent, "Opponent")))

        // Cancel Button
        val cancelButton = GuiElementBuilder.from(ItemStack(Items.BARRIER))
            .setName(Component.literal("Cancel").withStyle(ChatFormatting.RED)).setCallback { _, _, _, _ ->
                player.sendSystemMessage(Component.literal("Team preview cancelled"))
                gui.close()
                onCloseWithoutSelection()
            }

        gui.setSlot(30, cancelButton.build())

        // Confirm Button (only clickable when team is full)
        val confirmButton = GuiElementBuilder(ItemStack(Items.LIME_CONCRETE)).setName(
            Component.literal("Confirm Team Order").withStyle(ChatFormatting.GREEN)
        ).setCallback { _, _, _, _ ->
            if (selectedOrder.size == playerTeam.size) {
                player.sendSystemMessage(Component.literal("Team order confirmed."))
                gui.close()
                onConfirm(selectedOrder.toList())
            } else {
                player.sendSystemMessage(Component.literal("Select all Pokémon before confirming."))
            }
        }.build()


        // Fill other empty slots with gray pane
        val fillerGray =
            GuiElementBuilder(ItemStack(Items.GRAY_STAINED_GLASS_PANE)).setName(Component.literal(" ")).build()
        val fillerBlue =
            GuiElementBuilder(ItemStack(Items.LIGHT_BLUE_STAINED_GLASS_PANE)).setName(Component.literal(" ")).build()
        val fillerRed =
            GuiElementBuilder(ItemStack(Items.RED_STAINED_GLASS_PANE)).setName(Component.literal(" ")).build()
        val fillerYellow =
            GuiElementBuilder(ItemStack(Items.YELLOW_STAINED_GLASS_PANE)).setName(Component.literal(" ")).build()

        val teamOrder = GuiElementBuilder(ItemStack(Items.PAPER))
            .setName(Component.literal("Team Order"))
            .addLoreLine(Component.literal("Click pokemon in the top row"))
            .addLoreLine(Component.literal("to build your team order"))

        gui.setSlot(10, teamOrder)

        for (i in 0 until gui.size) {
            if (gui.getSlot(i) == null) {
                gui.setSlot(i, fillerGray)
            }
        }


        // Top Row: Clickable player Pokémon (slots 2–7)
        for (i in 0 until 6) {
            val battlePokemon = playerTeam.getOrNull(i)
            val element = if (battlePokemon != null) {
                val pokemon = battlePokemon.originalPokemon
                GuiElementBuilder(PokemonItem.from(pokemon)).setName(pokemon.nickname ?: pokemon.species.translatedName)
                    .setCallback { _, _, _, _ ->
                        if (battlePokemon !in selectedOrder && selectedOrder.size < playerTeam.size) {
                            selectedOrder.add(battlePokemon)
                            refreshSelectedOrder(gui, confirmButton, fillerYellow, fillerGray)
                        }
                    }.build()


            } else {
                fillerBlue
            }
            gui.setSlot(2 + i, element)
        }

        // Bottom Row: Opponent team display (slots 20–25)
        for (i in 0 until 6) {
            val battlePokemon = opponentTeam.getOrNull(i)
            val element = if (battlePokemon != null) {
                val pokemon = battlePokemon.originalPokemon
                GuiElementBuilder(PokemonItem.from(pokemon)).setName(pokemon.species.translatedName).build()
            } else {
                fillerRed
            }
            gui.setSlot(20 + i, element)
        }

        refreshSelectedOrder(gui, confirmButton, fillerYellow, fillerGray)


        val timeout = TeamPreviewSettings.config.timeout
        Scheduler.schedule( timeout * 20) {
            if (gui.isOpen) {
                player.sendSystemMessage(Component.literal("Team preview timed out."))
                gui.close()
                onCloseWithoutSelection()
            }
        }

        gui.open()
        return gui
    }

    private fun getPlayerHead(player: ServerPlayer?, label: String): ItemStack {
        return if (player != null) {
            ItemStack(Items.PLAYER_HEAD).apply {
                set(DataComponents.PROFILE, ResolvableProfile(player.gameProfile))
                set(DataComponents.CUSTOM_NAME, Component.literal(label))
            }
        } else {
            ItemStack(Items.SKELETON_SKULL).apply {
                set(DataComponents.CUSTOM_NAME, Component.literal(label))
            }
        }
    }

    // Function to refresh the middle (selected team order) row
    private fun refreshSelectedOrder(gui: SimpleGui, confirmButton: GuiElement, filler: GuiElement, filler2: GuiElement) {
        // Clear middle row
        val pokeballButton =
            GuiElementBuilder(ItemStack(CobblemonItems.POKE_BALL))
                .setName(Component.literal("Click a Pokémon to fill this slot."))

        for (i in 11..16) gui.setSlot(i, filler)
        for (i in 11..<11 + playerTeam.size) gui.setSlot(i, pokeballButton)

        selectedOrder.forEachIndexed { index, battlePokemon ->
            val pokemon = battlePokemon.originalPokemon
            gui.setSlot(
                11 + index,
                GuiElementBuilder(PokemonItem.from(pokemon)).setName(pokemon.nickname ?: pokemon.species.translatedName)
                    .setLore(listOf(Component.literal("Click to remove"))).setCallback { _, _, _, _ ->
                        selectedOrder.remove(battlePokemon)
                        refreshSelectedOrder(gui, confirmButton, filler, filler2)
                    }.build()
            )
        }

        if (selectedOrder.size == playerTeam.size) {
            gui.setSlot(32, confirmButton)
        } else {
            gui.setSlot(32, filler2)
        }
    }
}

