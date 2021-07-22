package io.github.codeutilities.mod.features.external;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.util.file.ILoader;
import io.github.codeutilities.sys.util.networking.State;
import org.apache.logging.log4j.Level;

import java.time.OffsetDateTime;

public class DFDiscordRPC implements ILoader {

    public static boolean delayRPC = false;

    public static RichPresence.Builder builder;
    private static DFDiscordRPC instance;
    private static State oldState = new State();
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
        });
    }

    public void connect() {
        if(!isConnected()){
            try {
                client.connect();
            } catch (NoDiscordClientException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        if(isConnected()) {
            client.close();
        }
    }

    public void update(State state) {

        if (!isConnected() && Config.getBoolean("discordRPC")) {
            connect();
        } else if (isConnected() && !Config.getBoolean("discordRPC")) {
            close();
            return;
        }

        RichPresence.Builder presence = new RichPresence.Builder();

        if (state.getMode() == State.Mode.SPAWN) {
            presence.setDetails("At Spawn");
            presence.setState("Node " + (state.getNode() != null ? state.getNode().getIdentifier() : "?"));
            if (state.isInSession()) presence.setSmallImage("supportsession", "In Support Session");
            else presence.setSmallImage(null, null);
            presence.setLargeImage("diamondfirelogo", "mcdiamondfire.com | CodeUtilities 2.2.2" + (CodeUtilities.BETA ? "-BETA" : ""));
        } else if(state.getMode() == State.Mode.PLAY || state.getMode() == State.Mode.DEV || state.getMode() == State.Mode.BUILD) {
            // BUILD RICH PRESENCE
            presence.setState("Plot ID: " + state.getPlot().getId() + " - Node " + (state.getNode() != null ? state.getNode().getIdentifier() : "?"));
            presence.setDetails(state.getPlot().getName() + " ");
            if(state.getMode() != State.Mode.SPAWN) {
                presence.setSmallImage("mode" + state.getMode().getIdentifier().toLowerCase(), state.getMode().getContinuousVerb());
                if (state.isInSession()) presence.setSmallImage("supportsession", "In Support Session (" + state.getMode().getContinuousVerb() + ")");
            }
            presence.setLargeImage("diamondfirelogo", state.getPlot().getStatus().equals("") ? "mcdiamondfire.com | CodeUtilities 2.2.2" + (CodeUtilities.BETA ? "-BETA" : "") : state.getPlot().getStatus());
        } else {
            close();
            return;
        }

        if (!oldState.equals(state)) {
            time = OffsetDateTime.now();
        }
        if (Config.getBoolean("discordRPCShowElapsed")) presence.setStartTimestamp(time);
        oldState = state;



        if (Config.getBoolean("discordRPC")) client.sendRichPresence(presence.build());
    }

    private static boolean isConnected() {
        return client.getStatus() == PipeStatus.CONNECTED;
    }
}
