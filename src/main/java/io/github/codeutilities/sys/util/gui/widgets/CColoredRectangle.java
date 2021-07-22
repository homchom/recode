package io.github.codeutilities.sys.util.gui.widgets;

import io.github.codeutilities.sys.util.render.RenderUtil;
import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class CColoredRectangle extends WWidget {

    private Color color;
    private Color darkmodeColor;

    public CColoredRectangle(Color color, Color darkmodecolor){
        this.color = color;
        this.darkmodeColor = darkmodecolor;
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderUtil.drawRect(matrices, x, y, x+this.width, y+this.height, LibGuiClient.config.darkMode ? this.darkmodeColor : this.color);
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
