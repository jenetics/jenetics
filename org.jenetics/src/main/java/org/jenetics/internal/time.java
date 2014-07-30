package org.jenetics.internal;

import java.time.Duration;
import java.time.Instant;

import org.jenetics.internal.util.NanoClock;
import org.jenetics.util.StaticObject;

public class time extends StaticObject {
    private time() {}

    public static final long NANOS_PER_SECOND = 1_000_000_000;

    public static Duration minus(final Instant a, final Instant b)  {
        final long seconds = a.getEpochSecond() - b.getEpochSecond();
        final long nanos = a.getNano() - b.getNano();

        return Duration.ofNanos(seconds*NANOS_PER_SECOND + nanos);
    }
}
