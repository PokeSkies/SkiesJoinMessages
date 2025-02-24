package com.pokeskies.skiesjoinmessages.storage

import com.pokeskies.skiesjoinmessages.config.StorageConfig
import com.pokeskies.skiesjoinmessages.storage.database.sql.SQLStorage
import com.pokeskies.skiesjoinmessages.storage.file.FileStorage
import java.util.UUID

interface IStorage {
    companion object {
        fun load(config: StorageConfig): IStorage {
            return when (config.type) {
                StorageType.JSON -> FileStorage()
                StorageType.MYSQL, StorageType.SQLITE -> SQLStorage(config)
            }
        }
    }

    fun hasUser(uuid: UUID): Boolean

    fun addUser(uuid: UUID, joinTime: Long): Boolean

    fun totalUsers(): Int

    fun close() {}
}
