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

public class ChoiceArgumentType implements ArgumentType<String> {
    public static final SimpleCommandExceptionType UNKNOWN_TYPE_EXCEPTION = new SimpleCommandExceptionType(new TextComponent("Unknown type"));
    private final String[] suggestions;

    private ChoiceArgumentType(String[] suggestions) {
        this.suggestions = suggestions;
    }

    public static ChoiceArgumentType choice(String[] suggestions) {
        return new ChoiceArgumentType(suggestions);
    }

    public String parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();

        if (!Arrays.asList(this.suggestions).contains(string)) {
            throw UNKNOWN_TYPE_EXCEPTION.createWithContext(stringReader);
        }

        return string;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(this.suggestions, builder);
    }
}
