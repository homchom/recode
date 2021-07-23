package io.github.codeutilities.sys.player;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.events.interfaces.HyperCubeEvents;
import io.github.codeutilities.sys.networking.State;
import net.minecraft.util.math.Vec3d;

public class DFInfo {

    public static final String IP = "mcdiamondfire.com";
    public static String patchId = "5.3";
    public static State.CurrentState currentState = new State.CurrentState();
    public static boolean isInBeta = false;
    public static Vec3d plotCorner = null;

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

    public static boolean isOnDF() {
        if (CodeUtilities.MC.getCurrentServerEntry() == null) return false;
        return CodeUtilities.MC.getCurrentServerEntry().address.contains("mcdiamondfire.com");
    }

    public static void setCurrentState(State state) {
        State.CurrentState newstate = new State.CurrentState(state);
        if(!currentState.equals(newstate)) HyperCubeEvents.CHANGE_STATE.invoker().update(newstate, currentState);
        currentState = newstate;
    }

    public static float TPS = 0.0f;
    private static long lastTpsTimestamp = 0;

    public static void calculateTps(long packetTimestamp) {
        if (!RenderSystem.isOnRenderThread()) {
            return;
        }

        if (lastTpsTimestamp == 0) {
            lastTpsTimestamp = packetTimestamp;
            return;
        }
        if (packetTimestamp - lastTpsTimestamp == 0) {
            lastTpsTimestamp = packetTimestamp;
            return;
        }

        long milliDiff = packetTimestamp - lastTpsTimestamp;
        float secDiff = milliDiff / 1000f;

        TPS = 20.0f / secDiff;
        lastTpsTimestamp = packetTimestamp;
    }
}
