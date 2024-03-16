package com.pokeskies.skiesjoinmessages.placeholders

import net.minecraft.server.level.ServerPlayer

interface IPlaceholderService {
    fun parsePlaceholders(player: ServerPlayer, text: String): String
}