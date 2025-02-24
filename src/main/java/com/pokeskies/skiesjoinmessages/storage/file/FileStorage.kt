package com.pokeskies.skiesjoinmessages.storage.file

import com.pokeskies.skiesjoinmessages.SkiesJoinMessages
import com.pokeskies.skiesjoinmessages.storage.IStorage
import java.util.*

class FileStorage : IStorage {
    private var fileData: FileData = SkiesJoinMessages.INSTANCE.loadFile(STORAGE_FILENAME, FileData(), true)

    companion object {
        private const val STORAGE_FILENAME = "storage.json"
    }

    override fun hasUser(uuid: UUID): Boolean {
        return fileData.users.containsKey(uuid)
    }

    override fun addUser(uuid: UUID, joinTime: Long): Boolean {
        if (hasUser(uuid)) return false

        fileData.users[uuid] = joinTime
        return SkiesJoinMessages.INSTANCE.saveFile(STORAGE_FILENAME, fileData)
    }

    override fun totalUsers(): Int {
        return fileData.users.size
    }
}
