package io.github.codeutilities.sys.networking.websocket.client;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebsocketServer extends WebSocketServer {
    public WebsocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {}

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

    @Override
    public void onMessage(WebSocket conn, String message) {
        String res = Clients.acceptData(message);
        if (res != null) conn.send(res);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println();
    }

    @Override
    public void onStart() {}
}