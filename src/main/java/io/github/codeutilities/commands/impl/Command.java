package io.github.codeutilities.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public abstract class Command {

    public abstract void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd);

}
