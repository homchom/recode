package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.gui.PlotsStorageUI;
import io.github.codeutilities.util.ItemUtil;
import io.github.codeutilities.util.file.ExternalFile;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlotsCommand extends Command {
    private static final File FILE = ExternalFile.PLOTS_DB.getFile();
    public static List<ItemStack> items = null;
    public static List<ItemStack> betaItems = null;
    static {
        getItems(null);
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("plots").executes(ctx -> {
            try {
                PlotsStorageUI plotsStorageUI = new PlotsStorageUI();
                plotsStorageUI.scheduleOpenGui(plotsStorageUI);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
            return 1;
        }));
    }

    public static void getItems(CompoundTag recievedTag) {
        try {
            CompoundTag compoundTag = recievedTag;
            if (compoundTag == null) compoundTag = NbtIo.read(FILE);
            if (compoundTag == null) {
                return;
            }
            betaItems = ItemUtil.fromListTag(compoundTag.getList("betaItems", 10));
            items = ItemUtil.fromListTag(compoundTag.getList("items", 10));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
