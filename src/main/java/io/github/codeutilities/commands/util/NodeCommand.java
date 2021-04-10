package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;

public class NodeCommand extends Command {

    private static final Map<String, String> NODE_MAP = new HashMap<>();

    static {
        NODE_MAP.put("1", "node1");
        NODE_MAP.put("2", "node2");
        NODE_MAP.put("3", "node3");
        NODE_MAP.put("4", "node4");
        NODE_MAP.put("5", "node5");
        NODE_MAP.put("beta", "beta");
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        LiteralArgumentBuilder<CottonClientCommandSource> cmd = ArgBuilder.literal("node");

        for (Map.Entry<String, String> node : NODE_MAP.entrySet()) {
            cmd.then(ArgBuilder.literal(node.getKey()).executes((context) -> {
                this.sendChatMessage(mc, "/server " + node.getValue());
                return 1;
            }));
        }

        cd.register(cmd);
    }
}
