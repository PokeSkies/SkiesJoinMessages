package com.pokeskies.skiesjoinmessages.storage.database.sql.providers

import com.pokeskies.skiesjoinmessages.SkiesJoinMessages
import com.pokeskies.skiesjoinmessages.config.StorageConfig
import com.zaxxer.hikari.HikariConfig
import java.io.File

class SQLiteProvider(config: StorageConfig) : HikariCPProvider(config) {
    override fun getConnectionURL(): String = String.format(
        "jdbc:sqlite:%s",
        File(SkiesJoinMessages.INSTANCE.configDir, "storage.db").toPath().toAbsolutePath()
    )

    override fun getDriverClassName(): String = "org.sqlite.JDBC"
    override fun getDriverName(): String = "sqlite"
    override fun configure(config: HikariConfig) {}
}
