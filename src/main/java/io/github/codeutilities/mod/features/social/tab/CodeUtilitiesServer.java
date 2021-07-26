package io.github.codeutilities.mod.features.social.tab;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CodeUtilitiesServer extends WebSocketClient {

    private static JsonArray users = new JsonArray();
    private static HashMap<String, Requester> requests = new HashMap<>();
    private final CodeUtilitiesServer instance;

    public CodeUtilitiesServer(URI serverUri) {
        super(serverUri);
        this.instance = this;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {
        JsonObject jsonObject = CodeUtilities.JSON_PARSER.parse(message).getAsJsonObject();
        Message msg = new Message(jsonObject.get("type").getAsString(), jsonObject.get("content"), jsonObject.get("id").getAsString());
        if(msg.getType().equals("users")){
            users = msg.getContent().getAsJsonArray();
        }
        if(msg.getType().equals("chat")){
            if(MinecraftClient.getInstance().player != null){
                MinecraftClient.getInstance().player.sendMessage(TextUtil.colorCodesToTextComponent(msg.getContent().getAsString()), false);
            }
        }
        Requester req = requests.get(msg.getId());
        if(req != null) {
            requests.remove(msg.getId());
            req.run(new Message(msg.getType(), msg.getContent(), msg.getId()));
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

    public static void requestMessage(Message message, Requester request) {
        if(Client.client.isOpen()){
            requests.put(message.getId(), request);
            Client.client.send(message.build());
        }
    }

}

