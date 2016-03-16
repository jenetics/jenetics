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
package org.jenetics.tool.evaluation;

import static java.util.Objects.requireNonNull;

import java.io.Console;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.jenetics.internal.util.require;

import org.jenetics.tool.trial.Trial;
import org.jenetics.tool.trial.TrialMeter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.5
 * @since 3.5
 */
public abstract class AbstractRunner<P> {

	private final Supplier<TrialMeter<P>> _trialMeter;
	private final int _sampleCount;
	private final Path _resultPath;

	private volatile Thread _trialThread = null;
	private final AtomicBoolean _stop = new AtomicBoolean(false);

	protected AbstractRunner(
		final Supplier<TrialMeter<P>> trialMeter,
		final int sampleCount,
		final Path resultPath
	) {
		_trialMeter = requireNonNull(trialMeter);
		_sampleCount = require.positive(sampleCount);
		_resultPath = requireNonNull(resultPath);
	}

	protected abstract double[] fitness(final P param);

	public void start() {
		if (_trialThread != null) {
			throw new IllegalStateException("Trial thread already running.");
		}

		final Trial<P> trial = new Trial<>(
			this::fitness,
			_trialMeter,
			count -> count >= _sampleCount || _stop.get(),
			_resultPath
		);

		_trialThread = new Thread(trial);
		_trialThread.start();
	}

	public void join() throws InterruptedException {
		if (_trialThread == null) {
			throw new IllegalStateException("Trial thread is not running.");
		}

		try {
			final Console console = System.console();
			if (console != null) {
				final Thread interrupter = new Thread(() -> {
					String command;
					do {
						command = console.readLine();
						Trial.info("Got command '" + command + "'");
					} while (!"exit".equals(command));

					Trial.info("Stopping trial...");
					_trialThread.interrupt();
				});
				interrupter.setName("Console read thread");
				interrupter.setDaemon(true);
				interrupter.start();
			}

			_trialThread.join();
			Trial.info("Sopped trial.");
		} finally {
			_trialThread = null;
		}
	}

}
