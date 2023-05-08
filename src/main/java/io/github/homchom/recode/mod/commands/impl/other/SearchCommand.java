package io.github.homchom.recode.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.commands.arguments.types.ChoiceArgumentType;
import io.github.homchom.recode.mod.features.commands.CodeSearcher;
import io.github.homchom.recode.server.DF;
import io.github.homchom.recode.server.PlotMode;
import io.github.homchom.recode.sys.hypercube.codeaction.Action;
import io.github.homchom.recode.sys.hypercube.codeaction.ActionDump;
import io.github.homchom.recode.sys.hypercube.codeaction.Types;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.util.ItemUtil;
import io.github.homchom.recode.sys.util.TextUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class SearchCommand extends Command {
	@Override
	public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
		String[] codeBlocks = new String[CodeSearcher.SearchType.values().length];
		int i = 0;
		for (CodeSearcher.SearchType searchType : CodeSearcher.SearchType.values()) {
			codeBlocks[i] = searchType.name().toLowerCase();
			i++;
		}

		cd.register(ArgBuilder.literal("search")
				.executes(ctx -> { // /search clear shortcut
					ChatUtil.sendMessage(Component.translatable("recode.template_search.cleared"), ChatType.SUCCESS);
					CodeSearcher.clearSearch();
					return 1;
				})
				.then(ArgBuilder.literal("clear")
						.executes(ctx -> { // /search clear
							ChatUtil.sendMessage(Component.translatable("recode.template_search.cleared"), ChatType.SUCCESS);
							CodeSearcher.clearSearch();
							return 1;
						}))
				.then(ArgBuilder.argument("action", StringArgumentType.greedyString())
						.executes(ctx -> { // /search <action..>
							try {
								String query = ctx.getArgument("action", String.class);
								ArrayList<Action> actions = ActionDump.getActions(query);
								mc.player.displayClientMessage(TextUtil.colorCodesToTextComponent("§x§0§0§b§5§f§c✎ §x§0§0§e§0§b§0" + new TranslatableContents("recode.template_search.begin_search", null, new String[]{"§x§0§0§f§8§f§c" + query + "§x§0§0§e§0§b§0"})
										.resolve(mc.player.createCommandSourceStack(), mc.player, 1).getString()).copy(), false);
								mc.player.displayClientMessage(Component.literal(""), false);
								for (Action action : actions) {
									try {
										mc.player.displayClientMessage(createMessage(action.getCodeBlock().getIdentifier(), action.getName(), action.getCodeBlock().getItem().toItemStack(), action.getIcon().toItemStack()), false);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								mc.player.displayClientMessage(createMessage("func", query, ActionDump.getCodeBlock("function").get(0).getItem().toItemStack(), ""), false);
								mc.player.displayClientMessage(createMessage("process", query, ActionDump.getCodeBlock("process").get(0).getItem().toItemStack(), ""), false);
							} catch (Exception e) {
								e.printStackTrace();

								ChatUtil.sendMessage(new TranslatableContents("recode.template_search.invalid", null, new String[]{ctx.getArgument("action", String.class)})
										.resolve(mc.player.createCommandSourceStack(), mc.player, 1), ChatType.FAIL);
							}
							return 1;
						})));

		cd.register(ArgBuilder.literal("exactsearch")
				.then(ArgBuilder.argument("codeblock", ChoiceArgumentType.choice(codeBlocks))
						.then(ArgBuilder.argument("action", StringArgumentType.greedyString())
								.executes(ctx -> { // /exactsearch <codeblock> <action..> (mostly used for the clickevent)
									try {
										String actionArgument = ctx.getArgument("action", String.class);
										String codeblockArgument = ctx.getArgument("codeblock", String.class);
										CodeSearcher.SearchType searchType = CodeSearcher.SearchType.valueOf(codeblockArgument.toUpperCase());
										if (DF.isInMode(DF.getCurrentDFState(), PlotMode.Dev)) {
											CodeSearcher.beginSearch(searchType, actionArgument);
										} else {
											ChatUtil.sendMessage(new TranslatableContents("recode.command.require_dev_mode", null, new String[]{ctx.getArgument("action", String.class)})
													.resolve(mc.player.createCommandSourceStack(), mc.player, 1), ChatType.FAIL);
										}

									} catch (Exception e) {
										e.printStackTrace();

										ChatUtil.sendMessage(new TranslatableContents("recode.template_search.invalid", null, TranslatableContents.NO_ARGS)
												.resolve(mc.player.createCommandSourceStack(), mc.player, 1), ChatType.FAIL);
									}
									return 1;

								})
						)));

	}

	@Override
	public String getDescription() {
		return "[blue]/search <query>[reset]\n"
				+ "\n"
				+ "Searches specified Code Block. The block found with the search will glow white.";
	}

	@Override
	public String getName() {
		return "/search";
	}

	private MutableComponent createMessage(String codeblockid, String actionname, Object codeblock, Object action) {

		String clickHere = "§x§f§c§6§6§0§3⏩ §x§f§c§d§3§0§3Click to Search §x§f§c§6§6§0§3⏪";
		Component clickHereText = TextUtil.colorCodesToTextComponent(clickHere);

		ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exactsearch " + codeblockid + " " + actionname);

		MutableComponent actionmsg = TextUtil.colorCodesToTextComponent("§x§f§c§f§f§5§9" + actionname).copy();
		actionmsg.setStyle(actionmsg.getStyle()
				.withClickEvent(clickEvent)
				.withHoverEvent(createHoverEvent(action, clickHere)));

		Types block = ActionDump.valueOf(codeblockid.toUpperCase());
		MutableComponent blockmsg = TextUtil.colorCodesToTextComponent(block.getColor() + block.getName()).copy();
		blockmsg.setStyle(blockmsg.getStyle()
				.withClickEvent(clickEvent)
				.withHoverEvent(createHoverEvent(codeblock, clickHere)));

		MutableComponent startarrow = TextUtil.colorCodesToTextComponent(" §x§f§c§4§7§0§0⏵ ").copy();
		startarrow.setStyle(startarrow.getStyle()
				.withClickEvent(clickEvent)
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, clickHereText)));

		MutableComponent middlearrow = TextUtil.colorCodesToTextComponent(" §x§f§c§8§b§0§0⇒ ").copy();
		middlearrow.setStyle(middlearrow.getStyle()
				.withClickEvent(clickEvent)
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, clickHereText)));

		return startarrow.append(blockmsg).append(middlearrow).append(actionmsg);
	}

	private HoverEvent createHoverEvent(Object hover, String clickHere) {
		if (hover instanceof String) {
			return new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtil.colorCodesToTextComponent(clickHere));
		}
		if (hover instanceof ItemStack) {
			return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(ItemUtil.addLore((ItemStack) hover, new String[]{TextUtil.toTextString(""), TextUtil.toTextString(clickHere)})));
		}
		return null;
	}
}
