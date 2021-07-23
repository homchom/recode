package io.github.codeutilities.mod.mixin.packet;

import io.github.codeutilities.mod.commands.impl.other.PlotsCommand;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import io.github.codeutilities.sys.file.ExternalFile;
import io.github.codeutilities.sys.util.ItemUtil;
import io.github.codeutilities.sys.player.DFInfo;
import io.github.codeutilities.sys.networking.State;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinInventoryPacketListener {
    private static final File FILE = ExternalFile.PLOTS_DB.getFile();
    private CompoundTag lastTag = null;

    @Inject(method = "onInventory", at = @At("RETURN"))
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        if (!Config.getBoolean("cmdLoadPlots")) {
            return;
        }

        List<ItemStack> contents = packet.getContents();
        if (DFInfo.currentState.getMode() != State.Mode.SPAWN) return;
        if (!MinecraftClient.getInstance().player.getMainHandStack().getName().getString().equals("◇ My Plots ◇"))
            return;
        boolean correctInventory = false;
        for (ItemStack item : contents) {
            if (item.getName().getString().equals("Claim new plot")) correctInventory = true;
        }
        if (!correctInventory) return;
        List<ItemStack> items = new ArrayList<>();
        for (short i = 0; i < contents.size(); i++) {
            ItemStack item = contents.get(i);
            if (item.getItem().toString().equals("air") || i >= 27) break;
            items.add(item);
        }
        try {
            CompoundTag compoundTag = new CompoundTag();
            List<ItemStack> antioverrider = DFInfo.isInBeta ? PlotsCommand.items : PlotsCommand.betaItems;
            if (antioverrider != null)
                compoundTag.put(DFInfo.isInBeta ? "items" : "betaItems", ItemUtil.toListTag(antioverrider));
            compoundTag.put(DFInfo.isInBeta ? "betaItems" : "items", ItemUtil.toListTag(items));
            if (lastTag != null && compoundTag.toString().equals(lastTag.toString())) return;
            NbtIo.write(compoundTag, FILE);
            PlotsCommand.getItems(compoundTag);
            lastTag = compoundTag;
        } catch (IOException e) {
            e.printStackTrace();
            ChatUtil.sendMessage("Failed to save plots data!", ChatType.FAIL);
        }
    }
}
