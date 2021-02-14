package io.github.codeutilities.util.socket.client;

import javax.websocket.Session;
import java.io.IOException;

public class SessionClient extends Client {

    private final Session session;

    public SessionClient(Session session) {
        this.session = session;
    }

    @Override
    public void sendData(String string) throws IOException {
        session.getBasicRemote().sendText(string);
    }

    @Override
    public void close() throws IOException {
        session.close();
    }
}
