package me.reasonless.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.commands.help.HelpCommand;
import me.reasonless.codeutilities.commands.image.ImageCommand;
import me.reasonless.codeutilities.commands.image.ImageLoadCommand;
import me.reasonless.codeutilities.commands.item.BreakableCommand;
import me.reasonless.codeutilities.commands.item.CanDestroyCommand;
import me.reasonless.codeutilities.commands.item.CanPlaceOnCommand;
import me.reasonless.codeutilities.commands.item.CustomheadCommand;
import me.reasonless.codeutilities.commands.item.GiveCommand;
import me.reasonless.codeutilities.commands.item.HideflagsCommand;
import me.reasonless.codeutilities.commands.item.ItemDataCommand;
import me.reasonless.codeutilities.commands.item.LoreCommand;
import me.reasonless.codeutilities.commands.item.ShowFlagsCommand;
import me.reasonless.codeutilities.commands.item.ShulkerCommand;
import me.reasonless.codeutilities.commands.misc.UnpackCommand;
import me.reasonless.codeutilities.commands.misc.WebViewCommand;
import me.reasonless.codeutilities.commands.nbs.NBSCommand;
import me.reasonless.codeutilities.commands.nbs.NBSLoadCommand;
import me.reasonless.codeutilities.commands.nbs.NBSPlayerCommand;
import me.reasonless.codeutilities.commands.util.AfkCommand;
import me.reasonless.codeutilities.commands.util.FriendCommand;
import me.reasonless.codeutilities.commands.util.PJoinCommand;
import me.reasonless.codeutilities.commands.util.PingCommand;
import me.reasonless.codeutilities.commands.util.RejoinCommand;
import me.reasonless.codeutilities.commands.util.UuidCommand;
import me.reasonless.codeutilities.objects.AutoCompleteArgType;
import me.reasonless.codeutilities.util.MinecraftColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.text.LiteralText;

public class Commands implements ClientCommandPlugin {
	MinecraftClient mc = MinecraftClient.getInstance();
	@Override
	public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {

		dispatcher.register(ArgumentBuilders.literal("nbs")
			.then(ArgumentBuilders.literal("player")
					.executes(ctx -> {
						try {
							return NBSPlayerCommand.execute(mc, ctx);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						return 0;
					}))
			.then(ArgumentBuilders.literal("load")
				.then(ArgumentBuilders.argument("location", StringArgumentType.greedyString())
						.executes(ctx -> {
							try {
								return NBSLoadCommand.execute(mc, ctx);
							} catch (Exception e) {
								mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_RED + " - " + MinecraftColors.RED + "There was an error loading this nbs file."), false);
								mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_RED + " - " + MinecraftColors.RED + "Maybe the nbs is made using an older Noteblock Studio Version."), false);
								mc.player.sendMessage(new LiteralText(MinecraftColors.GOLD + " - " + MinecraftColors.YELLOW + "The NBS function uses NBS File Format v4"), false);
								e.printStackTrace();
							}
							return 1;
						})))
			.executes(ctx -> NBSCommand.execute(mc, ctx)));

		dispatcher.register(ArgumentBuilders.literal("image")
				.then(ArgumentBuilders.literal("load")
						.then(ArgumentBuilders.argument("location", StringArgumentType.greedyString())
								.executes(ctx -> {
									try {
										return ImageLoadCommand.execute(mc, ctx);
									} catch (Exception e) {
										mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_RED + " - " + MinecraftColors.RED + "There was an error loading this image."), false);
										e.printStackTrace();
									}
									return 1;
								})))
				.executes(ctx -> ImageCommand.execute(mc, ctx)));

		dispatcher.register(ArgumentBuilders.literal("webview")
			.executes(ctx -> WebViewCommand.execute(mc, ctx)));
		dispatcher.register(ArgumentBuilders.literal("unpack")
				.executes(ctx -> UnpackCommand.execute(mc, ctx)));
		dispatcher.register(ArgumentBuilders.literal("codeutilities")
				.then(ArgumentBuilders.literal("music")
						.executes(ctx -> {
							return NBSCommand.execute(mc, ctx);
						}))
				.then(ArgumentBuilders.literal("image")
						.executes(ctx -> {
							return ImageCommand.execute(mc, ctx);
						}))
		.executes(ctx -> HelpCommand.execute(mc, ctx)));
		
		//blazingutilities
		
		if (!CodeUtilities.hasblazing) {
			give(dispatcher); //register the /give command
		    join(dispatcher); //register /join <player>
		    rejoin(dispatcher); //register the /rejoin command
		    ping(dispatcher); //register the /ping <proxy/server> command
		    shulker(dispatcher); //register the /shulker command
		    breakable(dispatcher); //register the /breakable <name> command
		    candestroy(dispatcher); //register the /candestroy <add/remove/clear> <block> command
		    canplaceon(dispatcher); //register the /canplaceon <add/remove/clear> <block> command
		    itemdata(dispatcher); //register the /itemdata command
		    hideflags(dispatcher); //registers the /hideflags <enchantments> <modifiers>... command
		    showflags(dispatcher); //registers the /showflags command
		    lore(dispatcher); //registers the /lore <add,clear,remove,insert,set> command
		    customhead(dispatcher); //registers the /customhead <value> command
		    afk(dispatcher); //registers the /afk <message> command
		    friend(dispatcher); //registers the /friend <add/remove/list> cmd
		    //autocomplete(dispatcher); //registers the autocompletion for /var
		    uuid(dispatcher); //registers the /uuid <player> command
		}
		
	}
	
	private void uuid(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("uuid")
	        .then(ArgumentBuilders.argument("player", EntityArgumentType.player())
	            .executes(ctx -> {
	                  try {
	                    return UuidCommand.run(MinecraftClient.getInstance(), ctx);
	                  } catch (Exception e) {
	                    e.printStackTrace();
	                  }
	                  return 0;
	                }
	            )
	        )
	    );
	  }

	  private void friend(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("friend")
	        .then(ArgumentBuilders.literal("add")
	            .then(ArgumentBuilders.argument("player", EntityArgumentType.player())
	                .executes(ctx -> {
	                  try {
	                    return FriendCommand.add(MinecraftClient.getInstance(), ctx);
	                  } catch (Exception e) {
	                    e.printStackTrace();
	                  }
	                  return 0;
	                })
	            )
	        )
	        .then(ArgumentBuilders.literal("remove")
	            .then(ArgumentBuilders.argument("player", EntityArgumentType.player())
	                .executes(ctx -> {
	                  try {
	                    return FriendCommand.remove(MinecraftClient.getInstance(), ctx);
	                  } catch (Exception e) {
	                    e.printStackTrace();
	                  }
	                  return 0;
	                })
	            )
	        )
	        .then(ArgumentBuilders.literal("list")
	            .executes(ctx -> {
	              try {
	                return FriendCommand.list(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            })
	        )
	    );
	  }

	  private void afk(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("afk")
	        .executes(ctx -> {
	          try {
	            return AfkCommand.run(MinecraftClient.getInstance(), ctx);
	          } catch (Exception e) {
	            e.printStackTrace();
	          }
	          return 0;
	        })
	        .then(ArgumentBuilders.argument("message", StringArgumentType.greedyString())
	            .executes(ctx -> {
	              try {
	                return AfkCommand.run(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            })
	        )
	    );
	  }

	  private void customhead(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("customhead")
	        .then(ArgumentBuilders.argument("value", StringArgumentType.greedyString())
	            .executes(ctx -> {
	              try {
	                return CustomheadCommand.run(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            })
	        )
	    );
	  }

	  private void lore(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("lore")
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

	  private void showflags(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("showflags")
	        .executes(ctx -> {
	          try {
	            return ShowFlagsCommand.run(MinecraftClient.getInstance(), ctx);
	          } catch (Exception e) {
	            e.printStackTrace();
	          }
	          return 0;
	        })
	    );
	  }

	  private void hideflags(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("hideflags")
	        .executes(ctx -> {
	          try {
	            return HideflagsCommand.run(MinecraftClient.getInstance(), ctx);
	          } catch (Exception e) {
	            e.printStackTrace();
	          }
	          return 0;
	        })
	        .then(ArgumentBuilders.argument("Enchantments", BoolArgumentType.bool())
	            .executes(ctx -> {
	              try {
	                return HideflagsCommand.run(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            }).then(ArgumentBuilders.argument("Modifiers", BoolArgumentType.bool())
	                .executes(ctx -> {
	                  try {
	                    return HideflagsCommand.run(MinecraftClient.getInstance(), ctx);
	                  } catch (Exception e) {
	                    e.printStackTrace();
	                  }
	                  return 0;
	                }).then(ArgumentBuilders.argument("Unbreakable", BoolArgumentType.bool())
	                    .executes(ctx -> {
	                      try {
	                        return HideflagsCommand.run(MinecraftClient.getInstance(), ctx);
	                      } catch (Exception e) {
	                        e.printStackTrace();
	                      }
	                      return 0;
	                    }).then(ArgumentBuilders.argument("CanDestroy", BoolArgumentType.bool())
	                        .executes(ctx -> {
	                          try {
	                            return HideflagsCommand.run(MinecraftClient.getInstance(), ctx);
	                          } catch (Exception e) {
	                            e.printStackTrace();
	                          }
	                          return 0;
	                        }).then(ArgumentBuilders.argument("CanPlaceOn", BoolArgumentType.bool())
	                            .executes(ctx -> {
	                              try {
	                                return HideflagsCommand.run(MinecraftClient.getInstance(), ctx);
	                              } catch (Exception e) {
	                                e.printStackTrace();
	                              }
	                              return 0;
	                            }).then(ArgumentBuilders.argument("HideOthers", BoolArgumentType.bool())
	                                .executes(ctx -> {
	                                  try {
	                                    return HideflagsCommand.run(MinecraftClient.getInstance(), ctx);
	                                  } catch (Exception e) {
	                                    e.printStackTrace();
	                                  }
	                                  return 0;
	                                })
	                            )
	                        )
	                    )
	                )
	            )
	        )
	    );
	  }

	  private void canplaceon(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("canplaceon")
	        .then(ArgumentBuilders.literal("add")
	            .then(ArgumentBuilders.argument("block", ItemStackArgumentType.itemStack())
	                .executes(ctx -> {
	                  try {
	                    return CanPlaceOnCommand.add(MinecraftClient.getInstance(), ctx);
	                  } catch (Exception e) {
	                    e.printStackTrace();
	                  }
	                  return 0;
	                })
	            )
	        )
	        .then(ArgumentBuilders.literal("remove")
	            .then(ArgumentBuilders.argument("block", ItemStackArgumentType.itemStack())
	                .executes(ctx -> {
	                  try {
	                    return CanPlaceOnCommand.remove(MinecraftClient.getInstance(), ctx);
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
	                return CanPlaceOnCommand.clear(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            })
	        ));
	  }

	  private void candestroy(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("candestroy")
	        .then(ArgumentBuilders.literal("add")
	            .then(ArgumentBuilders.argument("block", ItemStackArgumentType.itemStack())
	                .executes(ctx -> {
	                  try {
	                    return CanDestroyCommand.add(MinecraftClient.getInstance(), ctx);
	                  } catch (Exception e) {
	                    e.printStackTrace();
	                  }
	                  return 0;
	                })
	            )
	        )
	        .then(ArgumentBuilders.literal("remove")
	            .then(ArgumentBuilders.argument("block", ItemStackArgumentType.itemStack())
	                .executes(ctx -> {
	                  try {
	                    return CanDestroyCommand.remove(MinecraftClient.getInstance(), ctx);
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
	                return CanDestroyCommand.clear(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            })
	        ));
	  }

	  private void shulker(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("shulker")
	        .executes(ctx -> {
	          try {
	            return ShulkerCommand.run(MinecraftClient.getInstance(), ctx);
	          } catch (Exception e) {
	            e.printStackTrace();
	          }
	          return 0;
	        })
	    );
	  }

	  private void itemdata(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("itemdata")
	        .executes(ctx -> {
	          try {
	            return ItemDataCommand.run(MinecraftClient.getInstance(), ctx);
	          } catch (Exception e) {
	            e.printStackTrace();
	          }
	          return 0;
	        })
	    );
	  }

	  private void breakable(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("breakable")
	        .executes(ctx -> {
	          try {
	            return BreakableCommand.run(MinecraftClient.getInstance(), ctx);
	          } catch (Exception e) {
	            e.printStackTrace();
	          }
	          return 0;
	        })
	    );
	  }

	  private void ping(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("ping")
	        .executes(ctx -> {
	          try {
	            return PingCommand.both(MinecraftClient.getInstance(), ctx);
	          } catch (Exception e) {
	            e.printStackTrace();
	          }
	          return 0;
	        })
	        .then(ArgumentBuilders.literal("server")
	            .executes(ctx -> {
	              try {
	                return PingCommand.server(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            })
	        )
	        .then(ArgumentBuilders.literal("proxy")
	            .executes(ctx -> {
	              try {
	                return PingCommand.proxy(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            })
	        )
	        .then(ArgumentBuilders.literal("both")
	            .executes(ctx -> {
	              try {
	                return PingCommand.both(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            })
	        )
	    );
	  }

	  private void rejoin(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("rejoin")
	        .executes(ctx -> {
	          try {
	            return RejoinCommand.run(MinecraftClient.getInstance(), ctx);
	          } catch (Exception e) {
	            e.printStackTrace();
	          }
	          return 0;
	        }));
	  }

	  private void join(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("pjoin")
	        .then(ArgumentBuilders.argument("player",
	            EntityArgumentType.player())
	            .executes(ctx -> {
	              try {
	                return PJoinCommand.run(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            })
	        )
	    );
	  }

	  private void give(CommandDispatcher<CottonClientCommandSource> dispatcher) {
	    dispatcher.register(ArgumentBuilders.literal("give")
	        .then(ArgumentBuilders.argument("id", ItemStackArgumentType.itemStack())
	            .executes(ctx -> {
	              try {
	                return GiveCommand.run(MinecraftClient.getInstance(), ctx);
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              return 0;
	            })
	            .then(ArgumentBuilders.argument("amount", IntegerArgumentType.integer(1, 64))
	                .executes(ctx -> {
	                  try {
	                    return GiveCommand.run(MinecraftClient.getInstance(), ctx);
	                  } catch (Exception e) {
	                    e.printStackTrace();
	                  }
	                  return 0;
	                })
	                .then(ArgumentBuilders.argument("nbt", StringArgumentType.greedyString())
	                    .executes(ctx -> {
	                      try {
	                        return GiveCommand.run(MinecraftClient.getInstance(), ctx);
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
