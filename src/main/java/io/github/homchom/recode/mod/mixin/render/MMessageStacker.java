package io.github.homchom.recode.mod.mixin.render;

import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.util.OrderedTextUtil;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

// Message Stacker TODO: improve further
@Mixin(ChatComponent.class)
public abstract class MMessageStacker {
    @Unique
    private final List<GuiMessage.Line> copiedMessages = new ArrayList<>();

    @Shadow
    public abstract int getWidth();

    @Shadow public abstract void render(GuiGraphics guiGraphics, int i, int j, int k);

    // TODO: improve render mixin functions further

    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;isChatFocused()Z",
            ordinal = 0
    ))
    private void stackMessages(CallbackInfo ci) {
        if (Config.getBoolean("stackDuplicateMsgs")) {
            copiedMessages.addAll(trimmedMessages);
            stackMessagesImpl(trimmedMessages, copiedMessages);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void unstackMessages(CallbackInfo ci) {
        if (Config.getBoolean("stackDuplicateMsgs")) {
            trimmedMessages.clear();
            trimmedMessages.addAll(copiedMessages);
            copiedMessages.clear();
        }
    }

    @Unique
    private void addMessageToStacker(List<GuiMessage.Line> messages, GuiMessage.Line message, int count) {
        if (count == 1) {
            messages.add(message);
            return;
        }
        var text = FormattedCharSequence.composite(
                message.content(),
                TextUtil.colorCodesToTextComponent(" §bx" + count).getVisualOrderText()
        );
        messages.add(new GuiMessage.Line(message.addedTime(), text, message.tag(), true));
    }

    @Unique
    private void stackMessagesImpl(List<GuiMessage.Line> messages, List<GuiMessage.Line> copy) {
        messages.clear();
        if (copy.isEmpty()) return;

        var count = 1;

        var iterator = copy.iterator();
        var previous = iterator.next();
        while (iterator.hasNext()) {
            var message = iterator.next();

            var previousString = OrderedTextUtil.getString(previous.content());
            var messageString = OrderedTextUtil.getString(message.content());
            if (previousString.equals(messageString)) {
                count++;
            } else {
                addMessageToStacker(messages, previous, count);
                count = 1;
            }

            previous = message;
        }

        addMessageToStacker(messages, previous, count);
    }
}
