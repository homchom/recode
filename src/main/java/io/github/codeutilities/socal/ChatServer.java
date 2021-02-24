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
            wsc.connectToServer(this, new URI("https://CodeUtilitiesChatServer.blazemcworld1.repl.co"));

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @OnOpen
    private void onOpen(Session userSession) {
        session = userSession;
        sendMessage("message from client");
    }

    @OnClose
    private void onClose(Session userSession, CloseReason reason) {
        System.out.println("Socket closed: " + reason.getReasonPhrase());
    }

    @OnError
    private void onError(Session userSession, Throwable tr) {
        tr.printStackTrace();
    }

    @OnMessage
    private void onMessage(String msg) {
        System.out.println(msg);
    }

    private void sendMessage(String msg) {
        session.getAsyncRemote().sendText(msg);
    }

}
