package io.github.codeutilities.features.external;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.Config;
import io.github.codeutilities.util.file.ILoader;
import io.github.codeutilities.util.networking.State;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

import java.time.OffsetDateTime;

public class DFDiscordRPC implements ILoader {

    public static boolean delayRPC = false;

    public static boolean connected = false;
    public static RichPresence.Builder builder;
    private static DFDiscordRPC instance;
    private static String oldMode = "";
    private static OffsetDateTime time;
    private static IPCClient client;

    public DFDiscordRPC() {
        instance = this;
    }

    public static DFDiscordRPC getInstance() {
        return instance;
    }

    @Override
    public void load() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CodeUtilities.log(Level.INFO, "Closing Discord hook.");
            try {
                close();
            } catch (Exception e) {
                CodeUtilities.log(Level.ERROR, "Error while closing Discord hook.");
            }
        }));

        client = new IPCClient(813925725718577202L);
        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setDetails("Playing");
                DFDiscordRPC.builder = builder;
            }

            @Override
            public void onClose(IPCClient client, JSONObject json) {
                connected = false;
            }

            @Override
            public void onDisconnect(IPCClient client, Throwable t) {
                connected = false;
            }
        });
    }

    public void connect() {
        try {
            client.connect();
            connected = true;
        } catch (NoDiscordClientException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        client.close();
        connected = false;
    }

    public void update(State state) {

        if (!connected && Config.getBoolean("discordRPC")) {
            connect();
        } else if (connected && !Config.getBoolean("discordRPC")) {
            close();
            return;
        }

        if(state != null){
            if(state.getMode() == State.Mode.OFFLINE || state.getMode() == null){
                close();
                return;
            }
        } else {
            close();
            return;
        }

        RichPresence.Builder presence = new RichPresence.Builder();
        String mode = state.getMode().getIdentifier();

        if (state.getMode() == State.Mode.SPAWN) {
            presence.setDetails("At Spawn");
            presence.setState("Node " + (state.getNode() != null ? state.getNode().getIdentifier() : "?"));
            if (state.isInSession()) presence.setSmallImage("supportsession", "In Support Session");
            else presence.setSmallImage(null, null);
            presence.setLargeImage("diamondfirelogo", "mcdiamondfire.com");
        } else if(state.getPlot() != null && state.getNode() != null) {
            // BUILD RICH PRESENCE
            presence.setState("Plot ID: " + state.getPlot().getId() + " - Node " + (state.getNode() != null ? state.getNode().getIdentifier() : "?"));
            presence.setDetails(state.getPlot().getName() + " ");
            if(state.getMode() != State.Mode.SPAWN) {
                presence.setSmallImage("mode" + state.getMode().getIdentifier().toLowerCase(), state.getMode().getContinuousVerb());
                if (state.isInSession()) presence.setSmallImage("supportsession", "In Support Session (" + state.getMode().getContinuousVerb() + ")");
            }
            presence.setLargeImage("diamondfirelogo", state.getPlot().getStatus().equals("") ? "mcdiamondfire.com" : state.getPlot().getStatus());
        } else {
            return;
        }

        if (!oldMode.equals(mode)) {
            time = OffsetDateTime.now();
        }
        if (Config.getBoolean("discordRPCShowElapsed")) presence.setStartTimestamp(time);
        oldMode = mode;

        if (Config.getBoolean("discordRPC")) client.sendRichPresence(presence.build());
    }
}
