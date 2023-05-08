package io.github.homchom.recode.mod.features.commands.recode;

import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.CommandHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FeatureList {

    public static List<LegacyFeature> get() {
        List<LegacyFeature> features = new ArrayList<>();

        for (Command cmd : CommandHandler.getCommands()) {
            if (cmd.getName() != null) features.add(new LegacyFeature(cmd.getName(), cmd.getDescription()));
        }


        features.addAll(Arrays.asList(new LegacyFeature(
            "Discord RPC",
            "Displays which plot you are in on \"Currently Playing\" status of your Discord profile.\n" +
            "You can modify the appearance in the config menu."
        ), new LegacyFeature(
            "Tablist Stars",
            "A star is displayed next to each CodeUtilities mod user in the tab player list."
        ), new LegacyFeature(
            "Side Chat",
            "Gives you 2 chats in which you can seperate messages to. Check out the config for more information."
        ), new LegacyFeature(
            "Code Info",
            "When opening a code chest it shows the action description next to the chest menu."
        ), new LegacyFeature(
            "Item Tags",
            "When holding a keybind defined in your settings you can view its item tags."
        ), new LegacyFeature(
            "Message Stacker",
            "If you receive the same message multiple times it combines them into one."
        ), new LegacyFeature(
            "Var Highlighting",
            "When editing a variable, number or text you will receive a preview for texts or the syntax highlighted for variables and numbers."
        )));

        features.sort(Comparator.comparing(LegacyFeature::getName));
        return features;
    }

}
