package io.github.codeutilities.mod.commands.impl.item;

import static io.github.codeutilities.mod.commands.arguments.ArgBuilder.argument;
import static io.github.codeutilities.mod.commands.arguments.ArgBuilder.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class CustomTextureCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(literal("customtexture")
            .then(literal("texture")
                .then(literal("url")
                    .then(argument("url", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            CompoundTag t = getTags(mc);

                            CodeUtilities.EXECUTOR.submit(() -> {
                                try {
                                    BufferedImage img = ImageIO.read(new URL(ctx.getArgument("url", String.class)));

                                    if (img.getWidth()*img.getHeight()>50*50) {
                                        ChatUtil.sendMessage("Image is too large! (" + img.getWidth()*img.getHeight() + ">2500px)",ChatType.FAIL);
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

                            if (img.getWidth()*img.getHeight()>10000) {
                                ChatUtil.sendMessage("Image is too large! (" + img.getWidth()*img.getHeight() + ">10000px)",ChatType.FAIL);
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
            )
            .then(literal("scale")
                .then(argument("scale", FloatArgumentType.floatArg())
                    .executes(ctx -> {
                        CompoundTag t = getTags(mc);
                        t.putFloat("scale", ctx.getArgument("scale", Float.class));
                        setTags(mc, t);
                        return 1;
                    })
                )
            )
            .then(literal("offset")
                .then(argument("x", FloatArgumentType.floatArg())
                    .then(argument("y", FloatArgumentType.floatArg())
                        .then(argument("z", FloatArgumentType.floatArg())
                            .executes(ctx -> {
                                CompoundTag t = getTags(mc);
                                t.putFloat("x", ctx.getArgument("x", Float.class));
                                t.putFloat("y", ctx.getArgument("y", Float.class));
                                t.putFloat("z", ctx.getArgument("z", Float.class));
                                setTags(mc, t);
                                return 1;
                            })
                        )
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
            ChatUtil.sendMessage("Notice: You currently have this feature disabled in the config!",ChatType.INFO_YELLOW);
        }
    }

    @Override
    public String getDescription() {
        return "[blue]/customtexture[reset]\n"
            + "\n"
            + "Can be used for modifying the texture of an item which will be visible to all other CodeUtilities users.";
    }

    @Override
    public String getName() {
        return "/customtexture";
    }
}
