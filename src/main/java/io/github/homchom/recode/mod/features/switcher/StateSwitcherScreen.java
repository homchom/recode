package io.github.homchom.recode.mod.features.switcher;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class StateSwitcherScreen extends GenericSwitcherScreen {
    private static final SelectorOption[] SelectorOptions = {
            createOption("Play", Items.EMERALD),
            createOption("Build", Items.GRASS_BLOCK),
            createOption("Code", Items.COMMAND_BLOCK)
    };

    public StateSwitcherScreen(int nextKey) {
        super(nextKey, SelectorOptions, Component.literal("Press F5 to select"));
    }

    @Override
    int selectedOnOpen(int previousSelected) {
        if(DFInfo.currentState.getMode() == LegacyState.Mode.PLAY) return 2;
        return 0;
    }

    private static SelectorOption createOption(String mode, Item item) {
        return new SelectorOption(Component.literal(mode + " Mode"), new ItemStack(item)) {
            @Override
            void activate() {
                LegacyRecode.MC.player.commandUnsigned(mode);
            }
        };
    }
}
