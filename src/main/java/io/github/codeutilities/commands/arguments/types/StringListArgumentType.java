package io.github.codeutilities.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.omg.CORBA.UNKNOWN;

public class StringListArgumentType implements ArgumentType<String> {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private String[] suggestions;

    public static final SimpleCommandExceptionType UNKNOWN_TYPE_EXCEPTION = new SimpleCommandExceptionType(new LiteralText("Unknown type"));

    public StringListArgumentType(String[] suggestions) {
        this.suggestions = suggestions;
    }

    public static StringListArgumentType string(String[] suggestions) {
        return new StringListArgumentType(suggestions);
    }

    public String parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();

        while(stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }

        String string = stringReader.getString().substring(i, stringReader.getCursor());
        if (!Arrays.asList(this.suggestions).contains(string)) {
            Identifier identifier = Identifier.fromCommandInput(stringReader);

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
