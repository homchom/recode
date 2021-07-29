package io.github.codeutilities.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import io.github.codeutilities.sys.util.ItemUtil;
import io.github.codeutilities.sys.util.TextUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

public class NewlineCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("newline")
            .executes(ctx -> {
                if (mc.player.isCreative()) {
                    ItemStack item = new ItemStack(Items.BOOK);

                    CompoundTag tag = new CompoundTag();

                    CompoundTag htags = new CompoundTag();
                    htags.putString("hypercube:varitem",
                        "{\"id\":\"txt\",\"data\":{\"name\":\"\\n\"}}");
                    tag.put("PublicBukkitValues", htags);
                    item.setTag(tag);
                    item.setCustomName(TextUtil.colorCodesToTextComponent("§5Newline Char"));

                    ItemUtil.giveCreativeItem(item, true);
                } else {
                    ChatUtil.sendMessage("You need to be in creative for this command to work!",
                        ChatType.FAIL);
                }
                return 1;
            })
        );
    }
}
