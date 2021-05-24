package io.github.codeutilities.features.social.tab;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.chat.TextUtil;
import java.net.URI;
import net.minecraft.client.MinecraftClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class CodeUtilitiesServer extends WebSocketClient {

    private static JsonArray users = new JsonArray();

    public CodeUtilitiesServer(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {
        JsonObject jsonObject = CodeUtilities.JSON_PARSER.parse(message).getAsJsonObject();
        if(jsonObject.get("type").getAsString().equals("users")){
            users = jsonObject.get("content").getAsJsonArray();
        }
        if(jsonObject.get("type").getAsString().equals("chat")){
            if(MinecraftClient.getInstance().player != null){
                MinecraftClient.getInstance().player.sendMessage(TextUtil.colorCodesToTextComponent(jsonObject.get("content").getAsString()), false);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        users = new JsonArray();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static User getUser(String query){
        query = query.replaceAll("-", "");
        String mode = "uuid";
        if(query.length() <= 16) mode = "username";
        for(JsonElement jsonElement : users){
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.get(mode).getAsString().equals(query)){
                return new User(jsonObject);
            }
        }
        return null;
    }

    public static int getUserAmount(){
        return users.size();
    }

}

