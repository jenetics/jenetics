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

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-05 $</em>
 */
public final class Timer {

	private final Clock _clock;

    private Instant _start;
    private Instant _stop;

	private Timer(final Clock clock) {
		_clock = requireNonNull(clock);
	}

	public Timer start() {
        _start = _clock.instant();
		return this;
	}

	public Timer stop() {
		_stop = _clock.instant();
		return this;
	}

    public Duration getTime() {
        return time.minus(_stop, _start);
    }


    public static Timer of(final Clock clock) {
        return new Timer(clock);
    }

    public static Timer of() {
        return of(NanoClock.INSTANCE);
    }

}
