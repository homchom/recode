package io.github.homchom.recode.sys.renderer.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.homchom.recode.sys.renderer.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.awt.*;

public class CColorPreset extends WButton {

    private final Color color;
    private final CColorPicker picker;

    public CColorPreset(Color color, CColorPicker picker) {
        this.color = color;
        this.picker = picker;
    }

    @Override
    public void paint(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        RenderUtil.drawRect(guiGraphics, x-1, y-1, x+11, y+11, Color.black);
        RenderUtil.drawRect(guiGraphics, x, y, x+10, y+10, this.color);
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        if (isEnabled() && isWithinBounds(x, y)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            this.picker.setColor(this.color);
        }
        return InputResult.IGNORED;
    }

    @Override
    public void setSize(int x, int y) {
        this.width = x;
        this.height = y;
    }
}
