package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.util.OrderedTextUtil;
import io.github.codeutilities.sys.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class MChatHUD {

    List<ChatHudLine<OrderedText>> cuTempStorage = new ArrayList<>();
    @Shadow
    @Final
    private List<ChatHudLine<OrderedText>> visibleMessages;

    @Inject(method = "render", at = @At("HEAD"))
    public void preRender(MatrixStack matrices, int tickDelta, CallbackInfo ci) {
        try {
            if (Config.getBoolean("stackDuplicateMsgs")) {
                ChatHudLine<OrderedText> last = null;
                int count = 1;
                cuTempStorage.clear();
                cuTempStorage.addAll(visibleMessages);

                List<ChatHudLine<OrderedText>> copy = new ArrayList<>();

                for (ChatHudLine<OrderedText> msg : visibleMessages) {
                    if (last == null) {
                        last = msg;
                    } else {
                        if (OrderedTextUtil.getString(last.getText())
                            .equals(OrderedTextUtil.getString(msg.getText()))) {
                            count++;
                        } else {
                            if (count == 1) {
                                copy.add(last);
                            } else {
                                copy.add(new ChatHudLine<>(last.getCreationTick(),
                                    OrderedText.concat(
                                        last.getText(),
                                        TextUtil.colorCodesToTextComponent(" §bx" + count).asOrderedText()),
                                        last.getId()
                                    )
                                );
                            }
                            count = 1;
                            last = msg;
                        }
                    }
                }
                if (last != null) {
                    copy.add(last);
                }
                visibleMessages.clear();
                visibleMessages.addAll(copy);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void postRender(MatrixStack matrices, int tickDelta, CallbackInfo ci) {
        if (Config.getBoolean("stackDuplicateMsgs")) {
            visibleMessages.clear();
            visibleMessages.addAll(cuTempStorage);
        }
    }
}

//    @Shadow
//    @Final
//    private static Logger LOGGER;
//    @Shadow
//    @Final
//    private MinecraftClient client;
//
//    @Shadow
//    protected abstract void addMessage(Text message, int messageId, int timestamp,
//                                       boolean refresh);
//
//    int lastid = 0;
//    Text lastmsg = new LiteralText("");
//    int stackcount = 0;
//
//    @Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), cancellable = true)
//    private void addMessage(Text msg, int id, CallbackInfo ci) {
//        if (Config.getBoolean("stackDuplicateMsgs")) {
//            if (msg.getString().equals(lastmsg.getString())) {
//                stackcount++;
//                msg = new LiteralText("").append(msg).append(" §3§lx" + stackcount);
//                id = lastid;
//            } else {
//                stackcount = 1;
//                lastid++;
//                lastmsg = msg;
//                if (id == 0) {
//                    id = lastid;
//                } else {
//                    lastid = id;
//                }
//            }
//
//            addMessage(msg, id, client.inGameHud.getTicks(), false);
//            LOGGER.info("[CHAT] {}", msg.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
//            ci.cancel();
//        }
//    }
