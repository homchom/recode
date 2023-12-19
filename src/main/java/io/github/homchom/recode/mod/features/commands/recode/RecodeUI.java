package io.github.homchom.recode.mod.features.commands.recode;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.menu.ConfigScreen;
import io.github.homchom.recode.sys.renderer.IMenu;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RecodeUI extends LightweightGuiDescription implements IMenu {
    private static final ResourceLocation RECODE_ICON =
            new ResourceLocation("recode", "textures/gui/recode.png");

    @Override
    public void open(String... args) {
        WPlainPanel root = new WPlainPanel();
        root.setSize(220, 220);

        WSprite sprite = new WSprite(RECODE_ICON);
        sprite.setSize(120, 120);
        root.add(sprite, 8, 8);

        root.add(new WLabel(Component.literal("recode")), (220 - Minecraft.getInstance().font.width("recode")) / 2, 110);
        root.add(new WLabel(Component.literal("v" + Recode.INSTANCE.getVersion())), (220 - Minecraft.getInstance().font.width("v" + Recode.INSTANCE.getVersion())) / 2, 120);

        addButtons(root);
        setRootPanel(root);
    }

    private void addButtons(WPlainPanel panel) {
        // ------------------------ Features Button ------------------------
        WButton featuresButton = new WButton(Component.literal("Help / Features"));
        featuresButton.setOnClick(() -> {
            FeaturesUI gui_1 = new FeaturesUI();
            gui_1.scheduleOpenGui(gui_1);
        });
        panel.add(featuresButton, 60, 148, 100, 20);


        // ------------------------ Bug Report Button ------------------------
        WButton bugReport = new WButton(Component.literal("Report Issues"));
        bugReport.setOnClick(() -> {
            String link = "https://github.com/homchom/recode/issues";

            ConfirmLinkScreen gui_3 = new ConfirmLinkScreen((bool) -> {
                if (bool) {
                    Util.getPlatform().openUri(link);
                }
                RecodeUI gui = new RecodeUI();
                gui.scheduleOpenGui(gui);
            }, link, false);
            Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(gui_3));
        });
        panel.add(bugReport, 60, 170, 100, 20);

        // ------------------------ Options Button ------------------------
        WButton options = new WButton(Component.literal("Options"));
        options.setOnClick(() -> {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(ConfigScreen.getScreen(Minecraft.getInstance().screen));
        });
        panel.add(options, 60, 192, 100, 20);
    }

}