package io.github.codeutilities.util.socket.client;

import com.google.gson.*;
import io.github.codeutilities.util.ItemUtil;
import io.github.codeutilities.util.socket.SocketHandler;
import io.github.codeutilities.util.socket.client.type.SocketItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    
    private final Socket socket;
    private final BufferedReader reader;
    
    public SocketClient(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        new Thread(() -> {
            while (true) {
                JsonObject result = new JsonObject();
                try {
                    String line;
                    try {
                        line = reader.readLine();
                    } catch (IOException ioException) {
                        SocketHandler.clients.remove(this);
                        break;
                    }
                    
                    if (line == null) {
                        return;
                    }
                    
                    JsonObject data = new JsonParser().parse(line).getAsJsonObject();
                    String type = data.get("type").getAsString();
                    String itemData = data.get("data").getAsString();
                    String source = data.get("source").getAsString();
                    
                    SocketItem item = SocketHandler.ITEM_REGISTRY.get(type);
                    if (item == null) {
                        throw new IllegalArgumentException("Could not find an item type that matched " + type + "!");
                    }
                    
                    ItemUtil.giveCreativeItem(item.getItem(itemData));
                    
                    LiteralText recieved = new LiteralText("Received Item!");
                    LiteralText description = new LiteralText(source);
                    
                    MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, recieved, description));
                    MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 200, 1);
                    result.addProperty("status", "success");
                } catch (Throwable e) {
                    result.addProperty("status", "error");
                    result.addProperty("error", e.getMessage());
                    e.printStackTrace();
                }
                
                try {
                    socket.getOutputStream().write(result.toString().getBytes());
                } catch (IOException ioException) {
                    SocketHandler.clients.remove(this);
                    break;
                }
            }
        }).start();
    }
    
    public Socket getSocket() {
        return socket;
    }
    
}
