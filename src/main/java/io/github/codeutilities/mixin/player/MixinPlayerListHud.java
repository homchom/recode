package io.github.codeutilities.mixin.player;

import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.features.social.tab.PlayerlistStarServer;
import java.util.UUID;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    Text userStar = new LiteralText("§3⭐");
    Text devStar = new LiteralText("§d⭐");

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void getPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (!CodeUtilsConfig.getBool("loadTabStars")) {
            return;
        }

        UUID id = entry.getProfile().getId();
        Text name = cir.getReturnValue();

        if (PlayerlistStarServer.users.containsKey(id)) {
            boolean isDev = PlayerlistStarServer.users.get(id);
            Text star = isDev ? devStar : userStar;
            name = star.copy().append(name);
        }
        cir.setReturnValue(name);
    }
}
