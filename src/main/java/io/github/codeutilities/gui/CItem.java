package io.github.codeutilities.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
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
    public void addTooltip(TooltipBuilder tooltip) {
        MinecraftClient client = MinecraftClient.getInstance();
        for (Text text : getItems().get(0).getTooltip(client.player, client.options.advancedItemTooltips ?
                TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL)) {
            tooltip.add(text);
        }
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        MinecraftClient mc = CodeUtilities.mc;
//        if (x > mc.getWindow().getHeight() ||
//            y > mc.getWindow().getWidth() ||
//            x < 0 || y < 0) {
//            return;
//        }

//        RenderSystem.enableDepthTest();
//        ItemRenderer renderer = mc.getItemRenderer();
//        renderer.zOffset = 100.0F;
//        renderer.renderInGui(getItems().get(0), x + this.getWidth() / 2 - 9, y + this.getHeight() / 2 - 9);
//        renderer.zOffset = 0.0F;
        super.paint(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public int getWidth() {
        return (int) (super.getWidth() * scale);
    }

    @Override
    public int getHeight() {
        return (int) (super.getHeight() * scale);
    }
}
