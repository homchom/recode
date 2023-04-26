package io.github.homchom.recode.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.render.SideChatComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class MGui {
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;III)V"
    ))
    private void renderSideChat(ChatComponent instance, PoseStack poseStack, int tickDelta, int mouseX, int mouseY) {
        instance.render(poseStack, tickDelta, mouseX, mouseY);
        ((SideChatComponent) instance).renderSide(poseStack, tickDelta, mouseX, mouseY);
    }
}
