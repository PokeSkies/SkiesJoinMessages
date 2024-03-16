package com.pokeskies.skiesjoinmessages.config

import com.google.gson.annotations.SerializedName

class MessageGroup(
    val permission: String = "",
    @SerializedName("join_message")
    val joinMessage: List<String> = emptyList(),
    @SerializedName("leave_message")
    val leaveMessage: List<String> = emptyList()
) {
    override fun toString(): String {
        return "MessageGroup(permission='$permission', joinMessage=$joinMessage, leaveMessage=$leaveMessage)"
    }
}