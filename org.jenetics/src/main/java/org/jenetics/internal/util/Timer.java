package org.jenetics.internal.util;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.jenetics.internal.time;

public final class Timer {

	private final Clock _clock;

    private final ThreadLocal<Instant> _start = new ThreadLocal<>();
    private final ThreadLocal<Instant> _stop = new ThreadLocal<>();

    private final AtomicLong _time = new AtomicLong(0L);

	public Timer(final Clock clock) {
		_clock = clock;
	}

	private void start() {
        _start.set(_clock.instant());
	}

	private void stop() {
        _stop.set(_clock.instant());
        _time.addAndGet(time.minus(_stop.get(), _start.get()).toMillis());
	}

    public Duration getTime() {
        return Duration.ofNanos(_time.get());
    }

    public <T> Supplier<T> timing(final Supplier<T> supplier) {
        return () -> {
            start();
            try {
                return supplier.get();
            } finally {
                stop();
            }
        };
    }


    public static Timer of(final Clock clock) {
        return new Timer(clock);
    }

    public static Timer of() {
        return of(NanoClock.INSTANCE);
    }

}
