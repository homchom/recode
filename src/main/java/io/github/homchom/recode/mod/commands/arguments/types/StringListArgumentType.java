package io.github.homchom.recode.mod.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class StringListArgumentType implements ArgumentType<String> {
    public static final SimpleCommandExceptionType UNKNOWN_TYPE_EXCEPTION = new SimpleCommandExceptionType(new TextComponent("Unknown type"));
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
        if (context.getSource() instanceof SharedSuggestionProvider) {
            return SharedSuggestionProvider.suggest(this.suggestions, builder);
        } else {
            return Suggestions.empty();
        }
    }
}
