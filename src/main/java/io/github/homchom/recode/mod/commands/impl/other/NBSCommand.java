package io.github.homchom.recode.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.commands.arguments.types.PathArgumentType;
import io.github.homchom.recode.mod.features.commands.nbs.NBSDecoder;
import io.github.homchom.recode.mod.features.commands.nbs.NBSToTemplate;
import io.github.homchom.recode.mod.features.commands.nbs.SongData;
import io.github.homchom.recode.mod.features.commands.nbs.exceptions.OutdatedNBSException;
import io.github.homchom.recode.sys.file.ExternalFile;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.renderer.ToasterUtil;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.io.File;
import java.io.IOException;

public class NBSCommand extends Command {

    public static void loadNbs(File file, String fileName) {
        LegacyRecode.executor.submit(() -> {
            try {
                SongData d = NBSDecoder.parse(file);
                String code = new NBSToTemplate(d).convert();
                ItemStack stack = new ItemStack(Items.NOTE_BLOCK);
                TemplateUtil.compressTemplateNBT(stack, d.getName(), d.getAuthor(), code);

                if (d.getName().length() == 0) {
                    String name;
                    if (d.getFileName().indexOf(".") > 0) {
                        name = d.getFileName().substring(0, d.getFileName().lastIndexOf("."));
                    } else {
                        name = d.getFileName();
                    }
                    stack.setHoverName(Component.literal("§5SONG§7 -§f " + name));
                } else {
                    stack.setHoverName(Component.literal("§5SONG§7 -§f " + d.getName()));
                }

                ToasterUtil.sendToaster("NBS Loaded!", fileName, SystemToast.SystemToastId.NARRATOR_TOGGLE);
                ItemUtil.giveCreativeItem(stack, true);
            } catch (OutdatedNBSException e) {
                ToasterUtil.sendToaster("§cLoading Error!", "Unsupported file version", SystemToast.SystemToastId.NARRATOR_TOGGLE);
            } catch (IOException e) {
                ToasterUtil.sendToaster("§cLoading Error!", "Invalid file", SystemToast.SystemToastId.NARRATOR_TOGGLE);
            }
        });
    }

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("nbs")
                .then(ArgBuilder.literal("load")
                        .then(ArgBuilder.argument("filename", PathArgumentType.folder(ExternalFile.NBS_FILES.getPath(), true))
                                .executes(ctx -> {
                                    if (this.isCreative(mc)) {
                                        File file = PathArgumentType.getPath(ctx, "filename").toFile();

                                        loadNbs(file, file.getName());
                                    }
                                    return 1;
                                })
                        )
                )
                .then(ArgBuilder.literal("player")
                        .executes(ctx -> {
                            if (this.isCreative(mc)) {
                                ItemStack stack = new ItemStack(Items.JUKEBOX);
                                String templateData = "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";
                                TemplateUtil.applyRawTemplateNBT(stack, "Music Player", "Recode", templateData);
                                stack.setHoverName(Component.literal("§b§lFunction §3» §bRecode§5 Music Player"));
                                stack.enchant(Enchantments.FISHING_SPEED, 1);
                                stack.hideTooltipPart(ItemStack.TooltipPart.ENCHANTMENTS);
                                ChatUtil.sendMessage("You received the §dMusic Player§b! Place it down in your codespace and open the chest to get functions!",
                                        ChatType.INFO_BLUE);
                                ItemUtil.giveCreativeItem(stack, true);
                            }
                            return 1;
                        })
                )
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/nbs load <file>[reset]\n"
                + "[blue]/nbs load <file>[reset]\n"
                + "\n"
                + "Imports Note Block Song files (.nbs) into DiamondFire.\n"
                + "Put the files you want to use in [green].minecraft/recode/NBS Files[reset] folder.\n"
                + "Use [yellow]/nbs player[reset] to grab the Song Player which plays the imported song data.";
    }

    @Override
    public String getName() {
        return "/nbs";
    }
}
