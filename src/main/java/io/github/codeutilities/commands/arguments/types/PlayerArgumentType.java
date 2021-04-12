package io.github.codeutilities.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelectorReader;

public class PlayerArgumentType implements ArgumentType<String> {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public PlayerArgumentType() {
    }

    public static PlayerArgumentType player() {
        return new PlayerArgumentType();
    }

    public String parse(StringReader stringReader) {
        int i = stringReader.getCursor();

        while (stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }

        return stringReader.getString().substring(i, stringReader.getCursor());
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            StringReader stringReader = new StringReader(builder.getInput());
            stringReader.setCursor(builder.getStart());
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);

            return CommandSource.suggestMatching(((CommandSource) context.getSource()).getPlayerNames(), builder);
        } else {
            return Suggestions.empty();
        }
    }
}
