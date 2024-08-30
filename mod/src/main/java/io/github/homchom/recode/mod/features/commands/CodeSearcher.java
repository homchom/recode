package io.github.homchom.recode.mod.features.commands;

import io.github.homchom.recode.sys.renderer.ToasterUtil;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeSearcher {
    public static SearchType searchType;
    public static String searchValue;

    public static boolean isSignMatch(SignBlockEntity blockEntity) {
        if (searchType == null || searchValue == null) {
            return false;
        }

        return searchType.getSignText().contains(blockEntity.getText(true).getMessage(0, false).getString().trim()) &&
                searchValue.trim().equals(blockEntity.getText(true).getMessage(1, false).getString().trim());
    }

    public static void beginSearch(SearchType searchType, String searchValue) {
        CodeSearcher.searchType = searchType;
        CodeSearcher.searchValue = searchValue;
        ToasterUtil.sendToaster("Searching for", searchValue, SystemToast.SystemToastId.NARRATOR_TOGGLE);
    }

    public static void beginSearch(SignBlockEntity signBlockEntity) {
        SearchType searchType = SearchType.getType(signBlockEntity.getText(true).getMessage(0, false).getString());
        String searchValue = signBlockEntity.getText(true).getMessage(1, false).getString();

        if (searchType == null || searchValue.length() == 0) {
            clearSearch();
        } else {
            if (CodeSearcher.searchType == searchType && CodeSearcher.searchValue.equals(searchValue)) {
                clearSearch();
                return;
            }
            beginSearch(searchType, searchValue);
        }
    }

    public static void clearSearch() {
        if (searchType != null || searchValue != null)
            ToasterUtil.sendToaster("Code Search", "Search Cleared!", SystemToast.SystemToastId.NARRATOR_TOGGLE);
        CodeSearcher.searchType = null;
        CodeSearcher.searchValue = null;
    }

    public enum SearchType {
        PLAYER_ACTION("PLAYER ACTION"),
        IF_PLAYER("IF PLAYER"),
        FUNC("FUNCTION", "CALL FUNCTION"),
        ENTITY_EVENT("ENTITY EVENT"),
        SET_VAR("SET VARIABLE"),
        IF_ENTITY("IF ENTITY"),
        ENTITY_ACTION("ENTITY ACTION"),
        IF_VAR("IF VARIABLE"),
        SELECT_OBJ("SELECT OBJECT"),
        EVENT("PLAYER EVENT"),
        GAME_ACTION("GAME ACTION"),
        ELSE("ELSE"),
        PROCESS("PROCESS", "START PROCESS"),
        CONTROL("CONTROL"),
        REPEAT("REPEAT"),
        IF_GAME("IF GAME");

        public final List<String> signText;

        SearchType(String... signText) {
            this.signText = Arrays.asList(signText);
        }

        public static SearchType getType(String text) {
            for (SearchType searchType : values()) {
                if (searchType.getSignText().contains(text)) {
                    return searchType;
                }
            }
            return null;
        }

        public static final String[] STRINGS;

        static {
            List<String> stringList = new ArrayList<>();
            for (SearchType value : values()) {
                stringList.add(value.toString());
            }
            STRINGS = stringList.toArray(new String[0]);
        }


        public List<String> getSignText() {
            return signText;
        }
    }

}
