package io.github.homchom.recode.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.homchom.recode.feature.social.MCGuiWithSideChat;
import io.github.homchom.recode.feature.social.SideChat;
import io.github.homchom.recode.util.mixin.MixinField;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class MGui implements MCGuiWithSideChat {
    @Unique
    private final MixinField<SideChat, Gui> sideChat = new MixinField<>(SideChat::new);

    @WrapOperation(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;III)V"
    ))
    private void renderSideChat(
            ChatComponent mainChat,
            GuiGraphics graphics,
            int tickDelta,
            int x,
            int y,
            Operation<Void> operation
    ) {
        operation.call(mainChat, graphics, tickDelta, x, y);
        sideChat.get(thisGui()).render(graphics, tickDelta, x, y);
    }

    @Unique
    @NotNull
    public SideChat recode$getSideChat() {
        return sideChat.get(thisGui());
    }

    @Unique
    private Gui thisGui() {
        return (Gui) (Object) this;
    }
}
