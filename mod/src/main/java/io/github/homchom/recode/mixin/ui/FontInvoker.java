package io.github.homchom.recode.mixin.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Font.class)
public interface FontInvoker {
    @Invoker("getFontSet")
    FontSet invokeGetFontSet(ResourceLocation resourceLocation);
}
