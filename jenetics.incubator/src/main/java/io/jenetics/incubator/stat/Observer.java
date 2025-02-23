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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.stat;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Observer implements Supplier<Histogram> {
	private final Sampling sampling;
	private final Histogram.Partition partition;
	private final Executor executor;

	private Histogram histogram;

	public Observer(
		final Sampling sampling,
		final Histogram.Partition partition,
		final Executor executor
	) {
		this.sampling = requireNonNull(sampling);
		this.partition = requireNonNull(partition);
		this.executor = requireNonNull(executor);
	}

	@Override
	public synchronized Histogram get() {
		if (histogram == null) {
			executor.execute(() -> {
				histogram = new Histogram.Builder(partition).build(sampling);
			});
		}
		return histogram;
	}

	public Histogram.Partition partition() {
		return partition;
	}

}
