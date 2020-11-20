package io.github.codeutilities.commands.item.template;

import com.google.gson.*;
import io.github.codeutilities.util.*;
import io.github.codeutilities.util.socket.SocketHandler;
import io.github.codeutilities.util.socket.client.SocketClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;

import java.io.*;

public class SendTemplateCommand extends AbstractTemplateCommand {

    @Override
    protected String getName() {
        return "sendtemplate";
    }

    @Override
    protected void withTemplate(ItemStack stack) {
        CompoundTag rawNBT = MinecraftClient.getInstance().player.getMainHandStack().getTag();
        JsonObject bukkitValues = new JsonParser().parse(rawNBT.get("PublicBukkitValues").toString()).getAsJsonObject();
        JsonObject templateData = new JsonParser().parse(bukkitValues.get("hypercube:codetemplatedata").getAsString().replace("\\", "")).getAsJsonObject();
        try {
            JsonObject toSend = new JsonObject();
            toSend.addProperty("received", templateData.toString());
            toSend.addProperty("type", "template");
            for (SocketClient client : SocketHandler.clients) {
                OutputStream stream = client.getSocket().getOutputStream();
                stream.write(toSend.toString().getBytes());
                stream.write('\n');
            }
        
            MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 200, 1);
        } catch (IOException exception) {
            ChatUtil.sendMessage("Failed to send data", ChatType.FAIL);
        }
    
        ChatUtil.sendMessage("Sent your current held item to any connected clients!", ChatType.INFO_BLUE);
    }
}

