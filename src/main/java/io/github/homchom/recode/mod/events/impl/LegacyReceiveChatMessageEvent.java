package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.server.ReceiveChatMessageEvent;
import io.github.homchom.recode.server.ServerConstants;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacyReceiveChatMessageEvent {
    public LegacyReceiveChatMessageEvent() {
        var pattern = "§x§a§a§5§5§f§f⏵⏵ §f§l(" + ServerConstants.USERNAME_PATTERN + ")§7 is using a §x§f§f§f§f§a§a§l2§x§f§f§f§f§a§a§lx§7 booster.";
        tipPlayerRegex = Pattern.compile(pattern);
        ReceiveChatMessageEvent.INSTANCE.register(this::run);
    }

    public static boolean pjoin = false;

    // TODO: improve
    public static String tipPlayer = "";

    private final Pattern tipPlayerRegex;

    public boolean run(Component message, boolean send) {
        System.out.println("receiving message: " + message.getString());
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return false;

        boolean cancel = false;

        // TODO: temporary, migrate all code here
        String msgToString = message.getString();

        String msgWithColor = TextUtil.textComponentToColorCodes(message);
        String msgWithoutColor = msgWithColor.replaceAll("§.", "");

        //PJoin command
        if (pjoin) {
            if (msgWithoutColor.startsWith("                                       \n")) {
                if (msgWithoutColor.contains(" is currently at spawn\n")) {
                    ChatUtil.sendMessage("This player is not in a plot.", ChatType.FAIL);
                } else {
                    // PLOT ID
                    Pattern pattern = Pattern.compile("\\[\\d+]\n");
                    Matcher matcher = pattern.matcher(msgWithoutColor);
                    String id = "";
                    while (matcher.find()) {
                        id = matcher.group();
                    }
                    id = id.replaceAll("[\\[\\]\n]", "");

                    String cmd = "/join " + id;

                    if (cmd.matches("/join \\d+")) {
                        mc.player.commandUnsigned(cmd);
                    } else {
                        ChatUtil.sendMessage("Error while trying to join the plot.", ChatType.FAIL);
                    }

                }
                cancel = true;
                pjoin = false;
            }
        }

        // highlight name
        if (Config.getBoolean("highlight")) {
            String highlightMatcher = Config.getString("highlightMatcher").replaceAll("\\{name}", mc.player.getName().getString());
            if ((DFInfo.currentState.getMode() != LegacyState.Mode.PLAY && msgWithoutColor.matches("^[^0-z]+.*[a-zA-Z]+: .*"))
                    || (DFInfo.currentState.getMode() == LegacyState.Mode.PLAY && msgWithoutColor.matches("^.*[a-zA-Z]+: .*"))) {
                if ((!msgWithoutColor.matches("^.*" + highlightMatcher + ": .*")) || Config.getBoolean("highlightIgnoreSender")) {
                    if (msgWithoutColor.contains(highlightMatcher)) {
                        if (!msgWithoutColor.contains("» Joined game: ") && !msgWithoutColor.contains(" by " + highlightMatcher + ".")) {
                            String[] chars = msgWithColor.split("");
                            int i = 0;
                            int newMsgIter = 0;
                            StringBuilder getColorCodes = new StringBuilder();
                            String newMsg = msgWithColor;
                            String textLeft;

                            for (String currentChar : chars) {
                                textLeft = msgWithColor.substring(i) + " ";
                                i++;
                                if (currentChar.equals("§")) getColorCodes.append(currentChar).append(chars[i]);
                                if (textLeft.matches("^" + highlightMatcher + "[^a-zA-Z0-9].*")) {
                                    newMsg = newMsg.substring(0, newMsgIter) + Config.getString("highlightPrefix").replaceAll("&", "§")
                                            + highlightMatcher + getColorCodes + newMsg.substring(newMsgIter).replaceFirst("^" + highlightMatcher, "");

                                    newMsgIter = newMsgIter + Config.getString("highlightPrefix").length() + getColorCodes.toString().length();
                                }
                                newMsgIter++;
                            }
                            mc.player.displayClientMessage(TextUtil.colorCodesToTextComponent(newMsg), false);
                            if (Config.getBoolean("highlightOwnSenderSound") ||
                                    (!msgWithoutColor.matches("^.*" + highlightMatcher + ": .+"))) {
                                ChatUtil.playSound(
                                        Config.getSound("highlightSound"), 1, Config.getFloat("highlightSoundVolume"));
                            }
                            cancel = true;
                        }
                    }
                }
            }
        }

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

                && (msgToString.endsWith(" joined.") || msgToString.endsWith(" joined!") || msgToString.endsWith(" left."))) {

            // cancel message
            cancel = true;
        }

        // hide session spy
        if (Config.getBoolean("hideSessionSpy") && msgToString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=green")) {
            cancel = true;
        }

        // hide muted chat
        if (Config.getBoolean("hideMutedChat") && msgToString.startsWith("*") && msgToString.contains("text='*'") && msgToString.contains("color=red")) {
            cancel = true;
        }

        if (DFInfo.currentState.getMode() == LegacyState.Mode.DEV) {
            // hide var scope messages
            if (Config.getBoolean("hideVarScopeMessages") && msgToString.startsWith("Scope set to ")) {
                cancel = true;
            }

            if (Config.getBoolean("autoClickEditMsgs") && msgToString.startsWith("⏵ Click to edit variable: ")) {
                if (message.getStyle().getClickEvent().getAction() == Action.SUGGEST_COMMAND) {
                    String toOpen = message.getStyle().getClickEvent().getValue();
                    Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new ChatScreen(toOpen)));
                }
            }
        }

        // hide msg matching regex
        if (Config.getBoolean("hideMsgMatchingRegex") && msgToString.replaceAll("§.", "").matches(Config.getString("hideMsgRegex"))) {
            cancel = true;
        }

        if (Config.getBoolean("autoTip") && msgToString.startsWith("⏵⏵ ")) {
            var matcher = tipPlayerRegex.matcher(msgToString);
            if (matcher.matches()) {
                System.out.println("");
                for (int i = 0; i < matcher.groupCount(); i++) {
                    System.out.println("Group " + i + ": " + matcher.group(i));
                }
                tipPlayer = matcher.group(1);
            } else if (msgWithColor.matches("§x§a§a§5§5§f§f⏵⏵ §7Use §x§f§f§f§f§a§a/tip§7 to show your appreciation and receive a §x§f§f§d§4§2§a□ token notch§7!")) {
                LegacyRecode.executor.submit(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception ignored) {}
                    mc.player.commandUnsigned("tip " + tipPlayer);
                });
            }
        }

        return !cancel;
    }
}
