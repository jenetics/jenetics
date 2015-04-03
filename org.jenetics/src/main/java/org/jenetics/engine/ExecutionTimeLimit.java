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
package org.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.1
 * @version 3.1
 */
final class ExecutionTimeLimit implements Predicate<Object>  {

	private final Duration _duration;
	private final Clock _clock;

	private Instant _start = Instant.MIN;

	ExecutionTimeLimit(final Duration duration, final Clock clock) {
		_duration = requireNonNull(duration);
		_clock = requireNonNull(clock);
	}

	@Override
	public boolean test(final Object ignore) {
		if (_start == Instant.MIN) {
			_start = _clock.instant();
		}

		return _start.plus(_duration).isAfter(_clock.instant());
	}

}
