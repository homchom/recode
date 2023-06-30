package io.github.homchom.recode.mod.features.commands;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.homchom.recode.sys.player.chat.color.ColorUtil;
import io.github.homchom.recode.sys.player.chat.color.HSBColor;
import io.github.homchom.recode.sys.player.chat.color.MinecraftColors;
import io.github.homchom.recode.sys.renderer.IMenu;
import io.github.homchom.recode.sys.renderer.widgets.CColorPicker;
import io.github.homchom.recode.sys.renderer.widgets.CColorPreset;
import io.github.homchom.recode.sys.renderer.widgets.CColoredRectangle;
import io.github.homchom.recode.sys.renderer.widgets.CText;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.ArrayList;

public class ColorsMenu extends LightweightGuiDescription implements IMenu {

    public ArrayList<CColorPreset> recentColorWidgets = new ArrayList<>();
    public CColorPicker colorPicker;
    public WButton copyButton;
    public CText previewText;
    public CColoredRectangle previewTextRect;
    public WText recentText;
    public WTextField hexInput;
    public WTextField rInput;
    public WTextField gInput;
    public WTextField bInput;
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void open(String... args) {
        try{
            WGridPanel root = new WGridPanel(1);
            setRootPanel(root);
            root.setSize(256, 240);

            colorPicker = new CColorPicker(1f, new HSBColor(0, 1, 1));
            root.add(colorPicker, 10, 90);

            copyButton = new WButton(Component.literal("Copy")).setOnClick(() -> {
                Color color = colorPicker.getColor();
                var formatted = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                var copy = MinecraftColors.hexToMc(formatted).replaceAll("§", "&");
                Minecraft.getInstance().keyboardHandler.setClipboard(copy);

                ColorUtil.recentColors.remove(color);
                ColorUtil.recentColors.add(0, color);
                ColorUtil.recentColors = new ArrayList<>(ColorUtil.recentColors.subList(0, Mth.clamp(25, 0, ColorUtil.recentColors.size())));
                updateRecentColors(root);
            });
            root.add(copyButton, 30, 55, 196, 10);

            int textWidth = Minecraft.getInstance().font.width("Recode!");
            previewTextRect = new CColoredRectangle(new Color(0, 0, 0, 100), new Color(255, 255, 255, 100));
            root.add(previewTextRect, 120-(textWidth), 15, (textWidth*2)+12, 26);
            previewText = new CText(Component.literal("Recode!"), colorPicker.getColor().getRGB());
            root.add(previewText, 128-(textWidth), 20, textWidth, 8);

            updateRecentColors(root);
            if (ColorUtil.recentColors.size() > 0){
                String recentTextString = new TranslatableContents("key.recode.colors.recent_colors", null, TranslatableContents.NO_ARGS)
                        .resolve(mc.player.createCommandSourceStack(), mc.player, 1).getString();
                int recentTextWidth = Minecraft.getInstance().font.width(recentTextString);
                recentText = new WText(Component.literal(recentTextString));
                recentText.setColor(Color.black.getRGB(), Color.white.getRGB());
                root.add(recentText, 200-(recentTextWidth/2), 85, recentTextWidth, 8);
            }

            int hexTextWidth = Minecraft.getInstance().font.width("HEX");
            WText hexText = new WText(Component.literal("HEX"));
            hexText.setColor(Color.black.getRGB(), Color.white.getRGB());
            root.add(hexText, 60-(hexTextWidth/2), 200, hexTextWidth, 8);
            hexInput = new WTextField(Component.literal("HEX")).setMaxLength(7);
            hexInput.setText("#FF0000");
            root.add(hexInput, 32, 212, 56, 10);

            int rgbTextWidth = Minecraft.getInstance().font.width("RGB");
            hexText = new WText(Component.literal("RGB"));
            hexText.setColor(Color.black.getRGB(), Color.white.getRGB());
            root.add(hexText, 183-(rgbTextWidth/2), 200, rgbTextWidth, 8);
            rInput = new WTextField(Component.literal("Red")).setMaxLength(3);
            gInput = new WTextField(Component.literal("Green")).setMaxLength(3);
            bInput = new WTextField(Component.literal("Blue")).setMaxLength(3);
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
        return (ColorsMenu) ((CottonClientScreen) Minecraft.getInstance().screen).getDescription();
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