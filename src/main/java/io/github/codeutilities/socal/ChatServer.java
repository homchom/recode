package io.github.codeutilities.socal;

import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class ChatServer {

    Session session;

    public ChatServer() {
        try {
            WebSocketContainer wsc = ContainerProvider.getWebSocketContainer();
            session = wsc.connectToServer(this, new URI("wss://codeutilitieschatserver.blazemcworld1.repl.co"));

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