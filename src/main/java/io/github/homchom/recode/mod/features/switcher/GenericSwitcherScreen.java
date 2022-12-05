package io.github.homchom.recode.mod.features.switcher;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.LegacyRecode;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class GenericSwitcherScreen extends Screen {
    private List<SelectorOptionWidget> slots = Lists.newArrayList();
    private final int nextKey;
    private static final int DEBUG_KEY = InputConstants.KEY_F3;
    private static final int LEFT_SHIFT = InputConstants.KEY_LSHIFT;
    private static final int RIGHT_SHIFT = InputConstants.KEY_RSHIFT;

    static final ResourceLocation GAMEMODE_SWITCHER_LOCATION = new ResourceLocation("textures/gui/container/gamemode_switcher.png");

    private int currentlySelected;
    private static int previousSelected;
    private boolean goBackwards = false;

    protected GenericSwitcherScreen(int nextKey, SelectorOption[] selectorOptions) {
        super(GameNarrator.NO_TITLE);
        this.nextKey = nextKey;
        SelectorOptions = selectorOptions;
    }

    @Override
    protected void init() {
        super.init();
        int i = -1;
        int ALL_SLOTS_WIDTH = SelectorOptions.length * 31 - 5;
        for (SelectorOption option : SelectorOptions) {
            i += 1;
            this.slots.add(new SelectorOptionWidget(option, (this.width / 2) - (ALL_SLOTS_WIDTH / 2) + i * 31, this.height / 2 - 31));
        }
        this.currentlySelected = selectedOnOpen(previousSelected);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f) {
        if(checkClose()) return;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);

        int i1 = this.width / 2 - 62;
        int j2 = this.height / 2 - 31 - 27;
        GameModeSwitcherScreen.blit(poseStack, i1, j2, 0.0f, 0.0f, 125, 75, 128, 128);
        poseStack.popPose();

        super.render(poseStack, i, j, f);

        GameModeSwitcherScreen.drawCenteredString(poseStack, this.font, SelectorOptions[currentlySelected].name, this.width / 2, this.height / 2 - 31 - 20, -1);
        GameModeSwitcherScreen.drawCenteredString(poseStack, this.font, "Press F5 to select", this.width / 2, this.height / 2 + 5, 0xFFFFFF);

        for(SelectorOptionWidget widget : slots) {
            widget.setSelected(widget.icon == SelectorOptions[currentlySelected]);
            widget.render(poseStack,i,j,f);
        }
    }

    // Keys
    @Override
    public boolean keyPressed(int key, int j, int k) {
        if(key == nextKey) {
            this.next(goBackwards ? -1 : 1);
            return true;
        }
        if(key == LEFT_SHIFT || key == RIGHT_SHIFT) {
            goBackwards = true;
            return true;
        }
        return super.keyPressed(key, j, k);
    }
    @Override
    public boolean keyReleased(int key, int j, int k) {
        if(key == LEFT_SHIFT || key == RIGHT_SHIFT) {
            goBackwards = false;
            return true;
        }
        if(key == DEBUG_KEY) {
            checkClose();
            return true;
        }
        return super.keyReleased(key, j, k);
    }

    // Logic
    private boolean checkClose() {
        if(InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), DEBUG_KEY)) return false;
        this.doSelected();
        this.minecraft.setScreen(null);
        return true;
    }
    private void next(int by) {
        int len = SelectorOptions.length;

        currentlySelected += by;
        if(currentlySelected < 0) currentlySelected -= len;
        currentlySelected %= len;
        previousSelected = currentlySelected;
    }
    private void doSelected() {
        this.previousSelected = currentlySelected;
        this.minecraft.setScreen(null);
        SelectorOptions[currentlySelected].activate();
    }
    int selectedOnOpen(int previousSelected) {
        return previousSelected;
    }

    // Options
    public final SelectorOption[] SelectorOptions;
    public abstract static class SelectorOption {
        final Component name;
        final ItemStack icon;

        SelectorOption(Component name,ItemStack icon) {
            this.name = name;
            this.icon = icon;
        }

        abstract void activate();

        void render(ItemRenderer itemRenderer, int x, int y) {
            itemRenderer.renderAndDecorateItem(this.icon, x, y);
        }
    }
    private class SelectorOptionWidget extends AbstractWidget {
        private boolean isSelected;
        final SelectorOption icon;

        public SelectorOptionWidget(SelectorOption selectorOption, int i, int j) {
            super(i, j, 26, 26, selectorOption.name);
            this.icon = selectorOption;
            this.isSelected = true;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        @Override
        public void renderButton(PoseStack poseStack, int i, int j, float f) {
            this.drawSlot(poseStack);
            this.icon.render(GenericSwitcherScreen.this.itemRenderer, this.x + 5, this.y + 5);
            if (this.isSelected) {
                this.drawSelection(poseStack);
            }
        }

        private void drawSlot(PoseStack poseStack) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);
            poseStack.pushPose();
            poseStack.translate(this.x, this.y, 0.0);
            GameModeSwitcherScreen.GameModeSlot.blit(poseStack, 0, 0, 0.0f, 75.0f, 26, 26, 128, 128);
            poseStack.popPose();
        }

        private void drawSelection(PoseStack poseStack) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GAMEMODE_SWITCHER_LOCATION);
            poseStack.pushPose();
            poseStack.translate(this.x, this.y, 0.0);
            GameModeSwitcherScreen.GameModeSlot.blit(poseStack, 0, 0, 26.0f, 75.0f, 26, 26, 128, 128);
            poseStack.popPose();
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }
    }

    // Menu stuff
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
