package io.github.codeutilities.gui;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.template.TemplateItem;
import io.github.codeutilities.template.TemplateStorageHandler;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ItemUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class TemplateStorageUI extends LightweightGuiDescription {


    public TemplateStorageUI() {
        WGridPanel root = new WGridPanel(1);
        setRootPanel(root);
        root.setSize(256, 90);
        int index = 0;
        for (TemplateItem item : TemplateStorageHandler.getTemplates()) {
            CItem i = new CItem(item.getStack());
            i.setClickListener(() -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player.isCreative()) {
                    ItemUtil.giveCreativeItem(item.getStack());
                    mc.player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 2, 1);
                } else {
                    CodeUtilities.chat("You need to be in creative to get templates.", ChatType.FAIL);
                }
            });
            root.add(i, (int) (index % 14 * 17.8), index / 14 * 18, 17, 18);
            index++;
        }
        root.validate(this);
    }


}
