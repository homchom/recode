package io.github.homchom.recode.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.commands.arguments.types.FileArgumentType;
import io.github.homchom.recode.mod.features.commands.nbs.*;
import io.github.homchom.recode.mod.features.commands.nbs.exceptions.OutdatedNBSException;
import io.github.homchom.recode.sys.file.ExternalFile;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtils;
import io.github.homchom.recode.sys.player.chat.*;
import io.github.homchom.recode.sys.renderer.ToasterUtil;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;

import java.io.*;

public class NBSCommand extends Command {

    public static void loadNbs(File file, String fileName) {
        Recode.EXECUTOR.submit(() -> {
            try {
                SongData d = NBSDecoder.parse(file);
                String code = new NBSToTemplate(d).convert();
                ItemStack stack = new ItemStack(Items.NOTE_BLOCK);
                TemplateUtils.compressTemplateNBT(stack, d.getName(), d.getAuthor(), code);

                if (d.getName().length() == 0) {
                    String name;
                    if (d.getFileName().indexOf(".") > 0) {
                        name = d.getFileName().substring(0, d.getFileName().lastIndexOf("."));
                    } else {
                        name = d.getFileName();
                    }
                    stack.setHoverName(new TextComponent("§5SONG§7 -§f " + name));
                } else {
                    stack.setHoverName(new TextComponent("§5SONG§7 -§f " + d.getName()));
                }

                ToasterUtil.sendToaster("NBS Loaded!", fileName, SystemToast.SystemToastIds.NARRATOR_TOGGLE);
                ItemUtil.giveCreativeItem(stack, true);
            } catch (OutdatedNBSException e) {
                ToasterUtil.sendToaster("§cLoading Error!", "Unsupported file version", SystemToast.SystemToastIds.NARRATOR_TOGGLE);
            } catch (IOException e) {
                ToasterUtil.sendToaster("§cLoading Error!", "Invalid file", SystemToast.SystemToastIds.NARRATOR_TOGGLE);
            }
        });
    }

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("nbs")
                .then(ArgBuilder.literal("load")
                        .then(ArgBuilder.argument("filename", FileArgumentType.folder(ExternalFile.NBS_FILES.getFile(), true))
                                .executes(ctx -> {
                                    if (this.isCreative(mc)) {
                                        String filename = StringArgumentType.getString(ctx, "filename");
                                        String childName = filename + (filename.endsWith(".nbs") ? "" : ".nbs");
                                        File file = new File(ExternalFile.NBS_FILES.getFile(), childName);

                                        if (file.exists()) {
                                            loadNbs(file, childName);
                                        } else {
                                            ChatUtil.sendMessage("The file §6" + childName + "§c was not found.", ChatType.FAIL);
                                        }
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
                                TemplateUtils.applyRawTemplateNBT(stack, "Music Player", "CodeUtilities", templateData);
                                stack.setHoverName(new TextComponent("§b§lFunction §3» §Recode§5 Music Player"));
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
