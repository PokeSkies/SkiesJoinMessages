package com.pokeskies.skiesjoinmessages.storage.file

import java.util.*

class FileData {
    var users: HashMap<UUID, Long> = HashMap()
    override fun toString(): String {
        return "FileData(users=$users)"
    }
}
