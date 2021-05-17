package io.github.codeutilities.config.config;

import io.github.codeutilities.config.structure.ConfigGroup;
import io.github.codeutilities.config.structure.ConfigSubGroup;
import io.github.codeutilities.modules.Module;
import org.json.JSONObject;

public class ModulesGroup extends ConfigGroup {
    public ModulesGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {

        // TODO

        // Modules config
        for (JSONObject json : Module.MODULES) {
            JSONObject meta = json.getJSONObject("meta");
            String moduleId = meta.getString("id");

            ConfigSubGroup subGroup = new ConfigSubGroup(moduleId)
                    .setRawKey("test")
                    .setRawTooltip("fsdfsdf");
            this.register(subGroup);
        }

    }
}
