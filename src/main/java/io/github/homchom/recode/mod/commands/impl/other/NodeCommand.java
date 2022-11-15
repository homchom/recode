package io.github.homchom.recode.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

import java.util.*;

public class NodeCommand extends Command {

    private static final Map<String, String> NODE_MAP = new HashMap<>();

    static {
        NODE_MAP.put("1", "node1");
        NODE_MAP.put("2", "node2");
        NODE_MAP.put("3", "node3");
        NODE_MAP.put("4", "node4");
        NODE_MAP.put("5", "node5");
        NODE_MAP.put("6", "node6");
        NODE_MAP.put("7", "node7");
        NODE_MAP.put("beta", "beta");
    }

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        LiteralArgumentBuilder<FabricClientCommandSource> cmd = ArgBuilder.literal("node");

        for (Map.Entry<String, String> node : NODE_MAP.entrySet()) {
            cmd.then(ArgBuilder.literal(node.getKey()).executes((ctx) -> {
                this.sendCommand(mc, "server " + node.getValue());
                return 1;
            }));
        }

        cd.register(cmd);
    }

    @Override
    public String getDescription() {
        return "[blue]/node <1-7|beta>[reset]\n"
                + "\n"
                + "Joins the specified node.";
    }

    @Override
    public String getName() {
        return "/node";
    }
}
