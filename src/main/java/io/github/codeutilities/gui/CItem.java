package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.widget.WItem;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.StringRenderable;

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
        tooltip.addAll(getItems().get(0).getTooltip(client.player,
            client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED
                : TooltipContext.Default.NORMAL));
    }
}
