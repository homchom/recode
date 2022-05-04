package io.github.homchom.recode.mod.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class StringFuncArgumentType implements ArgumentType<String> {

    Function<Void,List<String>> func;
    boolean greedy;

    public StringFuncArgumentType(Function<Void, List<String>> func, boolean greedy) {
        this.func = func;
        this.greedy = greedy;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
        SuggestionsBuilder builder) {

        List<String> suggestions = func.apply(null);

        if (context.getSource() instanceof SharedSuggestionProvider) {
            return SharedSuggestionProvider.suggest(suggestions, builder);
        } else {
            return Suggestions.empty();
        }
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();

        while (reader.canRead()) {
            if (this.greedy) reader.skip();
            else if (reader.peek() != ' ') reader.skip();
        }

        return reader.getString().substring(i, reader.getCursor());
    }
}
