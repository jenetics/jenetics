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

import static java.lang.String.format;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ConcurrencyTest {

	//@org.testng.annotations.Test
	public void cpuTime() {
		final Random random = new Random(123);

		final List<Runnable> runnables = IntStream.range(0, 100)
			.mapToObj(i -> new Sleeper(i, random.nextDouble() > 0.1 ? 50 : 1000))
			.collect(Collectors.toList());

		final long start = System.currentTimeMillis();
		try (Concurrency concurrency = Concurrency.withCommonPool()) {
			concurrency.execute(runnables);
		}
		final long stop = System.currentTimeMillis();
		System.out.println("Runtime: " + (stop - start)/1000.0);
	}

	private static final class Sleeper implements Runnable {
		private final int _task;
		private final long _sleep;

		Sleeper(final int task, final long sleep) {
			_task = task;
			_sleep = sleep;
		}

		@Override
		public void run() {
			try {
				System.out.println(format(
					"Task: %d, thread %d: %d",
					_task, Thread.currentThread().getId(), _sleep
				));

				Thread.sleep(_sleep);
			} catch (InterruptedException e) {
				throw new CancellationException(e.getMessage());
			}
		}
	}

}
