package de.erdbeerbaerlp.splatcord2.util.wiiu;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.RotationByml;

import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RotationTimingUtil {
    public static final long rotationIncrement = TimeUnit.HOURS.toMillis(4);

    public static int getRotationForInstant(Instant instant, final RotationByml rotation) {
        final long mil = instant.toEpochMilli() - getBaseTimestamp(rotation);
        return getRotationForLong(mil);
    }

    private static long getBaseTimestamp(final RotationByml rotation) {
        return Instant.parse(rotation.root.DateTime.value).toEpochMilli();
    }


    public static long getNextRotationStart(long input,final RotationByml rotation) {
        long s = TimeUnit.MILLISECONDS.toHours(input) - TimeUnit.MILLISECONDS.toHours(getBaseTimestamp(rotation));
        final long l = TimeUnit.HOURS.toMillis((long) (s - (s % 4d)));
        return getBaseTimestamp(rotation) + l + rotationIncrement;
    }

    public static int getOffsetRotationForInstant(Instant instant, int offset,final RotationByml rotation) {
        return getRotationForLong((instant.toEpochMilli() + (offset * rotationIncrement)) - getBaseTimestamp(rotation));
    }

    private static int getRotationForLong(long l) {
        final int length = 179;
        long offset = l / rotationIncrement;
        if(offset >= length) return length;

        return (int) (offset);
    }

    public static HashMap<Long, Phase> getAllRotations(final RotationByml r){
        final HashMap<Long, Phase> out = new HashMap<>();
        final long base = getBaseTimestamp(r);
        long ts = base;
        for(int i=0;i<179;i++){
            out.put(ts,r.root.Phases[getRotationForInstant(Instant.ofEpochMilli(ts), r)]);
            ts += rotationIncrement;
        }
        return out;
    }
}
