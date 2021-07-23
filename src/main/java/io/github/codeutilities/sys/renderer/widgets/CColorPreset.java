package io.github.codeutilities.sys.renderer.widgets;

import io.github.codeutilities.sys.renderer.RenderUtil;
import io.github.cottonmc.cotton.gui.widget.WButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import java.awt.*;

public class CColorPreset extends WButton {

    private final Color color;
    private final CColorPicker picker;

    public CColorPreset(Color color, CColorPicker picker) {
        this.color = color;
        this.picker = picker;
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        RenderUtil.drawRect(matrices, x-1, y-1, x+11, y+11, Color.black);
        RenderUtil.drawRect(matrices, x, y, x+10, y+10, this.color);
    }

    public void onClick(int x, int y, int button) {
        if (isEnabled() && isWithinBounds(x, y)) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            this.picker.setColor(this.color);
        }
    }

    @Override
    public void setSize(int x, int y) {
        this.width = x;
        this.height = y;
    }
}
