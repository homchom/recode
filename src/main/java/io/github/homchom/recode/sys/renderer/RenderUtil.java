package io.github.homchom.recode.sys.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiComponent;
import org.joml.Matrix4f;

import java.awt.*;

public class RenderUtil extends GuiComponent {
    public static void drawRect(PoseStack matrices, int left, int top, int right, int bottom, Color color)
    {
        GuiComponent.fill(matrices, left, top, right, bottom, color.getRGB());
    }

    public static void drawGradientRect(PoseStack matrices, int xStart, int yStart, int xEnd, int yEnd, Color colorStart, Color colorEnd, int zOffset) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendEquation(7425);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        fillGradient(matrices.last().pose(), bufferBuilder, xStart, yStart, xEnd, yEnd, zOffset, colorStart.getRGB(), colorEnd.getRGB());
        tessellator.end();
        RenderSystem.blendEquation(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
    }

    protected void drawGradientRect(Matrix4f matrix, BufferBuilder bufferBuilder, int xStart, int yStart, int xEnd, int yEnd, int z, int colorStart, int colorEnd) {
        GuiComponent.fillGradient(matrix, bufferBuilder, xStart, yStart, xEnd, yEnd, z, colorStart, colorEnd);
    }

    public static void drawBox(PoseStack matrixStack) {

    }
}