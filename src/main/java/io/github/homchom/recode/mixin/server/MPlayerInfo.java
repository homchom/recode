package io.github.homchom.recode.mixin.server;

import io.github.homchom.recode.server.ServerTrust;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(PlayerInfo.class)
public class MPlayerInfo {
    @Redirect(method = "<init>", at = @At(value = "INVOKE",
            target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),
            slice = @Slice(
                from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/ProfilePublicKey;createValidated(Lnet/minecraft/util/SignatureValidator;Ljava/util/UUID;Lnet/minecraft/world/entity/player/ProfilePublicKey$Data;Ljava/time/Duration;)Lnet/minecraft/world/entity/player/ProfilePublicKey;"),
                to = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/SignedMessageValidator;create(Lnet/minecraft/world/entity/player/ProfilePublicKey;Z)Lnet/minecraft/network/chat/SignedMessageValidator;")
            )
    )
    public void suppressPublicKeyErrorIfTrusted(Logger instance, String s, Object o1, Object o2) {
        if (!ServerTrust.isServerTrusted()) instance.error(s, o1, o2);
    }
}
