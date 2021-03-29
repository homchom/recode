package io.github.codeutilities.util.socket.client;

import io.github.codeutilities.util.socket.SocketHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketClient extends Client {

    private static final ExecutorService SERVICE = Executors.newFixedThreadPool(5); // MAX of 5 connections (socket)

    private final Socket socket;
    private final BufferedReader reader;

    public SocketClient(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        SERVICE.execute(() -> {
            while (true) {
                try {
                    acceptData(reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                    SocketHandler.clients.remove(this);
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

    @Override
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
