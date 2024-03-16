package com.pokeskies.skiesjoinmessages.placeholders.services

import com.pokeskies.skiesjoinmessages.placeholders.IPlaceholderService
import net.minecraft.server.level.ServerPlayer

class DefaultPlaceholderService : IPlaceholderService {
    override fun parsePlaceholders(player: ServerPlayer, text: String): String {
        var returnText = text
            .replace("%player%", player.name.string)

        return returnText
    }
}