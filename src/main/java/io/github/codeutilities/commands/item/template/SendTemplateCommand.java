package io.github.codeutilities.commands.item.template;

import com.google.gson.*;
import io.github.codeutilities.template.MinecraftCommunicator;
import io.github.codeutilities.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;

import java.io.*;

public class SendTemplateCommand extends AbstractTemplateCommand {

    @Override
    protected String getName() {
        return "dfvisualsend";
    }

    @Override
    protected void withTemplate(ItemStack stack) {
        CompoundTag rawNBT = MinecraftClient.getInstance().player.getMainHandStack().getTag();
        JsonObject bukkitValues = new JsonParser().parse(rawNBT.get("PublicBukkitValues").toString()).getAsJsonObject();
        JsonObject templateData = new JsonParser().parse(bukkitValues.get("hypercube:codetemplatedata").getAsString().replace("\\", "")).getAsJsonObject();
        try {
            new PrintWriter(MinecraftCommunicator.socket.getOutputStream(), true).println(templateData.get("code").getAsString());
            MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 200, 1);
        } catch (IOException exception) {
            ChatUtil.sendMessage("Failed to communicate with DFVisual. Please ensure it is open.", ChatType.FAIL);
        }

        ChatUtil.sendMessage("Sent code template to DFVisual! Check it out.", ChatType.INFO_BLUE);

    }
}

