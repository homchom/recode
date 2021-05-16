package io.github.codeutilities.commands.impl.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.networking.WebUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;

public class CountCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("count")
                .executes(ctx -> {
                            new Thread(() -> {
                                String jsonObject = null;
                                try {
                                    jsonObject = WebUtil.getString("https://api.countapi.xyz/hit/CodeUtilitiesCounter");
                                    String count = CodeUtilities.JSON_PARSER.parse(jsonObject).getAsJsonObject().get("value").getAsString();
                                    ChatUtil.sendMessage("The CodeUtilities community has typed this command §b" + count + "§f times!", ChatType.SUCCESS);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }).start();
                            return 1;

                            })
        );
    }
}
