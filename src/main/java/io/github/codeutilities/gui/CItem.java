package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.widget.WItem;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.*;

import java.util.*;

public class CItem extends WItem {

    Runnable onclick;
    String hover = null;

    public CItem() {
        super(ItemStack.EMPTY);
    }

    public CItem(ItemStack stack) {
        super(stack);
    }

    public void onClick(int x, int y, int button) {
        if (onclick != null) {
            onclick.run();
        }
    }

    public void setClickListener(Runnable r) {
        onclick = r;
    }

    @Override
    public void addTooltip(List<StringRenderable> tooltip) {
        MinecraftClient client = MinecraftClient.getInstance();
        tooltip.addAll(getItems().get(0).getTooltip(client.player, client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL));
    }
}
