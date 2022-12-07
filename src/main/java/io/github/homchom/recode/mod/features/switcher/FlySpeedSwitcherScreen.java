package io.github.homchom.recode.mod.features.switcher;

import io.github.homchom.recode.LegacyRecode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FlySpeedSwitcherScreen extends GenericSwitcherScreen {
    static private final SelectorOption[] options = {
        createOption("Normal", Items.LEATHER_BOOTS,100),
        createOption("Faster", Items.IRON_BOOTS, 200),
        createOption("Fast", Items.DIAMOND_BOOTS, 500),
        createOption("Fastest", Items.GOLDEN_BOOTS, 1000),
    };

    public FlySpeedSwitcherScreen(int nextKey) {
        super(nextKey, options, Component.literal("Press F6 to select"));
    }


    private static SelectorOption createOption(String name, Item item, int speed) {
        return new SelectorOption(Component.literal(name), new ItemStack(item)) {
            @Override
            void activate() {
                LegacyRecode.MC.player.commandUnsigned("fs " + speed);
            }
        };
    }
}
