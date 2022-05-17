package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.events.interfaces.ChatEvents;
import io.github.homchom.recode.mod.features.social.chat.message.Message;
import io.github.homchom.recode.sys.networking.State;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiveChatMessageEvent {
    public ReceiveChatMessageEvent() {
        ChatEvents.RECEIVE_MESSAGE.register(this::run);
    }

    public static boolean pjoin = false;

    public static String tipPlayer = "";

    private InteractionResult run(Message message) {
        Minecraft mc = Minecraft.getInstance();

        Component text = message.getText();
        String stripped = text.getString();

        boolean cancel = false;

        if (mc.player == null) {
            return InteractionResult.FAIL;
        }

        //PJoin command
        if (pjoin) {
            String msg = stripped.replaceAll("§.", "");
            if (msg.startsWith("                                       \n")) {
                if (msg.contains(" is currently at spawn\n")) {
                    ChatUtil.sendMessage("This player is not in a plot.", ChatType.FAIL);
                } else {
                    // PLOT ID
                    Pattern pattern = Pattern.compile("\\[[0-9]+]\n");
                    Matcher matcher = pattern.matcher(msg);
                    String id = "";
                    while (matcher.find()) {
                        id = matcher.group();
                    }
                    id = id.replaceAll("\\[|]|\n", "");

                    String cmd = "/join " + id;

                    if (cmd.matches("/join [0-9]+")) {
                        mc.player.chat(cmd);
                    } else {
                        ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                    }

                }
                cancel = true;
                pjoin = false;
            }
        }

        String msgToString = message.toString();

        String msgWithColor = TextUtil.textComponentToColorCodes(text);

        // hide join/leave messages
        if (Config.getBoolean("hideJoinLeaveMessages")
                && msgToString.contains("', siblings=[], style=Style{ color=gray, bold=")

                // check TextComponent
                && (msgToString.contains("bold=false") || msgToString.contains("bold=null"))
                && (msgToString.contains("italic=false") || msgToString.contains("italic=null"))
                && (msgToString.contains("underlined=false") || msgToString.contains("underlined=null"))
                && (msgToString.contains("strikethrough=false") || msgToString.contains("strikethrough=null"))
                && (msgToString.contains("obfuscated=false") || msgToString.contains("obfuscated=null"))
                && (msgToString.contains("clickEvent=false") || msgToString.contains("clickEvent=null"))
                && (msgToString.contains("hoverEvent=false") || msgToString.contains("hoverEvent=null"))
                && (msgToString.contains("insertion=false") || msgToString.contains("insertion=null"))

                && (stripped.endsWith(" joined.") || stripped.endsWith(" joined!") || stripped.endsWith(" left."))) {

            // cancel message
            cancel = true;
        }

        // hide session spy
        if (Config.getBoolean("hideSessionSpy") && stripped.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=green")) {
            cancel = true;
        }

        // hide muted chat
        if (Config.getBoolean("hideMutedChat") && stripped.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=red")) {
            cancel = true;
        }

        if (DFInfo.currentState.getMode() == State.Mode.DEV) {
            // hide var scope messages
            if (Config.getBoolean("hideVarScopeMessages") && stripped.startsWith("Scope set to ")) {
                cancel = true;
            }

            if (Config.getBoolean("autoClickEditMsgs") && stripped.startsWith("⏵ Click to edit variable: ")) {
                if (text.getStyle().getClickEvent().getAction() == Action.SUGGEST_COMMAND) {
                    String toOpen = text.getStyle().getClickEvent().getValue();
                    Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new ChatScreen(toOpen)));
                }
            }
        }

        // hide msg matching regex
        if (Config.getBoolean("hideMsgMatchingRegex") && stripped.replaceAll("§.", "").matches(Config.getString("hideMsgRegex"))) {
            cancel = true;
        }

        if (Config.getBoolean("autoTip") && stripped.startsWith("⏵⏵ ")) {
            if (msgWithColor.matches("§x§a§a§5§5§f§f⏵⏵ §f§l\\w+§7 is using a §x§f§f§f§f§a§a§l2§x§f§f§f§f§a§a§lx§7 booster.")) {
                tipPlayer = stripped.split("§f§l")[1].split("§7")[0];
            } else if (msgWithColor.matches("§x§a§a§5§5§f§f⏵⏵ §7Use §x§f§f§f§f§a§a\\/tip§7 to show your appreciation and receive a §x§f§f§d§4§2§a□ token notch§7!")) {
                Recode.EXECUTOR.submit(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception ignored) {}
                    mc.player.chat("/tip " + tipPlayer);
                });
            }
        }


        //Cancelling (set cancel to true)
        if (cancel) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
