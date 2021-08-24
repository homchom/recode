package io.github.codeutilities.mod.commands.impl.ext;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class ImportMapCommand extends Command {
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("importmap")
                .then(ArgBuilder.literal("clipboard")
                        .executes(this::importClipboard))
                .then(ArgBuilder.argument("url", StringArgumentType.greedyString())));
    }

    public String getDescription() {
        return "Imports a map from the url on your clipboard.";
    }

    public String getName() {
        return "/importmap clipboard";
    }

    private int importClipboard(CommandContext<FabricClientCommandSource> ctx) {
        String url = CodeUtilities.MC.keyboard.getClipboard();
        if (url == null || url.isEmpty()) {
            ChatUtil.sendMessage("There is no text on your clipboard!", ChatType.FAIL);
            return 1;
        }
        ChatUtil.sendMessage("Loading image from URL: " + url, ChatType.INFO_BLUE);
        CodeUtilities.MC.player.sendChatMessage("/importmap " + url);
        return 1;
    }
}
