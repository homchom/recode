package io.github.codeutilities.commands.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.util.StringUtil;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UuidCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgumentBuilders.literal("uuid")
                .then(ArgumentBuilders.argument("username", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            boolean copy = false;
                            if (ctx.getArgument("username", String.class).contains(" copy")) {
                                copy = true;
                            }
                            String username = ctx.getArgument("username", String.class).replace(" copy", "");
                            String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
                            try {
                                String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
                                JsonObject json = JsonParser.parseString(UUIDJson).getAsJsonObject();
                                String uuid = json.get("id").getAsString();
                                String fullUUID = StringUtil.fromTrimmed(uuid);
                                CodeUtilities.chat("§eUUID of §b" + username + "§e is §d" + fullUUID + "§e!");
                                if (copy) {
                                    CodeUtilities.chat("§aThe UUID has been copied to the clipboard!");
                                    mc.keyboard.setClipboard(fullUUID);
                                } else if (CodeUtilities.isOnDF()) {
                                    mc.player.sendChatMessage("/txt " + fullUUID);
                                }
                            } catch (IOException e) {
                                CodeUtilities.chat("§cUser §6" + username + "§c was not found.");
                                e.printStackTrace();
                            }

                            return 1;
                        })
                        .then(ArgumentBuilders.literal("copy"))
                )
        );
    }
}
