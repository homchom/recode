package io.github.codeutilities.dfrpc;

import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.events.ChatReceivedEvent;
import io.github.codeutilities.util.DFInfo;

import io.github.codeutilities.dfrpc.libs.DiscordEventHandlers;
import io.github.codeutilities.dfrpc.libs.DiscordRPC;
import io.github.codeutilities.dfrpc.libs.DiscordRichPresence;

import net.minecraft.client.MinecraftClient;

import javax.swing.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DFDiscordRPC {

    public static boolean locating = false;

    private static boolean firstLocate = true;
    private static boolean firstUpdate = true;
    private static boolean ready = false;
    private static String oldmsg = "";

    private static Date date;
    private static long time;

    public static void main() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Closing Discord hook.");
            DiscordRPC.discordShutdown();
        }));

        System.out.println("Running callbacks...");

        DFRPCThread dfrpc = new DFRPCThread();
        dfrpc.start();

    }

    public static class DFRPCThread extends Thread {
        public void run(){
            MinecraftClient mc = MinecraftClient.getInstance();

            initDiscord();

            while(true) {
                DiscordRPC.discordRunCallbacks();

                if (!ready) continue;

                if (DFInfo.isOnDF()) {
                    if (mc.player != null) {
                        mc.player.sendChatMessage("/locate");
                        locating = true;
                        for (int i = 0; i < 400; i++) {
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
                        initDiscord();
                        firstLocate = false;
                    } else {
                        updDiscord();
                        firstUpdate = false;
                    }
                }
                else {
                    firstLocate = true;
                    firstUpdate = true;
                    DiscordRPC.discordShutdown();
                }

                if (!ModConfig.getConfig().discordRPC) {
                    firstLocate = true;
                    firstUpdate = true;
                    DiscordRPC.discordShutdown();
                }

                try {
                    DFRPCThread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private static void initDiscord() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            DFDiscordRPC.ready = true;

            System.out.println("Welcome " + user.username + "#" + user.discriminator + ".");
        }).build();
        DiscordRPC.discordInitialize("813925725718577202", handlers, false);
        DiscordRPC.discordRegister("813925725718577202", "");
    }

    private static void updDiscord() {
        DiscordRichPresence.Builder presence;
        System.out.println("EEE" + ChatReceivedEvent.dfrpcMsg);

        if (ChatReceivedEvent.dfrpcMsg.equals("                                       \nYou are currently at spawn.\n" +
                "                                       ")) {
            presence = new DiscordRichPresence.Builder("At spawn");
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
            presence = new DiscordRichPresence.Builder("Plot ID: " + id + " - " + node);

            if (ChatReceivedEvent.dfrpcMsg.startsWith("                                       \nYou are currently playing on:"))
                presence.setDetails("Playing on " + name);

            if (ChatReceivedEvent.dfrpcMsg.startsWith("                                       \nYou are currently building on:"))
                presence.setDetails("Building on " + name);

            if (ChatReceivedEvent.dfrpcMsg.startsWith("                                       \nYou are currently coding on:"))
                presence.setDetails("Coding on " + name);

        }
        presence.setBigImage("diamondfirelogo", "mcdiamondfire.com");

        if (!oldmsg.equals(ChatReceivedEvent.dfrpcMsg)) firstUpdate = true;

        if (firstUpdate) {
            date = new Date();
            time = date.getTime();
        }
        presence.setStartTimestamps(time);
        oldmsg = ChatReceivedEvent.dfrpcMsg;

        if (ModConfig.getConfig().discordRPC) DiscordRPC.discordUpdatePresence(presence.build());
    }

}