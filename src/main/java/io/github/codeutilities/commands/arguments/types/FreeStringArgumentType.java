package io.github.codeutilities.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.text.TranslatableText;

public class FreeStringArgumentType implements ArgumentType<String> {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public FreeStringArgumentType() {
    }

    public static FreeStringArgumentType string() {
        return new FreeStringArgumentType();
    }

    public String parse(StringReader stringReader) {
        int i = stringReader.getCursor();

        while(stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }

        return stringReader.getString().substring(i, stringReader.getCursor());
    }
}
