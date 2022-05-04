package io.github.homchom.recode.mod.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.concurrent.CompletableFuture;

public class PlayerArgumentType implements ArgumentType<String> {

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
        if (context.getSource() instanceof SharedSuggestionProvider) {
            StringReader stringReader = new StringReader(builder.getInput());
            stringReader.setCursor(builder.getStart());
            return SharedSuggestionProvider.suggest(((SharedSuggestionProvider) context.getSource()).getOnlinePlayerNames(), builder);
        } else {
            return Suggestions.empty();
        }
    }
}
