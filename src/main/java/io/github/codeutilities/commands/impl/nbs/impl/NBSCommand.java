package io.github.codeutilities.commands.impl.nbs.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.commands.impl.nbs.sys.NBSDecoder;
import io.github.codeutilities.commands.impl.nbs.sys.NBSToTemplate;
import io.github.codeutilities.commands.impl.nbs.sys.SongData;
import io.github.codeutilities.commands.impl.nbs.sys.exceptions.OutdatedNBSException;
import io.github.codeutilities.util.misc.ItemUtil;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.file.ExternalFile;
import io.github.codeutilities.util.render.ToasterUtil;
import io.github.codeutilities.util.templates.TemplateUtils;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

import java.io.File;
import java.io.IOException;

public class NBSCommand extends Command {

    public static void loadNbs(File file, String fileName) {
        CodeUtilities.EXECUTOR.submit(() -> {
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
                    stack.setCustomName(new LiteralText("§5SONG§7 -§f " + name));
                } else {
                    stack.setCustomName(new LiteralText("§5SONG§7 -§f " + d.getName()));
                }

                ToasterUtil.sendToaster("NBS Loaded!", fileName, SystemToast.Type.NARRATOR_TOGGLE);
                ItemUtil.giveCreativeItem(stack, true);
            } catch (OutdatedNBSException e) {
                ToasterUtil.sendToaster("§cLoading Error!", "Unsupported file version", SystemToast.Type.NARRATOR_TOGGLE);
            } catch (IOException e) {
                ToasterUtil.sendToaster("§cLoading Error!", "Invalid file", SystemToast.Type.NARRATOR_TOGGLE);
            }
        });
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("nbs")
                .then(ArgBuilder.literal("load")
                        .then(ArgBuilder.argument("filename", StringArgumentType.greedyString())
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
                                stack.setCustomName(new LiteralText("§b§lFunction §3» §bCodeUtilities§5 Music Player"));
                                stack.addEnchantment(Enchantments.LURE, 1);
                                stack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
                                ChatUtil.sendMessage("You received the §dMusic Player§b! Place it down in your codespace and open the chest to get functions!",
                                        ChatType.INFO_BLUE);
                                ItemUtil.giveCreativeItem(stack, true);
                            }
                            return 1;
                        })
                )
        );
    }
}
