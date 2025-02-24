package com.pokeskies.skiesjoinmessages.config

import com.google.gson.annotations.SerializedName

class MainConfig(
    var debug: Boolean = false,
    val storage: StorageConfig = StorageConfig(),
    @SerializedName("suppressed_messages")
    val suppressedMessages: List<String> = emptyList(),
    val groups: Map<String, MessageGroup> = emptyMap(),
    @SerializedName("first_join")
    val firstJoin: FirstJoinConfig? = null
) {
    override fun toString(): String {
        return "MainConfig(debug=$debug, storage=$storage, suppressedMessages=$suppressedMessages, groups=$groups, firstJoin=$firstJoin)"
    }
}
