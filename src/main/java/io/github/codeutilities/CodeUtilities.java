package io.github.codeutilities;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CodeUtilities implements ModInitializer {

  public static final String MOD_ID = "codeutilities";
  public static final String MOD_NAME = "CodeUtilities";
  public static final String SONG_PARSER_VERSION = "4"; //NBS parser version
  public static final String SONG_NBS_FORMAT_VERSION = "4"; //NBS format version
  public static Logger LOGGER = LogManager.getLogger();
  public static MinecraftClient mc = MinecraftClient.getInstance();

  public static void log(Level level, String message) {
    LOGGER.log(level, "[" + MOD_NAME + "] " + message);
  }

  public static void chat(String text) {
    mc.player.sendMessage(new LiteralText(text), false);
  }

  @Override
  public void onInitialize() {
    log(Level.INFO, "Initializing");
    //TODO: Initializer
  }

  public static void giveCreativeItem(ItemStack item) {
    assert mc.interactionManager != null;
    assert mc.player != null;
    mc.interactionManager.clickCreativeStack(item, 36 + mc.player.inventory.getSwappableHotbarSlot());
  }

}