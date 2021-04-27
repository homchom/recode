package io.github.codeutilities.util.templates;

import io.github.codeutilities.util.render.ToasterUtil;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.toast.SystemToast;

import java.util.Arrays;
import java.util.List;

public class FuncSearchUtil {

    public static SearchType searchType;
    public static String searchValue;

    public static boolean shouldGlow(SignBlockEntity blockEntity) {
        if (searchType == null || searchValue == null) {
            return false;
        }

        return searchType.getSignText().contains(blockEntity.getTextOnRow(0).getString()) &&
                searchValue.equals(blockEntity.getTextOnRow(1).getString());
    }

    public static void beginSearch(SignBlockEntity signBlockEntity) {
        SearchType searchType = SearchType.getType(signBlockEntity.getTextOnRow(0).getString());
        String searchValue = signBlockEntity.getTextOnRow(1).getString();

        if (searchType == null || searchValue.length() == 0) {
            clearSearch();
        } else {
            if (FuncSearchUtil.searchType == searchType && FuncSearchUtil.searchValue.equals(searchValue)) {
                clearSearch();
                return;
            }
            FuncSearchUtil.searchType = searchType;
            FuncSearchUtil.searchValue = searchValue;
            ToasterUtil.sendToaster("Searching for", searchValue, SystemToast.Type.NARRATOR_TOGGLE);
        }
    }

    public static void clearSearch() {
        if (searchType != null || searchValue != null) ToasterUtil.sendToaster("Code Search", "Search Cleared!", SystemToast.Type.NARRATOR_TOGGLE);
        FuncSearchUtil.searchType = null;
        FuncSearchUtil.searchValue = null;
    }

    public enum SearchType {
        FUNCTION(Arrays.asList("CALL FUNCTION", "FUNCTION")),
        PROCESS(Arrays.asList("START PROCESS", "PROCESS"));

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

        public List<String> getSignText() {
            return signText;
        }
    }

}
