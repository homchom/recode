package io.github.homchom.recode.mod.commands.impl.other;


import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

public class PingCommand extends Command {
    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("ping")
            .executes(ctx -> {
                ChatUtil.sendMessage("§aℹ §7Your ping: §6" + mc.getConnection().getPlayerInfo(mc.player.getUUID()).getLatency() + "ms");
                return 1;
            })
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/ping[reset]\n\nShows your current ping in milliseconds.";
    }

    @Override
    public String getName() {
        return "/ping";
    }
}