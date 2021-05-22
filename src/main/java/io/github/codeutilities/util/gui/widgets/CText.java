package io.github.codeutilities.util.gui.widgets;

import io.github.cottonmc.cotton.gui.widget.WText;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CText extends WText {

    public CText(Text text) {
        super(text);
    }

    public CText(Text text, int color) {
        super(text, color);
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        x /= 2;
        y /= 2;
        matrices.scale(2, 2, 0);
        super.paint(matrices, x, y, mouseX, mouseY);
        matrices.scale(0.5f, 0.5f, 0);
    }
}
