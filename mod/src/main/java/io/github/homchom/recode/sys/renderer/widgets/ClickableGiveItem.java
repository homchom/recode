package io.github.homchom.recode.sys.renderer.widgets;

import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public class ClickableGiveItem extends CItem {

    public ClickableGiveItem(ItemStack stack) {
        super(stack);
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        Minecraft mc = Minecraft.getInstance();
        ItemUtil.giveCreativeItem(getItems().get(0), true);
        mc.player.playNotifySound(SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 2, 1);
        return InputResult.IGNORED;
    }
}
