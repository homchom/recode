package com.samstuff.codeplacer;

import com.samstuff.codeplacer.codeblocks.CodeBlock;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class Mapping {
    public class CodeBlockNames {
        public final static String PLAYER_EVENT = "event";
        public final static String PLAYER_ACTION = "player_action";
        public final static String IF_PLAYER = "if_player";
        public final static String ENTITY_EVENT = "entity_event";
        public final static String ENTITY_ACTION = "entity_action";
        public final static String IF_ENTITY = "if_entity";
        public final static String PROCESS_EVENT = "process";
        public final static String CALL_PROCESS = "start_process";
        public final static String FUNCTION_EVENT = "func";
        public final static String CALL_FUNCTION = "call_func";
        public final static String GAME_ACTION = "game_action";
        public final static String IF_GAME = "if_game";
        public final static String SET_VARIABLE = "set_var";
        public final static String IF_VARIABLE = "if_var";
        public final static String CONTROL = "control";
        public final static String SELECT_OBJECT = "select_obj";
        public final static String REPEAT = "repeat";
        public final static String ELSE = "else";
    }
    public class CodeParameterNames {
        public final static String TEXT = "txt";
        public final static String NUMBER = "num";
        public final static String LOCATION = "loc";
        public final static String SOUND = "snd";
        public final static String POTION = "pot";
        public final static String VARIABLE = "var";
        public final static String GAME_VALUE = "g_var";
        public final static String SPECIAL_SPAWN_EGG = "ssegg";
        public final static String ITEM = "item";
    }
    public class CodeParameterVariableType {
        public final static String NORMAL = ""; //TODO: Find normal variable scope
        public final static String SAVED = "saved";
        public final static String LOCAL = "local"; // TODO: Find local variable scope
    }
    public class CodeSelection {
        //TODO: Add CodeSelection
    }

    private static Dictionary<String,String> codeBlockNames;
    private static Dictionary<String,Boolean> codeBlockHasAction;
    private static List<String> specialSpawnEggNames;

    static {
        codeBlockNames = new Hashtable<String,String>();
        // register the displayed name (name of item) of each code block
        codeBlockNames.put(CodeBlockNames.PLAYER_EVENT,"Player Action");

        codeBlockHasAction = new Hashtable<String,Boolean>();
        // will assume it to be true, unless stated here to be false
        codeBlockHasAction.put(CodeBlockNames.ELSE,false);

        specialSpawnEggNames = new ArrayList<String>();
        specialSpawnEggNames.add("§eIron Golem-minecraft:polar_bear_spawn_egg");
        specialSpawnEggNames.add("§eKiller Bunny-minecraft:rabbit_spawn_egg");
        specialSpawnEggNames.add("§eSnow Golem-minecraft:ghast_spawn_egg");
        specialSpawnEggNames.add("§eIllusioner-minecraft:villager_spawn_egg");
        specialSpawnEggNames.add("§eGiant-minecraft:zombie_spawn_egg");
        specialSpawnEggNames.add("§eWither-minecraft:wither_skeleton_spawn_egg");
        specialSpawnEggNames.add("§eEnderdragon-minecraft:enderman_spawn_egg");
    }

    public static String getCodeBlockDisplayName(String codeBlockName) {
        return codeBlockNames.get(codeBlockName);
    }

    public static Boolean getCodeBlockHasAction(String codeBlockName) {
        if (codeBlockHasAction.get(codeBlockName) == null) {
            return true;
        }
        return codeBlockHasAction.get(codeBlockName);
    }

    public static CodeBlock constructCodeBlock(String name, String action) {
        switch (name) { // matches the name up with its respective codeblock
            case CodeBlockNames.PLAYER_EVENT:
                return CodeBlock.PlayerEvent(action);

            case CodeBlockNames.PLAYER_ACTION:
                return CodeBlock.PlayerAction(action);

            case CodeBlockNames.IF_PLAYER:
                return CodeBlock.IfPlayer(action);

            case CodeBlockNames.ENTITY_EVENT:
                return CodeBlock.EntityEvent(action);

            case CodeBlockNames.ENTITY_ACTION:
                return CodeBlock.EntityAction(action);

            case CodeBlockNames.IF_ENTITY:
                return CodeBlock.IfEntity(action);

            case CodeBlockNames.PROCESS_EVENT:
                return CodeBlock.ProcessEvent(action);

            case CodeBlockNames.CALL_PROCESS:
                return CodeBlock.CallProcess(action);

            case CodeBlockNames.FUNCTION_EVENT:
                return CodeBlock.FunctionEvent(action);

            case CodeBlockNames.CALL_FUNCTION:
                return CodeBlock.CallFunction(action);

            case CodeBlockNames.GAME_ACTION:
                return CodeBlock.GameAction(action);

            case CodeBlockNames.IF_GAME:
                return CodeBlock.IfGame(action);

            case CodeBlockNames.SET_VARIABLE:
                return CodeBlock.SetVariable(action);

            case CodeBlockNames.IF_VARIABLE:
                return CodeBlock.IfVariable(action);

            case CodeBlockNames.CONTROL:
                return CodeBlock.Control(action);

            case CodeBlockNames.SELECT_OBJECT:
                return CodeBlock.SelectObject(action);

            case CodeBlockNames.REPEAT:
                return CodeBlock.Repeat(action);

            case CodeBlockNames.ELSE:
                return CodeBlock.Else();
        }
        return null;
    }

    public static boolean isItemSpecialSpawnEgg(String name, String itemType) {
        String key = name + "-" + itemType;
        return specialSpawnEggNames.contains(key);
    }
}
