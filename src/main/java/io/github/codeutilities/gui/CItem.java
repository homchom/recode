package io.github.codeutilities.gui;

import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class CItem extends WItem {

    Runnable onclick;
    float scale = 1;

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
    public void addTooltip(TooltipBuilder tooltip) {
        MinecraftClient client = MinecraftClient.getInstance();
        for (Text text : getItems().get(0).getTooltip(client.player, client.options.advancedItemTooltips ?
                TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL)) {
            tooltip.add(text);
        }
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        MinecraftClient mc = CodeUtilities.mc;
        if (x > mc.getWindow().getHeight() ||
            y > mc.getWindow().getWidth() ||
            x < 0 || y < 0) {
            return;
        }
        if (mc.currentScreen != null) {
            if (x > mc.getWindow().getWidth() / 4 + mc.currentScreen.width / 2 ||
                    x < mc.getWindow().getWidth() / 4 - mc.currentScreen.width / 2 ||
                    y > mc.getWindow().getHeight() / 4 + mc.currentScreen.height / 2 ||
                    y < mc.getWindow().getHeight() / 4 - mc.currentScreen.height / 2) {
                return;
            }
        }

        super.paint(matrices, x, y, mouseX, mouseY);
    }

}
