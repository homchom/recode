package io.github.homchom.recode.mod.features.commands.recode;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.menu.ConfigScreen;
import io.github.homchom.recode.sys.renderer.IMenu;
import io.github.homchom.recode.sys.renderer.widgets.CImage;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class RecodeUI extends LightweightGuiDescription implements IMenu {
    private static final ResourceLocation CODEUTILS_LOGO = new ResourceLocation("recode:icon.png");

    @Override
    public void open(String... args) {
        WPlainPanel root = new WPlainPanel();
        root.setSize(220, 220);

        CImage cImage = new CImage(CODEUTILS_LOGO);
        cImage.setSize(128, 128);
        root.add(cImage, 46, -10);

        root.add(new WLabel(new TextComponent("recode")), (220 - Minecraft.getInstance().font.width("recode")) / 2, 110);
        root.add(new WLabel(new TextComponent("v" + Recode.getVersion())), (220 - Minecraft.getInstance().font.width("v" + Recode.getVersion())) / 2, 120);

        addButtons(root);
        setRootPanel(root);
    }

    private void addButtons(WPlainPanel panel) {
        // ------------------------ Features Button ------------------------
        WButton featuresButton = new WButton(new TextComponent("Help / Features"));
        featuresButton.setOnClick(() -> {
            FeaturesUI gui_1 = new FeaturesUI();
            gui_1.scheduleOpenGui(gui_1);
        });
        panel.add(featuresButton, 60, 148, 100, 20);

        // ------------------------ Contributors Button ------------------------
        /*WButton contributorsButton = new WButton(new TextComponent("Contributors"));
        contributorsButton.setOnClick(() -> {
            ContributorsUI gui_2 = new ContributorsUI();
            gui_2.scheduleOpenGui(gui_2);
        });
        panel.add(contributorsButton, 60, 170, 100, 20);*/

        // ------------------------ Bug Report Button ------------------------
        WButton bugReport = new WButton(new TextComponent("Report Issues"));
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
        WButton options = new WButton(new TextComponent("Options"));
        options.setOnClick(() -> {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(ConfigScreen.getScreen(Minecraft.getInstance().screen));
        });
        panel.add(options, 60, 192, 100, 20);
    }

}