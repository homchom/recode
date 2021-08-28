package io.github.codeutilities.mod.events.impl;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.mod.events.interfaces.ChatEvents;
import io.github.codeutilities.mod.features.modules.triggers.impl.MessageReceivedTrigger;
import io.github.codeutilities.mod.features.social.chat.ConversationTimer;
import io.github.codeutilities.mod.features.modules.triggers.Trigger;
import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import io.github.codeutilities.sys.util.TextUtil;
import io.github.codeutilities.mod.features.CPU_UsageText;
import io.github.codeutilities.sys.player.DFInfo;
import io.github.codeutilities.sys.networking.State;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiveChatMessageEvent {
    public ReceiveChatMessageEvent() {
        ChatEvents.RECEIVE_MESSAGE.register(this::run);
    }

    public static boolean pjoin = false;

    public static String tipPlayer = "";

    private ActionResult run(Message message) {
        MinecraftClient mc = MinecraftClient.getInstance();

        Text text = message.getText();
        String stripped = text.getString();

        boolean cancel = false;

        if (mc.player == null) {
            return ActionResult.FAIL;
        }

        // module trigger
        Trigger.execute(new MessageReceivedTrigger());

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
                        mc.player.sendChatMessage(cmd);
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
        String msgWithoutColor = msgWithColor.replaceAll("§.", "");

        // highlight name
        if (Config.getBoolean("highlight")) {
            String highlightMatcher = Config.getString("highlightMatcher").replaceAll("\\{name}", mc.player.getName().getString());

            if (( DFInfo.currentState.getMode() != State.Mode.PLAY && msgWithoutColor.matches("^[^0-z]+.*[a-zA-Z]+: .*"))
                    || (DFInfo.currentState.getMode() == State.Mode.PLAY && msgWithoutColor.matches("^.*[a-zA-Z]+: .*"))) {
                if ((!msgWithoutColor.matches("^.*" + highlightMatcher + ": .*")) || Config.getBoolean("highlightIgnoreSender")) {
                    if (msgWithoutColor.contains(highlightMatcher)) {
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
                        mc.player.sendMessage(TextUtil.colorCodesToTextComponent(newMsg), false);
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

        // hide var scope messages (NOT WORKING RN)
        if (Config.getBoolean("hideVarScopeMessages")
                && (
                // local
                msgToString.equals("TextComponent{text='', siblings=[TextComponent{text='Scope set to ', siblings=[], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text='LOCAL', siblings=[], style=Style{ color=green, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text=' (specific to event thread).', siblings=[], style=Style{ color=white, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}") ||
                        // game
                        msgToString.equals("TextComponent{text='', siblings=[TextComponent{text='Scope set to ', siblings=[], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text='GAME', siblings=[], style=Style{ color=gray, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text=' (clears when all players leave).', siblings=[], style=Style{ color=white, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}") ||
                        // save
                        msgToString.equals("TextComponent{text='', siblings=[TextComponent{text='Scope set to ', siblings=[], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text='SAVE', siblings=[], style=Style{ color=yellow, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}, TextComponent{text=' (will be saved).', siblings=[], style=Style{ color=white, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}], style=Style{ color=null, bold=null, italic=null, underlined=null, strikethrough=null, obfuscated=null, clickEvent=null, hoverEvent=null, insertion=null, font=minecraft:default}}")
        )) {
            cancel = true;
        }

        // hide msg matching regex
        if (Config.getBoolean("hideMsgMatchingRegex") && stripped.replaceAll("§.", "").matches(Config.getString("hideMsgRegex"))) {
            cancel = true;
        }

        if (Config.getBoolean("autoClickEditMsgs") && stripped.startsWith("⏵ Click to edit variable: ")) {
            if (text.getStyle().getClickEvent().getAction() == Action.SUGGEST_COMMAND) {
                String toOpen = text.getStyle().getClickEvent().getValue();
                MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().openScreen(new ChatScreen(toOpen)));
            }
        }

        if (Config.getBoolean("autoTip") && stripped.startsWith("⏵⏵ ")) {
            if (msgWithColor.matches("§x§a§a§5§5§f§f⏵⏵ §f§l\\w+§7 is using a §x§f§f§f§f§a§a§l2§x§f§f§f§f§a§a§lx§7 booster.")) {
                tipPlayer = stripped.split("§f§l")[1].split("§7")[0];
            } else if (msgWithColor.matches("§x§a§a§5§5§f§f⏵⏵ §7Use §x§f§f§f§f§a§a\\/tip§7 to show your appreciation and receive a §x§f§f§d§4§2§a□ token notch§7!")) {
                CodeUtilities.EXECUTOR.submit(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception ignored) {}
                    mc.player.sendChatMessage("/tip " + tipPlayer);
                });
            }
        }


        //Cancelling (set cancel to true)
        if (cancel) {
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
