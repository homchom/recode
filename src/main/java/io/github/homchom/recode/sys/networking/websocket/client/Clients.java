package io.github.homchom.recode.sys.networking.websocket.client;

import com.google.gson.JsonObject;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.sys.networking.websocket.SocketHandler;
import io.github.homchom.recode.sys.networking.websocket.client.type.SocketItem;
import io.github.homchom.recode.sys.renderer.ToasterUtil;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.fabricmc.api.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;

@Environment(EnvType.CLIENT)
public class Clients {

    public static String acceptData(String line) {
        JsonObject result = new JsonObject();
        try {
            if (line == null) {
                return null;
            }
            LocalPlayer player = Minecraft.getInstance().player;

            JsonObject data = Recode.JSON_PARSER.parse(line).getAsJsonObject();
            String type = data.get("type").getAsString();
            String itemData = data.get("data").getAsString();
            String source = data.get("source").getAsString();

            SocketItem item = SocketHandler.getInstance().getSocketItems().get(type);
            if (item == null) {
                throw new IllegalArgumentException("Could not find an item type that matched " + type + "!");
            }
            if (player == null) {
                throw new Exception("Player is not logged in!");
            }

            if (player.isCreative()) {
                ItemUtil.giveCreativeItem(item.getItem(itemData), true);
                ToasterUtil.sendToaster("Received Item!", source, SystemToast.SystemToastIds.NARRATOR_TOGGLE);
                player.playSound(SoundEvents.ITEM_PICKUP, 200, 1);
                result.addProperty("status", "success");
            } else {
                throw new Exception("Player is not in creative!");
            }
        } catch (Throwable e) {
            result.addProperty("status", "error");
            result.addProperty("error", e.getMessage());
        }

        return result.toString();
    }

}
