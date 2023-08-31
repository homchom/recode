package io.github.homchom.recode.mod.features.commands.queue;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueueEntry {

    public static ArrayList<String> HIDDEN_ENTRIES = new ArrayList<>();

    private final Pattern ENTRY_PLOT_ID_REGEX = Pattern.compile("\\d{1,5}");
    private final String DESCRIPTION_REGEX = "^\\d+\\. ";
    private final String POSITION_REGEX = "\\. .*";

    private final String rawEntry;
    private final boolean beta;

    private String description;
    private Integer position;
    private Integer plotId;

    public QueueEntry(String rawEntry, int i) {
        this.rawEntry = rawEntry;

        // Contains Beta
        this.beta = rawEntry.toLowerCase().contains("beta");

        // Description (Full entry excluding position)
        this.description = rawEntry.replaceFirst(DESCRIPTION_REGEX, "");

        // Queue Position
        this.position = i;

        // Plot ID
        Matcher matcher = ENTRY_PLOT_ID_REGEX.matcher(description);
        while (matcher.find()) {
            try {
                this.plotId = Integer.parseInt(matcher.group(0));
            } catch (IndexOutOfBoundsException | IllegalStateException e) {
                this.plotId = null;
            }
            break;
        }

    }

    public boolean isBeta() {
        return beta;
    }

    public String getRawEntry() {
        return rawEntry;
    }

    public String getDescription() {
        return description;
    }

    public String getStrippedDescription() {
        try {
            return getDescription().replaceAll(getPlotId().toString(), "").replaceFirst("^( |-)+|\\1$", "");
        } catch (NullPointerException e) {
            return getDescription();
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getPlotId() {
        return plotId;
    }

    public void setPlotId(Integer plotId) {
        this.plotId = plotId;
    }
}
