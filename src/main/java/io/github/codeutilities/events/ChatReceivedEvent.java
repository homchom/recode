package io.github.codeutilities.events;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.DFInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ChatReceivedEvent {

    public static int rejoinStep = 0;
    public static int pjoinStep = 0;

    public static void onMessage(Text message, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        //CodeUtilities.log(Level.INFO, "CHAT: " + message.getString());
        String text = message.getString();

        boolean cancel = false;

        if (mc.player == null) {
            return;
        }
        
        //Patch Number detection
        if (text.matches("Current patch: .*\\. See the patch notes with \\/patch!")) {
            try {
                String patchText = text.replaceAll("Current patch: (.*)\\. See the patch notes with \\/patch!", "$1");

                DFInfo.isPatchNewer(patchText, "0"); //very lazy validation lol
                DFInfo.patchId = patchText;
                CodeUtilities.log(Level.INFO, "DiamondFire Patch " + DFInfo.patchId + " detected!");
            }catch (Exception e) {
                CodeUtilities.log(Level.INFO, "Error on parsing patch number!");
                e.printStackTrace();
            }
        }

        //Updating Player State
        //LOBBY is handled at ActionbarReceivedEvent
        //PLAY
        if (text.matches("Joined game: .* by .*") && text.startsWith("Joined game: ")) {
            DFInfo.currentState = DFInfo.State.PLAY;
        }
        //BUILD
        if (mc.player.isCreative() && text.contains("» You are now in build mode.") && text.startsWith("»")) {
            DFInfo.currentState = DFInfo.State.BUILD;
        }
        //DEV
        if (mc.player.isCreative() && text.contains("» You are now in dev mode.") && text.startsWith("»")) {
            DFInfo.currentState = DFInfo.State.DEV;
        }

        //Rejoin command
        if (rejoinStep > 0) {
            cancel = true;
            if (text.contains("          ")) {
                rejoinStep--;
            }else if (rejoinStep == 1) {
                if (text.contains("ID: ")) {
                    String finalId = StringUtils.substringBetween(text, "[ID: ", "]");
                    if (finalId.matches("[0-9]+")) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mc.player.sendChatMessage("/join " + finalId);
                            ChatUtil.sendMessage("Rejoined plot " + finalId, ChatType.SUCCESS);
                        }).start();
                    }else {
                        ChatUtil.sendMessage("Error while trying to rejoin the plot.", ChatType.FAIL);
                        rejoinStep = 0;
                    }
                }
            }
        }

        //PJoin command
        if (pjoinStep > 0) {
            cancel = true;
            if (text.contains("          ")) {
                pjoinStep--;
            }else if (pjoinStep == 1) {
                if (text.contains("is currently at §6spawn§e:")) {
                    ChatUtil.sendMessage("This player is not in a plot.", ChatType.FAIL);
                }else {
                    try {
                        String cmd = "/join " + (StringUtils.substringBetween(text, "[§7ID: ", "§8]"));
                        if (cmd.matches("/join [0-9]+")) {
                            mc.player.sendChatMessage(cmd);
                        } else {
                            ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                            pjoinStep = 0;
                        }
                    }catch (Exception e) {
                        ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                        e.printStackTrace();
                        pjoinStep = 0;
                    }
                }
            }
        }

        //Cancelling (set cancel to true)
        if (cancel) {
            CodeUtilities.log(Level.INFO, "[CANCELLED] " + text);
            ci.cancel();
        }
    }
}
