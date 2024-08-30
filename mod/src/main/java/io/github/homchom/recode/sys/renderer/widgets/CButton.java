package io.github.homchom.recode.sys.renderer.widgets;

import io.github.cottonmc.cotton.gui.client.LibGui;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;

public class CButton extends WButton {

    //paint method copied from wbutton and modified
    @Environment(EnvType.CLIENT)
    @Override
    public void paint(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        boolean hovered = (mouseX >= 0 && mouseY >= 0 && mouseX < getWidth()
            && mouseY < getHeight());
        int color = 0;
        if (hovered || isFocused()) {
            color = LibGui.isDarkMode() ? 0xff393E46 : 0xffdddddd;
        }
        if (!isEnabled()) {
            color = LibGui.isDarkMode() ? 0xaa00ADB5 : 0xff00ADB5;
        }

        int tcolor = LibGui.isDarkMode() ? 0xaaaaaa : 0x222222;

        if (getLabel() != null) {

            ScreenDrawing.coloredRect(guiGraphics, x,y+3, width, height, color);

            ScreenDrawing.drawString(guiGraphics, getLabel().getVisualOrderText(), alignment, x,
                y + ((20 - 8) / 2), width, tcolor);
        }
    }

    @Override
    public void setSize(int x, int y) {
        width = x;
        height = y;
    }

}
