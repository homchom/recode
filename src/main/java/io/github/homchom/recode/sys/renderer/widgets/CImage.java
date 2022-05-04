package io.github.homchom.recode.sys.renderer.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.resources.ResourceLocation;

public class CImage extends WWidget {
    private final ResourceLocation identifier;

    public CImage(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    @Override
    public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY) {
        ScreenDrawing.texturedRect(matrices, x, y, getWidth(), getHeight(), identifier, 0, 0, 1, 1, 0xffffff, 1f);
    }
}
