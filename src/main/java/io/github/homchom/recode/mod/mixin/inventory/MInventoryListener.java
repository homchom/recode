package io.github.homchom.recode.mod.mixin.inventory;

import io.github.homchom.recode.mod.commands.impl.other.PlotsCommand;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.file.ExternalFile;
import io.github.homchom.recode.sys.networking.State;
import io.github.homchom.recode.sys.player.DFInfo;
import io.github.homchom.recode.sys.player.chat.*;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.util.*;

@SuppressWarnings("ALL")
@Mixin(ClientPacketListener.class)
public class MInventoryListener {
    private static final File FILE = ExternalFile.PLOTS_DB.getFile();
    private CompoundTag lastTag = null;

    @Inject(method = "handleContainerContent", at = @At("RETURN"))
    private void handleContainerContent(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        if (!Config.getBoolean("cmdLoadPlots")) {
            return;
        }

        List<ItemStack> contents = packet.getItems();
        if (DFInfo.currentState.getMode() != State.Mode.SPAWN) return;
        if (!Minecraft.getInstance().player.getMainHandItem().getHoverName().getString().equals("◇ My Plots ◇"))
            return;
        boolean correctInventory = false;
        for (ItemStack item : contents) {
            if (item.getHoverName().getString().equals("Claim new plot")) correctInventory = true;
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
