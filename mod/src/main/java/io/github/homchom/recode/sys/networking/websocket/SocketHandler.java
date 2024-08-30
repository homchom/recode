package io.github.homchom.recode.sys.networking.websocket;

import io.github.homchom.recode.sys.file.ILoader;
import io.github.homchom.recode.sys.networking.websocket.client.SocketClient;
import io.github.homchom.recode.sys.networking.websocket.client.WebsocketServer;
import io.github.homchom.recode.sys.networking.websocket.client.type.NbtItem;
import io.github.homchom.recode.sys.networking.websocket.client.type.RawTemplateItem;
import io.github.homchom.recode.sys.networking.websocket.client.type.SocketItem;
import io.github.homchom.recode.sys.networking.websocket.client.type.TemplateItem;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketHandler implements ILoader {

    private static SocketHandler instance;
    private final Map<String, SocketItem> socketItemMap = new HashMap<>();
    private final List<SocketClient> clients = new ArrayList<>();

    private ServerSocket serverSocket;
    private WebsocketServer webSocketServer;

    public static SocketHandler getInstance() {
        return instance;
    }

    @Override
    public void load() {
        instance = this;

        this.addSocketItem(new NbtItem(), new TemplateItem(), new RawTemplateItem());
        try {
            serverSocket = new ServerSocket(31372);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (serverSocket == null) {
            return;
        }

        ExecutorService serverService = Executors.newSingleThreadExecutor();
        serverService.submit(() -> {
            System.out.println("Opened socket listener");
            while (true) {
                try {
                    SocketClient client = new SocketClient(serverSocket.accept());
                    this.register(client);
                    System.out.println("Clients connected on local server: " + client.getSocket());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        webSocketServer = new WebsocketServer(new InetSocketAddress("localhost", 31371));
        try {
            new Thread(webSocketServer, "Item-API-Websocket-Thread").start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addSocketItem(SocketItem... items) {
        Arrays.stream(items).forEach(item -> socketItemMap.put(item.getIdentifier(), item));
    }

    public void register(SocketClient client) {
        this.clients.add(client);
    }

    public void unregister(SocketClient socketClient) {
        this.clients.remove(socketClient);
    }

    public Map<String, SocketItem> getSocketItems() {
        return socketItemMap;
    }

    public void sendData(String message) {
        for (SocketClient client :
                clients) {
            try {
                client.sendData(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (WebSocket socket :
                webSocketServer.getConnections()) {
            socket.send(message);
        }
    }
}
