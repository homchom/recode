package io.github.codeutilities.gui;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ItemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ClickableGiveItem extends CItem {

    public ClickableGiveItem(ItemStack stack) {
        super(stack);
    }

    @Override
    public void onClick(int x, int y, int button) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player.isCreative()) {
            ItemUtil.giveCreativeItem(getItems().get(0));
            mc.player
                .playSound(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 2,
                    1);
        } else {
            CodeUtilities
                .chat("You need to be in creative to get heads.", ChatType.FAIL);
        }
    }
}
