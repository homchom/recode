package io.github.homchom.recode.mod.mixin.inventory;

import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.LagslayerHUD;
import io.github.homchom.recode.sys.hypercube.templates.*;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MItemSlotUpdate {
	final Minecraft mc = Minecraft.getInstance();
	private long lobbyTime = System.currentTimeMillis() - 1000;

	@Inject(method = "handleContainerSetSlot", at = @At("HEAD"))
	public void handleContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
		if (packet.getContainerId() == 0) {
			ItemStack stack = packet.getItem();
			if (TemplateUtil.isTemplate(stack)) {
				TemplateStorageHandler.addTemplate(stack);
			}

			CompoundTag nbt = stack.getOrCreateTag();
			CompoundTag display = nbt.getCompound("display");
			ListTag lore = display.getList("Lore", 8);
			if (mc.player == null) {
				return;
			}

			if (DFInfo.isOnDF() && stack.getHoverName().getString().contains("◇ Game Menu ◇")
					&& lore.toString().contains("\"Click to open the Game Menu.\"")
					&& lore.toString().contains("\"Hold and type in chat to search.\"")) {

				DFInfo.currentState.sendLocate();

				// Auto fly
				if (Config.getBoolean("autofly")) {
					if (System.currentTimeMillis() > lobbyTime) { // theres a bug with /fly running twice this is a temp fix.
						mc.player.commandUnsigned("fly");
						MessageGrabber.hide(1);
						lobbyTime = System.currentTimeMillis() + 1000;
					}
				}

				LagslayerHUD.lagSlayerEnabled = false;
			}
		}
	}
}
