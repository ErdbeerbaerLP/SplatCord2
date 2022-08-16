package de.erdbeerbaerlp.splatcord2.util.wiiu;

import de.erdbeerbaerlp.splatcord2.Main;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

public class RotationTimingUtil {
    public static final long rotationIncrement = TimeUnit.HOURS.toMillis(4);

    public static final int BASE_INDEX = 47;
    public static final long BASE_TIMESTAMP = 1658772000000L;


    public static int getRotationForInstant(Instant instant){
        final long mil = instant.toEpochMilli() - BASE_TIMESTAMP;
        return getRotationForLong(mil);
    }


    public static long getNextRotationStart(long input){
        long s = TimeUnit.MILLISECONDS.toHours(input)- TimeUnit.MILLISECONDS.toHours(BASE_TIMESTAMP);
        final long l = TimeUnit.HOURS.toMillis((long) (s - (s % 4d)));
        return BASE_TIMESTAMP+l+rotationIncrement;
    }

    public static int getOffsetRotationForInstant(Instant instant, int offset){
        return getRotationForLong((instant.toEpochMilli()+(offset*rotationIncrement)) - BASE_TIMESTAMP);
    }

    private static int getRotationForLong(long l){
        final int length = 180;
        long offset = l / rotationIncrement;
        while (offset+BASE_INDEX >= length) {
            offset-=length;
        }
        return (int) (BASE_INDEX+offset);
    }
}
