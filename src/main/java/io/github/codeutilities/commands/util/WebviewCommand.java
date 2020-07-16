package io.github.codeutilities.commands.util;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.TemplateUtils;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;

public class WebviewCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgumentBuilders.literal("webview")
                .executes(ctx -> {
                    ItemStack item = mc.player.getMainHandStack();
                    try {
                        JsonObject template = TemplateUtils.fromItemStack(item);
                        LiteralText text = new LiteralText("§9§l! §bClick this message to view this code template in web!");
                        text.styled((style) -> style
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, String.format("https://derpystuff.gitlab.io/code/?template=%s", template.get("code").getAsString()))));
                        mc.player.sendMessage(text, false);
                    } catch (Exception e) {
                        CodeUtilities.chat("The item you are holding is not a code template!", ChatType.FAIL);
                    }
                    return 1;
                })
        );
    }
}
