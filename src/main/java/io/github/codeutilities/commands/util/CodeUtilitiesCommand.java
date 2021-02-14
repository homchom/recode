package io.github.codeutilities.commands.util;


import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class CodeUtilitiesCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("codeutilities")
                .executes(ctx -> {
                    this.sendMessage(mc, new LiteralText("§9§m                                                                    \n" +
                            "§f> §b§lCodeUtilities\n" +
                            "§7- §fA set of utilities for DiamondFire\n" +
                            "\n" +
                            "§e§lHelp Menus:\n" +
                            "§7･§6/codeutilities item§f - Shows a list of item commands\n" +
                            "§7･§6/codeutilities music§f - Shows a list of music commands\n" +
                            "§7･§6/codeutilities image§f - Shows a list of image commands\n" +
                            "§7･§6/codeutilities schem2df§f - Shows a list of Schem2DF commands\n" +
                            "§7･§6/codeutilities misc§f - Shows a list of miscellaneous commands\n" +
                            "§9§m                                                                    "));
                    return 1;
                })
                .then(ArgBuilder.literal("item")
                        .executes(ctx -> {
                            this.sendMessage(mc, new LiteralText("§9§m                                                                    \n" +
                                    "§e§lItem Commands:\n" +
                                    "§7･§6/breakable§f - Makes the held item breakable\n" +
                                    "§7･§6/edititem§f - Opens a menu to edit the held item.\n" +
                                    "§7･§6/give <item>§f - Gives you a specified item.\n" +
                                    "§7･§6/itemdata§f - Shows the held item's (client-side) NBT data.\n" +
                                    "§7･§6/templates recent§f - Shows a list of recently used Code Templates.\n" +
                                    "§7･§6/templates <all|search|view-self>§f - Views the online Code Template uploader.\n" +
                                    "§7･§6/unpack§f - Extracts the contents in the held item (such as items in chests)\n" +
                                    "§7･§6/webview§f - Views the held Code Template in your web browser.\n" +
                                    "§7･§6/sendtemplate§f - Sends the held Code Template to connected third-party software.\n" +
                                    "§9§m                                                                    "
                            ));
                            return 1;
                        })
                )
                .then(ArgBuilder.literal("music")
                        .executes(ctx -> {
                            this.sendMessage(mc, new LiteralText("§9§m                                                                    \n" +
                                    "§e§lMusic Commands:\n" +
                                    "§7･§6/nbs player§f - Gives you the Song Player functions Code Template.\n" +
                                    "§7･§6/nbs load <filename>§f - Imports a Note Block Song (.nbs) to a Code Template\n" +
                                    "§7> §fTo use the load command, place .nbs files in §b.minecraft/CodeUtilities/NBS Files§f.\n" +
                                    "§9§m                                                                    "
                            ));
                            return 1;
                        })
                )
                .then(ArgBuilder.literal("image")
                        .executes(ctx -> {
                            this.sendMessage(mc, new LiteralText("§9§m                                                                    \n" +
                                    "§e§lImage Commands:\n" +
                                    "§7･§6/imagehologram load colorcodes <filename>§f - Converts an image to hologram, with legacy color codes.\n" +
                                    "§7･§6/imagehologram load hex <filename>§f - Converts an image to hologram, with hex color codes.\n" +
                                    "§7･§6/imageparticle load <filename>§f - Creates image data to print with particles.\n" +
                                    "§7･§6/imageparticle printer§f - Gives you the Particle Image Printer code template.\n" +
                                    "§7> §fTo use the load command, place image files in §b.minecraft/CodeUtilities/Images§f.\n" +
                                    "§7> §f(Hologram) Maximum size of a legacy color code image is §b64x64§f.\n" +
                                    "§7> §f(Hologram) Maximum size of a hex color code image is §b17x17§f.\n" +
                                    "§7> §f(Particle) Maximum size of an image is §b40x40§f.\n" +
                                    "§9§m                                                                    "
                            ));
                            return 1;
                        })
                )
                .then(ArgBuilder.literal("schem2df")
                        .executes(ctx -> {
                            this.sendMessage(mc, new LiteralText("§9§m                                                                    \n" +
                                    "§e§lSchem2DF Commands:\n" +
                                    "§7･§6/schem load <filename>§f - Creates the Structure Data code templates.\n" +
                                    "§7･§6/schem builder§f - Gives you the Schem2DF Builder.\n" +
                                    "§7･§6/schem saver§f - Gives you the Schem2DF Saver.\n" +
                                    "§7･§6/schem transferer§f - Gives you the Schem2DF Transferer.\n" +
                                    "§7> §fTo use the load command, place structure files in §b.minecraft/schematic§f.\n" +
                                    "§7> §fSupported structure files: §b.schematic .schem .litematic§f or §bStructure Block NBT Files§f.\n" +
                                    "§9§m                                                                    "
                            ));
                            return 1;
                        })
                )
                .then(ArgBuilder.literal("misc")
                        .executes(ctx -> {
                            this.sendMessage(mc, new LiteralText("§9§m                                                                    \n" +
                                    "§e§lMiscellaneous Commands:\n" +
                                    "§7･§6/color rgb <r> <g> <b>§f - Creates a hex color code based on RGB values.\n" +
                                    "§7･§6/color hex <hex-code>§f - Creates a hex color code based on hex values.\n" +
                                    "§7･§6/color hsb <h> <s> <b>§f - Creates a hex color code based on HSB values.\n" +
                                    "§7･§6/colors§f - Shows a simple hex color picker in the chat.\n" +
                                    "§7･§6/heads§f - Opens a menu to search and get custom heads.\n" +
                                    "§7･§6/node <1|2|3|beta>§f - Sends you to the specified DiamondFire node.\n" +
                                    "§7･§6/pjoin <player>§f - Joins a plot that the specified player is currently in\n" +
                                    "§7･§6/rejoin§f - Rejoins the current plot.\n" +
                                    "§7･§6/uuid <username>§f - Copies the UUID of a specified player to the clipboard.\n" +
                                    "§9§m                                                                    "
                            ));
                            return 1;
                        })
                )
        );
    }
}
