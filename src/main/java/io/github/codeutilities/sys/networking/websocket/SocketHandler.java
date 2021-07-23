package io.github.codeutilities.sys.networking.websocket;

import io.github.codeutilities.mod.commands.IManager;
import io.github.codeutilities.sys.networking.websocket.client.Client;
import io.github.codeutilities.sys.networking.websocket.client.SocketClient;
import io.github.codeutilities.sys.networking.websocket.client.type.NbtItem;
import io.github.codeutilities.sys.networking.websocket.client.type.RawTemplateItem;
import io.github.codeutilities.sys.networking.websocket.client.type.SocketItem;
import io.github.codeutilities.sys.networking.websocket.client.type.TemplateItem;
import org.glassfish.tyrus.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketHandler implements IManager<Client> {

    private static SocketHandler instance;
    private final Map<String, SocketItem> socketItemMap = new HashMap<>();
    private final List<Client> clients = new ArrayList<>();

    private ServerSocket server;

    public static SocketHandler getInstance() {
        return instance;
    }

    @Override
    public void initialize() {
        instance = this;

        this.addSocketItem(new NbtItem(), new TemplateItem(), new RawTemplateItem());
        try {
            server = new ServerSocket(31372);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (server == null) {
            return;
        }

        ExecutorService serverService = Executors.newSingleThreadExecutor();
        serverService.submit(() -> {
            System.out.println("Opened socket listener");
            while (true) {
                try {
                    SocketClient client = new SocketClient(server.accept());
                    this.register(client);
                    System.out.println("Client connected on local server: " + client.getSocket());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Server server = new Server("localhost", 31371, "/codeutilities", ItemWebEndpoint.class);
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addSocketItem(SocketItem... items) {
        Arrays.stream(items).forEach(item -> socketItemMap.put(item.getIdentifier(), item));
    }

    @Override
    public void register(Client client) {
        this.clients.add(client);
    }

    @Override
    public List<Client> getRegistered() {
        return this.clients;
    }

    public Map<String, SocketItem> getSocketItems() {
        return socketItemMap;
    }
}
