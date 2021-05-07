package io.github.codeutilities.mixin.item;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.features.social.chat.ChatReceivedEvent;
import io.github.codeutilities.util.render.gui.CPU_UsageText;
import io.github.codeutilities.features.keybinds.FlightspeedToggle;
import io.github.codeutilities.util.templates.TemplateStorageHandler;
import io.github.codeutilities.util.networking.DFInfo;
import io.github.codeutilities.util.templates.TemplateUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinItemSlotUpdate {
    final MinecraftClient mc = MinecraftClient.getInstance();
    private long lobbyTime = System.currentTimeMillis() - 1000;
    private long lastDevCheck = 0;

    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"))
    public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) {
            ItemStack stack = packet.getItemStack();
            if (TemplateUtils.isTemplate(stack)) {
                TemplateStorageHandler.addTemplate(stack);
            }

            CompoundTag nbt = stack.getOrCreateTag();
            CompoundTag display = nbt.getCompound("display");
            ListTag lore = display.getList("Lore", 8);
            if (mc.player == null) {
                return;
            }

            if (DFInfo.isOnDF() && stack.getName().getString().contains("◇ Game Menu ◇")
                    && lore.toText().getString().contains("\"Click to open the Game Menu.\"")
                    && lore.toText().getString().contains("\"Hold and type in chat to search.\"")) {

                if (DFInfo.currentState != DFInfo.State.LOBBY) {
                    DFInfo.currentState = DFInfo.State.LOBBY;

                    // Auto fly
                    if (CodeUtilsConfig.getBool("autofly")) {
                        if (System.currentTimeMillis() > lobbyTime) { // theres a bug with /fly running twice this is a temp fix.
                            mc.player.sendChatMessage("/fly");
                            ChatReceivedEvent.cancelFlyMsg = true;
                            lobbyTime = System.currentTimeMillis() + 1000;
                        }

                    }

                    CPU_UsageText.lagSlayerEnabled = false;

                    // fs toggle
                    FlightspeedToggle.fs_is_normal = true;
                }
            }

            if (DFInfo.isOnDF() && mc.player.isCreative() && stack.getName().getString().contains("Player Event")
                    && lore.toText().getString().contains("\"Used to execute code when something\"")
                    && lore.toText().getString().contains("\"is done by (or happens to) a player.\"")
                    && lore.toText().getString().contains("\"Example:\"")) {

                if (DFInfo.currentState != DFInfo.State.DEV) {
                    DFInfo.currentState = DFInfo.State.DEV;
                    DFInfo.plotCorner = mc.player.getPos().add(10, -50, -10);

                    // Auto LagSlayer
                    if (!CPU_UsageText.lagSlayerEnabled && CodeUtilsConfig.getBool("autolagslayer")) {
                        mc.player.sendChatMessage("/lagslayer");
                        ChatReceivedEvent.cancelLagSlayerMsg = true;
                    }

                    // fs toggle
                    FlightspeedToggle.fs_is_normal = true;

                    long time = System.currentTimeMillis() / 1000L;
                    if (time - lastDevCheck > 1) {

                        new Thread(() -> {
                            try {
                                Thread.sleep(10);
                                if (CodeUtilsConfig.getBool("autoRC")) {
                                    mc.player.sendChatMessage("/rc");
                                }
                                if (CodeUtilsConfig.getBool("autotime")) {
                                    mc.player.sendChatMessage("/time " + CodeUtilsConfig.getInt("autotimeval"));
                                    ChatReceivedEvent.cancelTimeMsg = true;
                                }
                                if (CodeUtilsConfig.getBool("autonightvis")) {
                                    mc.player.sendChatMessage("/nightvis");
                                    ChatReceivedEvent.cancelNVisionMsg = true;
                                }
                            } catch (Exception e) {
                                CodeUtilities.log(Level.ERROR, "Error while executing the task!");
                                e.printStackTrace();
                            }
                        }).start();

                        lastDevCheck = time;
                    }
                }
            }
        }
    }

}
