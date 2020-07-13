package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UuidCommand {

    static MinecraftClient mc = MinecraftClient.getInstance();

    public static void run(CommandContext<CottonClientCommandSource> ctx) {
        boolean copy = false;
        if (ctx.getArgument("username", String.class).contains(" copy")) {
            copy = true;
        }
        String username = ctx.getArgument("username", String.class).replace(" copy","");
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        try {
            String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(UUIDJson);
            String uuid = json.getString("id");
            CodeUtilities.chat("§eUUID of §b" + username + "§e is §d" + fromTrimmed(uuid) + "§e!");
            if (copy) {
                CodeUtilities.chat("§aThe UUID has been copied to the clipboard!");
                mc.keyboard.setClipboard(fromTrimmed(uuid));
            }
            else mc.player.sendChatMessage("/txt " + fromTrimmed(uuid));
        } catch (IOException | JSONException e) {
            CodeUtilities.chat("§cUser §6" + username + "§c was not found.");
            e.printStackTrace();
        }
    }

    public static String fromTrimmed(String trimmedUUID) throws IllegalArgumentException{
        if(trimmedUUID == null) throw new IllegalArgumentException();
        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e){
            throw new IllegalArgumentException();
        }
        return builder.toString();
    }

    public static void register(CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgumentBuilders.literal("uuid")
            .then(ArgumentBuilders.argument("username", StringArgumentType.greedyString())
                .executes(ctx -> {
                    try {
                        run(ctx);
                        return 1;
                    }catch (Exception err) {
                        CodeUtilities.chat("§cError while executing command.");
                        return -1;
                    }
                })
                .then(ArgumentBuilders.literal("copy"))
            )
        );
    }
}
