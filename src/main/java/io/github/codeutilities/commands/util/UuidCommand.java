package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UuidCommand {
    public static void run(CommandContext<CottonClientCommandSource> ctx) {
        String username = ctx.getArgument("username", String.class);
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        try {
            String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
            if(UUIDJson.isEmpty()) {
                CodeUtilities.chat("§cUnknown player!");
            }

            JSONObject json = new JSONObject(UUIDJson);
            String uuid = json.getString("id");
            CodeUtilities.chat("/txt " + fromTrimmed(uuid));
        } catch (IOException | JSONException e) {
            CodeUtilities.chat("§cUnknown Player!");
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
            )
        );
    }
}
