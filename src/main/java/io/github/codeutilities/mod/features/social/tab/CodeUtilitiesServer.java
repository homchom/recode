package io.github.codeutilities.mod.features.social.tab;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.util.TextUtil;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.FutureTask;

import net.minecraft.client.MinecraftClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

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
        WebMessage msg = new WebMessage(jsonObject.get("type").getAsString(), jsonObject.get("content"), jsonObject.get("id").getAsString());
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
            try {
                req.run(new WebMessage(msg.getType(), msg.getContent(), msg.getId()));
            } catch (Exception e) {
                System.err.println(String.format("Error running ws callback: %s : %s @ %s", msg.getType(), msg.getContent(), msg.getId()));
                e.printStackTrace();
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

    public static void requestMessage(WebMessage message, Requester request) {
        if(Client.client.isOpen()){
            requests.put(message.getId(), request);
            Client.client.send(message.build());
        }
    }

    public static String requestURL(String url) {
        try {
            FutureTask<Object> ft = new FutureTask<>(() -> {
            }, new Object());
            String[] response = new String[1];
            requestMessage(new WebMessage("req-proxy",url), msg -> {
                response[0] = msg.getContent().getAsString();
                ft.run();
            });
            ft.get();
            return response[0];
        } catch (Exception err) {
            err.printStackTrace();
            return "";
        }
    }

}

