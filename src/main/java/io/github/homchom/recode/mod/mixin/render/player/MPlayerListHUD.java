package io.github.homchom.recode.mod.mixin.render.player;

import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.social.tab.*;
import io.github.homchom.recode.sys.util.StringUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerTabOverlay.class)
public class MPlayerListHUD {
    private static final Component SPACE = Component.nullToEmpty(" ");

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void getPlayerName(PlayerInfo entry, CallbackInfoReturnable<Component> cir) {
        if(!Config.getBoolean("loadTabStars")) return;
        if(RecodeServer.getUserAmount() == 0) return;
        
        UUID id = entry.getProfile().getId();
        Component name = entry.getTabListDisplayName() != null ? this.spectatorFormat(entry, entry.getTabListDisplayName().copy()) : this.spectatorFormat(entry, PlayerTeam.formatNameForTeam(entry.getTeam(), new TextComponent(entry.getProfile().getName())));
        User user = RecodeServer.getUser(id.toString());

        if (user != null) {
            TextComponent star = new TextComponent(StringUtil.STRIP_CHARS_PATTERN.matcher(user.getStar()).replaceAll(""));
            if (Config.getBoolean("relocateTabStars")) {
                name = name.copy().append(SPACE).append(star);
            } else {
                name = star.append(SPACE).append(name);
            }
        }
        cir.setReturnValue(name);
    }

    private Component spectatorFormat(PlayerInfo playerListEntry, MutableComponent mutableText) {
        return playerListEntry.getGameMode() == GameType.SPECTATOR ? mutableText.withStyle(ChatFormatting.ITALIC) : mutableText;
    }
}
