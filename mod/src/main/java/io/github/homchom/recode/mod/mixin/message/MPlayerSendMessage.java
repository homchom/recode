package io.github.homchom.recode.mod.mixin.message;

import io.github.homchom.recode.hypercube.state.DF;
import io.github.homchom.recode.hypercube.state.DFState;
import io.github.homchom.recode.hypercube.state.PlotMode;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.mod.features.social.chat.ConversationTimer;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MPlayerSendMessage {
    private final Minecraft minecraftClient = Minecraft.getInstance();

    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    public void chat(String string, CallbackInfo ci) {
        String[] args = string.split(" ");
        if (minecraftClient.player != null) {
            if (!string.startsWith("/")) {
                ItemStack mainHand = minecraftClient.player.getMainHandItem();
                if (mainHand.hasTag()) {
                    CompoundTag tag = mainHand.getTag();
                    CompoundTag publicBukkitValues = tag.getCompound("PublicBukkitValues");
                    if (!tag.contains("PublicBukkitValues") || !publicBukkitValues.contains("hypercube:varitem")) {
                        if (!(DF.getCurrentDFState() instanceof DFState.AtSpawn) || !mainHand.getHoverName().getString().equals("◇ Game Menu ◇"))
                            conversationMessage(string, ci);
                    }
                } else conversationMessage(string, ci);
            }
        }
        //start conversation
        if (LegacyConfig.getBoolean("automsg") && (string.startsWith("/msg ") || string.startsWith("/w "))) {
            if (args.length == 2) {
                ci.cancel();
                ConversationTimer.currentConversation = args[1];
                ConversationTimer.conversationUpdateTime = String.valueOf(System.currentTimeMillis());
                if (LegacyConfig.getBoolean("automsg_timeout")) ConversationTimer.isTimerOn = true;
                ChatUtil.sendMessage("Started a conversation with " + ConversationTimer.currentConversation + "!", ChatType.SUCCESS);

            }
        }
        //end conversation
        if (ConversationTimer.currentConversation != null && (string.startsWith("/chat ") || string.startsWith("/c "))) {
            ConversationTimer.currentConversation = null;
            ConversationTimer.isTimerOn = false;
            ChatUtil.sendMessage("The conversation was ended.", ChatType.SUCCESS);
        }
    }

    private void conversationMessage(String message, CallbackInfo ci) {
        if (LegacyConfig.getBoolean("automsg") && ConversationTimer.currentConversation != null && (!DF.isInMode(DF.getCurrentDFState(), PlotMode.Play.INSTANCE) || !message.startsWith("@"))) {
            ci.cancel();
            ConversationTimer.conversationUpdateTime = String.valueOf(System.currentTimeMillis());
            minecraftClient.player.connection.sendUnsignedCommand("msg " + ConversationTimer.currentConversation + " " + message);
        }
    }
}
