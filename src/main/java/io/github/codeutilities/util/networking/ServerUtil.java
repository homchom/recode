package io.github.codeutilities.util.networking;

import io.github.codeutilities.CodeUtilities;

public class ServerUtil {

    /**
     * Checks if the Minecraft client is currently on DiamondFire.
     *
     * @return True if connected to DF, otherwise false.
     */
    public static boolean isOnDF() {
        if (CodeUtilities.MC.getCurrentServerEntry() == null) return false;
        return CodeUtilities.MC.getCurrentServerEntry().address.contains(DFInfo.IP);
    }

}
