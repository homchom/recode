package io.github.homchom.recode.mod.features.switcher;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.sys.networking.LegacyState;
import io.github.homchom.recode.sys.player.DFInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class StateSwitcherScreen extends GenericSwitcherScreen {
    private static final SelectorOption[] SelectorOptions = {
            new SelectorOption(Component.literal("Play Mode"), new ItemStack(Items.EMERALD)) {
                @Override
                void activate() {
                    runCommand("mode play");
                }
            },
            new SelectorOption(Component.literal("Build Mode"), new ItemStack(Items.GRASS_BLOCK)) {
                @Override
                void activate() {
                    runCommand("mode build");
                }
            },
            new SelectorOption(Component.literal("Dev Mode"), new ItemStack(Items.COMMAND_BLOCK)) {
                @Override
                void activate() {
                    runCommand("mode code");
                }
            }
    };

    public StateSwitcherScreen(int nextKey) {
        super(nextKey, SelectorOptions);
    }

    @Override
    int selectedOnOpen(int previousSelected) {
        if(DFInfo.currentState.getMode() == LegacyState.Mode.PLAY) return 2;
        return 0;
    }

    private static void runCommand(String command) {
        LegacyRecode.MC.player.commandUnsigned(command);
    }
}
