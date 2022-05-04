package io.github.homchom.recode.sys.renderer;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.cottonmc.cotton.gui.client.*;
import net.minecraft.client.Minecraft;

public interface IMenu {
    default void scheduleOpenGui(LightweightGuiDescription gui, String... args) {
        try{
            this.open(args);
            Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new CottonClientScreen(gui)));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void open(String... args) throws CommandSyntaxException;
}
