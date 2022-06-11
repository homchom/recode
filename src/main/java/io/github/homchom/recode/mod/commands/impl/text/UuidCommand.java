package io.github.homchom.recode.mod.commands.impl.text;

import com.google.gson.*;
import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.commands.arguments.types.PlayerArgumentType;
import io.github.homchom.recode.sys.networking.DFState;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.*;
import io.github.homchom.recode.sys.util.StringUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UuidCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("uuid")
                .then(ArgBuilder.argument("username", PlayerArgumentType.player())
                        .executes(ctx -> {
                            LegacyRecode.EXECUTOR.submit(() -> {
                                String username = ctx.getArgument("username", String.class);
                                String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
                                try {
                                    String UUIDJson = IOUtils
                                            .toString(new URL(url), StandardCharsets.UTF_8);
                                    if (UUIDJson.isEmpty()) {
                                        ChatUtil.sendMessage("Player was not found!", ChatType.FAIL);
                                        return;
                                    }
                                    JsonObject json = new JsonParser().parse(UUIDJson).getAsJsonObject();
                                    String uuid = json.get("id").getAsString();
                                    String fullUUID = StringUtil.fromTrimmed(uuid);

                                    Component text = new TextComponent("§eUUID of §6" + username + " §eis §b" + fullUUID + "§e!")
                                            .withStyle(s -> s.withHoverEvent(
                                                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("§eClick to copy to clipboard."))
                                            ).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, fullUUID)));
                                    this.sendMessage(mc, text);

                                    if (this.isCreative(mc) && DFInfo.isOnDF() && DFInfo.currentState.getMode() == DFState.Mode.DEV) {
                                        this.sendChatMessage(mc, "/txt " + fullUUID);
                                    }
                                } catch (IOException e) {
                                    ChatUtil.sendMessage("§cUser §6" + username + "§c was not found.");
                                    e.printStackTrace();
                                }
                            });
                            return 1;
                        })
                )
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/uuid <username>[reset]\n"
            + "\n"
            + "Copies the uuid of the player to your clipboard and if you're in dev mode also gives you the uuid as a text item.";
    }

    @Override
    public String getName() {
        return "/uuid";
    }
}
