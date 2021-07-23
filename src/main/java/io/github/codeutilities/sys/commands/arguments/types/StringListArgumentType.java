package io.github.codeutilities.sys.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class StringListArgumentType implements ArgumentType<String> {
    public static final SimpleCommandExceptionType UNKNOWN_TYPE_EXCEPTION = new SimpleCommandExceptionType(new LiteralText("Unknown type"));
    private final String[] suggestions;

    public StringListArgumentType(String[] suggestions) {
        this.suggestions = suggestions;
    }

    public static StringListArgumentType string(String[] suggestions) {
        return new StringListArgumentType(suggestions);
    }

    public String parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();

        while (stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }

        String string = stringReader.getString().substring(i, stringReader.getCursor());
        if (!Arrays.asList(this.suggestions).contains(string)) {
            throw UNKNOWN_TYPE_EXCEPTION.createWithContext(stringReader);
        }

        return string;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSource) {
            return CommandSource.suggestMatching(this.suggestions, builder);
        } else {
            return Suggestions.empty();
        }
    }
}
