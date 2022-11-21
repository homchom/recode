package io.github.homchom.recode.sys.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BlendableTexturedButtonWidget extends ImageButton {

    public BlendableTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, ResourceLocation texture, OnPress pressAction) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, 256, 256, pressAction);
    }

    public BlendableTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, ResourceLocation texture, int textureWidth, int textureHeight, OnPress pressAction) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, Component.empty());
    }

    public BlendableTexturedButtonWidget(int i, int j, int k, int l, int m, int n, int o, ResourceLocation identifier, int p, int q, OnPress pressAction, Component text) {
        super(i, j, k, l, m, n, o, identifier, p, q, pressAction, NO_TOOLTIP, text);
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        super.renderButton(matrices, mouseX, mouseY, delta);
    }
}