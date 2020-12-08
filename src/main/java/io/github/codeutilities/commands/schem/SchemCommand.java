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
