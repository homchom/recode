package io.github.homchom.recode.mod.features.modules;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.features.modules.actions.json.ModuleJson;
import io.github.homchom.recode.mod.features.modules.tasks.Task;
import io.github.homchom.recode.mod.features.modules.translations.Translation;
import io.github.homchom.recode.mod.features.modules.triggers.Trigger;
import io.github.homchom.recode.sys.file.FileUtil;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.json.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

public class Module {

    private static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();

    public static List<ModuleJson> MODULES = new ArrayList<>();
    private static final HashMap<String, ModuleJson> KEY_MODULES = new HashMap<>();

    public static ModuleJson getModule(String moduleId) {
        return KEY_MODULES.get(moduleId);
    }

    public static void loadModules() {
        Path modFolder = FABRIC_LOADER.getGameDir().resolve("CodeUtilities");
        Path modulesFolder = modFolder.resolve("Modules");
        File modulesFile = modulesFolder.toFile();

        if (!modulesFile.exists()) {
            modulesFile.mkdir();
        }

        Recode.log(Level.INFO, "Loading modules...");

        File[] moduleFiles = modulesFile.listFiles();
        if (moduleFiles != null) {
            Recode.log(Level.INFO, moduleFiles.length + " module" + (moduleFiles.length == 1 ? "" : "s") + " found.");

            int successfulLoads = 0;
            for (File file : moduleFiles) {
                // Load file
                String jsonString = "";
                try {
                    jsonString = FileUtil.readFile(String.valueOf(file.toPath()), Charset.defaultCharset());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JSONObject jsonRead;
                try {
                    jsonRead = new JSONObject(jsonString);
                } catch (JSONException e) {
                    Recode.log(Level.ERROR, "Error while loading module '" + file.getName() + "'. Stack Trace:");
                    e.printStackTrace();
                    continue;
                }
                ModuleJson json = new ModuleJson(jsonRead);

                MODULES.add(json);

                // --------- Load objects

                // Load meta
                JSONObject meta = json.getMeta();
                String moduleId = json.getId();

                KEY_MODULES.put(moduleId, json);

                //if (!Config.getBoolean("module.super."+moduleId+".enabled")) continue; TODO

                Iterator<String> keys = meta.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = meta.getString(key);

                    Translation.put("module." + moduleId + ".meta." + key, value);
                }

                // Load triggers
                JSONArray triggers = json.getTriggers();
                for (int i = 0; i < triggers.length(); i++) {
                    JSONObject trigger = triggers.getJSONObject(i);

                    String eventId = trigger.getString("event");
                    Trigger event = Trigger.getTrigger(eventId);
                    String task = trigger.getString("task");

                    Trigger.putTask(event, task, moduleId);
                }

                // Load tasks
                JSONArray tasks = json.getTasks();
                for (int i = 0; i < tasks.length(); i++) {
                    JSONObject task = tasks.getJSONObject(i);

                    String taskName = moduleId + "." + task.getString("name");
                    JSONArray actions = task.getJSONArray("actions");

                    Task.putActions(taskName, actions);
                }

                // Load translations
                JSONObject translations = json.getTranslations();

                JSONObject lang = translations.getJSONObject("en_us");
                keys = lang.keys();
                for (int i = 0; i < 2; i++) {
                    while (keys.hasNext()) {
                        String key = keys.next();
                        Translation.put("module." + moduleId + "." + key, lang.getString(key));
                    }
                    if (!translations.has(Recode.CLIENT_LANG)) break;
                    lang = translations.getJSONObject(Recode.CLIENT_LANG);
                    keys = lang.keys();
                }

                // TODO Load config
                JSONObject config = json.getConfig();

                successfulLoads++;
            }
            Recode.log(Level.INFO, "Successfully loaded " + successfulLoads + " (" + successfulLoads + "/" + moduleFiles.length + ") module" + (successfulLoads == 1 ? "" : "s") + "!");
        } else {
            Recode.log(Level.INFO, "No modules found.");
        }

    }

    public List<ModuleJson> getModules() {
        return MODULES;
    }

}
