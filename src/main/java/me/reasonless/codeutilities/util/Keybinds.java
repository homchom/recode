package me.reasonless.codeutilities.util;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    private static FabricKeyBinding devKeybind;
    private static FabricKeyBinding buildKeybind;
    private static FabricKeyBinding playKeybind;

    private MinecraftClient minecraftClient = MinecraftClient.getInstance();

    public Keybinds() {
        devKeybind = FabricKeyBinding.Builder.create(
                new Identifier("codeutilities", "dev"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "CodeUtilities"
        ).build();

        buildKeybind = FabricKeyBinding.Builder.create(
                new Identifier("codeutilities", "build"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "CodeUtilities"
        ).build();

        playKeybind = FabricKeyBinding.Builder.create(
                new Identifier("codeutilities", "play"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "CodeUtilities"
        ).build();

        KeyBindingRegistry.INSTANCE.addCategory("CodeUtilities");
        KeyBindingRegistry.INSTANCE.register(devKeybind);
        KeyBindingRegistry.INSTANCE.register(buildKeybind);
        KeyBindingRegistry.INSTANCE.register(playKeybind);

        ClientTickCallback.EVENT.register(e ->
        {
            if(minecraftClient != null) {
                if(minecraftClient.player != null) {
                    if(minecraftClient.player.abilities.creativeMode) {
                        if (devKeybind.isPressed()) minecraftClient.player.sendChatMessage("/dev");
                        if (buildKeybind.isPressed()) minecraftClient.player.sendChatMessage("/build");
                        if (playKeybind.isPressed()) minecraftClient.player.sendChatMessage("/play");
                    }
                }
            }


        });
    }
}
