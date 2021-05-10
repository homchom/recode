package io.github.codeutilities.util.templates;

import io.github.codeutilities.util.render.ToasterUtil;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.toast.SystemToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchUtil {

    public static SearchType searchType;
    public static String searchValue;

    public static boolean shouldGlow(SignBlockEntity blockEntity) {
        if (searchType == null || searchValue == null) {
            return false;
        }

        return searchType.getSignText().contains(blockEntity.getTextOnRow(0).getString().trim()) &&
                searchValue.equals(blockEntity.getTextOnRow(1).getString().trim());
    }

    public static void beginSearch(SearchType searchType, String searchValue) {
        SearchUtil.searchType = searchType;
        SearchUtil.searchValue = searchValue;
        ToasterUtil.sendToaster("Searching for", searchValue, SystemToast.Type.NARRATOR_TOGGLE);
    }

    public static void beginSearch(SignBlockEntity signBlockEntity) {
        SearchType searchType = SearchType.getType(signBlockEntity.getTextOnRow(0).getString());
        String searchValue = signBlockEntity.getTextOnRow(1).getString();

        if (searchType == null || searchValue.length() == 0) {
            clearSearch();
        } else {
            if (SearchUtil.searchType == searchType && SearchUtil.searchValue.equals(searchValue)) {
                clearSearch();
                return;
            }
            beginSearch(searchType, searchValue);
        }
    }

    public static void clearSearch() {
        if (searchType != null || searchValue != null)
            ToasterUtil.sendToaster("Code Search", "Search Cleared!", SystemToast.Type.NARRATOR_TOGGLE);
        SearchUtil.searchType = null;
        SearchUtil.searchValue = null;
    }

    public enum SearchType {
        FUNCTION(Arrays.asList("CALL FUNCTION", "FUNCTION")),
        PROCESS(Arrays.asList("START PROCESS", "PROCESS")),
        PLAYER_EVENT(Collections.singletonList("PLAYER EVENT")),
        PLAYER_ACTION(Collections.singletonList("PLAYER ACTION")),
        IF_PLAYER(Collections.singletonList("IF PLAYER")),
        SET_VARIABLE(Collections.singletonList("SET VARIABLE")),
        IF_VARIABLE(Collections.singletonList("IF VARIABLE")),
        GAME_ACTION(Collections.singletonList("GAME ACTION")),
        IF_GAME(Collections.singletonList("IF GAME")),
        ENTITY_ACTION(Collections.singletonList("ENTITY ACTION")),
        ENTITY_EVENT(Collections.singletonList("ENTITY EVENT")),
        IF_ENTITY(Collections.singletonList("IF ENTITY")),
        CONTROL(Collections.singletonList("CONTROL")),
        SELECT_OBJECT(Collections.singletonList("SELECT OBJECT")),
        REPEAT(Collections.singletonList("REPEAT"));

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
