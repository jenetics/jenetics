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
package org.jenetics.internal.engine;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

import org.jenetics.internal.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-01 $</em>
 */
public abstract class StageResult {

	protected final Timer _timer;

	protected StageResult(final Timer timer) {
		_timer = requireNonNull(timer);
	}

	public <T> Supplier<T> timing(final Supplier<T> supplier) {
		return () -> {
			_timer.start();
			try {
				return supplier.get();
			} finally {
				_timer.stop();
			}
		};
	}

	public Timer getTimer() {
		return _timer;
	}

}
