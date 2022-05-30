package io.github.homchom.recode.sys.renderer.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.homchom.recode.mod.features.commands.ColorsMenu;
import io.github.homchom.recode.sys.player.chat.color.*;
import io.github.homchom.recode.sys.renderer.RenderUtil;
import net.minecraft.util.Mth;

import java.awt.*;

public class CColorPicker extends WWidget {

    private HSBColor pickedColor;
    private boolean leftClickDown = false;
    private final float scale;
    private String lastHex = "#FF0000";
    private int lastR = 0;
    private int lastG = 0;
    private int lastB = 0;

    public CColorPicker(float scale, HSBColor defaultColor) {
        super();
        this.pickedColor = defaultColor;
        this.scale = scale;
    }

    @Override
    public void paint(PoseStack matrixStack, int x, int y, int mouseX, int mouseY) {
        float wheelwidth = 100f*scale;
        float wheelheight = 100f*scale;
        float hueSliderOffset = 110f*scale;
        float hueSliderWidth = 20f*scale;
        this.width = (int) (wheelwidth + hueSliderOffset + hueSliderWidth);
        this.height = (int) wheelheight;
        mouseX += x;
        mouseY += y;



        for (int i = 0; i <= wheelwidth; i++) {
            if (isLeftClickDown()) {
                if (mouseX >= x && mouseY >= y && mouseX <= x + wheelwidth && mouseY <= y + wheelheight) {
                    pickedColor.setSaturation((mouseX - x) / wheelwidth);
                    pickedColor.setBrightness(1f - ((mouseY - y) / wheelheight));
                }
            }
            Color color1 = Color.getHSBColor(pickedColor.getHue(), i / wheelwidth, 1f);
            Color color2 = Color.black;
            RenderUtil.drawGradientRect(matrixStack, x + i, y, x + i + 1, (int) (y + 1 + wheelheight), color1, color2, 0);
            Color crossColor = Color.getHSBColor(pickedColor.getHue(), 0f, 1 - pickedColor.getBrightness());
            matrixStack.translate(0, 0, 1);
            RenderUtil.drawRect(matrixStack, (int) (x + (pickedColor.getSaturation() * wheelheight) - 2), (int) (y + ((1 - pickedColor.getBrightness()) * wheelwidth)), (int) (x + (pickedColor.getSaturation() * wheelheight) + 3), (int) (y + ((1 - pickedColor.getBrightness()) * wheelwidth) + 1), crossColor);
            RenderUtil.drawRect(matrixStack, (int) (x + (pickedColor.getSaturation() * wheelheight)), (int) (y + ((1 - pickedColor.getBrightness()) * wheelwidth) - 2), (int) (x + (pickedColor.getSaturation() * wheelheight) + 1), (int) (y + ((1 - pickedColor.getBrightness()) * wheelwidth) + 3), crossColor);
            matrixStack.translate(0, 0, -1);
        }

        for (int i = 0; i <= wheelheight; i++) {
            if (isLeftClickDown()) {
                if (mouseX >= x + hueSliderOffset && mouseY >= y && mouseX <= x + hueSliderOffset + hueSliderWidth && mouseY <= y + wheelheight) {
                    pickedColor.setHue(Mth.clamp((mouseY - y) / wheelheight, 0f, 1f));
                }
            }
            RenderUtil.drawRect(matrixStack, (int) (x + hueSliderOffset), y + i, (int) (x + hueSliderOffset + hueSliderWidth), y + i+1, Color.getHSBColor(i / wheelheight, 1f, 1f));
            if (pickedColor.getHue() == i / wheelheight) {
                RenderUtil.drawRect(matrixStack, (int) (x + hueSliderOffset - 1), y + i, (int) (x + hueSliderOffset + hueSliderWidth + 1), y + i + 1, Color.WHITE);
            }
        }
    }

    @Override
    public InputResult onMouseDown(int x, int y, int button) {
        if (button == 0){
            leftClickDown = true;
        }
        return super.onMouseDown(x, y, button);
    }

    @Override
    public InputResult onMouseUp(int x, int y, int button) {
        if (button == 0){
            leftClickDown = false;
        }
        return super.onMouseUp(x, y, button);
    }

    @Override
    public void tick() {
        Color color = getColor();
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());

        try{
            Color decodedHex = Color.decode(ColorsMenu.getGUI().hexInput.getText());
            if (!ColorsMenu.getGUI().hexInput.isFocused() && !lastHex.equals(hex)){
                lastHex = hex;
                ColorsMenu.getGUI().hexInput.setText(hex);
            }else if (!lastHex.equals(ColorsMenu.getGUI().hexInput.getText())){
                setColor(decodedHex);
            }
            ColorsMenu.getGUI().hexInput.setDisabledColor((0xFF000000));
        }catch(Exception e){
            ColorsMenu.getGUI().hexInput.setDisabledColor((0xFF3d0000));
        }


        try{
            int r = Integer.parseInt(ColorsMenu.getGUI().rInput.getText());
            if (!ColorsMenu.getGUI().rInput.isFocused() && lastR != color.getRed()){
                lastR = color.getRed();
                ColorsMenu.getGUI().rInput.setText(""+color.getRed());
            }else if (lastR != r){
                setColor(new Color(r, color.getGreen(), color.getBlue()));
            }
            ColorsMenu.getGUI().rInput.setDisabledColor((0xFF000000));
        }catch(Exception e){
            ColorsMenu.getGUI().rInput.setDisabledColor(0xFF3d0000);
        }


        try{
            int g = Integer.parseInt(ColorsMenu.getGUI().gInput.getText());
            if (!ColorsMenu.getGUI().gInput.isFocused() && lastG != color.getGreen()){
                lastG = color.getGreen();
                ColorsMenu.getGUI().gInput.setText(""+color.getGreen());
            }else if (lastG != g){
                setColor(new Color(color.getRed(), g, color.getBlue()));
            }
            ColorsMenu.getGUI().gInput.setDisabledColor((0xFF000000));
        }catch(Exception e){
            ColorsMenu.getGUI().gInput.setDisabledColor((0xFF3d0000));
        }


        try{
            int b = Integer.parseInt(ColorsMenu.getGUI().bInput.getText());
            if (!ColorsMenu.getGUI().bInput.isFocused() && lastB != color.getBlue()){
                lastB = color.getBlue();
                ColorsMenu.getGUI().bInput.setText(""+color.getBlue());
            }else if (lastB != b){
                setColor(new Color(color.getRed(), color.getGreen(), b));
            }
            ColorsMenu.getGUI().bInput.setDisabledColor((0xFF000000));
        }catch(Exception e){
            ColorsMenu.getGUI().bInput.setDisabledColor((0xFF3d0000));
        }


        ColorsMenu.getGUI().previewText.setColor(color.getRGB(), color.getRGB());
        Color rectcolor = Color.getHSBColor(pickedColor.getHue(), 0f, 1 - pickedColor.getBrightness());
        ColorsMenu.getGUI().previewTextRect.setColor(new Color(rectcolor.getRed(), rectcolor.getGreen(), rectcolor.getBlue(), 100), new Color(rectcolor.getRed(), rectcolor.getGreen(), rectcolor.getBlue(), 100));

        super.tick();
    }

    private boolean isLeftClickDown(){
        return leftClickDown;
    }

    public Color getColor(){
        return Color.getHSBColor(pickedColor.getHue(), pickedColor.getSaturation(), pickedColor.getBrightness());
    }

    public void setColor(Color color){
        pickedColor = ColorUtil.toHSB(color);
    }
}