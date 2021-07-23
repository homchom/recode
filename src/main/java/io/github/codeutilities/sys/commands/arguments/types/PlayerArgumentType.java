package io.github.codeutilities.sys.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;

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
        if (context.getSource() instanceof CommandSource) {
            StringReader stringReader = new StringReader(builder.getInput());
            stringReader.setCursor(builder.getStart());
            return CommandSource.suggestMatching(((CommandSource) context.getSource()).getPlayerNames(), builder);
        } else {
            return Suggestions.empty();
        }
    }
}
