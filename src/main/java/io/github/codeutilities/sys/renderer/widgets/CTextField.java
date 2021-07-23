package io.github.codeutilities.sys.renderer.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

public class CTextField extends WTextField {

    private TextRenderer font;

    public CTextField(Text suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public void onKeyPressed(int ch, int key, int modifiers) {
        super.onKeyPressed(ch, key, modifiers);
        if (onChanged != null) {
            onChanged.accept(text);
        }
    }

    @Override
    public void onCharTyped(char ch) {
        super.onCharTyped(ch);
        if (onChanged != null) {
            onChanged.accept(text);
        }
    }

    public CTextField setMaxLength(int max) {
        this.maxLength = max;
        if (this.text.length() > max) {
            this.text = this.text.substring(0, max);
            this.onChanged.accept(this.text);
        }
        return this;
    }

    @Override
    protected void renderTextField(MatrixStack matrices, int x, int y) {
        if (this.font==null) this.font = MinecraftClient.getInstance().textRenderer;

        int borderColor = (this.isFocused()) ? 0xFF_FFFFA0 : 0xFF_A0A0A0;
        ScreenDrawing.coloredRect(x-1, y-1, width+2, height+2, borderColor);
        ScreenDrawing.coloredRect(x, y, width, height, uneditableColor);


        int textColor = this.editable ? this.enabledColor : this.uneditableColor;

        String trimText = font.trimToWidth(this.text, this.width-OFFSET_X_TEXT);

        boolean selection = (select!=-1);
        boolean focused = this.isFocused(); //this.isFocused() && this.focusedTicks / 6 % 2 == 0 && boolean_1; //Blinks the cursor

        //int textWidth = font.getStringWidth(trimText);
        //int textAnchor = (font.isRightToLeft()) ?
        //		x + OFFSET_X_TEXT + textWidth :
        //		x + OFFSET_X_TEXT;

        int textX = x + OFFSET_X_TEXT;
        //(font.isRightToLeft()) ?
        //textAnchor - textWidth :
        //textAnchor;

        int textY = y + (height - 8) / 2;

        int adjustedCursor = this.cursor;
        if (adjustedCursor > trimText.length()) {
            adjustedCursor = trimText.length();
        }

        int preCursorAdvance = textX;
        if (!trimText.isEmpty()) {
            String string_2 = trimText.substring(0,adjustedCursor);
            preCursorAdvance = font.drawWithShadow(matrices, string_2, textX, textY, textColor);
        }

        if (adjustedCursor<trimText.length()) {
            font.drawWithShadow(matrices, trimText.substring(adjustedCursor), preCursorAdvance-1, (float)textY, textColor);
        }


        if (text.length()==0 && this.suggestion != null) {
            font.drawWithShadow(matrices, this.suggestion, textX, textY, 0xFF808080);
        }

        //int var10002;
        //int var10003;
        if (focused && !selection) {
            if (adjustedCursor<trimText.length()) {
                //int caretLoc = WTextField.getCaretOffset(text, cursor);
                //if (caretLoc<0) {
                //	caretLoc = textX+MinecraftClient.getInstance().textRenderer.getStringWidth(trimText)-caretLoc;
                //} else {
                //	caretLoc = textX+caretLoc-1;
                //}
                ScreenDrawing.coloredRect(preCursorAdvance-1, textY-2, 1, 12, 0xFFD0D0D0);
                //if (boolean_3) {
                //	int var10001 = int_7 - 1;
                //	var10002 = int_9 + 1;
                //	var10003 = int_7 + 1;
                //
                //	DrawableHelper.fill(int_9, var10001, var10002, var10003 + 9, -3092272);

            } else {
                font.drawWithShadow(matrices, "_", preCursorAdvance, textY, textColor);
            }
        }

        if (selection) {
            int a = WTextField.getCaretOffset(text, cursor);
            int b = WTextField.getCaretOffset(text, select);
            if (b<a) {
                int tmp = b;
                b = a;
                a = tmp;
            }
            invertedRect(textX+a-1, textY-1, Math.min(b-a, width - OFFSET_X_TEXT), 12);
            //	int int_10 = int_6 + MinecraftClient.getInstance().textRenderer.getStringWidth(trimText.substring(0, adjustedCursor));
            //	var10002 = int_7 - 1;
            //	var10003 = int_10 - 1;
            //	int var10004 = int_7 + 1;
            //	//this.method_1886(int_9, var10002, var10003, var10004 + 9);
        }
    }

    private void invertedRect(int x, int y, int width, int height) {
        Tessellator tessellator_1 = Tessellator.getInstance();
        BufferBuilder bufferBuilder_1 = tessellator_1.getBuffer();
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder_1.begin(GL11.GL_QUADS, VertexFormats.POSITION);
        bufferBuilder_1.vertex(x,       y+height, 0.0D).next();
        bufferBuilder_1.vertex(x+width, y+height, 0.0D).next();
        bufferBuilder_1.vertex(x+width, y,        0.0D).next();
        bufferBuilder_1.vertex(x,       y,        0.0D).next();
        tessellator_1.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }
}
