package me.reasonless.codeutilities;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import me.reasonless.codeutilities.events.KeyInputEvent;
import me.reasonless.codeutilities.events.TickEvent;
//import me.reasonless.codeutilities.util.Keybinds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;

public class CodeUtilities implements ModInitializer {
	
	public static final String MOD_VERSION = "1.3"; //CodeUtilities mod version
	
	public static final String PARSER_VERSION = "4"; //NBS parser version
	public static final String NBS_FORMAT_VERSION = "4"; //NBS format version

	public static int lastrc = 0;
	public static int playerjoin = 0;
	public static long sping = 0;
	public static long pping = 0;
	public static int rejoin = 0;
	public static boolean afk = false;
	public static boolean chestpreview = false;
	public static boolean hasblazing = false;
	public static PlayMode playMode = PlayMode.SPAWN;
	public static Properties p = new Properties();
	public static BlockPos plotPos = new BlockPos(0, 0, 0);
	static MinecraftClient mc = MinecraftClient.getInstance();
	
	//private Keybinds keybinds;
	
	@Override 
	public void onInitialize() {
		if (FabricLoader.getInstance().isModLoaded("blazingutilities")) {
			hasblazing = true;
			System.out.println("[CodeUtilities] BlazingUtilities has been detected! This mod will not activate merged BlazingUtilities features.");
		}
		
		File codeUtilities = new File("CodeUtilities");
		File nbsFolder = new File("CodeUtilities/NBS Files");
		File imagesFolder = new File("CodeUtilities/Images");

		if(!codeUtilities.exists()) {
			codeUtilities.mkdir();
		}

		if(!nbsFolder.exists()) {
			nbsFolder.mkdir();
		}

		if(!imagesFolder.exists()) {
			imagesFolder.mkdir();
		}

		//this.keybinds = new Keybinds();
		if (!hasblazing) {
			try {
			      p.load(new FileReader("CodeUtilities/Config.cfg"));
			    } catch (IOException err) {
			      System.out.println("No Config File for CodeUtilities found!");
			    }

			    if (p.getProperty("autorc") == null) {
			      p.setProperty("autorc", "false");
			    }
			    if (p.getProperty("autotip") == null) {
			      p.setProperty("autotip", "false");
			    }
			    if (p.getProperty("autofly") == null) {
			      p.setProperty("autofly", "false");
			    }
			    if (p.getProperty("afkmsg") == null) {
			      p.setProperty("afkmsg", "I'm currently afk (automated msg)");
			    }
			    if (p.getProperty("autoafk") == null) {
			      p.setProperty("autoafk", "false");
			    }
			    if (p.getProperty("autoafktime") == null) {
			      p.setProperty("autoafktime", "300");
			    }
			    updateConfig();

			    KeyInputEvent.register();
			    TickEvent.register();
		}
	}
	
	public static void successMsg(String msg) {
	    assert mc.player != null;
	    mc.player.sendMessage(new LiteralText("§l§l§2 - §a" + msg), false);
	}
	
	public static void errorMsg(String msg) {
	    assert mc.player != null;
	    mc.player.sendMessage(new LiteralText("§l§l§4 - §c" + msg), false);
	}
	
	public static void infoMsgYellow(String msg) {
	    assert mc.player != null;
	    mc.player.sendMessage(new LiteralText("§l§l§6 - §e" + msg), false);
	}
	
	public static void infoMsgBlue(String msg) {
	    assert mc.player != null;
	    mc.player.sendMessage(new LiteralText("§l§l§3 - §b" + msg), false);
	}
	
	public static void infoMsgPink(String msg) {
	    assert mc.player != null;
	    mc.player.sendMessage(new LiteralText("§l§l§5 - §d" + msg), false);
	}
	
	public static void giveCreativeItem(ItemStack item) {
	    assert mc.interactionManager != null;
	    assert mc.player != null;
	    mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.getSwappableHotbarSlot());
	}
	
	public static void updateConfig() {
	    try {
	      if (!new File("CodeUtilities/").exists()) new File("CodeUtilities/").mkdir();
	      if (!new File("CodeUtilities/AutoCompletion").exists()) new File("CodeUtilities/AutoCompletion").mkdir();
	      p.store(new FileWriter("CodeUtilities/Config.cfg"),
	          "Config file for the CodeUtilities mod.");
	    } catch (IOException ignored) {
	    }
	}
	
	public enum PlayMode {
	    PLAY, DEV, BUILD, SPAWN
	}
}
