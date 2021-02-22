package io.github.codeutilities.commands.util;

import com.google.gson.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.*;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UuidCommand extends Command {
    //todo: make this async
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("uuid")
                .then(ArgBuilder.argument("username", StringArgumentType.greedyString())
                        .executes(ctx -> {
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
                                
                                Text text = new LiteralText("UUID of ").styled(s -> s.withColor(Formatting.YELLOW))
                                        .append(new LiteralText(username).styled(s -> s.withColor(Formatting.BLUE)))
                                        .append(" is ")
                                        .append(new LiteralText(uuid).styled(s -> s.withColor(Formatting.LIGHT_PURPLE)))
                                        .append("!").styled(s ->
                                                s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy to clipboard.")
                                                        .styled(style -> style.withColor(Formatting.YELLOW))))
                                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, fullUUID)));
                                mc.player.sendMessage(text, false);
                                if(mc.player.isCreative()) {
                                    mc.player.sendChatMessage("/txt " + fullUUID);
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
