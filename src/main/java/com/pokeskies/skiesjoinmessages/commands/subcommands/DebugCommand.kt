package com.pokeskies.skiesjoinmessages.commands.subcommands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skiesjoinmessages.SkiesJoinMessages
import com.pokeskies.skiesjoinmessages.config.ConfigManager
import com.pokeskies.skiesjoinmessages.utils.SubCommand
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

class DebugCommand : SubCommand {
    override fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("debug")
            .requires(Permissions.require("skiesjoinmessages.command.debug", 4))
            .executes(Companion::debug)
            .build()
    }

    companion object {
        fun debug(ctx: CommandContext<CommandSourceStack>): Int {
            val newMode = !ConfigManager.CONFIG.debug
            ConfigManager.CONFIG.debug = newMode
            SkiesJoinMessages.INSTANCE.saveFile("config.json", ConfigManager.CONFIG)

            if (newMode) {
                ctx.source.sendMessage(Component.text("Debug mode has been enabled!").color(NamedTextColor.GREEN))
            } else {
                ctx.source.sendMessage(Component.text("Debug mode has been disabled!").color(NamedTextColor.RED))
            }
            return 1
        }
    }
}