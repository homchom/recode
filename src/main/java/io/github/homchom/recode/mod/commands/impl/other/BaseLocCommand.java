package io.github.homchom.recode.mod.commands.impl.other;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.netty.handler.codec.base64.Base64Encoder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.CommandBuildContext;

import java.util.Base64;

public class BaseLocCommand extends Command {
    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("baseloc").executes(ctx -> {
            ItemStack heldItem = mc.player.getMainHandItem();
            CompoundTag tag = heldItem.getTagElement("PublicBukkitValues");

            if (tag != null) {
                if (tag.getString("hypercube:varitem") != "") {
                    JsonObject data = JsonParser.parseString(tag.getString("hypercube:varitem")).getAsJsonObject();
                    if (!data.get("id").getAsString().contains("loc")) {
                        ChatUtil.sendMessage("The item your holding is not a valid location variable.", ChatType.FAIL);
                    } else {
                        mc.keyboardHandler.setClipboard(Base64.getEncoder().encodeToString(data.get("data").getAsJsonObject().get("loc").toString().getBytes()));
                        ChatUtil.sendMessage("Successfully copied to clipboard", ChatType.SUCCESS);
                    }
                } else {
                    ChatUtil.sendMessage("The item your holding is not a valid location variable.", ChatType.FAIL);
                }
            } else {
                ChatUtil.sendMessage("The item your holding is not a valid location variable.", ChatType.FAIL);
            }


            return 1;
        }));
    }

    @Override
    public String getDescription() {
        return "[blue]/baseloc[reset]\n\nCopies a base64 string to the clipboard, used to pin-point locations, which is used on most DiamondFire compilers.";
    }

    @Override
    public String getName() {
        return "/baseloc";
    }
}
