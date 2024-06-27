package io.github.homchom.recode.mod.commands.impl.item.template;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import io.github.homchom.recode.sys.networking.websocket.SocketHandler;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.sidedchat.ChatPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

public class TemplateDataCommand extends AbstractTemplateCommand {
    @Override
    public String getDescription() {
        return "[blue]/templatedata[reset]\n"
            + "\n"
            + "Prints the raw data of the code template in your main hand," +
            "allowing you to copy it";
    }

    @Override
    public String getName() {
        return "/templatedata";
    }

    @Override
    protected String getCmdName() {
        return "templatedata";
    }

    @Override
    protected void withTemplate(ItemStack stack) {
        JsonObject nbt = TemplateUtil.read(stack);
        String compressed = nbt.get("code").getAsString();
        String json = TemplateUtil.dataToJson(compressed).toString();

        MutableComponent component1 = Component.literal("Click to copy compressed code data: §8" + truncateString(compressed))
            .withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§7Click to copy!")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/copytxt " + compressed)));
        MutableComponent component2 = Component.literal("Click to copy raw JSON code data: §8" + truncateString(json))
            .withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§7Click to copy!")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/copytxt " + json)));

        ChatUtil.sendMessage(component1, ChatType.INFO_BLUE);
        ChatUtil.sendMessage(component2, ChatType.INFO_BLUE);
        Minecraft.getInstance().player.playSound(SoundEvents.ARMOR_EQUIP_ELYTRA);
    }

    private String truncateString(String input) {
        if (input.length() < 400) return input;
        return input.substring(0, 400) + "§7...";
    }
}
