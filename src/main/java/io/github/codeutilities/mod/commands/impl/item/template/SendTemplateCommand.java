package io.github.codeutilities.mod.commands.impl.item.template;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import io.github.codeutilities.sys.networking.websocket.SocketHandler;
import io.github.codeutilities.sys.networking.websocket.client.Client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;

import java.io.IOException;

public class SendTemplateCommand extends AbstractTemplateCommand {

    @Override
    protected String getName() {
        return "sendtemplate";
    }

    @Override
    protected void withTemplate(ItemStack stack) {
        CompoundTag rawNBT = MinecraftClient.getInstance().player.getMainHandStack().getTag();
        JsonObject bukkitValues = CodeUtilities.JSON_PARSER.parse(rawNBT.get("PublicBukkitValues").toString()).getAsJsonObject();
        JsonObject templateData = CodeUtilities.JSON_PARSER.parse(bukkitValues.get("hypercube:codetemplatedata").getAsString().replace("\\", "")).getAsJsonObject();
        try {
            JsonObject toSend = new JsonObject();
            toSend.addProperty("received", templateData.toString());
            toSend.addProperty("type", "template");
            for (Client client : SocketHandler.getInstance().getRegistered()) {
                client.sendData(toSend.toString());
            }

            CodeUtilities.MC.player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 200, 1);
        } catch (IOException exception) {
            ChatUtil.sendMessage("Failed to send data", ChatType.FAIL);
        }

        ChatUtil.sendMessage("Sent your current held item to any connected clients!", ChatType.INFO_BLUE);
    }
}

