package io.github.codeutilities.mod.features.commands.codeutilities;

import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.CommandHandler;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FeatureList {

    public static List<Feature> get() {
        List<Feature> features = new ArrayList<>();

        for (Command cmd : CommandHandler.getCommands()) {
            if (cmd.getName() != null) features.add(new Feature(cmd.getName(), cmd.getDescription()));
        }


        features.add(new Feature("test", "some other feature"));


        features.sort(Comparator.comparing(Feature::getName));
        return features;
    }

}
