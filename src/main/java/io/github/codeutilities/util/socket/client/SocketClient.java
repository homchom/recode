package io.github.codeutilities.util.socket.client;

import io.github.codeutilities.util.socket.SocketHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketClient extends Client {

    private final Socket socket;
    private final BufferedReader reader;

    public SocketClient(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(() -> {
            while (true) {
                try {
                    acceptData(reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                    SocketHandler.clients.remove(this);
                    return;
                }
            }
        }).start();
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void sendData(String string) throws IOException {
        socket.getOutputStream().write(string.getBytes());
        socket.getOutputStream().write('\n');
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
