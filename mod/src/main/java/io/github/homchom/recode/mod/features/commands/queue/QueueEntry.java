package io.github.homchom.recode.mod.features.commands.queue;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueueEntry {
    public static ArrayList<String> HIDDEN_ENTRIES = new ArrayList<>();

    private static final Pattern ENTRY_PLOT_ID_REGEX = Pattern.compile("\\d+");
    private static final String DESCRIPTION_REGEX = "^\\d+\\. ";

    private final boolean beta;

    private String description;
    private final Integer position;
    private Integer plotId;

    public QueueEntry(String rawEntry, int i) {
        // Contains Beta
        this.beta = rawEntry.toLowerCase().contains("beta");

        // Description (Full entry excluding position)
        this.description = rawEntry.replaceFirst(DESCRIPTION_REGEX, "");

        // Queue Position
        this.position = i;

        // Plot ID
        Matcher matcher = ENTRY_PLOT_ID_REGEX.matcher(description);
        if (matcher.find()) try {
            this.plotId = Integer.parseInt(matcher.group(0));
        } catch (IndexOutOfBoundsException | IllegalStateException e) {
            this.plotId = null;
        }

    }

    public boolean isBeta() {
        return beta;
    }

    public String getDescription() {
        return description;
    }

    public String getStrippedDescription() {
        try {
            return getDescription().replaceAll(getPlotId().toString(), "")
                    .replaceFirst("^( |-)+|\\1$", ""); // TODO: ??
        } catch (NullPointerException e) { // TODO: do this without a catch
            return getDescription();
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPosition() {
        return position;
    }

    public Integer getPlotId() {
        return plotId;
    }
}
