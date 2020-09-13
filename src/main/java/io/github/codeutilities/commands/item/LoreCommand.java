package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.*;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.text.Text.Serializer;

import java.util.Objects;

public class LoreCommand extends Command {

    private int add(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
        if (!ModConfig.getConfig().improvedLoreCmd) {
            return 0;
        }
        if (mc.player.isCreative()) {
            ItemStack item = mc.player.getMainHandStack();
            if (item.getItem() != Items.AIR) {
                CompoundTag nbt = item.getOrCreateTag();
                CompoundTag display = nbt.getCompound("display");
                ListTag lore = display.getList("Lore", 8);
                lore.add(StringTag.of(StringTag.escape(ctx.getArgument("lore", String.class))
                        .replaceAll("&", "§")));
                display.put("Lore", lore);
                nbt.put("display", display);
                item.setTag(nbt);
                mc.interactionManager
                        .clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
                ChatUtil.sendMessage(
                        "§aAdded §5§o" + ctx.getArgument("lore", String.class).replaceAll("&", "§")
                                + "§a!");
                showLore(lore);
                return 1;
            } else {
                ChatUtil.sendMessage("You need to hold an item in your main hand!", ChatType.FAIL);
            }
        } else {
            ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
        }
        return -1;
    }

    private int clear(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
        if (!ModConfig.getConfig().improvedLoreCmd) {
            return 0;
        }
        if (mc.player.isCreative()) {
            ItemStack item = mc.player.getMainHandStack();
            if (item.getItem() != Items.AIR) {
                CompoundTag nbt = item.getOrCreateTag();
                CompoundTag display = nbt.getCompound("display");
                ListTag lore = display.getList("Lore", 8);
                lore.clear();
                display.put("Lore", lore);
                nbt.put("display", display);
                item.setTag(nbt);
                mc.interactionManager
                        .clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
                ChatUtil.sendMessage("Cleared!", ChatType.SUCCESS);
                return 1;
            } else {
                ChatUtil.sendMessage("You need to hold an item in your main hand!", ChatType.FAIL);
            }

        } else {
            ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
        }
        return -1;
    }

    private int insert(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
        if (!ModConfig.getConfig().improvedLoreCmd) {
            return 0;
        }
        if (mc.player.isCreative()) {
            ItemStack item = mc.player.getMainHandStack();
            if (item.getItem() != Items.AIR) {
                CompoundTag nbt = item.getOrCreateTag();
                CompoundTag display = nbt.getCompound("display");
                ListTag lore = display.getList("Lore", 8);
                lore.add(ctx.getArgument("line", Integer.class) - 1,
                        StringTag.of(StringTag.escape(ctx.getArgument("lore", String.class))));
                display.put("Lore", lore);
                nbt.put("display", display);
                item.setTag(nbt);
                mc.interactionManager
                        .clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
                ChatUtil.sendMessage(
                        "Inserted §5§o" + ctx.getArgument("lore", String.class).replaceAll("&", "§")
                                + " §aat "
                                + ctx.getArgument("line", Integer.class) + "§a!", ChatType.SUCCESS);
                showLore(lore);
                return 1;
            } else {
                ChatUtil.sendMessage("You need to hold an item in your main hand!", ChatType.FAIL);
            }
        } else {
            ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
        }
        return -1;
    }

    private int remove(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
        if (!ModConfig.getConfig().improvedLoreCmd) {
            return 0;
        }
        if (mc.player.isCreative()) {
            ItemStack item = mc.player.getMainHandStack();
            if (item.getItem() != Items.AIR) {
                CompoundTag nbt = item.getOrCreateTag();
                CompoundTag display = nbt.getCompound("display");
                ListTag lore = display.getList("Lore", 8);
                lore.remove(ctx.getArgument("line", Integer.class) - 1);
                display.put("Lore", lore);
                nbt.put("display", display);
                item.setTag(nbt);
                mc.interactionManager
                        .clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
                ChatUtil.sendMessage("Removed line " + ctx.getArgument("line", Integer.class) + "§a!",
                        ChatType.SUCCESS);
                showLore(lore);
                return 1;
            } else {
                ChatUtil.sendMessage("You need to hold an item in your main hand!", ChatType.FAIL);
            }
        } else {
            ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
        }
        return -1;
    }

    private int set(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
        if (!ModConfig.getConfig().improvedLoreCmd) {
            return 0;
        }
        if (mc.player.isCreative()) {
            ItemStack item = mc.player.getMainHandStack();
            if (item.getItem() != Items.AIR) {
                CompoundTag nbt = item.getOrCreateTag();
                CompoundTag display = nbt.getCompound("display");
                ListTag lore = display.getList("Lore", 8);
                lore.set(ctx.getArgument("line", Integer.class) - 1,
                        StringTag.of(StringTag.escape(ctx.getArgument("lore", String.class))
                                .replaceAll("&", "§")));
                display.put("Lore", lore);
                nbt.put("display", display);
                item.setTag(nbt);
                mc.interactionManager
                        .clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
                ChatUtil.sendMessage(
                        "Changed line " + ctx.getArgument("line", Integer.class) + " to §5§o" + ctx
                                .getArgument("lore", String.class).replaceAll("&", "§") + "§a!",
                        ChatType.SUCCESS);
                showLore(lore);
                return 1;
            } else {
                ChatUtil.sendMessage("You need to hold an item in your main hand!", ChatType.FAIL);
            }
        } else {
            ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
        }
        return -1;
    }

    private void showLore(ListTag lore) {
        int index = 0;
        ChatUtil.sendMessage("New lore text:", ChatType.INFO_YELLOW);
        for (Tag line : lore) {
            String text = Objects.requireNonNull(Serializer.fromJson(line.asString())).asString();
            index++;
            text = "§b" + index + ". §5§o" + text;
            ChatUtil.sendMessage(text);
        }
    }


    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("lore")
                .then(ArgBuilder.literal("add")
                        .then(ArgBuilder.argument("lore", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    return add(MinecraftClient.getInstance(), ctx);
                                })
                        )
                )
                .then(ArgBuilder.literal("clear")
                        .executes(ctx -> {
                            return clear(MinecraftClient.getInstance(), ctx);
                        })
                )
                .then(ArgBuilder.literal("remove")
                        .then(ArgBuilder.argument("line", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    return remove(MinecraftClient.getInstance(), ctx);
                                })
                        )
                )
                .then(ArgBuilder.literal("set")
                        .then(ArgBuilder.argument("line", IntegerArgumentType.integer(1))
                                .then(ArgBuilder.argument("lore", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            return set(MinecraftClient.getInstance(), ctx);
                                        })
                                )
                        )
                )
                .then(ArgBuilder.literal("insert")
                        .then(ArgBuilder.argument("line", IntegerArgumentType.integer(1))
                                .then(ArgBuilder.argument("lore", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            return insert(MinecraftClient.getInstance(), ctx);
                                        })
                                )
                        )
                )
        );
    }
}
