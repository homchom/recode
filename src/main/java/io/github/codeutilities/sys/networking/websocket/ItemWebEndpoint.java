package io.github.codeutilities.sys.networking.websocket;

import io.github.codeutilities.sys.networking.websocket.client.Client;
import io.github.codeutilities.sys.networking.websocket.client.SessionClient;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;

@ServerEndpoint(value = "/item")
public class ItemWebEndpoint {

    final HashMap<String, SessionClient> clients = new HashMap<>();

    @OnMessage
    public void handleTextMessage(String message, Session session) {
        clients.get(session.getId()).acceptData(message);
    }

    @OnOpen
    public void onOpen(Session session) {
        SessionClient client = new SessionClient(session);
        SocketHandler.getInstance().register(client);
        clients.put(session.getId(), client);
    }

    @OnClose
    public void onClose(Session session) {
        Client client = clients.remove(session.getId());
        if (client != null) {
            SocketHandler.getInstance().unregister(client);
        }
    }

}
