package io.github.homchom.recode.mixin.render;

import io.github.homchom.recode.feature.social.MCGuiWithSideChat;
import io.github.homchom.recode.feature.social.SideChat;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class MGui implements MCGuiWithSideChat {
    @Unique
    private final SideChat sideChat = new SideChat();

    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;III)V"
    ))
    private void renderSideChat(ChatComponent mainChat, GuiGraphics graphics, int tickDelta, int x, int y) {
        mainChat.render(graphics, tickDelta, x, y);
        sideChat.render(graphics, tickDelta, x, y);
    }

    @Unique
    @NotNull
    public SideChat recode$getSideChat() {
        return sideChat;
    }
}
