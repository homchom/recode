package io.github.codeutilities.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

public class CItem extends WItem {

    public CItem(ItemStack stack) {
        super(stack);
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
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen != null) {
            if (y > screen.height ||
                    x >  screen.width  ||
                    x < 0 || y < 0) {
                return;
            }
        }

        super.paint(matrices, x, y, mouseX, mouseY);
    }

}
