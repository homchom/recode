package com.samstuff.codeplacer.codeblocks;

import com.samstuff.codeplacer.Mapping;
import com.samstuff.codeplacer.codeitems.*;

import java.util.ArrayList;
import java.util.List;

public class CodeBlock {
    String blockType;
    String signText;
    boolean hasChest;
    CodeBlockSignType signType;
    List<CodeTag> tags;
    List<CodeParameter> parameters;

    public boolean hasChest() {
        return this.hasChest;
    }

    public CodeBlockSignType getSignType() {
        return this.signType;
    }

    public List<CodeTag> getTags() {
        return this.tags;
    }
    public boolean hasTags() {
        return this.tags != null;
    }
    public void addTag(CodeTag tag) {
        if (tags == null) tags = new ArrayList<CodeTag>();
        tags.add(tag);
    }
    public void setTags(List<CodeTag> tags) {
        this.tags = tags;
    }

    public List<CodeParameter> getParameters() {
        return this.parameters;
    }
    public boolean hasParameters() {
        return this.parameters != null;
    }
    public void addParameter(CodeParameter parameter) {
        if (parameters == null) parameters = new ArrayList<CodeParameter>();
        parameters.add(parameter);
    }
    public void setParameters(List<CodeParameter> parameters) {
        this.parameters = parameters;
    }

    public String getBlockType() {
        return this.blockType;
    }

    public String getSignText() {
        return this.signText;
    }

    @Override
    public String toString() {
        return "CodeBlock{" +
                "blockType='" + blockType + '\'' +
                ", signText='" + signText + '\'' +
                ", hasChest=" + hasChest +
                ", signType=" + signType +
                ", tags=" + tags +
                ", parameters=" + parameters +
                '}';
    }

    CodeBlock(
            String blockType,
            String signText,
            boolean hasChest,
            CodeBlockSignType signType) {
        this.blockType = blockType;
        this.signText = signText;
        this.hasChest = hasChest;
        this.signType = signType;
        this.parameters = null;
        this.tags = null;
    }

    public static CodeBlock PlayerEvent(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.PLAYER_EVENT,
                action,
                false,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock PlayerAction(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.PLAYER_ACTION,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock IfPlayer(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.IF_PLAYER,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock EntityEvent(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.ENTITY_EVENT,
                action,
                false,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock EntityAction(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.ENTITY_ACTION,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock IfEntity(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.IF_ENTITY,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock ProcessEvent(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.PROCESS_EVENT,
                action,
                true,
                CodeBlockSignType.ASSIGN
        );
    }

    public static CodeBlock CallProcess(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.CALL_PROCESS,
                action,
                true,
                CodeBlockSignType.ASSIGN
        );
    }

    public static CodeBlock FunctionEvent(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.FUNCTION_EVENT,
                action,
                true,
                CodeBlockSignType.ASSIGN
        );
    }

    public static CodeBlock CallFunction(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.CALL_FUNCTION,
                action,
                false,
                CodeBlockSignType.ASSIGN
        );
    }

    public static CodeBlock GameAction(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.GAME_ACTION,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock IfGame(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.IF_GAME,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock SetVariable(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.SET_VARIABLE,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock IfVariable(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.IF_VARIABLE,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock Control(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.CONTROL,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock SelectObject(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.SELECT_OBJECT,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock Repeat(String action) {
        return new CodeBlock(
                Mapping.CodeBlockNames.REPEAT,
                action,
                true,
                CodeBlockSignType.MENU
        );
    }

    public static CodeBlock Else() {
        return new CodeBlock(
                Mapping.CodeBlockNames.ELSE,
                null, // no sign on the else
                true,
                null // no menu type
        );
    }
}
