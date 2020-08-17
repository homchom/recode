package io.github.codeutilities.gui;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.StringUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import java.util.Collections;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemEditorGui extends LightweightGuiDescription {

    public ItemEditorGui(ItemStack in) {
        MinecraftClient mc = CodeUtilities.mc;
        final ItemStack[] item = {in.copy()};//intellij wants me to do this, dont ask me why
        WGridPanel root = new WGridPanel(1);
        root.setSize(256, 240);

        //Item Display
        CItem icon = new CItem(item[0]);
        icon.setScale(1.5F);
        root.add(icon, 0, 0, 20, 20);

        //Item Name
        CTextField name = new CTextField(new LiteralText(""));
        name.setMaxLength(Integer.MAX_VALUE);
        name.setSuggestion(new TranslatableText(item[0].getItem().getTranslationKey()));
        name.setText(StringUtil.textToString(item[0].getName()).replaceAll("§", "&"));
        name.setChangedListener(s -> {
            if (name.getText().isEmpty()) {
                item[0].removeCustomName();
            } else {
                item[0].setCustomName(new LiteralText(name.getText().replaceAll("&", "§")));
            }
        });
        root.add(name, 30, 0, 226, 0);

        //Save & Quit
        WButton save = new WButton(new LiteralText("Save & Quit"));

        save.setOnClick(() -> {
            mc.interactionManager.clickCreativeStack(item[0], mc.player.inventory.selectedSlot + 36);
            mc.currentScreen.onClose();
        });

        root.add(save, 190, 220, 70, 20);

        //Item Material

        CTextField material = new CTextField(new LiteralText(""));
        material.setMaxLength(Integer.MAX_VALUE);
        material.setText(item[0].getItem().toString());
        material.setChangedListener(s -> {
            Item newMat = Registry.ITEM.get(new Identifier("minecraft:" + s));
            if (newMat != Items.AIR) {
                save.setEnabled(true);
                ItemStack newItem =  new ItemStack(newMat, item[0].getCount());
                newItem.setTag(item[0].getOrCreateTag());
                item[0] = newItem;
                icon.setItems(Collections.singletonList(item[0]));
            } else save.setEnabled(false);
        });
        root.add(material, 30, 25, 226, 0);


        setRootPanel(root);
        root.validate(this);


    }

}
