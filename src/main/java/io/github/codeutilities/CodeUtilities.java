package io.github.codeutilities;

import io.github.codeutilities.commands.CommandHandler;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.gui.CustomHeadSearchGui;
import io.github.codeutilities.template.*;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.cotton.gui.client.*;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.*;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.*;

import java.util.Random;

public class CodeUtilities implements ModInitializer {

    public static final String MOD_ID = "codeutilities";
    public static final String MOD_NAME = "CodeUtilities";
    public static Logger LOGGER = LogManager.getLogger();
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static Random rng = new Random();


    // This should be moved into its own class
    public static void openGuiAsync(LightweightGuiDescription gui) {
        new Thread(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MinecraftClient.getInstance().openScreen(new CottonClientScreen(gui));
        }).start();
    }

    // Perhaps some kind of "ServerHandler"
    public static boolean isOnDF() {
        if (mc.getCurrentServerEntry() == null) return false;
        return mc.getCurrentServerEntry().address.contains("mcdiamondfire.com");
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    public static void chat(String text) {
        mc.player.sendMessage(new LiteralText(text), false);
    }

    public static void chat(String text, ChatType prefixType) {
        mc.player.sendMessage(new LiteralText(prefixType.getString() + text), false);
        if (ModConfig.getConfig().errorSound) {
            if (prefixType == ChatType.FAIL) {
                MinecraftClient.getInstance().player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 2, 0);
            }
        }
    }


    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        // Add a shutdown hook so we can save players template data on exit.
        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));
        MinecraftCommunicator.initalize();
        CommandHandler.initialize();
        CustomHeadSearchGui.load();
        TemplateStorageHandler.load();
    }

    public void onClose() {
        System.out.println("CLOSED");
        TemplateStorageHandler.save();
    }


}