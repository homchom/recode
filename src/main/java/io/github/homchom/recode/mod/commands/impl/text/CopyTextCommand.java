package io.github.homchom.recode.mod.commands.impl.text;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

public class CopyTextCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("copytxt")
                .then(ArgBuilder.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            mc.keyboardHandler.setClipboard(ctx.getArgument("text", String.class));
                            ChatUtil.sendMessage("Copied text!", ChatType.INFO_BLUE);
                            return 1;
                        })
                )
        );
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
