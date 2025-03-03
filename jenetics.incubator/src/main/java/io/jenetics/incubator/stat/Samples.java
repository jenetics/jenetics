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

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * This functional interface serves as a sink for sample values.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Samples {

	/**
	 * Adding a new sample value.
	 *
	 * @param sample the sample value
	 */
	void add(double sample);

	/**
	 * Adding a new sample value.
	 *
	 * @param sample the sample value
	 */
	default void add(final Number sample) {
		add(sample.doubleValue());
	}

	/**
	 * Adding a new sample values.
	 *
	 * @param samples the sample values
	 */
	default void addAll(final int... samples) {
		for (var sample : samples) {
			add(sample);
		}
	}

	/**
	 * Adding a new sample values.
	 *
	 * @param samples the sample values
	 */
	default void addAll(final long... samples) {
		for (var sample : samples) {
			add(sample);
		}
	}

	/**
	 * Adding a new sample values.
	 *
	 * @param samples the sample values
	 */
	default void addAll(final double... samples) {
		for (var sample : samples) {
			add(sample);
		}
	}

	/**
	 * Adding a new sample values.
	 *
	 * @param samples the sample values
	 */
	default void addAll(final Iterable<? extends Number> samples) {
		samples.forEach(this::add);
	}

	/**
	 * Adding a new sample values.
	 *
	 * @param samples the sample values
	 */
	default void addAll(final DoubleStream samples) {
		samples.forEach(this::add);
	}

	/**
	 * Adding a new sample values.
	 *
	 * @param samples the sample values
	 */
	default void addAll(final Stream<? extends Number> samples) {
		samples.forEach(this::add);
	}

}
