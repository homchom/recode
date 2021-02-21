package io.github.codeutilities.commands.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.DFInfo;
import io.github.codeutilities.util.StringUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UuidCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("uuid")
                .then(ArgBuilder.argument("username", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            boolean copy = false;
                            if (ctx.getArgument("username", String.class).contains(" copy")) {
                                copy = true;
                            }
                            String username = ctx.getArgument("username", String.class).replace(" copy", "");
                            String url = "https://mc-heads.net/minecraft/profile/" + username;
                            try {
                                String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
                                if (UUIDJson.isEmpty()) {
                                    ChatUtil.sendMessage("Player was not found!", ChatType.FAIL);
                                    return -1;
                                }
                                JsonObject json = new JsonParser().parse(UUIDJson).getAsJsonObject();
                                String uuid = json.get("id").getAsString();
                                String fullUUID = StringUtil.fromTrimmed(uuid);
                                ChatUtil.sendMessage("§eUUID of §b" + username + "§e is §d" + fullUUID + "§e!");
                                if (copy) {
                                    ChatUtil.sendMessage("§aThe UUID has been copied to the clipboard!");
                                    mc.keyboard.setClipboard(fullUUID);
                                } else if (DFInfo.isOnDF()) {
                                    this.sendChatMessage(mc, "/txt " + fullUUID);
                                }
                            } catch (IOException e) {
                                ChatUtil.sendMessage("§cUser §6" + username + "§c was not found.");
                                e.printStackTrace();
                            }

                            return 1;
                        })
                )
        );
    }
}
