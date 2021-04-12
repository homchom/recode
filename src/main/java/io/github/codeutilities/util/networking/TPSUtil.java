package io.github.codeutilities.util.networking;

import java.math.BigDecimal;

public class TPSUtil {

    private static final BigDecimal dValue = new BigDecimal(1000);
    public static float TPS = 0.0f;
    private static long lastTpsTimestamp = 0;

    public static void calculateTps(long packetTimestamp) {
        if (!Thread.currentThread().getName().contains("Render thread")) {
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
