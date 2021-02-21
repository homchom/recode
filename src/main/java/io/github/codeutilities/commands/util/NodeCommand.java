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

    private static final Map<String, String> nodeMap = new HashMap<>();

    static {
        nodeMap.put("1", "node1");
        nodeMap.put("2", "node2");
        nodeMap.put("3", "node3");
        nodeMap.put("4", "node4");
        nodeMap.put("5", "node5");
        nodeMap.put("beta", "beta");
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        LiteralArgumentBuilder<CottonClientCommandSource> cmd = ArgBuilder.literal("node");

        for (Map.Entry<String, String> node : nodeMap.entrySet()) {
            cmd.then(ArgBuilder.literal(node.getKey()).executes((context) -> {
                this.sendChatMessage(mc, "/server " + node.getValue());
                return 1;
            }));
        }

        cd.register(cmd);
    }
}
