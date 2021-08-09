package io.github.codeutilities.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.mod.features.commands.nbs.NBSDecoder;
import io.github.codeutilities.mod.features.commands.nbs.NBSToTemplate;
import io.github.codeutilities.mod.features.commands.nbs.SongData;
import io.github.codeutilities.mod.features.commands.nbs.exceptions.OutdatedNBSException;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import io.github.codeutilities.sys.file.ExternalFile;
import io.github.codeutilities.sys.util.ItemUtil;
import io.github.codeutilities.sys.renderer.ToasterUtil;
import io.github.codeutilities.sys.hypercube.templates.TemplateUtils;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
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
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
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
