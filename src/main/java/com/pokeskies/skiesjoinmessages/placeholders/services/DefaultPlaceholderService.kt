package com.pokeskies.skiesjoinmessages.placeholders.services

import com.pokeskies.skiesjoinmessages.SkiesJoinMessages
import com.pokeskies.skiesjoinmessages.config.ConfigManager
import com.pokeskies.skiesjoinmessages.config.FirstJoinConfig
import com.pokeskies.skiesjoinmessages.placeholders.IPlaceholderService
import net.minecraft.server.level.ServerPlayer

class DefaultPlaceholderService : IPlaceholderService {
    override fun parsePlaceholders(player: ServerPlayer, text: String): String {
        val returnText = text
            .replace("%player%", player.name.string)
            .replace("%unique_players%", SkiesJoinMessages.INSTANCE.getUniquePlayers().toString())

        return returnText
    }

}
