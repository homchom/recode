package io.github.codeutilities.commands.schem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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
           literal.then(ArgumentBuilders.argument("filepath", StringArgumentType.greedyString())
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
                                    //String template = Schematica.parse(finalTarget);
                                    //System.out.println(template);
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
                }))
                .then(ArgBuilder.literal("loader")
                        .executes(ctx -> {
                            if (mc.player.isCreative()) {
                                ItemStack stack = new ItemStack(Items.STICKY_PISTON);
                                String templateData = "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";
                                TemplateUtils.applyRawTemplateNBT(stack, "Schematic Loader", "CodeUtilities", templateData);
                                stack.setCustomName(new LiteralText("§b§lFunction §3» Code§bUtilities§d Schematic Loader"));
                                ChatUtil.sendMessage("You received the §dSchematic Loader§b! Place it down in your codespace and open the chest to get functions!",
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
