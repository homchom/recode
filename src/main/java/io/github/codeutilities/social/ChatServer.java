package io.github.codeutilities.social;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class ChatServer {

    Session session;

    public ChatServer() {
        try {
            WebSocketContainer wsc = ContainerProvider.getWebSocketContainer();
            session = wsc.connectToServer(this, new URI("wss://codeutilitieschatserver.blazemcworld1.repl.co/chat/"));

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen() {
        System.out.println("Socket open");
        sendMessage("message from client");
    }

    @OnClose
    public void onClose(CloseReason reason) {
        System.out.println("Socket closed: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Throwable tr) {
        tr.printStackTrace();
    }

    @OnMessage
    public void onMessage(String msg) {
        System.out.println(msg);
    }

    public void sendMessage(String msg) {
        session.getAsyncRemote().sendText(msg);
    }

}