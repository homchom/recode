package io.github.homchom.recode.sys.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;

import java.awt.*;

public class RenderUtil {
    public static void drawRect(GuiGraphics guiGraphics, int left, int top, int right, int bottom, Color color)
    {
        guiGraphics.fill(left, top, right, bottom, color.getRGB());
    }

    public static void drawGradientRect(GuiGraphics guiGraphics, int xStart, int yStart, int xEnd, int yEnd, Color colorStart, Color colorEnd, int zOffset) {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendEquation(7425);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        guiGraphics.fillGradient(xStart, yStart, xEnd, yEnd, zOffset, colorStart.getRGB(), colorEnd.getRGB());
        tessellator.end();
        RenderSystem.blendEquation(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    protected void drawGradientRect(GuiGraphics guiGraphics, BufferBuilder bufferBuilder, int xStart, int yStart, int xEnd, int yEnd, int z, int colorStart, int colorEnd) {
        guiGraphics.fillGradient(xStart, yStart, xEnd, yEnd, z, colorStart, colorEnd);
    }

    public static void drawBox(PoseStack matrixStack) {

    }
}