package io.github.homchom.recode.mod.commands.arguments;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

// These argument classes will eventually become our own command dispatcher, removing the dependency for Cotton Clients Commands.
public final class ArgBuilder {

    private ArgBuilder() {
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        return new LiteralArgumentBuilder<>(name) {
            @Override
            public LiteralArgumentBuilder<FabricClientCommandSource> executes(Command<FabricClientCommandSource> command) {
                return super.executes(onExecute(command));
            }
        };
    }

    public static <T> RequiredArgumentBuilder<FabricClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        return new RequiredArgumentBuilder<>(name, type) {
            @Override
            public RequiredArgumentBuilder<FabricClientCommandSource, T> executes(Command<FabricClientCommandSource> command) {
                return super.executes(onExecute(command));
            }
        };
    }

    public static <T> Command<T> onExecute(Command<T> cmd) {
        return context -> {
            try {
                cmd.run(context);
                return 1;
            } catch (Throwable e) {
                e.printStackTrace();
                ChatUtil.sendMessage("An error occurred while executing this command.", ChatType.FAIL);
                return -1;
            }
        };
    }

    // this is necessary so we can have a constructor with higher access because access wideners don't want to touch anything in mc dependencies
    // simply a copy of the original - might want to wrap this elsewhere (client command stuff?)
    public static class RequiredArgumentBuilder<S, T> extends ArgumentBuilder<S, RequiredArgumentBuilder<S, T>> {
        private final String name;
        private final ArgumentType<T> type;
        private SuggestionProvider<S> suggestionsProvider = null;

        private RequiredArgumentBuilder(final String name, final ArgumentType<T> type) {
            this.name = name;
            this.type = type;
        }

        public static <S, T> RequiredArgumentBuilder<S, T> argument(final String name, final ArgumentType<T> type) {
            return new RequiredArgumentBuilder<>(name, type);
        }

        public RequiredArgumentBuilder<S, T> suggests(final SuggestionProvider<S> provider) {
            this.suggestionsProvider = provider;
            return getThis();
        }

        public SuggestionProvider<S> getSuggestionsProvider() {
            return suggestionsProvider;
        }

        @Override
        protected RequiredArgumentBuilder<S, T> getThis() {
            return this;
        }

        public ArgumentType<T> getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public ArgumentCommandNode<S, T> build() {
            final ArgumentCommandNode<S, T> result = new ArgumentCommandNode<>(getName(), getType(), getCommand(), getRequirement(), getRedirect(), getRedirectModifier(), isFork(), getSuggestionsProvider());

            for (final CommandNode<S> argument : getArguments()) {
                result.addChild(argument);
            }

            return result;
        }
    }
}
