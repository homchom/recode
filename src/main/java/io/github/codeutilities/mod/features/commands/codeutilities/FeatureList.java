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
            "Displays which plot you are in on \"Currently Playing\" status of your Discord profile.\n" +
            "You can modify the appearance in the config menu."
        ), new Feature(
            "Tablist Stars",
            "A star is displayed next to each CodeUtilities mod user in the tab player list."
        ), new Feature(
            "Project: Audio",
            "[red]*Disabled by default, you can enable this feature in config*[reset]\n" +
            "Plays custom audio in plots. With CodeUtilities, you do not need to open web browser to use Project: Audio."
        ), new Feature(
            "Side Chat",
            "Gives you 2 chats in which you can seperate messages to. Check out the config for more information."
        ), new Feature(
            "Code Info",
            "When opening a code chest it shows the action description next to the chest menu."
        ), new Feature(
            "Item Tags",
            "When holding a keybind defined in your settings you can view its item tags."
        ), new Feature(
            "Message Stacker",
            "If you receive the same message multiple times it combines them into one."
        ), new Feature(
            "Var Highlighting",
            "When editing a variable, number or text you will receive a preview for texts or the syntax highlighted for variables and numbers."
        )));

        features.sort(Comparator.comparing(Feature::getName));
        return features;
    }

}
