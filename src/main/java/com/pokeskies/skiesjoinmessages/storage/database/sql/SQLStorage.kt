package com.pokeskies.skiesjoinmessages.storage.database.sql

import com.pokeskies.skiesjoinmessages.config.StorageConfig
import com.pokeskies.skiesjoinmessages.storage.IStorage
import com.pokeskies.skiesjoinmessages.storage.StorageType
import com.pokeskies.skieskits.storage.database.sql.ConnectionProvider
import com.pokeskies.skiesjoinmessages.storage.database.sql.providers.MySQLProvider
import com.pokeskies.skiesjoinmessages.storage.database.sql.providers.SQLiteProvider
import java.sql.SQLException
import java.util.*

class SQLStorage(config: StorageConfig) : IStorage {
    private val connectionProvider: ConnectionProvider = when (config.type) {
        StorageType.MYSQL -> MySQLProvider(config)
        StorageType.SQLITE -> SQLiteProvider(config)
        else -> throw IllegalStateException("Invalid storage type!")
    }

    init {
        connectionProvider.init()
    }

    override fun hasUser(uuid: UUID): Boolean {
        try {
            connectionProvider.createConnection().use {
                val statement = it.createStatement()
                val result = statement.executeQuery(String.format("SELECT * FROM userdata WHERE uuid='%s'", uuid.toString()))
                if (result != null && result.next()) {
                    return true
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    override fun addUser(uuid: UUID, joinTime: Long): Boolean {
        return try {
            connectionProvider.createConnection().use {
                val statement = it.createStatement()
                statement.execute(String.format("REPLACE INTO userdata (uuid, time) VALUES ('%s', '%s')",
                    uuid.toString(),
                    joinTime
                ))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun totalUsers(): Int {
        return try {
            connectionProvider.createConnection().use {
                val statement = it.createStatement()
                val result = statement.executeQuery("SELECT COUNT(*) FROM userdata")
                if (result != null && result.next()) {
                    return result.getInt(1)
                }
            }
            0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    override fun close() {
        connectionProvider.shutdown()
    }
}
