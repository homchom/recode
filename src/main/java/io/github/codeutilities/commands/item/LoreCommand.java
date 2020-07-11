package io.github.codeutilities.commands.item;

import java.util.Objects;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text.Serializer;

public class LoreCommand {

	  public static int add(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
		if (!ModConfig.getConfig().improvedLoreCmd) return 0;
	    try {
	      assert mc.player != null;
	      if (mc.player.isCreative()) {
	        ItemStack item = mc.player.getMainHandStack();
	        CompoundTag nbt = item.getOrCreateTag();
	        CompoundTag display = nbt.getCompound("display");
	        ListTag lore = display.getList("Lore", 8);
	        lore.add(StringTag.of(StringTag.escape(ctx.getArgument("lore", String.class)).replaceAll("&", "§")));
	        display.put("Lore", lore);
	        nbt.put("display", display);
	        item.setTag(nbt);
	        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
	        CodeUtilities.chat(
	            "§aAdded §5§o" + ctx.getArgument("lore", String.class).replaceAll("&", "§") + "§e!");
	        showLore(lore);
	        return 1;
	      } else {
	        CodeUtilities.chat("§cYou need to be in creative for this command to work!");
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return -1;
	  }

	  public static int clear(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
		if (!ModConfig.getConfig().improvedLoreCmd) return 0;
	    try {
	      assert mc.player != null;
	      if (mc.player.isCreative()) {
	        ItemStack item = mc.player.getMainHandStack();
	        CompoundTag nbt = item.getOrCreateTag();
	        CompoundTag display = nbt.getCompound("display");
	        ListTag lore = display.getList("Lore", 8);
	        lore.clear();
	        display.put("Lore", lore);
	        nbt.put("display", display);
	        item.setTag(nbt);
	        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
	        CodeUtilities.chat("§aCleared!");
	        return 1;
	      } else {
	        CodeUtilities.chat("§cYou need to be in creative for this command to work!");
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return -1;
	  }

	  public static int insert(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
		if (!ModConfig.getConfig().improvedLoreCmd) return 0;
	    try {
	      assert mc.player != null;
	      if (mc.player.isCreative()) {
	        ItemStack item = mc.player.getMainHandStack();
	        CompoundTag nbt = item.getOrCreateTag();
	        CompoundTag display = nbt.getCompound("display");
	        ListTag lore = display.getList("Lore", 8);
	        lore.add(ctx.getArgument("line", Integer.class) - 1,
	            StringTag.of(StringTag.escape(ctx.getArgument("lore", String.class))));
	        display.put("Lore", lore);
	        nbt.put("display", display);
	        item.setTag(nbt);
	        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
	        CodeUtilities.chat(
	            "§aInserted §5§o" + ctx.getArgument("lore", String.class).replaceAll("&", "§") + " §eat "
	                + ctx.getArgument("line", Integer.class) + "!");
	        showLore(lore);
	        return 1;
	      } else {
	        CodeUtilities.chat("§cYou need to be in creative for this command to work!");
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return -1;
	  }

	  public static int remove(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
		if (!ModConfig.getConfig().improvedLoreCmd) return 0;
	    try {
	      assert mc.player != null;
	      if (mc.player.isCreative()) {
	        ItemStack item = mc.player.getMainHandStack();
	        CompoundTag nbt = item.getOrCreateTag();
	        CompoundTag display = nbt.getCompound("display");
	        ListTag lore = display.getList("Lore", 8);
	        lore.remove(ctx.getArgument("line", Integer.class) - 1);
	        display.put("Lore", lore);
	        nbt.put("display", display);
	        item.setTag(nbt);
	        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
	        CodeUtilities.chat("§aRemoved line " + ctx.getArgument("line", Integer.class) + "!");
	        showLore(lore);
	        return 1;
	      } else {
	        CodeUtilities.chat("§cYou need to be in creative for this command to work!");
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return -1;
	  }

	  public static int set(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
		if (!ModConfig.getConfig().improvedLoreCmd) return 0;
	    try {
	      assert mc.player != null;
	      if (mc.player.isCreative()) {
	        ItemStack item = mc.player.getMainHandStack();
	        CompoundTag nbt = item.getOrCreateTag();
	        CompoundTag display = nbt.getCompound("display");
	        ListTag lore = display.getList("Lore", 8);
	        lore.set(ctx.getArgument("line", Integer.class) - 1,
	            StringTag.of(StringTag.escape(ctx.getArgument("lore", String.class)).replaceAll("&", "§")));
	        display.put("Lore", lore);
	        nbt.put("display", display);
	        item.setTag(nbt);
	        mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.selectedSlot);
	        CodeUtilities.chat(
	            "§aChanged line " + ctx.getArgument("line", Integer.class) + " to §5§o" + ctx
	                .getArgument("lore", String.class).replaceAll("&", "§") + "§e!");
	        showLore(lore);
	        return 1;
	      } else {
	        CodeUtilities.chat("§cYou need to be in creative for this command to work!");
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return -1;
	  }

	  private static void showLore(ListTag lore) {
	    int index = 0;
	    CodeUtilities.chat("§eNew lore text:");
	    for (Tag line : lore) {
	      String text = Objects.requireNonNull(Serializer.fromJson(line.asString())).asString();
	      index++;
	      text = "§b" + index + ". §5§o" + text;
	      assert MinecraftClient.getInstance().player != null;
	      CodeUtilities.chat(text);
	    }
	  }

	  public static void register(CommandDispatcher<CottonClientCommandSource> cd) {
		    cd.register(ArgumentBuilders.literal("lore")
		        .then(ArgumentBuilders.literal("add")
		            .then(ArgumentBuilders.argument("lore", StringArgumentType.greedyString())
		                .executes(ctx -> {
		                  try {
		                    return LoreCommand.add(MinecraftClient.getInstance(), ctx);
		                  } catch (Exception e) {
		                    e.printStackTrace();
		                  }
		                  return 0;
		                })
		            )
		        )
		        .then(ArgumentBuilders.literal("clear")
		            .executes(ctx -> {
		              try {
		                return LoreCommand.clear(MinecraftClient.getInstance(), ctx);
		              } catch (Exception e) {
		                e.printStackTrace();
		              }
		              return 0;
		            })
		        )
		        .then(ArgumentBuilders.literal("remove")
		            .then(ArgumentBuilders.argument("line", IntegerArgumentType.integer(1))
		                .executes(ctx -> {
		                  try {
		                    return LoreCommand.remove(MinecraftClient.getInstance(), ctx);
		                  } catch (Exception e) {
		                    e.printStackTrace();
		                  }
		                  return 0;
		                })
		            )
		        )
		        .then(ArgumentBuilders.literal("set")
		            .then(ArgumentBuilders.argument("line", IntegerArgumentType.integer(1))
		                .then(ArgumentBuilders.argument("lore", StringArgumentType.greedyString())
		                    .executes(ctx -> {
		                      try {
		                        return LoreCommand.set(MinecraftClient.getInstance(), ctx);
		                      } catch (Exception e) {
		                        e.printStackTrace();
		                      }
		                      return 0;
		                    })
		                )
		            )
		        )
		        .then(ArgumentBuilders.literal("insert")
		            .then(ArgumentBuilders.argument("line", IntegerArgumentType.integer(1))
		                .then(ArgumentBuilders.argument("lore", StringArgumentType.greedyString())
		                    .executes(ctx -> {
		                      try {
		                        return LoreCommand.insert(MinecraftClient.getInstance(), ctx);
		                      } catch (Exception e) {
		                        e.printStackTrace();
		                      }
		                      return 0;
		                    })
		                )
		            )
		        )
		    );
		  }
	  
	}
