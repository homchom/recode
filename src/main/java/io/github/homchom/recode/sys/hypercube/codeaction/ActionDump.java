package io.github.homchom.recode.sys.hypercube.codeaction;

import com.google.gson.*;
import io.github.homchom.recode.sys.file.ILoader;
import io.github.homchom.recode.sys.networking.WebUtil;

import java.util.*;

// TODO: move this to separate repo
public class ActionDump implements ILoader {

    private static JsonObject types;
    private static final HashMap<String, Action> actions = new HashMap<>();
    private static final HashMap<String, CodeBlock> codeblocks = new HashMap<>();

    @Override
    public void load() {
        try{
            JsonObject actionDump = WebUtil.getObject(
                "https://raw.githubusercontent.com/CodeUtilities/data/main/actiondump/db.json");
            if (!actionDump.isJsonNull()){
                // gather codeblocks
                JsonArray codeblockArray = actionDump.getAsJsonArray("codeblocks");
                codeblockArray.forEach(jsonElement -> {
                    JsonObject codeblock = jsonElement.getAsJsonObject();
                    codeblocks.put(codeblock.get("name").getAsString(), new CodeBlock(codeblock));
                });

                // gather actions
                HashMap<String, Integer> dupes = new HashMap<>();
                JsonArray actionsArray = actionDump.getAsJsonArray("actions");
                actionsArray.forEach(jsonElement -> {
                    JsonObject action = jsonElement.getAsJsonObject();
                    String actionname = action.get("name").getAsString();
                    dupes.put(actionname, dupes.containsKey(actionname) ? (dupes.get(actionname)+1) : 1);
                    actions.put(actionname + dupes.get(actionname), new Action(action));
                });
            }

            types = WebUtil.getObject("https://raw.githubusercontent.com/CodeUtilities/data/main/actiondump/types.json");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static ArrayList<Action> getActions(String action){
        ArrayList<String> queries = autoCompleteActions(action);

        ArrayList<Action> results = new ArrayList<>();
        for(String query : queries){
            results.add(actions.get(query));
        }

        return results;
    }

    public static ArrayList<CodeBlock> getCodeBlock(String codeblock){
        ArrayList<String> queries = autoCompleteCodeBlocks(codeblock);

        ArrayList<CodeBlock> results = new ArrayList<>();
        for(String query : queries){
            results.add(codeblocks.get(query));
        }

        return results;
    }

    private static ArrayList<String> autoCompleteActions(String query){
        ArrayList<String> results = new ArrayList<>();
        for(String s : actions.keySet()){
            if (!actions.get(s).getIcon().getName().equals("")){
                if (s.equalsIgnoreCase(query) || actions.get(s).getIcon().getName().equalsIgnoreCase(query)){
                    results.add(0, s);
                }
                if (s.toLowerCase().contains(query.toLowerCase()) || actions.get(s).getIcon().getName().toLowerCase().contains(query.toLowerCase())){
                    results.add(s);
                }else{
                    for(String alias : actions.get(s).getAliases()){
                        if (alias.toLowerCase().contains(query.toLowerCase())){
                            results.add(s);
                            break;
                        }
                    }
                }
            }
        }
        return results;
    }

    private static ArrayList<String> autoCompleteCodeBlocks(String query){
        ArrayList<String> results = new ArrayList<>();
        for(Map.Entry<String, CodeBlock> entry : codeblocks.entrySet()){
            if (entry.getValue().getIdentifier().equalsIgnoreCase(query)){
                results.add(0, entry.getKey());
                continue;
            }
            if (entry.getKey().equalsIgnoreCase(query)){
                results.add(0, entry.getKey());
                continue;
            }
            if (entry.getValue().getIdentifier().toLowerCase().contains(query.toLowerCase())){
                results.add(entry.getKey());
                continue;
            }
            if (entry.getKey().toLowerCase().contains(query.toLowerCase())){
                results.add(entry.getKey());
                continue;
            }
        }
        return results;
    }



    public <T> T deepCopy(T object, Class<T> type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(gson.toJson(object, type), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Types valueOf(String id){
        try{
            JsonObject type = types.getAsJsonObject(id);
            return new Types(type.get("id").getAsString(), type.get("color").getAsString());
        }catch(Exception e){
            return null;
        }
    }

}