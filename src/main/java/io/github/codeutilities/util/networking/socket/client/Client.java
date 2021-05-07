package io.github.codeutilities.util.networking.socket.client;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.misc.ItemUtil;
import io.github.codeutilities.util.render.ToasterUtil;
import io.github.codeutilities.util.networking.socket.SocketHandler;
import io.github.codeutilities.util.networking.socket.client.type.SocketItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;

import java.io.Closeable;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public abstract class Client implements Closeable {

    public abstract void sendData(String string) throws IOException;

    public void acceptData(String line) {
        JsonObject result = new JsonObject();
        try {
            if (line == null) {
                return;
            }
            ClientPlayerEntity player = MinecraftClient.getInstance().player;

            JsonObject data = CodeUtilities.JSON_PARSER.parse(line).getAsJsonObject();
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
                ToasterUtil.sendToaster("Received Item!", source, SystemToast.Type.NARRATOR_TOGGLE);
                player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 200, 1);
                result.addProperty("status", "success");
            } else {
                throw new Exception("Player is not in creative!");
            }
        } catch (Throwable e) {
            result.addProperty("status", "error");
            result.addProperty("error", e.getMessage());
        }

        try {
            sendData(result.toString());
        } catch (IOException ioException) {
            SocketHandler.getInstance().unregister(this);
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
