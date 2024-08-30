package io.github.homchom.recode.mod.features.commands;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.homchom.recode.sys.renderer.IMenu;
import io.github.homchom.recode.sys.renderer.widgets.CItem;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;

public class ItemEditorMenu extends LightweightGuiDescription implements IMenu {
    private final ItemStack itemStack;

    public ItemEditorMenu(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void open(String... args) {
        Minecraft mc = Minecraft.getInstance();
        final ItemStack[] item = {itemStack.copy()};
        WGridPanel root = new WGridPanel(1);
        root.setSize(256, 240);

        //Item Display
        CItem icon = new CItem(item[0]);
        root.add(icon, 0, 0, 20, 20);

        //Item Name
        WTextField name = new WTextField(Component.literal(""));
        name.setMaxLength(Integer.MAX_VALUE);
        name.setSuggestion(Component.translatable(item[0].getItem().getDescriptionId()));
        name.setText(TextUtil.toLegacyCodes(item[0].getHoverName()).replaceAll("§", "&"));
        name.setChangedListener(s -> {
            if (name.getText().isEmpty()) {
                item[0].resetHoverName();
            } else {
                item[0].setHoverName(TextUtil.colorCodesToTextComponent(name.getText().replaceAll("&", "§")));
            }
        });
        root.add(name, 30, 0, 226, 0);

        //Save & Quit
        WButton save = new WButton(Component.literal("Save & Quit"));

        save.setOnClick(() -> {
            mc.gameMode.handleCreativeModeItemAdd(item[0], mc.player.getInventory().selected + 36);
            mc.screen.onClose();
        });

        root.add(save, 190, 220, 70, 20);

        //Item Material
        WTextField material = new WTextField(Component.literal(""));
        material.setMaxLength(Integer.MAX_VALUE);
        material.setText(item[0].getItem().toString());
        material.setChangedListener(s -> {
            Item newMat = BuiltInRegistries.ITEM.get(new ResourceLocation("minecraft:" + s));
            if (newMat != Items.AIR) {
                save.setEnabled(true);
                ItemStack newItem = new ItemStack(newMat, item[0].getCount());
                newItem.setTag(item[0].getOrCreateTag());
                item[0] = newItem;
                icon.setItems(Collections.singletonList(item[0]));
            } else save.setEnabled(false);
        });
        root.add(material, 30, 25, 226, 0);


        setRootPanel(root);
        root.validate(this);
        root.setInsets(Insets.ROOT_PANEL);
    }
}
