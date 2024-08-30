package io.github.homchom.recode.sys.player;

public class DFInfo {
    public static String patchId = "5.3";

    public static boolean isPatchNewer(String base, String target) {
        String[] baseSplit = base.split("\\.", 0);
        String[] targetSplit = target.split("\\.", 0);

        boolean oldNumberFound = false;

        int l = baseSplit.length;
        if (targetSplit.length > baseSplit.length) l = targetSplit.length;

        for (int i = 0; i < l; i++) {
            String currentBase = "0";
            String currentTarget = "0";

            if (baseSplit.length > i) currentBase = baseSplit[i];
            if (targetSplit.length > i) currentTarget = targetSplit[i];

            if (Integer.parseInt(currentBase) > Integer.parseInt(currentTarget)) {
                return true;
            } else {
                if (Integer.parseInt(currentBase) < Integer.parseInt(currentTarget)) {
                    oldNumberFound = true;
                }
            }
        }
        return !oldNumberFound;
    }
}
