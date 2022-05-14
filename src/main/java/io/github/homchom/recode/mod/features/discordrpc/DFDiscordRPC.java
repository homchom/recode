package io.github.homchom.recode.mod.features.discordrpc;

import com.jagrosh.discordipc.*;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.file.ILoader;
import io.github.homchom.recode.sys.networking.State;
import org.apache.logging.log4j.Level;

import java.time.OffsetDateTime;
import java.util.HashMap;

public class DFDiscordRPC implements ILoader {

    public static RichPresence.Builder builder;
    private static DFDiscordRPC instance;
    private static State oldState = new State();
    private static IPCClient client;

    private static final HashMap<String, String> vars = new HashMap<>();
    private static final HashMap<RPCElapsedOption, OffsetDateTime> times = new HashMap<>();

    public DFDiscordRPC() {
        instance = this;
        setTime(RPCElapsedOption.STARTUP);
    }

    public static DFDiscordRPC getInstance() {
        return instance;
    }

    @Override
    public void load() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Recode.info("Closing Discord hook.");
            try {
                close();
            } catch (Exception e) {
                Recode.error("Error while closing Discord hook.");
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

        // Put vars
        vars.put("node.id", state.getNode() != null ? state.getNode().getIdentifier() : "?");

        if (state.getMode() == State.Mode.SPAWN) {

            presence.setDetails(dyn("discordRPCSpawnDetails"));
            presence.setState(dyn("discordRPCSpawnState"));

            if (Config.getBoolean("discordRPCShowSession") && state.isInSession()) {
                presence.setSmallImage("supportsession", "In Support Session");
            } else {
                presence.setSmallImage(null, null);
            }

            presence.setLargeImage("diamondfirelogo", "mcdiamondfire.com | Recode" + (Recode.BETA ? "-BETA" : ""));

        } else if (state.getMode() == State.Mode.PLAY || state.getMode() == State.Mode.DEV || state.getMode() == State.Mode.BUILD) {

            // Put vars
            vars.put("plot.name", state.getPlot().getName());
            vars.put("plot.id", state.getPlot().getId());
            vars.put("plot.status", state.getPlot().getStatus());

            presence.setDetails(dyn("discordRPCPlotDetails") + " ");
            presence.setState(dyn("discordRPCPlotState"));

            if (state.getMode() != State.Mode.SPAWN) {
                if (Config.getBoolean("discordRPCShowPlotMode")) {
                    presence.setSmallImage("mode" + state.getMode().getIdentifier().toLowerCase(), state.getMode().getContinuousVerb());
                }

                if (Config.getBoolean("discordRPCShowSession") && state.isInSession()) {
                    presence.setSmallImage("supportsession", "In Support Session (" + state.getMode().getContinuousVerb() + ")");
                }
            }
            presence.setLargeImage("diamondfirelogo", state.getPlot().getStatus().equals("") ? "mcdiamondfire.com | CodeUtilities 2.2.2" + (Recode.BETA ? "-BETA" : "") : state.getPlot().getStatus());
        } else {
            close();
            return;
        }

        State.Plot oldPlot = oldState.getPlot();
        String oldId;
        if (oldPlot == null) oldId = "";
        else oldId = oldPlot.getId();
        State.Plot newPlot = state.getPlot();
        String newId;
        if (newPlot == null) newId = "";
        else newId = newPlot.getId();

        if (!oldId.equals(newId) ||
                (oldState.getMode() != state.getMode() && state.getMode().equals(State.Mode.SPAWN))) {
            setTime(RPCElapsedOption.PLOT);
        }
        if (oldState.getMode() != state.getMode()) {
            setTime(RPCElapsedOption.MODE);
        }

        if (Config.getBoolean("discordRPCShowElapsed")) {
            presence.setStartTimestamp(getTime());
        } else {
            presence.setStartTimestamp(null);
        }

        oldState = state;

        if (Config.getBoolean("discordRPC")) {
            client.sendRichPresence(presence.build());
        }
    }

    private static String dyn(String key) {
        String result = Config.getDynamicString(key, vars);
        if (result.length() == 0) return null;
        else return result;
    }

    public static void setTime(RPCElapsedOption option) {
        setTime(option, OffsetDateTime.now());
    }

    public static void setTime(RPCElapsedOption option, OffsetDateTime date) {
        times.put(option, date);
    }

    public static RPCElapsedOption getElapsedOption() {
        return Config.getEnum("discordRPCElapsed", RPCElapsedOption.class);
    }

    private static OffsetDateTime getTime() {
        return times.get(getElapsedOption());
    }

    private static OffsetDateTime getTime(RPCElapsedOption option) {
        return times.get(option);
    }

    private static boolean isConnected() {
        return client.getStatus() == PipeStatus.CONNECTED;
    }
}
