package com.pokeskies.skiesjoinmessages.commands.subcommands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skiesjoinmessages.SkiesJoinMessages
import com.pokeskies.skiesjoinmessages.utils.SubCommand
import com.pokeskies.skiesjoinmessages.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

class ReloadCommand : SubCommand {
    override fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("reload")
            .requires(Permissions.require("skiesjoinmessages.command.reload", 4))
            .executes(Companion::reload)
            .build()
    }

    companion object {
        fun reload(ctx: CommandContext<CommandSourceStack>): Int {
            SkiesJoinMessages.INSTANCE.reload()
            ctx.source.sendMessage(Utils.deserializeText("<green>Reloaded SkiesJoinMessages!"))
            return 1
        }
    }
}