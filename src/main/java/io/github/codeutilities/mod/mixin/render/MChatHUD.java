package io.github.codeutilities.mod.mixin.render;

import io.github.codeutilities.mod.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class MChatHUD {

    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void addMessage(Text message, int messageId, int timestamp,
                                       boolean refresh);

    int lastid = 0;
    Text lastmsg = new LiteralText("");
    int stackcount = 0;

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), cancellable = true)
    private void addMessage(Text msg, int id, CallbackInfo ci) {
        if (Config.getBoolean("stackDuplicateMsgs")) {
            if (msg.getString().equals(lastmsg.getString())) {
                stackcount++;
                msg = new LiteralText("").append(msg).append(" §3§lx" + stackcount);
                id = lastid;
            } else {
                stackcount = 1;
                lastid++;
                lastmsg = msg;
                if (id == 0) {
                    id = lastid;
                } else {
                    lastid = id;
                }
            }

            addMessage(msg, id, client.inGameHud.getTicks(), false);
            LOGGER.info("[CHAT] {}", msg.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
            ci.cancel();
        }
    }
}
