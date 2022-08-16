package de.erdbeerbaerlp.splatcord2.util.wiiu;

import de.erdbeerbaerlp.splatcord2.Main;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class RotationTimingUtil {
    public static final long rotationIncrement = TimeUnit.HOURS.toMillis(4);




    public static int getRotationForInstant(Instant instant){
        final long mil = instant.toEpochMilli() - getBaseTimestamp();
        return getRotationForLong(mil);
    }

    private static long getBaseTimestamp() {
        return Instant.parse(Main.s1rotations.root.DateTime.value).toEpochMilli();
    }


    public static long getNextRotationStart(long input){
        long s = TimeUnit.MILLISECONDS.toHours(input)- TimeUnit.MILLISECONDS.toHours(getBaseTimestamp());
        final long l = TimeUnit.HOURS.toMillis((long) (s - (s % 4d)));
        return getBaseTimestamp()+l+rotationIncrement;
    }

    public static int getOffsetRotationForInstant(Instant instant, int offset){
        return getRotationForLong((instant.toEpochMilli()+(offset*rotationIncrement)) - getBaseTimestamp());
    }

    private static int getRotationForLong(long l){
        final int length = 179;
        long offset = l / rotationIncrement;
        while (offset+0 >= length) {
            offset-=length;
        }
        return (int) (0+offset);
    }
}
