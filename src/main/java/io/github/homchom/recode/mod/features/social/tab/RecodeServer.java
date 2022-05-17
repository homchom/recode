package io.github.homchom.recode.mod.features.social.tab;

import com.google.gson.*;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.Minecraft;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.*;
import java.util.concurrent.FutureTask;

public class RecodeServer extends WebSocketClient {

    private static List<User> users = Collections.emptyList();
    private static HashMap<String, Requester> requests = new HashMap<>();
    private final RecodeServer instance;

    public RecodeServer(URI serverUri) {
        super(serverUri);
        this.instance = this;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {}

    @Override
    public void onMessage(String message) {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        WebMessage msg = new WebMessage(jsonObject.get("type").getAsString(), jsonObject.get("content"), jsonObject.get("id").getAsString());
        if (jsonObject.get("type").getAsString().equals("users")) {
            List<User> users2 = new ArrayList<>();
            for (JsonElement element :
                    jsonObject.get("content").getAsJsonArray()) {
                users2.add(new User(element.getAsJsonObject()));
            }

            users = users2;
        } else if (jsonObject.get("type").getAsString().equals("chat")) {
            if(Minecraft.getInstance().player != null){
                Minecraft.getInstance().player.displayClientMessage(TextUtil.colorCodesToTextComponent(msg.getContent().getAsString()), false);
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
        users = Collections.emptyList();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static User getUser(String query){
        query = query.replaceAll("-", "");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if(query.length() <= 16 && user.getUsername().equals(query)) return user;
            else if (user.getUuid().equals(query)) return user;
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

