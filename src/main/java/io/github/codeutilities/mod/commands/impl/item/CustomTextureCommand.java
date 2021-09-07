package io.github.codeutilities.mod.commands.impl.item;

import static io.github.codeutilities.mod.commands.arguments.ArgBuilder.argument;
import static io.github.codeutilities.mod.commands.arguments.ArgBuilder.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.imageio.ImageIO;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import org.apache.commons.io.FileUtils;

public class CustomTextureCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(literal("customtexture")
            .then(literal("texture")
                .then(literal("url")
                    .then(argument("url", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            if (!isCreative(mc)) return -1;
                            CompoundTag t = getTags(mc);

                            CodeUtilities.EXECUTOR.submit(() -> {
                                try {
                                    BufferedImage img = ImageIO.read(new URL(ctx.getArgument("url", String.class)));

                                    if (img.getWidth() * img.getHeight() > 64 * 64) {
                                        ChatUtil.sendMessage("Image is too large! (" + img.getWidth() * img.getHeight() + ">" + (64 * 64) + "px)", ChatType.FAIL);
                                        return;
                                    }

                                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                                    ImageIO.write(img, "png", os);

                                    t.putString("texture", Base64.getEncoder().encodeToString(os.toByteArray()));
                                    setTags(mc, t);
                                } catch (Exception e) {
                                    ChatUtil.sendMessage("Failed Loading image!", ChatType.FAIL);
                                    e.printStackTrace();
                                }
                            });
                            return 1;
                        })
                    )
                )
                .then(literal("clipboard")
                    .executes(ctx -> {
                        if (!isCreative(mc)) return -1;
                        try {
                            CompoundTag t = getTags(mc);
                            Transferable content = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                            if (content == null) {
                                ChatUtil.sendMessage("Your clipboard is empty!", ChatType.FAIL);
                                return -1;
                            }
                            if (!content.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                                ChatUtil.sendMessage("Your clipboard doesnt contain an image!", ChatType.FAIL);
                                return -1;
                            }
                            BufferedImage img = (BufferedImage) content.getTransferData(DataFlavor.imageFlavor);

                            if (img.getWidth() * img.getHeight() > 64 * 64) {
                                ChatUtil.sendMessage("Image is too large! (" + img.getWidth() * img.getHeight() + ">" + (64 * 64) + "px)", ChatType.FAIL);
                                return -1;
                            }

                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            ImageIO.write(img, "png", os);

                            t.putString("texture", Base64.getEncoder().encodeToString(os.toByteArray()));
                            setTags(mc, t);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ChatUtil.sendMessage("Unexpected error!", ChatType.FAIL);
                        }
                        return 1;
                    })
                )
                .then(literal("file")
                    .executes(ctx -> {
                        if (!isCreative(mc)) return -1;
                        CodeUtilities.EXECUTOR.submit(() -> {
                            try {
                                CompoundTag t = getTags(mc);

                                FileDialog fd = new FileDialog((Dialog) null, "Choose a model file", FileDialog.LOAD);
                                fd.setVisible(true);
                                File[] f = fd.getFiles();
                                if (f.length == 0) {
                                    ChatUtil.sendMessage("Aborted.",ChatType.FAIL);
                                    return;
                                }

                                BufferedImage img = ImageIO.read(f[0]);

                                if (img.getWidth() * img.getHeight() > 64 * 64) {
                                    ChatUtil.sendMessage("Image is too large! (" + img.getWidth() * img.getHeight() + ">" + (64 * 64) + "px)", ChatType.FAIL);
                                    return;
                                }

                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                ImageIO.write(img, "png", os);

                                t.putString("texture", Base64.getEncoder().encodeToString(os.toByteArray()));
                                setTags(mc, t);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ChatUtil.sendMessage("Unexpected error!", ChatType.FAIL);
                            }
                        });
                        return 1;
                    })
                )
            )
            .then(literal("type")
                .then(literal("model")
                    .executes(ctx -> {
                        if (!isCreative(mc)) return -1;
                        CodeUtilities.EXECUTOR.submit(() -> {
                            try {
                                FileDialog fd = new FileDialog((Dialog) null, "Choose a model file", FileDialog.LOAD);
                                fd.setVisible(true);
                                File[] f = fd.getFiles();
                                if (f.length == 0) {
                                    ChatUtil.sendMessage("Aborted.",ChatType.FAIL);
                                    return;
                                }
                                String json = FileUtils.readFileToString(f[0], StandardCharsets.UTF_8);
                                json = CodeUtilities.JSON_PARSER.parse(json).toString();//syntax check & remove intends

                                CompoundTag tag = getTags(mc);
                                tag.put("model", StringTag.of(json));
                                setTags(mc,tag);
                            } catch (Exception err) {
                                err.printStackTrace();
                                ChatUtil.sendMessage("Unexpected Error.",ChatType.FAIL);
                            }
                        });
                        return 1;
                    })
                )
                .then(literal("item")
                    .executes(ctx -> {
                        if (!isCreative(mc)) return -1;
                        CompoundTag tag = getTags(mc);
                        tag.remove("model");
                        setTags(mc,tag);
                        return 1;
                    })
                    .then(literal("default")
                        .executes(ctx -> {
                            if (!isCreative(mc)) return -1;
                            CompoundTag tag = getTags(mc);
                            tag.remove("weapon");
                            setTags(mc,tag);
                            return 1;
                        })
                    )
                    .then(literal("weapon")
                        .executes(ctx -> {
                            if (!isCreative(mc)) return -1;
                            CompoundTag tag = getTags(mc);
                            tag.putBoolean("weapon",true);
                            setTags(mc,tag);
                            return 1;
                        })
                    )
                )
            )
        );
    }

    private CompoundTag getTags(MinecraftClient mc) {
        return mc.player.getMainHandStack().getOrCreateSubTag("CodeutilitiesTextureData");
    }

    private void setTags(MinecraftClient mc, CompoundTag tag) {
        ItemStack item = mc.player.getMainHandStack();
        item.putSubTag("CodeutilitiesTextureData", tag);
        mc.interactionManager.clickCreativeStack(item, mc.player.inventory.selectedSlot + 36);
        if (!Config.getBoolean("betaItemTextures")) {
            ChatUtil.sendMessage("Notice: You currently have this feature disabled in the config!", ChatType.INFO_YELLOW);
        }
    }

    @Override
    public String getDescription() {
        return "[blue]/customtexture[reset]\n"
            + "\n"
            + "Can be used for modifying the texture & model of an item which will be visible to all other CodeUtilities users.\n"
            + "\n"
            + "[yellow]/customtexture texture...[reset] Sets the texture of the item to\n"
            + "[yellow]...url <url> [reset]an img from the web..\n"
            + "[yellow]...file [reset]a local file.\n"
            + "[yellow]...clipboard [reset]your clipboard.\n"
            + "\n"
            + "[yellow]/customtexture type...[reset] Sets the render mode of the item to\n"
            + "[yellow]...model[reset] a model json file.\n"
            + "[yellow]...item [default/weapon][reset] a flat item.\n"
            + "The difference between weapon&default is the way its being held.";
    }

    @Override
    public String getName() {
        return "/customtexture";
    }
}
