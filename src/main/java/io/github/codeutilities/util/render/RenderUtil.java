package io.github.codeutilities.util.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderUtil extends DrawableHelper {
    public static void drawRect(MatrixStack matrices, int left, int top, int right, int bottom, Color color)
    {
        DrawableHelper.fill(matrices, left, top, right, bottom, color.getRGB());
    }

    public static void drawGradientRect(MatrixStack matrices, int xStart, int yStart, int xEnd, int yEnd, Color colorStart, Color colorEnd, int zOffset) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        fillGradient(matrices.peek().getModel(), bufferBuilder, xStart, yStart, xEnd, yEnd, zOffset, colorStart.getRGB(), colorEnd.getRGB());
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    protected void drawGradientRect(Matrix4f matrix, BufferBuilder bufferBuilder, int xStart, int yStart, int xEnd, int yEnd, int z, int colorStart, int colorEnd) {
        DrawableHelper.fillGradient(matrix, bufferBuilder, xStart, yStart, xEnd, yEnd, z, colorStart, colorEnd);
    }
}