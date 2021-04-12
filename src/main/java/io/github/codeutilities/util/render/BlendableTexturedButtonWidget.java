package io.github.codeutilities.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

public class BlendableTexturedButtonWidget extends TexturedButtonWidget {

    public BlendableTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, PressAction pressAction) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, 256, 256, pressAction);
    }

    public BlendableTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, PressAction pressAction) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, LiteralText.EMPTY);
    }

    public BlendableTexturedButtonWidget(int i, int j, int k, int l, int m, int n, int o, Identifier identifier, int p, int q, PressAction pressAction, Text text) {
        super(i, j, k, l, m, n, o, identifier, p, q, pressAction, EMPTY, text);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        super.renderButton(matrices, mouseX, mouseY, delta);
    }
}