package io.github.codeutilities.util.render.gui.widgets;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CImage extends WWidget {
    private final Identifier identifier;

    public CImage(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        ScreenDrawing.texturedRect(x, y, getWidth(), getHeight(), identifier, 0, 0, 1, 1, 0xffffff, 1f);
    }
}
