package io.github.homchom.recode.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.features.commands.PlotsMenu;
import io.github.homchom.recode.sys.file.ExternalFile;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;

import java.io.*;
import java.util.List;

public class PlotsCommand extends Command {
    private static final File FILE = ExternalFile.PLOTS_DB.getFile();
    public static List<ItemStack> items = null;
    public static List<ItemStack> betaItems = null;

    static {
        getItems(null);
    }

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
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
        return "[blue]/plots[reset]\n"
                + "\n"
                + "Shows the list of your own plots. Click on the icon to join the plot.";
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
