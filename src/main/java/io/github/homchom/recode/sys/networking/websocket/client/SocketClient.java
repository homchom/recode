package io.github.homchom.recode.sys.networking.websocket.client;

import io.github.homchom.recode.sys.networking.websocket.SocketHandler;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketClient implements Closeable {

    private static final ExecutorService SERVICE = Executors.newFixedThreadPool(5); // MAX of 5 connections (socket)

    private final Socket socket;
    private final BufferedReader reader;

    public SocketClient(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        SERVICE.execute(() -> {
            while (true) {
                try {
                    String res = Clients.acceptData(reader.readLine());
                    sendData(res);
                } catch (IOException e) {
                    e.printStackTrace();
                    SocketHandler.getInstance().unregister(this);
                    try {
                        SocketClient.this.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    return;
                }
            }
        });
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendData(String string) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(string.getBytes());
        outputStream.write('\n');
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        socket.close();
        reader.close();
    }
}
