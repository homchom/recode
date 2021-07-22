package io.github.codeutilities.sys.util.gui;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import net.minecraft.client.MinecraftClient;

public interface IMenu {
    default void scheduleOpenGui(LightweightGuiDescription gui, String... args) {
        try{
            this.open(args);
            MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().openScreen(new CottonClientScreen(gui)));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void open(String... args) throws CommandSyntaxException;
}
