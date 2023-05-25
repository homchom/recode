package io.github.homchom.recode.mixin.server;

import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import io.github.homchom.recode.server.DF;

@Mixin(ServerAddress.class)
public abstract class MServerAddress {
    @ModifyVariable(argsOnly = true, method = "parseString", at = @At("HEAD"))
    private static String switchIpIfDf(String address) {
        return DF.fixDfIp(address);
    }
}
