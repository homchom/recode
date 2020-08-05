package io.github.codeutilities.gui;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.*;
import io.github.cottonmc.cotton.gui.widget.WItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.*;
import net.minecraft.text.LiteralText;

import java.util.Collections;

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
