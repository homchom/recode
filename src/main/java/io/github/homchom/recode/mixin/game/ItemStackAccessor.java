package io.github.homchom.recode.mixin.game;

import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStack.class)
public interface ItemStackAccessor {
    @Accessor("LORE_STYLE")
    static Style getLoreVanillaStyle() {
        throw new AssertionError();
    }
}
