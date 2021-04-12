package io.github.codeutilities.commands.image;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.images.ImageToParticle;
import io.github.codeutilities.images.ParticleImage;
import io.github.codeutilities.util.*;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.externalfile.ExternalFile;
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

public class ImageParticleCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("imageparticle")
                .then(ArgBuilder.literal("load")
                        .then(ArgBuilder.argument("filename", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    try {
                                        String location = StringArgumentType.getString(ctx, "filename");
                                        String fileName = location + (location.endsWith(".png") ? "" : ".png");
                                        File f = new File(ExternalFile.IMAGE_FILES.getFile(), fileName);

                                        if (f.exists()) {
                                            ParticleImage image = ImageToParticle.convert(f);

                                            ItemStack stack = new ItemStack(Items.REDSTONE_ORE);
                                            TemplateUtils.compressTemplateNBT(stack, fileName, mc.player.getName().asString(), convertToTemplate(image, fileName));
                                            stack.setCustomName(new LiteralText("§6IMAGE§7 -§f " + fileName));
                                            ItemUtil.giveCreativeItem(stack, true);

                                            ToasterUtil.sendToaster("Image Loaded!", fileName, SystemToast.Type.NARRATOR_TOGGLE);
                                        } else {
                                            ToasterUtil.sendToaster("§cLoading Error!", "Invalid file", SystemToast.Type.NARRATOR_TOGGLE);
                                        }
                                        return 1;
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                        ChatUtil.sendMessage("An error occurred while executing the command.", ChatType.FAIL);
                                        return 0;
                                    }
                                })
                        )
                )
                .then(ArgBuilder.literal("printer")
                        .executes(ctx -> {
                            try {
                                ItemStack stack = new ItemStack(Items.GOLD_BLOCK);
                                String templateData = "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";
                                TemplateUtils.applyRawTemplateNBT(stack, "Particle Image Printer", "CodeUtilities", templateData);
                                stack.setCustomName(new LiteralText("§b§lFunction §3» §bCodeUtilities§6 Particle Image Printer"));
                                stack.addEnchantment(Enchantments.LURE, 1);
                                stack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
                                ChatUtil.sendMessage("You've received §6Particle Image Printer§b! Place it down in your codespace and open the chest to get functions!",
                                        ChatType.INFO_BLUE);
                                ItemUtil.giveCreativeItem(stack, true);
                                return 1;
                            }catch (Exception e) {
                                e.printStackTrace();
                                ChatUtil.sendMessage("An error occurred while executing the command.", ChatType.FAIL);
                                return 0;
                            }
                        })
                )
        );
    }

    private String convertToTemplate(ParticleImage image, String name) {
        String[] data = image.getData();
        int width = image.getWidth();
        int height = image.getHeight();
        StringBuilder code = new StringBuilder();
        StringBuilder currentBlock = new StringBuilder();
        String codeblockType = "CreateList";

        System.out.println("Image size: " + data.length);
        System.out.println("Image width: " + width);
        System.out.println("Image height: " + height);

        code.append(String.format("{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"%s\"}", name));

        int slot = 1;
        for (String s : data) {
            if (slot > 26) {
                code.append(String.format(",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"imageData\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"%s\"}", currentBlock.toString(), codeblockType));
                currentBlock.delete(0, currentBlock.length());
                codeblockType = "AppendValue";
                slot = 1;
            }
            currentBlock.append(String.format(",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":%d}", s, slot));
            slot++;
        }
        code.append(String.format(",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"imageData\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"%s\"}", currentBlock.toString(), codeblockType));

        code.append(String.format(",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"imageSize\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":1},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":2}]},\"action\":\"CreateList\"}", width, height));

        return "{\"blocks\":[" + code + "]}";
    }
}
