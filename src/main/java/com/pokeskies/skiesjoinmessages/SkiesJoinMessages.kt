package com.pokeskies.skiesjoinmessages

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.pokeskies.skiesjoinmessages.commands.BaseCommand
import com.pokeskies.skiesjoinmessages.config.ConfigManager
import com.pokeskies.skiesjoinmessages.config.FirstJoinConfig
import com.pokeskies.skiesjoinmessages.placeholders.PlaceholderManager
import com.pokeskies.skiesjoinmessages.storage.IStorage
import com.pokeskies.skiesjoinmessages.storage.StorageType
import com.pokeskies.skiesjoinmessages.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files

class SkiesJoinMessages : ModInitializer {
    companion object {
        lateinit var INSTANCE: SkiesJoinMessages
        val LOGGER: Logger = LogManager.getLogger("skiesjoinmessages")
    }

    lateinit var configDir: File
    lateinit var configManager: ConfigManager

    lateinit var placeholderManager: PlaceholderManager

    lateinit var adventure: FabricServerAudiences
    lateinit var server: MinecraftServer
    var storage: IStorage? = null

    var gson: Gson = GsonBuilder().disableHtmlEscaping()
        .registerTypeAdapter(StorageType::class.java, StorageType.StorageTypeAdaptor())
        .registerTypeAdapter(FirstJoinConfig.Mode::class.java, FirstJoinConfig.Mode.FirstJoinModeAdaptor())
        .create()

    var gsonPretty: Gson = gson.newBuilder().setPrettyPrinting().create()

    override fun onInitialize() {
        INSTANCE = this

        this.configDir = File(FabricLoader.getInstance().configDirectory, "skiesjoinmessages")
        this.configManager = ConfigManager(configDir)

        this.storage = IStorage.load(ConfigManager.CONFIG.storage)

        this.placeholderManager = PlaceholderManager()

        ServerLifecycleEvents.SERVER_STARTING.register(ServerStarting { server: MinecraftServer? ->
            this.adventure = FabricServerAudiences.of(
                server!!
            )
            this.server = server
        })
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            BaseCommand().register(
                dispatcher
            )
        }

        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            handleLogout(handler.player)
        }
    }

    fun reload() {
        this.configManager.reload()
        this.storage = IStorage.load(ConfigManager.CONFIG.storage)
    }

    fun handleLogin(player: ServerPlayer) {
        // First join stuff. This will return if a valid first join player is found, otherwise continue on
        if (isFirstJoin(player)) {
            ConfigManager.CONFIG.firstJoin?.message?.forEach { message ->
                adventure.all().sendMessage(Utils.deserializeText(Utils.parsePlaceholders(player, message)))
            }
            return
        }

        for ((id, group) in ConfigManager.CONFIG.groups) {
            Utils.printDebug("Player Login - Checking player '${player.name.string}' for permission '${group.permission}' from group '${id}', result=${Permissions.check(player, group.permission)}")
            if (group.permission.isEmpty() || Permissions.check(player, group.permission, 1)) {
                group.joinMessage.forEach { message ->
                    adventure.all().sendMessage(Utils.deserializeText(Utils.parsePlaceholders(player, message)))
                }
                break
            }
        }
    }

    fun handleLogout(player: ServerPlayer) {
        for ((id, group) in ConfigManager.CONFIG.groups) {
            Utils.printDebug("Player Logout - Checking player '${player.name.string}' for permission '${group.permission}' from group '${id}', result=${Permissions.check(player, group.permission)}")
            if (group.permission.isEmpty() || Permissions.check(player, group.permission)) {
                group.leaveMessage.forEach { message ->
                    adventure.all().sendMessage(Utils.deserializeText(Utils.parsePlaceholders(player, message)))
                }
                break
            }
        }
    }

    private fun isFirstJoin(player: ServerPlayer): Boolean {
        val firstJoinConfig = ConfigManager.CONFIG.firstJoin ?: return false
        when (firstJoinConfig.mode) {
            FirstJoinConfig.Mode.STATS -> {
                if (player.stats.getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) <= 0) {
                    return true
                }
            }
            FirstJoinConfig.Mode.STORAGE -> {
                if (storage != null) {
                    if (!storage!!.hasUser(player.uuid)) {
                        storage!!.addUser(player.uuid, System.currentTimeMillis())
                        return true
                    }
                } else {
                    Utils.printError("Storage is null, but First Join is set to STORAGE mode! Cannot properly check if player is new...")
                }
            }
            FirstJoinConfig.Mode.HYBRID -> {
                if (player.stats.getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) > 0) {
                    if (storage != null) {
                        storage!!.addUser(player.uuid, System.currentTimeMillis())
                    }
                    return false
                }
                if (storage != null) {
                    if (!storage!!.hasUser(player.uuid)) {
                        storage!!.addUser(player.uuid, System.currentTimeMillis())
                        return true
                    }
                } else {
                    Utils.printError("Storage is null, but First Join is set to HYBRID mode! Cannot properly check if player is new...")
                }
            }
        }

        return false
    }

    fun getUniquePlayers(): Int? {
        return storage?.totalUsers()
    }

    fun <T : Any> loadFile(filename: String, default: T, create: Boolean = false): T {
        val file = File(configDir, filename)
        var value: T = default
        try {
            Files.createDirectories(configDir.toPath())
            if (file.exists()) {
                FileReader(file).use { reader ->
                    val jsonReader = JsonReader(reader)
                    value = gsonPretty.fromJson(jsonReader, default::class.java)
                }
            } else if (create) {
                Files.createFile(file.toPath())
                FileWriter(file).use { fileWriter ->
                    fileWriter.write(gsonPretty.toJson(default))
                    fileWriter.flush()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return value
    }

    fun <T> saveFile(filename: String, `object`: T): Boolean {
        val file = File(configDir, filename)
        try {
            FileWriter(file).use { fileWriter ->
                fileWriter.write(gsonPretty.toJson(`object`))
                fileWriter.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
}
