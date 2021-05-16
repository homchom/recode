package io.github.codeutilities.modules;

import com.google.gson.stream.JsonReader;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.modules.tasks.Task;
import io.github.codeutilities.modules.triggers.Trigger;
import io.github.codeutilities.util.file.FileUtil;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;

public class Module {

    private static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();

    public static void loadModules() {
        Path modFolder = FABRIC_LOADER.getGameDir().resolve("CodeUtilities");
        Path modulesFolder = modFolder.resolve("Modules");
        File modulesFile = modulesFolder.toFile();

        if (!modulesFile.exists()) {
            modulesFile.mkdir();
        }

        CodeUtilities.log(Level.INFO, "Loading Modules...");

        File[] moduleFiles = modulesFile.listFiles();
        if (moduleFiles != null) {
            CodeUtilities.log(Level.INFO, moduleFiles.length+" module"+(moduleFiles.length==1?"":"s")+" found.");
            for (File file : moduleFiles) {
                // Load file
                String jsonString = "";
                try { jsonString = FileUtil.readFile(String.valueOf(file.toPath()), Charset.defaultCharset());
                } catch (IOException e) { e.printStackTrace(); }

                JSONObject json = new JSONObject();
                try { json = new JSONObject(jsonString);
                } catch (JSONException e) {
                    CodeUtilities.log(Level.ERROR, "Error while loading module '"+file.getName()+"'. Stack Trace:");
                    e.printStackTrace();
                }

                // --------- Load objects

                // Load meta
                JSONObject meta = json.getJSONObject("meta");
                String moduleId = meta.getString("id");

                // Load triggers
                JSONArray triggers = json.getJSONArray("triggers");
                for (int i = 0; i < triggers.length(); i++) {
                    JSONObject trigger = triggers.getJSONObject(i);

                    String eventId = trigger.getString("event");
                    Trigger event = Trigger.getTrigger(eventId);
                    String task = trigger.getString("task");

                    Trigger.putTask(event, task, moduleId);
                }

                // Load tasks
                JSONArray tasks = json.getJSONArray("tasks");
                for (int i = 0; i < tasks.length(); i++) {
                    JSONObject task = tasks.getJSONObject(i);

                    String taskName = moduleId+"."+task.getString("name");
                    JSONArray actions = task.getJSONArray("actions");

                    Task.putActions(taskName, actions);
                }

                JSONObject config = json.getJSONObject("config");
                JSONObject translations = json.getJSONObject("translations");

            }
            CodeUtilities.log(Level.INFO, "Successfully loaded "+moduleFiles.length+" module"+(moduleFiles.length==1?"":"s")+"!");
        } else {
            CodeUtilities.log(Level.INFO, "No modules found.");
        }

    }

}
