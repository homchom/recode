package io.github.homchom.recode.mod.features.commands;

import io.github.homchom.recode.sys.renderer.ToasterUtil;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.*;

public class CodeSearcher {

    public static SearchType searchType;
    public static String searchValue;

    public static boolean shouldGlow(SignBlockEntity blockEntity) {
        if (searchType == null || searchValue == null) {
            return false;
        }

        return searchType.getSignText().contains(blockEntity.getMessage(0, false).getString().trim()) &&
                searchValue.trim().equals(blockEntity.getMessage(1, false).getString().trim());
    }

    public static void beginSearch(SearchType searchType, String searchValue) {
        CodeSearcher.searchType = searchType;
        CodeSearcher.searchValue = searchValue;
        ToasterUtil.sendToaster("Searching for", searchValue, SystemToast.SystemToastIds.NARRATOR_TOGGLE);
    }

    public static void beginSearch(SignBlockEntity signBlockEntity) {
        SearchType searchType = SearchType.getType(signBlockEntity.getMessage(0, false).getString());
        String searchValue = signBlockEntity.getMessage(1, false).getString();

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
            ToasterUtil.sendToaster("Code Search", "Search Cleared!", SystemToast.SystemToastIds.NARRATOR_TOGGLE);
        CodeSearcher.searchType = null;
        CodeSearcher.searchValue = null;
    }

    public enum SearchType {
        PLAYER_ACTION(Collections.singletonList("PLAYER ACTION")),
        IF_PLAYER(Collections.singletonList("IF PLAYER")),
        FUNC(Arrays.asList("CALL FUNCTION", "FUNCTION")),
        ENTITY_EVENT(Collections.singletonList("ENTITY EVENT")),
        SET_VAR(Collections.singletonList("SET VARIABLE")),
        IF_ENTITY(Collections.singletonList("IF ENTITY")),
        ENTITY_ACTION(Collections.singletonList("ENTITY ACTION")),
        IF_VAR(Collections.singletonList("IF VARIABLE")),
        SELECT_OBJ(Collections.singletonList("SELECT OBJECT")),
        EVENT(Collections.singletonList("PLAYER EVENT")),
        GAME_ACTION(Collections.singletonList("GAME ACTION")),
        ELSE(Collections.singletonList("ELSE")),
        PROCESS(Arrays.asList("START PROCESS", "PROCESS")),
        CONTROL(Collections.singletonList("CONTROL")),
        REPEAT(Collections.singletonList("REPEAT")),
        IF_GAME(Collections.singletonList("IF GAME"));

        public final List<String> signText;

        SearchType(List<String> signText) {
            this.signText = signText;
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
