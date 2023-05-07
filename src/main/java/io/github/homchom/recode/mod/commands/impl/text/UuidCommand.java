package io.github.homchom.recode.mod.commands.impl.text;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.commands.arguments.types.PlayerArgumentType;
import io.github.homchom.recode.server.DF;
import io.github.homchom.recode.server.PlotMode;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.util.StringUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UuidCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("uuid")
                .then(ArgBuilder.argument("username", PlayerArgumentType.player())
                        .executes(ctx -> {
                            LegacyRecode.executor.submit(() -> {
                                String username = ctx.getArgument("username", String.class);
                                String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
                                try {
                                    String UUIDJson = IOUtils
                                            .toString(new URL(url), StandardCharsets.UTF_8);
                                    if (UUIDJson.isEmpty()) {
                                        ChatUtil.sendMessage("Player was not found!", ChatType.FAIL);
                                        return;
                                    }
                                    JsonObject json = JsonParser.parseString(UUIDJson).getAsJsonObject();
                                    String uuid = json.get("id").getAsString();
                                    String fullUUID = StringUtil.fromTrimmed(uuid);

                                    Component text = Component.literal("§eUUID of §6" + username + " §eis §b" + fullUUID + "§e!")
                                            .withStyle(s -> s.withHoverEvent(
                                                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§eClick to copy to clipboard."))
                                            ).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, fullUUID)));
                                    this.sendMessage(mc, text);

                                    if (DF.isInMode(DF.getCurrentDFState(), PlotMode.Dev)) {
                                        this.sendCommand(mc, "txt " + fullUUID);
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
