package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.TemplateUtils;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import org.json.JSONObject;

public class WebviewCommand {

    static MinecraftClient mc = MinecraftClient.getInstance();

    public static void run() {
            ItemStack item = mc.player.getMainHandStack();
            if (item.getItem() != Items.AIR) {
                try {
                    JSONObject template = new JSONObject();
                    LiteralText text = new LiteralText(
                        "§9§l! §bClick this message to view this code template in web!");
                    text.styled((style) -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.values()[0],
                            String.format("https://derpystuff.gitlab.io/code/?template=%s",
                                    template.getString("code")))));
                    mc.player.sendMessage(text, false);
                } catch (NullPointerException e) {
                    CodeUtilities
                        .chat("The item you are holding is not a code template!", ChatType.FAIL);
                }
            } else {
                CodeUtilities.chat("You have to hold an item in your hand!", ChatType.FAIL);
            }
    }

    public static void register(CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgumentBuilders.literal("webview")
            .executes(ctx -> {
                try {
                    run();
                    return 1;
                } catch (Exception err) {
                    CodeUtilities.chat("Error while executing command.", ChatType.FAIL);
                    err.printStackTrace();
                    return -1;
                }
            })
        );
    }
}
