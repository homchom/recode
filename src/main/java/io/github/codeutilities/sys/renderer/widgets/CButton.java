package io.github.codeutilities.sys.renderer.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

public class CButton extends WButton {

    //paint method copied from wbutton and modified
    @Environment(EnvType.CLIENT)
    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        boolean hovered = (mouseX >= 0 && mouseY >= 0 && mouseX < getWidth()
            && mouseY < getHeight());
        int color = 0;
        if (hovered || isFocused()) {
            color = 0xaaffff00;
        }
        if (!isEnabled()) {
            color = 0xaa010101;
        }

        if (getLabel() != null) {

            ScreenDrawing.coloredRect(x,y,width,height, color);

            ScreenDrawing.drawStringWithShadow(matrices, getLabel().asOrderedText(), alignment, x,
                y + ((20 - 8) / 2), width, 0xaaaaaa);
        }
    }

    @Override
    public void setSize(int x, int y) {
        width = x;
        height = y;
    }

}
