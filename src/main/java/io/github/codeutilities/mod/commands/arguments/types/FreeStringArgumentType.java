package io.github.codeutilities.mod.commands.arguments.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.client.MinecraftClient;

public class FreeStringArgumentType implements ArgumentType<String> {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public FreeStringArgumentType() {
    }

    public static FreeStringArgumentType string() {
        return new FreeStringArgumentType();
    }

    public String parse(StringReader stringReader) {
        int i = stringReader.getCursor();

        while (stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }

        return stringReader.getString().substring(i, stringReader.getCursor());
    }
}
