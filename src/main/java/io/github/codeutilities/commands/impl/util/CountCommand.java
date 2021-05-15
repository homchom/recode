package io.github.codeutilities.commands.impl.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CountCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("count")
                .executes(ctx -> {
                            new Thread(() -> {
                                try {
                                    String sURL = "https://api.countapi.xyz/hit/CodeUtilitiesCounter";
                                    URL url = new URL(sURL);
                                    URLConnection request = url.openConnection();
                                    request.connect();

                                    JsonParser jp = new JsonParser();
                                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                                    JsonObject rootobj = root.getAsJsonObject();

                                    String count = rootobj.get("value").getAsString();
                                    ChatUtil.sendMessage("The CodeUtilities community has typed this command §b" + count + "§f times!", ChatType.SUCCESS);

                                } catch (IOException e) {
                                    CodeUtilities.log(Level.ERROR, String.valueOf(e));
                                }

                            }).start();
                            return 1;

                            })
        );
    }
}
