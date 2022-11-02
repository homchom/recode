package io.github.homchom.recode.mixin.server;

import io.github.homchom.recode.server.ServerTrust;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Duration;
import java.util.UUID;

@Mixin(ProfilePublicKey.class)
public class MProfilePublicKey {
    @Inject(method = "createValidated", at = @At("HEAD"), cancellable = true)
    private static void createIfTrusted(SignatureValidator validator, UUID uuid, ProfilePublicKey.Data data,
                                        Duration duration, CallbackInfoReturnable<ProfilePublicKey> ci) {
        if (ServerTrust.isServerTrusted()) ci.setReturnValue(new ProfilePublicKey(data));
    }
}