package io.github.homchom.recode.mod.config.impl;

//import io.github.homchom.recode.feature.social.DiscordActivityTimeMeasurement;
import io.github.homchom.recode.mod.config.structure.ConfigGroup;
import io.github.homchom.recode.mod.config.structure.ConfigSubGroup;
import io.github.homchom.recode.mod.config.types.BooleanSetting;
import io.github.homchom.recode.mod.config.types.DynamicStringSetting;
import io.github.homchom.recode.mod.config.types.EnumSetting;
import io.github.homchom.recode.mod.config.types.TextDescription;

public class DiscordRPCGroup extends ConfigGroup {
    public DiscordRPCGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {

        this.register(new TextDescription("discordRPCDescription"));

        this.register(new BooleanSetting("discordRPC", true));
        this.register(new BooleanSetting("discordRPCShowSession", true));

        // Spawn RPC
        ConfigSubGroup spawn = new ConfigSubGroup("discordRPCSpawn");
        spawn.register(new DynamicStringSetting("discordRPCSpawnDetails", "At Spawn"));
        spawn.register(new DynamicStringSetting("discordRPCSpawnState", "Node ${node.id}"));

        // Plot RPC
        ConfigSubGroup plot = new ConfigSubGroup("discordRPCPlot");
        plot.register(new DynamicStringSetting("discordRPCPlotDetails", "${plot.name}"));
        plot.register(new DynamicStringSetting("discordRPCPlotState",
                "Plot ID: ${plot.id} - Node ${node.id}"));
        plot.register(new BooleanSetting("discordRPCShowPlotMode", true));

        // Elapsed Behaviour
        ConfigSubGroup elapsed = new ConfigSubGroup("discordActivityTimeMeasurement");
        elapsed.register(new BooleanSetting("discordRPCShowElapsed", true));
        //elapsed.register(new EnumSetting<>("discordActivityTimeMeasurement", DiscordActivityTimeMeasurement.class, DiscordActivityTimeMeasurement.ON_SERVER));

        this.register(spawn);
        this.register(plot);
        this.register(elapsed);
    }
}