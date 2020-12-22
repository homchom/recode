package io.github.codeutilities.commands.image;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sun.scenario.effect.ImageData;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.images.ImageToHologram;
import io.github.codeutilities.images.ImageToParticle;
import io.github.codeutilities.images.ParticleImage;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.ItemUtil;
import io.github.codeutilities.util.TemplateUtils;
import io.github.codeutilities.util.externalfile.ExternalFile;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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
                                        File f = new File(ExternalFile.IMAGE_FILES.getFile(), location + (location.endsWith(".png") ? "" : ".png"));

                                        if (f.exists()) {
                                            ParticleImage image = ImageToParticle.convert(f);

                                            ItemStack stack = new ItemStack(Items.ENDER_CHEST);
                                            TemplateUtils.compressTemplateNBT(stack, location, mc.player.getName().asString(), convertToTemplate(image, location));
                                            ItemUtil.giveCreativeItem(stack);
                                            ChatUtil.sendMessage("Image loaded! Change the first Set Variable to the location!", ChatType.SUCCESS);
                                        } else {
                                            ChatUtil.sendMessage("That image doesn't exist.", ChatType.FAIL);
                                        }
                                        return 1;
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                        ChatUtil.sendTranslateMessage("An error occurred while executing the command.", ChatType.FAIL);
                                        return 0;
                                    }
                                })
                        )
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
            if (slot >= 26) {
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
