package io.github.codeutilities.gui;

import io.github.cottonmc.cotton.gui.widget.WItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;

import java.util.Collections;

public class CItem extends WItem {

    Runnable onclick;
    String hover = null;

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

    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        super.paint(matrices, x, y, mouseX, mouseY);
        if (mouseX >= 0 && mouseY >= 0 && mouseX < this.getWidth() && mouseY < this.getHeight()) {
            Screen screen = MinecraftClient.getInstance().currentScreen;
            ItemStack stack = getItems().get(0);

            if (hover == null) {
                screen.renderTooltip(matrices, screen.getTooltipFromItem(stack), mouseX + x, mouseY + y);
            } else {
                screen.renderTooltip(matrices, Collections.singletonList(new LiteralText(hover)), mouseX + x, mouseY + y);
            }
        }
    }
}
