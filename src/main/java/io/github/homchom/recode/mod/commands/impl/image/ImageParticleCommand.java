package io.github.homchom.recode.mod.commands.impl.image;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.features.commands.image.ImageToParticle;
import io.github.homchom.recode.mod.features.commands.image.ParticleImage;
import io.github.homchom.recode.sys.hypercube.templates.Templates;
import io.github.homchom.recode.sys.renderer.ToasterUtil;
import io.github.homchom.recode.sys.util.TemplateConstants;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.world.item.Items;

import java.io.IOException;
import java.nio.file.Path;

public class ImageParticleCommand extends AbstractImageCommand {
    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("imageparticle")
                .then(ArgBuilder.literal("load").then(fileArgument(this::execute)))
                .then(ArgBuilder.literal("printer")
                        .executes(ctx -> {
                            Templates.giveInternalTemplate(
                                    Items.GOLD_BLOCK,
                                    "§b§lFunction §3» §bRecode §6Particle Image Printer",
                                    "Recode",
                                    TemplateConstants.IMAGE_PARTICLE_PRINTER
                            );
                            return 1;
                        })
                )
        );
    }

    public int execute(Path path) {
        String fileName = path.getFileName().toString();

        ParticleImage image = null;
        try {
            image = ImageToParticle.convert(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Templates.giveUserTemplate(
                Items.REDSTONE_ORE,
                fileName,
                convertToTemplate(image, fileName)
        );

        ToasterUtil.sendToaster("Image Loaded!", fileName, SystemToast.SystemToastId.NARRATOR_TOGGLE);

        return 0;
    }

    @Override
    public String getDescription() {
        return "[blue]/imageparticle load <file>[reset]\n"
            + "[blue]/imageparticle printer[reset]\n"
            + "\n"
            + "Generates a code template that displays images using colored particles.\n"
            + "Put the image file you want to convert in [green].minecraft/recode/Images[reset] folder.\n"
            + "Maximum image size is [yellow]40x40[reset].\n"
            + "Use [yellow]/imageparticle printer[reset] to grab the Image Printer which displays the image.";
    }

    @Override
    public String getName() {
        return "/imageparticle";
    }

    private String convertToTemplate(ParticleImage image, String name) {
        String[] data = image.getData();
        int width = image.getWidth();
        int height = image.getHeight();
        StringBuilder code = new StringBuilder();
        StringBuilder currentBlock = new StringBuilder();
        String codeBlockType = "CreateList";

        System.out.println("Image size: " + data.length);
        System.out.println("Image width: " + width);
        System.out.println("Image height: " + height);

        code.append(String.format("{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"%s\"}", name));

        int slot = 1;
        for (String s : data) {
            if (slot > 26) {
                code.append(String.format(",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"imageData\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"%s\"}", currentBlock, codeBlockType));
                currentBlock.delete(0, currentBlock.length());
                codeBlockType = "AppendValue";
                slot = 1;
            }
            currentBlock.append(String.format(",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":%d}", s, slot));
            slot++;
        }
        code.append(String.format(",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"imageData\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"%s\"}", currentBlock, codeBlockType));

        code.append(String.format(",{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"imageSize\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":1},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":2}]},\"action\":\"CreateList\"}", width, height));

        return "{\"blocks\":[" + code + "]}";
    }
}
