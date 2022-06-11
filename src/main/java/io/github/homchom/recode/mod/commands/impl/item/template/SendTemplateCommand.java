package io.github.homchom.recode.mod.commands.impl.item.template;

import com.google.gson.*;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.sys.networking.websocket.SocketHandler;
import io.github.homchom.recode.sys.player.chat.*;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class SendTemplateCommand extends AbstractTemplateCommand {

    @Override
    public String getDescription() {
        return "[blue]/sendtemplate[reset]\n"
            + "\n"
            + "Sends the code template in your main hand to external programs like DFVisual";
    }

    @Override
    public String getName() {
        return "/sendtemplate";
    }

    @Override
    protected String getCmdName() {
        return "sendtemplate";
    }

    @Override
    protected void withTemplate(ItemStack stack) {
        CompoundTag rawNBT = Minecraft.getInstance().player.getMainHandItem().getTag();
        JsonObject bukkitValues = JsonParser.parseString(rawNBT.get("PublicBukkitValues").toString()).getAsJsonObject();
        JsonObject templateData = JsonParser.parseString(bukkitValues.get("hypercube:codetemplatedata").getAsString().replace("\\", "")).getAsJsonObject();
        JsonObject toSend = new JsonObject();
        toSend.addProperty("received", templateData.toString());
        toSend.addProperty("type", "template");
        SocketHandler.getInstance().sendData(toSend.toString());

        LegacyRecode.MC.player.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 200, 1);
        ChatUtil.sendMessage("Sent your current held item to any connected clients!", ChatType.INFO_BLUE);
    }
}

