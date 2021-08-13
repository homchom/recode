package io.github.codeutilities.mod.features.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.codeutilities.sys.renderer.IMenu;
import io.github.codeutilities.sys.renderer.widgets.CTextField;
import io.github.codeutilities.sys.renderer.widgets.ItemScrollablePanel;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;

public class CodeVaultMenu extends LightweightGuiDescription implements IMenu {

    String[] categories = {
        "Soft-Coding",
        "Concepts",
        "Commands",
        "Tools",
        "Game Mechanics",
        "Misc",
        "Var Manipulation",
        "NBS Songs"
    };

    @Override
    public void open(String... args) throws CommandSyntaxException {
        WPlainPanel root = new WPlainPanel();
        root.setSize(300, 100);

        WTextField searchBox = new WTextField(
            new LiteralText("Search..."));
        searchBox.setMaxLength(100);
        root.add(searchBox, 110, 0, 250, 0);

        ItemScrollablePanel panel = ItemScrollablePanel.with(sampleItems());
        panel.setScrollingVertically(TriState.TRUE);
        panel.setScrollingHorizontally(TriState.FALSE);
        root.add(panel, 110, 25, 250, 140);

        int y = 0;
        for (String category : categories) {
            WButton btn = new WButton(new LiteralText(category));
            root.add(btn,0,y,100,22);
            y+=21;
        }

        setRootPanel(root);
        root.validate(this);
    }

    private List<ItemStack> sampleItems() {
        List<ItemStack> items = new ArrayList<>();
        Random rng = new Random();

        int count = rng.nextInt(50)+50;

        for (int i = 0; i < count; i++) {
            ItemStack item = new ItemStack(Registry.ITEM.get(rng.nextInt(100)));
            item.setCustomName(new LiteralText(UUID.randomUUID().toString()));
            items.add(item);
        }

        return items;
    }
}
