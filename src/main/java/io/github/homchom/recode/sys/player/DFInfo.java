package io.github.homchom.recode.sys.player;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.server.*;
import io.github.homchom.recode.sys.networking.LegacyState;
import net.minecraft.world.phys.Vec3;

public class DFInfo {

    public static final String IP = "mcdiamondfire.com";
    public static String patchId = "5.3";
    public static LegacyState.CurrentState currentState = new LegacyState.CurrentState();
    public static boolean isInBeta = false;
    public static Vec3 plotCorner = null;

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

    @Deprecated
    // use isOnDF in DFState (DF.isOnDF()) instead
    public static boolean isOnDF() {
        if (LegacyRecode.MC.getCurrentServer() == null) return false;
        return LegacyRecode.MC.getCurrentServer().ip.contains("mcdiamondfire.com");
    }

    public static void setCurrentState(LegacyState state) {
        LegacyState.CurrentState newState = new LegacyState.CurrentState(state);
        if (!currentState.equals(newState)) {
            ChangeDFStateEvent.INSTANCE.invoke(new StateChange(newState, currentState));
        }
        currentState = newState;
    }
}
