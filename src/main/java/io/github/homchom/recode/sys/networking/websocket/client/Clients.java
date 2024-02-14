package io.github.homchom.recode.sys.networking.websocket.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.homchom.recode.sys.networking.websocket.SocketHandler;
import io.github.homchom.recode.sys.networking.websocket.client.type.SocketItem;
import io.github.homchom.recode.sys.renderer.ToasterUtil;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class Clients {

    public static String acceptData(String line) {
        JsonObject result = new JsonObject();
        try {
            if (line == null) {
                return null;
            }
            LocalPlayer player = Minecraft.getInstance().player;

            JsonObject data = JsonParser.parseString(line).getAsJsonObject();
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
                final ItemStack itemStack = item.getItem(itemData);
                Minecraft.getInstance().submit(() -> {
                    ItemUtil.giveCreativeItem(itemStack, true);
                    ToasterUtil.sendToaster("Received Item!", source, SystemToast.SystemToastId.NARRATOR_TOGGLE);
                    player.playSound(SoundEvents.ITEM_PICKUP, 200, 1);
                });
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
