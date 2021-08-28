package io.github.codeutilities.mod.features.commands.codeutilities;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.menu.ConfigScreen;
import io.github.codeutilities.sys.renderer.IMenu;
import io.github.codeutilities.sys.renderer.widgets.CImage;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class CodeUtilitiesUI extends LightweightGuiDescription implements IMenu {
    private static final Identifier CODEUTILS_LOGO = new Identifier("codeutilities:icon.png");

    @Override
    public void open(String... args) {
        WPlainPanel root = new WPlainPanel();
        root.setSize(220, 220);

        CImage cImage = new CImage(CODEUTILS_LOGO);
        cImage.setSize(128, 128);
        root.add(cImage, 46, -10);

        root.add(new WLabel(new LiteralText("CodeUtilities")), (220 - MinecraftClient.getInstance().textRenderer.getWidth("CodeUtilities")) / 2, 110);
        root.add(new WLabel(new LiteralText("v" + CodeUtilities.MOD_VERSION)), (220 - MinecraftClient.getInstance().textRenderer.getWidth("v" + CodeUtilities.MOD_VERSION)) / 2, 120);

        addButtons(root);
        setRootPanel(root);
    }

    private void addButtons(WPlainPanel panel) {
        // ------------------------ Features Button ------------------------
        WButton featuresButton = new WButton(new LiteralText("Help / Features"));
        featuresButton.setOnClick(() -> {
            FeaturesUI gui_1 = new FeaturesUI();
            gui_1.scheduleOpenGui(gui_1);
        });
        panel.add(featuresButton, 60, 148, 100, 20);

        // ------------------------ Contributors Button ------------------------
        WButton contributorsButton = new WButton(new LiteralText("Contributors"));
        contributorsButton.setOnClick(() -> {
            ContributorsUI gui_2 = new ContributorsUI();
            gui_2.scheduleOpenGui(gui_2);
        });
        panel.add(contributorsButton, 60, 170, 100, 20);

        // ------------------------ Bug Report Button ------------------------
        WButton bugReport = new WButton(new LiteralText("Bug Report"));
        bugReport.setOnClick(() -> {
            String link = "https://github.com/CodeUtilities/CodeUtilities/issues";

            ConfirmChatLinkScreen gui_3 = new ConfirmChatLinkScreen((bool) -> {
                if (bool) {
                    Util.getOperatingSystem().open(link);
                }
                CodeUtilitiesUI gui = new CodeUtilitiesUI();
                gui.scheduleOpenGui(gui);
            }, link, false);
            MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().openScreen(gui_3));
        });
        panel.add(bugReport, 60, 192, 100, 20);

        // ------------------------ Options Button ------------------------
        WButton options = new WButton(new LiteralText("Options"));
        options.setOnClick(() -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.openScreen(ConfigScreen.getScreen(MinecraftClient.getInstance().currentScreen));
        });
        panel.add(options, 60, 214, 100, 20);
    }

}