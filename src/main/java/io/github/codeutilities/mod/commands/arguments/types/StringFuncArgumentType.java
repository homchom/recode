package io.github.codeutilities.mod.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;

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

        if (context.getSource() instanceof CommandSource) {
            return CommandSource.suggestMatching(suggestions, builder);
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
