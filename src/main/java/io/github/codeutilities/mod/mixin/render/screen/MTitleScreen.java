package io.github.codeutilities.mod.mixin.render.screen;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.renderer.BlendableTexturedButtonWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MTitleScreen extends Screen {

    // Valid positions: "top_left" && "bottom_right".
    // DM 8Blits if you would like to have the numbers be in another position, or if you want other texture changes.
    private static final String NUM_LOCATION = "top_left";

    private final Identifier identifier_main = new Identifier(CodeUtilities.MOD_ID + ":textures/gui/df.png");
    private final Identifier identifier_beta = new Identifier(CodeUtilities.MOD_ID + ":textures/gui/" + NUM_LOCATION + "/beta.png");
    private final Identifier identifier_node1 = new Identifier(CodeUtilities.MOD_ID + ":textures/gui/" + NUM_LOCATION + "/node1.png");
    private final Identifier identifier_node2 = new Identifier(CodeUtilities.MOD_ID + ":textures/gui/" + NUM_LOCATION + "/node2.png");
    private final Identifier identifier_node3 = new Identifier(CodeUtilities.MOD_ID + ":textures/gui/" + NUM_LOCATION + "/node3.png");
    private final Identifier identifier_node4 = new Identifier(CodeUtilities.MOD_ID + ":textures/gui/" + NUM_LOCATION + "/node4.png");
    private final Identifier identifier_node5 = new Identifier(CodeUtilities.MOD_ID + ":textures/gui/" + NUM_LOCATION + "/node5.png");
    private final Identifier identifier_node6 = new Identifier(CodeUtilities.MOD_ID + ":textures/gui/" + NUM_LOCATION + "/node6.png");
    private final Identifier identifier_node7 = new Identifier(CodeUtilities.MOD_ID + ":textures/gui/" + NUM_LOCATION + "/node7.png");

    protected MTitleScreen(LiteralText title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "initWidgetsNormal")
    public void drawMenuButton(int y, int spacingY, CallbackInfo info) {
        if (Config.getBoolean("dfButton")) {
            if (!Config.getBoolean("dfNodeButtons")) {
                // Default Server Join
                this.addButton(new BlendableTexturedButtonWidget(this.width / 2 + 104, y + spacingY, 20, 20, 0, 0, 20, identifier_main, 20, 40,
                        (button) -> {
                            MinecraftClient mc = MinecraftClient.getInstance();
                            ServerInfo serverInfo = new ServerInfo("DF", "mcdiamondfire.com:25565", false);
                            mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
                        }));
            }
        }

        if (Config.getBoolean("dfNodeButtons")) {
            boolean modMenuButtonPresent = false;
            if (FabricLoader.getInstance().isModLoaded("modmenu")) {
                modMenuButtonPresent = io.github.codeutilities.sys.util.ModMenuSupport.isModsButtonPresent();
            }

            // Default Server Join
            this.addButton(new BlendableTexturedButtonWidget(this.width / 2 + 104, y - spacingY, 20, 20, 0, 0, 20, identifier_main, 20, 40,
                    (button) -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        ServerInfo serverInfo = new ServerInfo("DF", "mcdiamondfire.com:25565", false);
                        mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
                    }));

            // Node Beta
            this.addButton(new BlendableTexturedButtonWidget(this.width / 2 + 104 + 22, y - spacingY, 20, 20, 0, 0, 20, identifier_beta, 20, 40,
                    (button) -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        ServerInfo serverInfo = new ServerInfo("DF Beta", "beta.mcdiamondfire.com:25565", false);
                        mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
                    }));

            // Node 1
            this.addButton(new BlendableTexturedButtonWidget(this.width / 2 + 104 + 44, y - spacingY, 20, 20, 0, 0, 20, identifier_node1, 20, 40,
                    (button) -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        ServerInfo serverInfo = new ServerInfo("DF Node1", "node1.mcdiamondfire.com:25565", false);
                        mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
                    }));

            // Node 2
            this.addButton(new BlendableTexturedButtonWidget(this.width / 2 + 104, y, 20, 20, 0, 0, 20, identifier_node2, 20, 40,
                    (button) -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        ServerInfo serverInfo = new ServerInfo("DF Node2", "node2.mcdiamondfire.com:25565", false);
                        mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
                    }));

            // Node 3
            this.addButton(new BlendableTexturedButtonWidget(this.width / 2 + 104 + 22, y, 20, 20, 0, 0, 20, identifier_node3, 20, 40,
                    (button) -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        ServerInfo serverInfo = new ServerInfo("DF Node3", "node3.mcdiamondfire.com:25565", false);
                        mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
                    }));

            // Node 4
            this.addButton(new BlendableTexturedButtonWidget(this.width / 2 + 104 + 44, y, 20, 20, 0, 0, 20, identifier_node4, 20, 40,
                    (button) -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        ServerInfo serverInfo = new ServerInfo("DF Node4", "node4.mcdiamondfire.com:25565", false);
                        mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
                    }));

            // Node 5
            this.addButton(new BlendableTexturedButtonWidget(this.width / 2 + 104, y + spacingY, 20, 20, 0, 0, 20, identifier_node5, 20, 40,
                    (button) -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        ServerInfo serverInfo = new ServerInfo("DF Node5", "node5.mcdiamondfire.com:25565", false);
                        mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
                    }));

            // Node 6
            this.addButton(new BlendableTexturedButtonWidget(this.width / 2 + 104 + 22, y + spacingY, 20, 20, 0, 0, 20, identifier_node6, 20, 40,
                    (button) -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        ServerInfo serverInfo = new ServerInfo("DF Node6", "node6.mcdiamondfire.com:25565", false);
                        mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
                    }));

            // Node 7
            this.addButton(new BlendableTexturedButtonWidget(this.width / 2 + 104 + 44, y + spacingY, 20, 20, 0, 0, 20, identifier_node7, 20, 40,
                    (button) -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        ServerInfo serverInfo = new ServerInfo("DF Node7", "node7.mcdiamondfire.com:25565", false);
                        mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
                    }));
        }
    }

}
