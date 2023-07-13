package io.github.homchom.recode.sys.renderer.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cottonmc.cotton.gui.client.LibGui;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.homchom.recode.sys.renderer.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public class CColoredRectangle extends WWidget {

    private Color color;
    private Color darkmodeColor;

    public CColoredRectangle(Color color, Color darkmodecolor){
        this.color = color;
        this.darkmodeColor = darkmodecolor;
    }

    @Override
    public void paint(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        RenderUtil.drawRect(guiGraphics, x, y, x+this.width, y+this.height, LibGui.isDarkMode() ? this.darkmodeColor : this.color);
    }

    @Override
    public boolean canResize() {
        return true;
    }

    public void setColor(Color color, Color darkmodecolor){
        this.color = color;
        this.darkmodeColor = darkmodecolor;
    }
}
