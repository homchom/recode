package io.github.codeutilities.commands.arguments;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;

//These argument classes will eventually become our own command dispatcher, removing the dependency for Cotton Client Commands.
public final class ArgBuilder {

    private ArgBuilder() {
    }

    public static LiteralArgumentBuilder<CottonClientCommandSource> literal(String name) {
        return LiteralArgument.literal(name);
    }

    public static <T> RequiredArgument<CottonClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgument.argument(name, type);
    }

    public static <T> Command<T> onExecute(Command<T> cmd) {
        return context -> {
            try {
                cmd.run(context);
                return 1;
            } catch (Throwable e) {
                e.printStackTrace();
                CodeUtilities.chat("An error occurred while executing this command.", ChatType.FAIL);
                return -1;
            }
        };
    }


}
