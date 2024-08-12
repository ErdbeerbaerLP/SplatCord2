package de.erdbeerbaerlp.splatcord2.util.wiiu;

import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.Phase;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon1.RotationByml;

import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RotationTimingUtil {

    public static int getRotationForInstant(Instant instant, final RotationByml rotation) {
        final long mil = instant.toEpochMilli();
        return getRotationForLong(mil, rotation);
    }

    private static long getBaseTimestamp(final RotationByml rotation) {
        return Instant.parse(rotation.root.DateTime.value).toEpochMilli();
    }


    public static long getNextRotationStart(long inTime, final RotationByml r) {
        long time = getBaseTimestamp(r);


        if (inTime < time) return 0;

        for (int i = 0; i < r.root.Phases.length; i++) {
            time += TimeUnit.HOURS.toMillis(r.root.Phases[i].getTime());
            if (time >= inTime) return time;
        }
        return time;
    }

    public static int getOffsetRotationForInstant(Instant instant, int offset, final RotationByml r) {
        final long inTime = instant.toEpochMilli();
        long time = getBaseTimestamp(r);


        if (inTime < time) return 0;

        int rot = -1;
        for (int i = 0; i < r.root.Phases.length; i++) {
            if (time >= inTime) {
                if (offset == 0)
                    return rot;
                else offset--;
            }
            rot = i;
            time += TimeUnit.HOURS.toMillis(r.root.Phases[i].getTime());

        }
        return rot;
    }

    private static int getRotationForLong(long inTime, final RotationByml r) {
        long time = getBaseTimestamp(r);

        if (inTime < time) return 0;

        int rot = -1;
        for (int i = 0; i < r.root.Phases.length; i++) {
            if (time >= inTime) return rot;
            rot = i;
            time += TimeUnit.HOURS.toMillis(r.root.Phases[i].getTime());
        }
        return rot;
    }

    public static HashMap<Long, Phase> getAllRotations(final RotationByml r) {
        final HashMap<Long, Phase> out = new HashMap<>();
        long ts = getBaseTimestamp(r);
        for (int i = 0; i < r.root.Phases.length - 1; i++) {
            final Phase rot = r.root.Phases[i];
            out.put(ts, rot);
            ts += TimeUnit.HOURS.toMillis(rot.getTime());
        }
        return out;
    }
}
