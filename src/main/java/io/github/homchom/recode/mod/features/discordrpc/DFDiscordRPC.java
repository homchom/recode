package io.github.homchom.recode.mod.features.discordrpc;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.server.DF;
import io.github.homchom.recode.server.DFState;
import io.github.homchom.recode.server.PlayState;
import io.github.homchom.recode.server.SpawnState;
import io.github.homchom.recode.sys.file.ILoader;
import io.github.homchom.recode.util.BasicTypeExtensions;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Objects;

public class DFDiscordRPC implements ILoader {

    public static RichPresence.Builder builder;
    private static DFDiscordRPC instance;
    private static DFState oldState = DF.getCurrentDFState();
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
            LegacyRecode.info("Closing Discord hook.");
            try {
                close();
            } catch (Exception e) {
                LegacyRecode.error("Error while closing Discord hook.");
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
        if (!isConnected()){
            try {
                client.connect();
            } catch (NoDiscordClientException e) {
                Recode.logInfo("No Discord Client found, so Discord RPC will not be used");
            } catch (UnsatisfiedLinkError e) {
                Recode.logError("Discord RPC is not supported with your JVM. This will be fixed soon");
            }
        }
    }

    public void close() {
        if (isConnected()) {
            client.close();
        }
    }

    public void update(DFState state) {
        if (!isConnected() && Config.getBoolean("discordRPC")) {
            connect();
        } else if (isConnected() && !Config.getBoolean("discordRPC")) {
            close();
            return;
        }

        RichPresence.Builder presence = new RichPresence.Builder();

        // Put vars
        if (state != null) vars.put("node.id", DF.getNodeDisplayName(state));

        if (state instanceof SpawnState) {
            presence.setDetails(dyn("discordRPCSpawnDetails"));
            presence.setState(dyn("discordRPCSpawnState"));

            // TODO: readd
            /*if (Config.getBoolean("discordRPCShowSession") && state.isInSession()) {
                presence.setSmallImage("supportsession", "In Support Session");
            } else {
                presence.setSmallImage(null, null);
            }*/
            presence.setSmallImage(null, null);

            presence.setLargeImage("diamondfirelogo", getLargeImageText());

        } else if (state instanceof PlayState playState) {
            // Put vars
            vars.put("plot.name", playState.getPlot().getName());
            vars.put("plot.id", String.valueOf(playState.getPlot().getId()));
            vars.put("plot.status", playState.getStatus());

            presence.setDetails(dyn("discordRPCPlotDetails") + " ");
            presence.setState(dyn("discordRPCPlotState"));

            if (Config.getBoolean("discordRPCShowPlotMode")) {
                presence.setSmallImage("mode" + playState.getMode().getId(), BasicTypeExtensions.capitalize(playState.getMode().getDescriptor()));
            }

            // TODO: readd
            /*if (Config.getBoolean("discordRPCShowSession") && state.isInSession()) {
                presence.setSmallImage("supportsession", "In Support Session (" + playState.getMode().getDescriptor() + ")");
            }*/

            presence.setLargeImage("diamondfirelogo", Objects.equals(playState.getStatus(), "") ? getLargeImageText() : playState.getStatus());

            if (oldState instanceof PlayState oldPlayState) {
                if (oldPlayState.getPlot().getId() != playState.getPlot().getId()) {
                    setTime(RPCElapsedOption.PLOT);
                }
                if (oldPlayState.getMode() != playState.getMode()) {
                    setTime(RPCElapsedOption.MODE);
                }
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
        } else {
            close();
        }
    }

    private String getLargeImageText() {
        return "mcdiamondfire.com | recode " + Recode.getTrimmedModVersion();
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
