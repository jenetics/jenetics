/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-01 $</em>
 */
public final class Timer {

	private final Clock _clock;

    private final ThreadLocal<Instant> _start = new ThreadLocal<>();
    private final ThreadLocal<Instant> _stop = new ThreadLocal<>();

    private final AtomicLong _time = new AtomicLong(0L);

	public Timer(final Clock clock) {
		_clock = clock;
	}

	public void start() {
        _start.set(_clock.instant());
	}

	public void stop() {
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
