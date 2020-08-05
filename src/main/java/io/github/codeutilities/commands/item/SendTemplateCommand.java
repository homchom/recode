package io.github.codeutilities.commands.item;

import com.google.gson.*;
import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.template.MinecraftCommunicator;
import io.github.cottonmc.clientcommands.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;

import java.io.PrintWriter;

public class SendTemplateCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("send").executes(
                source -> {
                    try {
                        if (!MinecraftClient.getInstance().player.getMainHandStack().isEmpty()) {
                            CompoundTag rawNBT = MinecraftClient.getInstance().player.getMainHandStack().getTag();

                            if (rawNBT.get("PublicBukkitValues") != null) {
                                JsonObject bukkitValues = (JsonObject) new JsonParser().parse(rawNBT.get("PublicBukkitValues").toString());
                                if (bukkitValues.get("hypercube:codetemplatedata") != null) {
                                    JsonObject templateData = (JsonObject) new JsonParser().parse(bukkitValues.get("hypercube:codetemplatedata").getAsString().replace("\\", ""));
                                    new PrintWriter(MinecraftCommunicator.socket.getOutputStream(), true).println(templateData.get("code").getAsString());
                                    MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 200, 1);
                                    CodeUtilities.chat("Sent code template to DFVisual! Check it out.");
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 1;
                }
        ));
    }
}
