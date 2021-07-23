package io.github.codeutilities.mod.features.commands.queue;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.menu.ConfigScreen;
import io.github.codeutilities.mod.features.commands.codeutilities.ContributorsUI;
import io.github.codeutilities.mod.features.commands.codeutilities.FeaturesUI;
import io.github.codeutilities.sys.renderer.IMenu;
import io.github.codeutilities.sys.renderer.widgets.CImage;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class QueueMenu extends LightweightGuiDescription implements IMenu {
    private static final Identifier CODEUTILS_LOGO = new Identifier("codeutilities:icon.png");

    private final LinkedHashSet<QueueEntry> queue;

    public QueueMenu(LinkedHashSet<QueueEntry> queue) {
        this.queue = queue;
    }

    @Override
    public void open(String... args) {
        WPlainPanel root = new WPlainPanel();
        root.setSize(520, 220);

        CImage cImage = new CImage(CODEUTILS_LOGO);
        cImage.setSize(74, 74);
        root.add(cImage, 450, -10);

        root.add(new WLabel(new LiteralText("Plot Queue")), (80 - MinecraftClient.getInstance().textRenderer.getWidth("CodeUtilities")), 5);

        setRootPanel(root);
    }

}