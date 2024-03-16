package com.pokeskies.skiesjoinmessages.config

import com.google.gson.annotations.SerializedName

class MainConfig(
    var debug: Boolean = false,
    @SerializedName("suppressed_messages")
    val suppressedMessages: List<String> = emptyList(),
    val groups: Map<String, MessageGroup> = emptyMap()
) {
    override fun toString(): String {
        return "MainConfig(debug=$debug, suppressedMessages=$suppressedMessages, groups=$groups)"
    }
}