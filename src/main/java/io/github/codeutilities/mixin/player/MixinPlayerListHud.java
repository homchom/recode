package io.github.codeutilities.mixin.player;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.util.networking.WebUtil;

import java.util.*;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    Map<UUID, Integer> codeutilitiesUsers = Collections.synchronizedMap(new HashMap<>());
//    0 = request made
//    1 = vanilla
//    2 = codeutilities
//    3 = codeutilities dev

    Text userStar = new LiteralText("§7⭐");
    Text devStar = new LiteralText("§a⭐");

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void getPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (!CodeUtilsConfig.getBool("loadTabStars")) {
            return;
        }
        
        UUID id = entry.getProfile().getId();
        Text name = cir.getReturnValue();
        
        if (codeutilitiesUsers.containsKey(id)) {
            int num = codeutilitiesUsers.get(id);
            if (num == 2 || num == 3) {
                Text star = num == 3 ? devStar : userStar;
                name = star.copy().append(name);
            }
        } else {
            codeutilitiesUsers.put(id, 0);
            CodeUtilities.EXECUTOR.submit(() -> {
                try {
                    JsonObject json = WebUtil
                            .getJson("https://untitled-mnlfv6uw5c06.runkit.sh/get/" + id.toString().replaceAll("-",""))
                            .getAsJsonObject();

                    if (json.get("success").getAsBoolean()) {
                        boolean hasCodeutilities = json.get("codeutilities").getAsBoolean();

                        if (hasCodeutilities) {
                            try {
                                JsonObject jsonData = WebUtil
                                        .getJson("https://codeutilities.github.io/data/cosmetics/players/" + id + ".json")
                                        .getAsJsonObject();

                                boolean dev = jsonData.get("dev").getAsBoolean();
                                codeutilitiesUsers.put(id, dev ? 3 : 2);
                            } catch (Exception e) {
                                codeutilitiesUsers.put(id, 2);
                            }

                        } else {
                            codeutilitiesUsers.put(id, 1);
                        }

                    } else {
                        throw new Exception(json.get("error").getAsString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // codeutilitiesUsers.remove(id); Don't remove as this forces a reload.
                }
            });
        }
        cir.setReturnValue(name);
    }
}
