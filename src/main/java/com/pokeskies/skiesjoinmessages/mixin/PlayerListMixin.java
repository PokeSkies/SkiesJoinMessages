package com.pokeskies.skiesjoinmessages.mixin;

import com.pokeskies.skiesjoinmessages.SkiesJoinMessages;
import com.pokeskies.skiesjoinmessages.config.ConfigManager;
import com.pokeskies.skiesjoinmessages.utils.Utils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Ljava/util/function/Function;Z)V", at = @At("HEAD"), cancellable = true)
    public void skiesjoinmessages$broadcastSystemMessage(Component serverMessage, Function<ServerPlayer, Component> playerMessageFactory, boolean bypassHiddenChat, CallbackInfo ci) {
        if (serverMessage.getContents() instanceof TranslatableContents translated) {
            if (ConfigManager.CONFIG.getSuppressedMessages().contains(translated.getKey())) {
                Utils.INSTANCE.printDebug("Suppressing message: " + translated.getKey(), false);
                ci.cancel();
            }
        }
    }

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void skiesjoinmessages$placeNewPlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        SkiesJoinMessages.INSTANCE.playerLogin(player);
    }
}
