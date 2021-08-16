package io.github.codeutilities.mod.features.commands.codeutilities;

import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.CommandHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FeatureList {

    public static List<Feature> get() {
        List<Feature> features = new ArrayList<>();

        for (Command cmd : CommandHandler.getCommands()) {
            if (cmd.getName() != null) features.add(new Feature(cmd.getName(), cmd.getDescription()));
        }


        features.addAll(Arrays.asList(new Feature(
            "Discord RPC",
            "Set a currently playing status based on which df plot you're playing right now."
        ), new Feature(
            "Tablist Stars",
            "Every CodeUtilities user gets a fancy star next to their name in the tablist."
        ), new Feature(
            "Project-Audio",
            "Once enabled in the config you can use project audio directly ingame."
        ), new Feature(
            "Side Chat",
            "Gives you 2 chats in which you can seperate msgs to. Check out the config for more."
        ), new Feature(
            "CodeDesc",
            "When opening a codeblock chest it shows the action description."
        ), new Feature(
            "ItemTags",
            "When holding a keybind defined in your settings you can view its item tags."
        ), new Feature(
            "MessageStacker",
            "If you receive the same message multiple times it combines them into one."
        ), new Feature(
            "VarHighlighting",
            "When editing a variable, number or text you will receive a preview for texts or the syntax highlighted for vars and nums."
        )));

        features.sort(Comparator.comparing(Feature::getName));
        return features;
    }

}
