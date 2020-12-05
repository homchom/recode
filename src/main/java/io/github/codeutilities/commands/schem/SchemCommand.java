package io.github.codeutilities.commands.schem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.schem.Litematica;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.ItemUtil;
import io.github.codeutilities.util.TemplateUtils;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

import java.io.File;

public class SchemCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(subcommand(mc, ArgBuilder.literal("schem")));
        cd.register(subcommand(mc, ArgBuilder.literal("litematic")));
        cd.register(subcommand(mc, ArgBuilder.literal("schematic")));
    }

    public LiteralArgumentBuilder<CottonClientCommandSource> subcommand(MinecraftClient mc, LiteralArgumentBuilder<CottonClientCommandSource> literal){
            literal.then(ArgBuilder.literal("load")
                    .then(ArgumentBuilders.argument("filepath", StringArgumentType.greedyString())
                .executes(ctx -> {
                    if (mc.player.isCreative()){
                        String arg = StringArgumentType.getString(ctx, "filepath");
                    File target = new File("schematics/" + arg);
                    String format = "";

                    if (!target.getName().endsWith(".litematic") && !target.getName().endsWith(".schematic") && !target.getName().endsWith(".schem")) {
                        if (new File("schematics/" + arg + ".litematic").exists()) {
                            target = new File("schematics/" + arg + ".litematic");
                            format = "litematic";
                        }
                        if (new File("schematics/" + arg + ".schematic").exists()) {
                            target = new File("schematics/" + arg + ".schematic");
                            format = "schematic";
                        }
                        if (new File("schematics/" + arg + ".schem").exists()) {
                            target = new File("schematics/" + arg + ".schem");
                            format = "schem";
                        }
                    } else {
                        if (target.getName().endsWith(".litematic")) {
                            format = "litematic";
                        }
                        if (target.getName().endsWith(".schematic")) {
                            format = "schematic";
                        }
                        if (target.getName().endsWith(".schem")) {
                            format = "schem";
                        }
                    }

                    if (target.exists()) {
                        System.out.println(target.exists());
                        if (format == "litematic") {
                            System.out.println("its a litematic");
                            File finalTarget = target;
                            new Thread(() -> {
                                try {
                                    String template = Litematica.parse(finalTarget);
                                    System.out.println(template);
                                } catch (Exception e) {
                                    ChatUtil.sendMessage("An error occurred while executing this command.", ChatType.FAIL);
                                    e.printStackTrace();
                                }
                            }).start();

                        } else if (format == "schematic") {
                            System.out.println("its a schematic");
                            File finalTarget = target;
                            new Thread(() -> {
                                try {
                                } catch (Exception e) {
                                    ChatUtil.sendMessage("An error occurred while executing this command.", ChatType.FAIL);
                                    e.printStackTrace();
                                }
                            }).start();
                        } else {
                            ChatUtil.sendMessage("The file has to be a litematic, schematic, schem or vanilla nbt format.", ChatType.FAIL);
                        }
                    } else {
                        ChatUtil.sendMessage("The file §6" + arg + "§c was not found.", ChatType.FAIL);
                    }
                }else{
                    ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
                }
                    return 1;
                })))
                .then(ArgBuilder.literal("builder")
                        .executes(ctx -> {
                            if (mc.player.isCreative()) {
                                ItemStack stack = new ItemStack(Items.STICKY_PISTON);
                                String templateData = "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";
                                TemplateUtils.applyRawTemplateNBT(stack, "Schem2DF Builder", "CodeUtilities", templateData);
                                stack.setCustomName(new LiteralText("§b§lFunction §3» Code§bUtilities§7-§dSchem2DF Builder"));
                                ChatUtil.sendMessage("You received the §dSchem2DF Builder§b! Place it down in your codespace and open the chest to get functions!",
                                        ChatType.INFO_BLUE);
                                ItemUtil.giveCreativeItem(stack);
                            } else {
                                ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
                            }
                            return 1;
                        })
                )
                    .then(ArgBuilder.literal("saver")
                            .executes(ctx -> {
                                if (mc.player.isCreative()) {
                                    ItemStack stack = new ItemStack(Items.PISTON);
                                    String templateData = "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";
                                    TemplateUtils.applyRawTemplateNBT(stack, "Schem2DF Saver", "CodeUtilities", templateData);
                                    stack.setCustomName(new LiteralText("§b§lFunction §3» Code§bUtilities§7-§dSchem2DF Saver"));
                                    ChatUtil.sendMessage("You received the §dSchem2DF Saver§b! Place it down in your codespace and open the chest to get functions!",
                                            ChatType.INFO_BLUE);
                                    ItemUtil.giveCreativeItem(stack);
                                } else {
                                    ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
                                }
                                return 1;
                            })
                    );
        return literal;
    }

}
