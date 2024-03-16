package com.pokeskies.skiesjoinmessages.config

import com.pokeskies.skiesjoinmessages.SkiesJoinMessages
import com.pokeskies.skiesjoinmessages.utils.Utils
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ConfigManager(private val configDir: File) {
    companion object {
        lateinit var CONFIG: MainConfig
    }

    init {
        reload()
    }

    fun reload() {
        copyDefaults()
        CONFIG = SkiesJoinMessages.INSTANCE.loadFile("config.json", MainConfig())
    }

    fun copyDefaults() {
        val classLoader = SkiesJoinMessages::class.java.classLoader

        configDir.mkdirs()

        // Main Config
        val configFile = configDir.resolve("config.json")
        if (!configFile.exists()) {
            try {
                val inputStream: InputStream = classLoader.getResourceAsStream("assets/skiesjoinmessages/config.json")
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: Exception) {
                Utils.printError("Failed to copy the default config file: $e - ${e.message}")
            }
        }
    }
}