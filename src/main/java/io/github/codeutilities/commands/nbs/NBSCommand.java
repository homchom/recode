package io.github.codeutilities.commands.nbs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.nbs.*;
import io.github.codeutilities.nbs.exceptions.OutdatedNBSException;
import io.github.codeutilities.util.*;
import io.github.codeutilities.util.externalfile.ExternalFile;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.item.*;
import net.minecraft.text.LiteralText;

import java.io.*;

public class NBSCommand extends Command {


    public static void loadNbs(File file, String fileName) {
        new Thread(() -> {
            try {
                SongData d = io.github.codeutilities.commands.nbs.NBSDecoder.parse(file);
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
                ItemUtil.giveCreativeItem(stack);
            } catch (OutdatedNBSException e) {
                ToasterUtil.sendToaster("§cLoading Error!", "Unsupported file version", SystemToast.Type.NARRATOR_TOGGLE);
            } catch (IOException e) {
                ToasterUtil.sendToaster("§cLoading Error!", "Invalid file", SystemToast.Type.NARRATOR_TOGGLE);
            }
        }).start();
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("nbs")
                .then(ArgBuilder.literal("load")
                        .then(ArgBuilder.argument("filename", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    if (mc.player.isCreative()) {
                                        String filename = StringArgumentType.getString(ctx, "filename");
                                        String childName = filename + (filename.endsWith(".nbs") ? "" : ".nbs");
                                        File file = new File(ExternalFile.NBS_FILES.getFile(), childName);

                                        if (file.exists()) {
                                            loadNbs(file, childName);
                                        } else {
                                            ChatUtil.sendMessage("The file §6" + childName + "§c was not found.", ChatType.FAIL);
                                        }
                                    } else {
                                        ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
                                    }
                                    return 1;
                                })
                        )
                )
                .then(ArgBuilder.literal("player")
                        .executes(ctx -> {
                            if (mc.player.isCreative()) {
                                ItemStack stack = new ItemStack(Items.JUKEBOX);
                                String templateData = "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";
                                TemplateUtils.applyRawTemplateNBT(stack, "Music Player", "CodeUtilities", templateData);
                                stack.setCustomName(new LiteralText("§b§lFunction §3» Code§bUtilities§5 Music Player"));
                                ChatUtil.sendMessage("You received the §dMusic Player§b! Place it down in your codespace and open the chest to get functions!",
                                        ChatType.INFO_BLUE);
                                ItemUtil.giveCreativeItem(stack);
                            } else {
                                ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
                            }
                            return 1;
                        })
                )
        );
    }
}
