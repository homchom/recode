package io.github.codeutilities.sys.renderer.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.LibGuiConfig;
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
            color = LibGuiClient.config.darkMode ? 0xff393E46 : 0xffdddddd;
        }
        if (!isEnabled()) {
            color = LibGuiClient.config.darkMode ? 0xaa00ADB5 : 0xff00ADB5;
        }

        int tcolor = LibGuiClient.config.darkMode ? 0xaaaaaa : 0x222222;

        if (getLabel() != null) {

            ScreenDrawing.coloredRect(x,y+3,width,height, color);

            ScreenDrawing.drawString(matrices, getLabel().asOrderedText(), alignment, x,
                y + ((20 - 8) / 2), width, tcolor);
        }
    }

    @Override
    public void setSize(int x, int y) {
        width = x;
        height = y;
    }

}
