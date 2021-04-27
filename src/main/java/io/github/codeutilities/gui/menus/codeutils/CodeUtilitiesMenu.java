package io.github.codeutilities.gui.menus.codeutils;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.gui.widgets.CImage;
import io.github.codeutilities.util.IMenu;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class CodeUtilitiesMenu extends LightweightGuiDescription implements IMenu {
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
        WButton featuresButton = new WButton(new LiteralText("Features"));
        panel.add(featuresButton, 60, 148, 100, 20);

        WButton contributorsButton = new WButton(new LiteralText("Contributors"));
        contributorsButton.setOnClick(() -> ContributorsMenu.getInstance().scheduleOpenGui(ContributorsMenu.getInstance()));
        panel.add(contributorsButton, 60, 170, 100, 20);

        WButton bugReport = new WButton(new LiteralText("Bug Report"));
        bugReport.setOnClick(() -> {
            //todo: make bug report button work
        });
        panel.add(bugReport, 60, 192, 100, 20);
    }

}
