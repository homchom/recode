package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.event.*;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.social.chat.message.Message;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.*;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;

import java.util.regex.*;

public class LegacyReceiveChatMessageEvent {
    public LegacyReceiveChatMessageEvent() {
        RecodeEvents.RECEIVE_CHAT_MESSAGE.listen(this::run);
    }

    public static boolean pjoin = false;

    public static String tipPlayer = "";

    private void run(EventValidator result, Message message) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            result.setValid(false);
            return;
        }

        Component text = message.getText();
        String stripped = text.getString();

        boolean cancel = false;

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
                    id = id.replaceAll("[\\[]\n]", "");

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

        if (DFInfo.currentState.getMode() == LegacyState.Mode.DEV) {
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
            } else if (msgWithColor.matches("§x§a§a§5§5§f§f⏵⏵ §7Use §x§f§f§f§f§a§a/tip§7 to show your appreciation and receive a §x§f§f§d§4§2§a□ token notch§7!")) {
                LegacyRecode.EXECUTOR.submit(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception ignored) {}
                    mc.player.chat("/tip " + tipPlayer);
                });
            }
        }

        result.setValid(!cancel);
    }
}
