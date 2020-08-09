package io.github.codeutilities.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.cotton.gui.widget.WItem;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringRenderable;
import org.lwjgl.opengl.GL11;

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
    public void addTooltip(List<StringRenderable> tooltip) {
        MinecraftClient client = MinecraftClient.getInstance();
        tooltip.addAll(getItems().get(0).getTooltip(client.player,
            client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED
                : TooltipContext.Default.NORMAL));
    }

    public void setScale(float scale) {
        this.scale = scale;
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

        GL11.glTranslatef(x,y,0);
        GL11.glScalef(scale, scale, 1);
        super.paint(matrices, 0, 0, mouseX, mouseY);
        GL11.glScalef(1/scale,1/scale, 1);
        GL11.glTranslatef(-x,-y,0);
    }
}
