package io.github.homchom.recode.mixin.server;

import io.github.homchom.recode.server.ServerConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

@Mixin(JoinMultiplayerScreen.class)
public abstract class MJoinMultiplayerScreen {
    @Unique
    private static final String[] unofficialDFAddresses = {
            "mcdiamondfire.net",
            "luke.cash"
    };

    @Shadow private ServerList servers;

    @Inject(method = "join", at = @At("HEAD"))
    public void replaceUnofficialDFAddresses(ServerData serverData, CallbackInfo ci) {
        var addressPattern = String.join("|", unofficialDFAddresses);
        @SuppressWarnings("RegExpUnnecessaryNonCapturingGroup")
        var regex = Pattern.compile("(?<prefix>\\w+\\.)?(?:" + addressPattern + ")(?<suffix>:\\d+)?");
        var matcher = regex.matcher(serverData.ip);

        if (!matcher.matches()) return;

        var mc = Minecraft.getInstance();
        var toast = SystemToast.multiline(mc,
                SystemToast.SystemToastIds.UNSECURE_SERVER_WARNING,
                Component.translatable("multiplayer.recode.unofficial_address.toast.title"),
                Component.translatable("multiplayer.recode.unofficial_address.toast")
        );
        mc.getToasts().addToast(toast);

        var prefix = matcher.group("prefix");
        if (prefix == null) prefix = "";
        var suffix = matcher.group("suffix");
        if (suffix == null) suffix = "";
        serverData.ip = prefix + ServerConstants.SERVER_ADDRESS + suffix;

        servers.save();
    }
}
