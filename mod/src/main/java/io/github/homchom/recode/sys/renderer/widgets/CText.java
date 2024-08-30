package io.github.homchom.recode.sys.renderer.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cottonmc.cotton.gui.widget.WText;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class CText extends WText {

    public CText(Component text) {
        super(text);
    }

    public CText(Component text, int color) {
        super(text, color);
    }

    @Override
    public void paint(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        x /= 2;
        y /= 2;
        PoseStack matrices = guiGraphics.pose();
        matrices.scale(2, 2, 0);
        super.paint(guiGraphics, x, y, mouseX, mouseY);
        matrices.scale(0.5f, 0.5f, 0);
    }
}
