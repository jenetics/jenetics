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
package org.jenetics.tool.trial;

import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public class Trial<T> implements Runnable {

	private final Function<T, double[]> _function;
	private final Supplier<TrialMeter<T>> _trialMeter;
	private final Predicate<Integer> _stop;
	private final Path _resultPath;

	public Trial(
		final Function<T, double[]> function,
		final Supplier<TrialMeter<T>> trialMeter,
		final Predicate<Integer> stop,
		final Path resultPath
	) {
		_function = requireNonNull(function);
		_trialMeter = requireNonNull(trialMeter);
		_stop = requireNonNull(stop);
		_resultPath = requireNonNull(resultPath);
	}

	@Override
	public void run() {
		final TrialMeter<T> trialMeter;
		if (exists(_resultPath)) {
			trialMeter = TrialMeter.read(_resultPath);

			info("Continue existing trial: '%s'.", _resultPath.toAbsolutePath());
			info("    " + trialMeter);
		} else {
			trialMeter = _trialMeter.get();

			info("Writing results to '%s'.", _resultPath.toAbsolutePath());
		}

		while (!_stop.test(trialMeter.dataSize()) &&
			!Thread.currentThread().isInterrupted())
		{
			trialMeter.sample(param -> {
				//trialMeter.write(_resultPath);
				info(trialMeter.toString());

				return _function.apply(param);
			});

			trialMeter.write(_resultPath);
		}
	}

	private static final DateTimeFormatter FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	public static void info(final String pattern, final Object... params) {
		final LocalDateTime time = LocalDateTime.now();
		System.out.println(
			"" + FORMATTER.format(time) + " - " + format(pattern, params)
		);
		System.out.flush();
	}

}
