package io.github.codeutilities.util;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.toast.SystemToast;

import java.util.Arrays;
import java.util.List;

public class FuncSearchUtil {

    public static SearchType searchType;
    public static String searchValue;

    public static boolean shouldGlow(SignBlockEntity blockEntity) {
        if(searchType == null || searchValue == null) {
            return false;
        }

        if(blockEntity.getTextOnRow(0).getSiblings().size() > 0 && blockEntity.getTextOnRow(1).getSiblings().size() > 0) {
            return searchType.getSignText().contains(blockEntity.getTextOnRow(0).getSiblings().get(0).asString()) &&
                    searchValue.equals(blockEntity.getTextOnRow(1).getSiblings().get(0).asString());
        }else {
            return false;
        }
    }

    public static void beginSearch(SignBlockEntity signBlockEntity) {
        if(signBlockEntity.getTextOnRow(0).getSiblings().size() == 0||signBlockEntity.getTextOnRow(1).getSiblings().size() == 0) {
            clearSearch();
            return;
        }
        SearchType searchType = SearchType.getType(signBlockEntity.getTextOnRow(0).getSiblings().get(0).asString());
        String searchValue = signBlockEntity.getTextOnRow(1).getSiblings().get(0).asString();

        if(searchType == null || searchValue.length() == 0) {
            clearSearch();
        }else {
            if(FuncSearchUtil.searchType == searchType && FuncSearchUtil.searchValue.equals(searchValue)) {
                clearSearch();
                return;
            }
            FuncSearchUtil.searchType = searchType;
            FuncSearchUtil.searchValue = searchValue;
            ToasterUtil.sendToaster("Searching for", searchValue, SystemToast.Type.NARRATOR_TOGGLE);
        }
    }

    public static void clearSearch() {
        FuncSearchUtil.searchType = null;
        FuncSearchUtil.searchValue = null;
        ToasterUtil.sendToaster("Code Search", "Search Cleared!", SystemToast.Type.NARRATOR_TOGGLE);
    }

    public enum SearchType {
        FUNCTION(Arrays.asList("CALL FUNCTION", "FUNCTION")),
        PROCESS(Arrays.asList("START PROCESS", "PROCESS"));

        public List<String> signText;

        SearchType(List<String> signText) {
            this.signText = signText;
        }

        public List<String> getSignText() {
            return signText;
        }

        public static SearchType getType(String text) {
            for(SearchType searchType:values()) {
                if(searchType.getSignText().contains(text)) {
                    return searchType;
                }
            }
            return null;
        }
    }

}
