package io.github.homchom.recode.mod.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

// These argument classes will eventually become our own command dispatcher, removing the dependency for Cotton Clients Commands.
public final class ArgBuilder {
    // TODO: nice exception handler - maybe custom client commands impl?

    private ArgBuilder() {
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<FabricClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
