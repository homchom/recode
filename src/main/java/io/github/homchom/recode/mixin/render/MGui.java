package io.github.homchom.recode.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.render.SideChatComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class MGui {
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;III)V"
    ))
    private void renderSideChat(ChatComponent instance, GuiGraphics guiGraphics, int tickDelta, int mouseX, int mouseY) {
        instance.render(guiGraphics, tickDelta, mouseX, mouseY);
        ((SideChatComponent) instance).renderSide(guiGraphics, tickDelta, mouseX, mouseY);
    }
}
