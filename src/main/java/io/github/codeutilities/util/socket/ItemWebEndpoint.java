package io.github.codeutilities.util.socket;

import io.github.codeutilities.util.socket.client.*;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;

@ServerEndpoint(value = "/item")
public class ItemWebEndpoint {
    
    HashMap<String, SessionClient> clients = new HashMap<>();
    
    @OnMessage
    public void handleTextMessage(String message, Session session) {
        clients.get(session.getId()).acceptData(message);
    }

    @OnOpen
    public void onOpen(Session session) {
        SessionClient client = new SessionClient(session);
        SocketHandler.clients.add(client);
        clients.put(session.getId(), client);
    }

    @OnClose
    public void onClose(Session session) {
        Client client = clients.remove(session.getId());
        if (client != null) {
            SocketHandler.clients.remove(client);
        }
    }
    
}
