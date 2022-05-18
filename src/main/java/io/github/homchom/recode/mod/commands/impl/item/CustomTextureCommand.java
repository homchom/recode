package io.github.homchom.recode.mod.commands.impl.item;

import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.github.homchom.recode.mod.commands.arguments.ArgBuilder.argument;
import static io.github.homchom.recode.mod.commands.arguments.ArgBuilder.literal;

public class CustomTextureCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        registerImg("texture", mc, cd,16*4,16*4);
        registerImg("armor", mc, cd,64*4,32*4);
        cd.register(literal("customtexture")
            .then(literal("type")
                .then(literal("model")
                    .executes(ctx -> {
                        if (!isCreative(mc)) {
                            return -1;
                        }
                        Recode.EXECUTOR.submit(() -> {
                            try {
                                FileDialog fd = new FileDialog((Dialog) null, "Choose a model file", FileDialog.LOAD);
                                fd.setVisible(true);
                                File[] f = fd.getFiles();
                                if (f.length == 0) {
                                    ChatUtil.sendMessage("Aborted.", ChatType.FAIL);
                                    return;
                                }
                                String json = FileUtils.readFileToString(f[0], StandardCharsets.UTF_8);
                                json = JsonParser.parseString(json).toString();//syntax check & remove intends

                                CompoundTag tag = getTags(mc);
                                tag.put("model", StringTag.valueOf(json));
                                setTags(mc, tag);
                            } catch (Exception err) {
                                err.printStackTrace();
                                ChatUtil.sendMessage("Unexpected Error.", ChatType.FAIL);
                            }
                        });
                        return 1;
                    })
                )
                .then(literal("item")
                    .executes(ctx -> {
                        if (!isCreative(mc)) {
                            return -1;
                        }
                        CompoundTag tag = getTags(mc);
                        tag.remove("model");
                        setTags(mc, tag);
                        return 1;
                    })
                    .then(literal("default")
                        .executes(ctx -> {
                            if (!isCreative(mc)) {
                                return -1;
                            }
                            CompoundTag tag = getTags(mc);
                            tag.remove("weapon");
                            setTags(mc, tag);
                            return 1;
                        })
                    )
                    .then(literal("weapon")
                        .executes(ctx -> {
                            if (!isCreative(mc)) {
                                return -1;
                            }
                            CompoundTag tag = getTags(mc);
                            tag.putBoolean("weapon", true);
                            setTags(mc, tag);
                            return 1;
                        })
                    )
                )
            )
        );
    }

    private void registerImg(String texture, Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, int maxwidth, int maxheight) {
        cd.register(literal("customtexture").then(literal(texture)
                .then(literal("url")
                    .then(argument("url", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            if (!isCreative(mc)) {
                                return -1;
                            }
                            CompoundTag t = getTags(mc);

                            Recode.EXECUTOR.submit(() -> {
                                try {
                                    BufferedImage img = ImageIO.read(new URL(ctx.getArgument("url", String.class)));

                                    if (img.getWidth() * img.getHeight() > maxwidth * maxheight) {
                                        ChatUtil.sendMessage("Image is too large! (" + img.getWidth() * img.getHeight() + ">" + (maxwidth * maxheight) + "px)", ChatType.FAIL);
                                        return;
                                    }

                                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                                    ImageIO.write(img, "png", os);

                                    t.putString(texture, Base64.getEncoder().encodeToString(os.toByteArray()));
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
                        if (!isCreative(mc)) {
                            return -1;
                        }
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

                            t.putString(texture, Base64.getEncoder().encodeToString(os.toByteArray()));
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
                        if (!isCreative(mc)) {
                            return -1;
                        }
                        Recode.EXECUTOR.submit(() -> {
                            try {
                                CompoundTag t = getTags(mc);

                                FileDialog fd = new FileDialog((Dialog) null, "Choose a model file", FileDialog.LOAD);
                                fd.setVisible(true);
                                File[] f = fd.getFiles();
                                if (f.length == 0) {
                                    ChatUtil.sendMessage("Aborted.", ChatType.FAIL);
                                    return;
                                }

                                BufferedImage img = ImageIO.read(f[0]);

                                if (img.getWidth() * img.getHeight() > 64 * 64) {
                                    ChatUtil.sendMessage("Image is too large! (" + img.getWidth() * img.getHeight() + ">" + (64 * 64) + "px)", ChatType.FAIL);
                                    return;
                                }

                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                ImageIO.write(img, "png", os);

                                t.putString(texture, Base64.getEncoder().encodeToString(os.toByteArray()));
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
        );
    }


    private CompoundTag getTags(Minecraft mc) {
        return mc.player.getMainHandItem().getOrCreateTagElement("RecodeTextureData");
    }

    private void setTags(Minecraft mc, CompoundTag tag) {
        ItemStack item = mc.player.getMainHandItem();
        item.addTagElement("RecodeTextureData", tag);
        mc.gameMode.handleCreativeModeItemAdd(item, mc.player.getInventory().selected + 36);
        if (!Config.getBoolean("betaItemTextures")) {
            ChatUtil.sendMessage("Notice: You currently have this feature disabled in the config!", ChatType.INFO_YELLOW);
        }
    }

    @Override
    public String getDescription() {
        return "[blue]/customtexture[reset]\n"
            + "\n"
            + "A complex command for giving items a custom appearance.\n"
            + "For more info ask in the CodeUtilities Discord";
//        return "[blue]/customtexture[reset]\n"
//            + "\n"
//            + "Can be used for modifying the texture & model of an item which will be visible to all other CodeUtilities users.\n"
//            + "\n"
//            + "[yellow]/customtexture texture...[reset] Sets the texture of the item to\n"
//            + "[yellow]...url <url> [reset]an img from the web..\n"
//            + "[yellow]...file [reset]a local file.\n"
//            + "[yellow]...clipboard [reset]your clipboard.\n"
//            + "\n"
//            + "[yellow]/customtexture type...[reset] Sets the render mode of the item to\n"
//            + "[yellow]...model[reset] a model json file.\n"
//            + "[yellow]...item [default/weapon][reset] a flat item.\n"
//            + "The difference between weapon&default is the way its being held.";
    }

    @Override
    public String getName() {
        return "/customtexture";
    }
}
