package io.github.homchom.recode.mixin.render.chat;

import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CommandSuggestions.class)
public interface CommandSuggestionsAccessor {
    @Accessor("LITERAL_STYLE")
    static Style getCommandStyle() {
        throw new AssertionError();
    }
}
