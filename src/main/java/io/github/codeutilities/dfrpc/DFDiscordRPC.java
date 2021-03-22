package io.github.codeutilities.dfrpc;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.events.ChatReceivedEvent;
import io.github.codeutilities.util.DFInfo;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.Callback;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;

import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DFDiscordRPC {

    public static boolean locating = false;

    private static boolean firstLocate = true;
    private static boolean firstUpdate = true;
    private static String oldmsg = "";
    private static OffsetDateTime time;

    public static IPCClient client;
    public static RichPresence.Builder builder;

    public static void main() throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CodeUtilities.log(Level.INFO, "Closing Discord hook.");
            client.close();
        }));

        client = new IPCClient(813925725718577202L);
        client.setListener(new IPCListener(){
            @Override
            public void onReady(IPCClient client)
            {
                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setDetails("Idle")
                        .setStartTimestamp(OffsetDateTime.now())
                        .setLargeImage("canary-large", "Discord Canary")
                        .setSmallImage("ptb-small", "Discord PTB");
                io.github.codeutilities.dfrpc.DFDiscordRPC.builder = builder;
                client.sendRichPresence(builder.build());
            }
        });

        DFRPCThread dfrpc = new DFRPCThread();
        dfrpc.start();

    }

    public static class DFRPCThread extends Thread {
        public void run(){
            MinecraftClient mc = MinecraftClient.getInstance();
            String oldState = "Not on DF";

            CodeUtilities.log(Level.INFO,"STARTING RPC");

            while(true) {

                if (DFInfo.isOnDF()) {
                    if (!String.valueOf(DFInfo.currentState).equals(oldState)) {
                        if (mc.player != null) {
                            if (!DFInfo.isPatchNewer(DFInfo.patchId, "5.3.1") && ModConfig.getConfig().discordRPC) mc.player.sendChatMessage("/locate");
                            locating = true;
                            for (int i = 0; i < 800; i++) {
                                try {
                                    DFRPCThread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!locating) break;
                            }
                            locating = false;
                        }

                        if (firstLocate) {
                            try {
                                client.connect();
                            } catch (NoDiscordClientException ignored) {
                            }
                            firstLocate = false;
                        } else {
                            updDiscord();
                            firstUpdate = false;
                            CodeUtilities.log(Level.INFO, "----------- RPC Updated! Status: " + client.getStatus());
                        }
                    }
                } else {
                    firstLocate = true;
                    firstUpdate = true;
                    try {
                        client.close();
                        } catch (Exception ignored) { }
                }

                if (!ModConfig.getConfig().discordRPC) {
                    firstLocate = true;
                    firstUpdate = true;
                    try {
                        client.close();
                    } catch (Exception ignored) { }

                }

                if (DFInfo.isOnDF()) {
                    oldState = String.valueOf(DFInfo.currentState);
                } else { oldState = "Not on DF"; }

                try {
                    DFRPCThread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private static void updDiscord() {
        RichPresence.Builder presence = new RichPresence.Builder();

        if (ChatReceivedEvent.dfrpcMsg.equals("                                       \nYou are currently at spawn.\n" +
                "                                       ")) {
            presence.setState("At spawn");
            presence.setSmallImage(null, null);
        }
        else {
            // PLOT ID
            Pattern pattern = Pattern.compile("\\[[0-9]+]\n");
            Matcher matcher = pattern.matcher(ChatReceivedEvent.dfrpcMsg);
            String id = "";
            while (matcher.find()) {
                id = matcher.group();
            }
            id = id.replaceAll("\\[|]|\n", "");

            // PLOT NODE
            pattern = Pattern.compile("Node ([0-9]|Beta)\n");
            matcher = pattern.matcher(ChatReceivedEvent.dfrpcMsg);
            String node = "";
            while (matcher.find()) {
                node = matcher.group();
            }

            // PLOT NAME
            pattern = Pattern.compile("» .+ \\[[0-9]+]\n");
            matcher = pattern.matcher(ChatReceivedEvent.dfrpcMsg);
            String name = "";
            while (matcher.find()) {
                name = matcher.group();
            }
            name = name.replaceAll("(^» )|( \\[[0-9]+]\n$)", "");

            // BUILD RICH PRESENCE
            presence.setState("Plot ID: " + id + " - " + node);
            presence.setDetails(name);

            if (ChatReceivedEvent.dfrpcMsg.startsWith("                                       \nYou are currently playing on:"))
                presence.setSmallImage("modeplay", "Playing");

            if (ChatReceivedEvent.dfrpcMsg.startsWith("                                       \nYou are currently building on:"))
                presence.setSmallImage("modebuild", "Building");

            if (ChatReceivedEvent.dfrpcMsg.startsWith("                                       \nYou are currently coding on:"))
                presence.setSmallImage("modedev", "Coding");

        }
        presence.setLargeImage("diamondfirelogo", "mcdiamondfire.com");

        if (!oldmsg.equals(ChatReceivedEvent.dfrpcMsg)) firstUpdate = true;

        if (firstUpdate) {
            time = OffsetDateTime.now();
        }
        presence.setStartTimestamp(time);
        oldmsg = ChatReceivedEvent.dfrpcMsg;

        if (ModConfig.getConfig().discordRPC) client.sendRichPresence(presence.build());
    }

}