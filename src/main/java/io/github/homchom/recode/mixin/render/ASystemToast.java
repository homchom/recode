package io.github.homchom.recode.mixin.render;

import net.minecraft.client.gui.components.toasts.SystemToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SystemToast.class)
public interface ASystemToast {

    @Accessor
    SystemToast.SystemToastIds getId();

}
