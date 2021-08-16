package io.github.codeutilities.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.file.ExternalFile;
import io.github.codeutilities.mod.features.commands.PlotsMenu;
import io.github.codeutilities.sys.util.ItemUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
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
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("plots").executes(ctx -> {
            try {
                PlotsMenu plotsStorageUI = new PlotsMenu();
                plotsStorageUI.scheduleOpenGui(plotsStorageUI);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
            return 1;
        }));
    }

    @Override
    public String getDescription() {
        return "/plots\n\nOpen the df plots menu from anywhere.";
    }

    @Override
    public String getName() {
        return "/plots";
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
