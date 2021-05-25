package io.github.codeutilities.mixin.player;

import io.github.codeutilities.config.Config;
import io.github.codeutilities.features.social.tab.CodeUtilitiesServer;
import io.github.codeutilities.features.social.tab.User;
import java.util.UUID;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void getPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if(!Config.getBoolean("loadTabStars")) return;
        if(CodeUtilitiesServer.getUserAmount() == 0) return;
        
        UUID id = entry.getProfile().getId();
        Text name = entry.getDisplayName() != null ? this.spectatorFormat(entry, entry.getDisplayName().shallowCopy()) : this.spectatorFormat(entry, Team.modifyText(entry.getScoreboardTeam(), new LiteralText(entry.getProfile().getName())));
        User user = CodeUtilitiesServer.getUser(id.toString());

        if (user != null) {
            LiteralText star = new LiteralText(user.getStar());
            name = star.shallowCopy().append(name);
        }
        cir.setReturnValue(name);
    }

    private Text spectatorFormat(PlayerListEntry playerListEntry, MutableText mutableText) {
        return playerListEntry.getGameMode() == GameMode.SPECTATOR ? mutableText.formatted(Formatting.ITALIC) : mutableText;
    }
}
