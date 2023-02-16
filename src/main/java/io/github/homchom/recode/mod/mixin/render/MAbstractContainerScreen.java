package io.github.homchom.recode.mod.mixin.render;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface MAbstractContainerScreen<T extends AbstractContainerMenu> {
	@Invoker("isHovering")
	boolean isHovering(Slot slot, double mouseX, double mouseY);
}
