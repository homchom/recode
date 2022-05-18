package io.github.homchom.recode.mod.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.concurrent.CompletableFuture;

public class PlayerArgumentType implements ArgumentType<String> {

    private PlayerArgumentType() {
    }

    public static PlayerArgumentType player() {
        return new PlayerArgumentType();
    }

    public String parse(StringReader stringReader) {
        return stringReader.readUnquotedString();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(((SharedSuggestionProvider) context.getSource()).getOnlinePlayerNames(), builder);
    }
}
