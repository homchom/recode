package io.github.codeutilities.mod.config.config;

import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.mod.config.types.BooleanSetting;
import io.github.codeutilities.sys.modules.Module;
import io.github.codeutilities.sys.modules.translations.Translation;
import org.json.JSONObject;

public class ModulesGroup extends ConfigGroup {
    public ModulesGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {

        // Modules config
        for (JSONObject json : Module.MODULES) {
            // get module data
            JSONObject meta = json.getJSONObject("meta");
            String moduleId = meta.getString("id");

            String name = Translation.get(moduleId, "meta.name");
            String author = Translation.get(moduleId, "meta.author");
            String description = Translation.get(moduleId, "meta.description");
            String version = Translation.get(moduleId, "meta.version");

            // set presets
            ConfigSubGroup subGroup = new ConfigSubGroup(moduleId)
                    .setRawKey(name + " - by " + author)
                    .setRawTooltip(name + " (" + version + ")\n" +
                            Translation.getExternal("config.codeutilities.category.modules.madeby").replaceAll("\\$author\\$", author) +
                            "\n\n" + description + "\n\n⚠ " + Translation.getExternal("config.codeutilities.category.modules.warning"))
                    .setStartExpanded(false);
            //subGroup.register(new DescriptionSetting( .... TODO
            subGroup.register(new BooleanSetting("enabled", true)
                    //.setKey("module.super."+moduleId+".enabled")
                    .setRawKey(Translation.getExternal("config.codeutilities.category.modules.enabled"))
                    .setRawTooltip(Translation.getExternal("config.codeutilities.category.modules.enabled.tooltip")));

            //repeat over each custom config entry for the module TODO

            this.register(subGroup);
        }

    }
}
