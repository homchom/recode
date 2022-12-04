package io.github.homchom.recode.mod.features;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

@Environment(value=EnvType.CLIENT)
public class PlotModeSwitcherScreen extends Screen {
    static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");
    private static final int SPRITE_SHEET_WIDTH = 128;
    private static final int SPRITE_SHEET_HEIGHT = 128;
    private static final int SLOT_AREA = 26;
    private static final int SLOT_PADDING = 5;
    private static final int SLOT_AREA_PADDED = 31;
    private static final int HELP_TIPS_OFFSET_Y = 5;
    private static final int ALL_SLOTS_WIDTH = GameModeIcon.values().length * 31 - 5;
    private static final Component SELECT_KEY = Component.translatable("debug.gamemodes.select_next", Component.translatable("debug.gamemodes.press_f4").withStyle(ChatFormatting.AQUA));
    private final Optional<GameModeIcon> previousHovered;
    private Optional<GameModeIcon> currentlyHovered = Optional.empty();
    private int firstMouseX;
    private int firstMouseY;
    private boolean setFirstMousePos;
    private LegacyState.Mode previousMode = LegacyState.Mode.PLAY;
    private final List<GameModeSlot> slots = Lists.newArrayList();

    public PlotModeSwitcherScreen() {
        super(GameNarrator.NO_TITLE);
        this.previousHovered = GameModeIcon.getFromGameType(this.getDefaultSelected());
    }

    private LegacyState.Mode getDefaultSelected() {
        if(previousMode == LegacyState.Mode.OFFLINE || previousMode == LegacyState.Mode.SPAWN) return LegacyState.Mode.DEV;
        return previousMode;
    }

    @Override
    protected void init() {
        super.init();
        this.currentlyHovered = this.previousHovered.isPresent() ? this.previousHovered : GameModeIcon.getFromGameType(DFInfo.currentState.getMode());
        for (int i = 0; i < GameModeIcon.VALUES.length; ++i) {
            GameModeIcon gameModeIcon = GameModeIcon.VALUES[i];
            this.slots.add(new GameModeSlot(gameModeIcon, this.width / 2 - ALL_SLOTS_WIDTH / 2 + i * 31, this.height / 2 - 31));
        }
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        if (this.checkToClose()) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);
        int k = this.width / 2 - 62;
        int l = this.height / 2 - 31 - 27;
        PlotModeSwitcherScreen.blit(poseStack, k, l, 0.0f, 0.0f, 125, 75, 128, 128);
        poseStack.popPose();
        super.render(poseStack, i, j, f);
        this.currentlyHovered.ifPresent(gameModeIcon -> PlotModeSwitcherScreen.drawCenteredString(poseStack, this.font, gameModeIcon.getName(), this.width / 2, this.height / 2 - 31 - 20, -1));
        PlotModeSwitcherScreen.drawCenteredString(poseStack, this.font, SELECT_KEY, this.width / 2, this.height / 2 + 5, 0xFFFFFF);
        if (!this.setFirstMousePos) {
            this.firstMouseX = i;
            this.firstMouseY = j;
            this.setFirstMousePos = true;
        }
        boolean bl = this.firstMouseX == i && this.firstMouseY == j;
        for (GameModeSlot gameModeSlot : this.slots) {
            gameModeSlot.render(poseStack, i, j, f);
            this.currentlyHovered.ifPresent(gameModeIcon -> gameModeSlot.setSelected(gameModeIcon == gameModeSlot.icon));
            if (bl || !gameModeSlot.isHoveredOrFocused()) continue;
            this.currentlyHovered = Optional.of(gameModeSlot.icon);
        }
    }

    private void switchToHoveredGameMode() {
        PlotModeSwitcherScreen.switchToHoveredGameMode(this.minecraft, this.currentlyHovered);
    }

    private static void switchToHoveredGameMode(Minecraft minecraft, Optional<GameModeIcon> optional) {
        if (minecraft.gameMode == null || minecraft.player == null || !optional.isPresent()) {
            return;
        }
        Optional<GameModeIcon> optional2 = GameModeIcon.getFromGameType(DFInfo.currentState.getMode());
        GameModeIcon gameModeIcon = optional.get();
        if (optional2.isPresent()) {
            minecraft.player.commandUnsigned(gameModeIcon.getCommand());
        }
    }

    private boolean checkToClose() {
        if (!InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292)) {
            this.switchToHoveredGameMode();
            this.minecraft.setScreen(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 293 && this.currentlyHovered.isPresent()) {
            this.setFirstMousePos = false;
            this.currentlyHovered = this.currentlyHovered.get().getNext();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    enum GameModeIcon {
        PLAY(Component.literal("Play Mode"), "mode play", new ItemStack(Items.DIAMOND)),
        BUILD(Component.literal("Build Mode"), "mode build", new ItemStack(Items.GRASS_BLOCK)),
        DEV(Component.literal("Code Mode"), "mode code", new ItemStack(Items.COMMAND_BLOCK));
//        SOMETHING(Component.translatable("gameMode.spectator"), "gamemode spectator", new ItemStack(Items.ENDER_EYE));

        protected static final GameModeIcon[] VALUES;
        private static final int ICON_AREA = 16;
        protected static final int ICON_TOP_LEFT = 5;
        final Component name;
        final String command;
        final ItemStack renderStack;

        GameModeIcon(Component component, String string2, ItemStack itemStack) {
            this.name = component;
            this.command = string2;
            this.renderStack = itemStack;
        }

        void drawIcon(ItemRenderer itemRenderer, int i, int j) {
            itemRenderer.renderAndDecorateItem(this.renderStack, i, j);
        }

        Component getName() {
            return this.name;
        }

        String getCommand() {
            return this.command;
        }

        Optional<GameModeIcon> getNext() {
            switch (this) {
                case PLAY: {
                    return Optional.of(BUILD);
                }
                case BUILD: {
                    return Optional.of(DEV);
                }
                case DEV: {
                    return Optional.of(PLAY);
                }
            }
            return Optional.of(PLAY);
        }

        static Optional<GameModeIcon> getFromGameType(LegacyState.Mode gameType) {
            switch (gameType) {
                case BUILD: {
                    return Optional.of(BUILD);
                }
                case PLAY: {
                    return Optional.of(PLAY);
                }
                case DEV: {
                    return Optional.of(DEV);
                }
            }
            return Optional.empty();
        }

        static {
            VALUES = GameModeIcon.values();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class GameModeSlot
            extends AbstractWidget {
        final GameModeIcon icon;
        private boolean isSelected;

        public GameModeSlot(GameModeIcon gameModeIcon, int i, int j) {
            super(i, j, 26, 26, gameModeIcon.getName());
            this.icon = gameModeIcon;
        }

        @Override
        public void renderButton(PoseStack poseStack, int i, int j, float f) {
            Minecraft minecraft = Minecraft.getInstance();
            this.drawSlot(poseStack, minecraft.getTextureManager());
            this.icon.drawIcon(PlotModeSwitcherScreen.this.itemRenderer, this.x + 5, this.y + 5);
            if (this.isSelected) {
                this.drawSelection(poseStack, minecraft.getTextureManager());
            }
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }

        @Override
        public boolean isHoveredOrFocused() {
            return super.isHoveredOrFocused() || this.isSelected;
        }

        public void setSelected(boolean bl) {
            this.isSelected = bl;
        }

        private void drawSlot(PoseStack poseStack, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);
            poseStack.pushPose();
            poseStack.translate(this.x, this.y, 0.0);
            GameModeSlot.blit(poseStack, 0, 0, 0.0f, 75.0f, 26, 26, 128, 128);
            poseStack.popPose();
        }

        private void drawSelection(PoseStack poseStack, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);
            poseStack.pushPose();
            poseStack.translate(this.x, this.y, 0.0);
            GameModeSlot.blit(poseStack, 0, 0, 26.0f, 75.0f, 26, 26, 128, 128);
            poseStack.popPose();
        }
    }
}

