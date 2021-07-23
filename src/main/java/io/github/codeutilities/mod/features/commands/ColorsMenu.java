package io.github.codeutilities.mod.features.commands;

import io.github.codeutilities.sys.player.chat.color.ColorUtil;
import io.github.codeutilities.sys.player.chat.color.HSBColor;
import io.github.codeutilities.sys.renderer.IMenu;
import io.github.codeutilities.sys.renderer.widgets.*;
import io.github.codeutilities.sys.util.StringUtil;
import io.github.codeutilities.sys.player.chat.color.MinecraftColors;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;

public class ColorsMenu extends LightweightGuiDescription implements IMenu {

    public ArrayList<CColorPreset> recentColorWidgets = new ArrayList<>();
    public CColorPicker colorPicker;
    public WButton copyButton;
    public CText previewText;
    public CColoredRectangle previewTextRect;
    public WText recentText;
    public CTextField hexInput;
    public CTextField rInput;
    public CTextField gInput;
    public CTextField bInput;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void open(String... args) {
        try{
            WGridPanel root = new WGridPanel(1);
            setRootPanel(root);
            root.setSize(256, 240);

            // root.add(new CColoredRectangle(new Color(0, 0, 0, 100), new Color(255, 255, 255, 100)), 0, 80, 256, 120);
            colorPicker = new CColorPicker(1f, new HSBColor(0, 1, 1));
            root.add(colorPicker, 10, 90);

            copyButton = new WButton(new LiteralText("Copy")).setOnClick(() -> {
                Color color = colorPicker.getColor();
                StringUtil.copyToClipboard(MinecraftColors.hexToMc(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue())).replaceAll("§", "&"));
                ColorUtil.recentColors.remove(color);
                ColorUtil.recentColors.add(0, color);
                ColorUtil.recentColors = new ArrayList<>(ColorUtil.recentColors.subList(0, MathHelper.clamp(25, 0, ColorUtil.recentColors.size())));
                updateRecentColors(root);
            });
            root.add(copyButton, 30, 55, 196, 10);

            int textWidth = MinecraftClient.getInstance().textRenderer.getWidth("CodeUtilities!");
            previewTextRect = new CColoredRectangle(new Color(0, 0, 0, 100), new Color(255, 255, 255, 100));
            root.add(previewTextRect, 120-(textWidth), 15, (textWidth*2)+12, 26);
            previewText = new CText(new LiteralText("CodeUtilities!"), colorPicker.getColor().getRGB());
            root.add(previewText, 128-(textWidth), 20, textWidth, 8);

            updateRecentColors(root);
            if(ColorUtil.recentColors.size() > 0){
                String recentTextString = new TranslatableText("key.codeutilities.colors.recent_colors").parse(mc.player.getCommandSource(), mc.player, 1).getString();
                int recentTextWidth = MinecraftClient.getInstance().textRenderer.getWidth(recentTextString);
                recentText = new WText(new LiteralText(recentTextString));
                recentText.setColor(Color.black.getRGB(), Color.white.getRGB());
                root.add(recentText, 200-(recentTextWidth/2), 85, recentTextWidth, 8);
            }

            int hexTextWidth = MinecraftClient.getInstance().textRenderer.getWidth("HEX");
            WText hexText = new WText(new LiteralText("HEX"));
            hexText.setColor(Color.black.getRGB(), Color.white.getRGB());
            root.add(hexText, 60-(hexTextWidth/2), 200, hexTextWidth, 8);
            hexInput = new CTextField(new LiteralText("HEX")).setMaxLength(7);
            hexInput.setText("#FF0000");
            root.add(hexInput, 32, 212, 56, 10);

            int rgbTextWidth = MinecraftClient.getInstance().textRenderer.getWidth("RGB");
            hexText = new WText(new LiteralText("RGB"));
            hexText.setColor(Color.black.getRGB(), Color.white.getRGB());
            root.add(hexText, 183-(rgbTextWidth/2), 200, rgbTextWidth, 8);
            rInput = new CTextField(new LiteralText("Red")).setMaxLength(3);
            gInput = new CTextField(new LiteralText("Green")).setMaxLength(3);
            bInput = new CTextField(new LiteralText("Blue")).setMaxLength(3);
            rInput.setText("255");
            gInput.setText("0");
            bInput.setText("0");
            root.add(rInput, 120, 212, 42, 10);
            root.add(gInput, 162, 212, 42, 10);
            root.add(bInput, 204, 212, 42, 10);

            root.validate(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static ColorsMenu getGUI(){
        return (ColorsMenu) ((CottonClientScreen) MinecraftClient.getInstance().currentScreen).getDescription();
    }

    public void updateRecentColors(WGridPanel root){
        for(CColorPreset recent : recentColorWidgets){
            root.remove(recent);
        }
        recentColorWidgets = new ArrayList<>();
        int i = 0;
        for(Color color : ColorUtil.recentColors){
            CColorPreset savedColor = new CColorPreset(color, colorPicker);
            root.add(savedColor, (i%5)*20+155, (int) (Math.floor(i/5)*20+100), 10, 10);
            recentColorWidgets.add(savedColor);
            i++;
        }
    }
}