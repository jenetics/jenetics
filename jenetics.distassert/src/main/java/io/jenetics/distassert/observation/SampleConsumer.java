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
package io.jenetics.distassert.observation;

import static java.util.Objects.requireNonNull;

import java.util.function.DoubleConsumer;
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
public interface SampleConsumer {

	/**
	 * Adding a new sample value.
	 *
	 * @param sample the sample value
	 * @return {@code this} samples object for method chaining
	 */
	SampleConsumer accept(double sample);

	/**
	 * Adding a new sample value.
	 *
	 * @param sample the sample value
	 * @return {@code this} samples object for method chaining
	 */
	default SampleConsumer accept(final Number sample) {
		accept(sample.doubleValue());
		return this;
	}

	/**
	 * Adding a new sample value.
	 *
	 * @param samples the sample values
	 * @return {@code this} samples object for method chaining
	 */
	default SampleConsumer acceptAll(final int... samples) {
		for (var sample : samples) {
			accept(sample);
		}
		return this;
	}

	/**
	 * Adding a new sample value.
	 *
	 * @param samples the sample values
	 * @return {@code this} samples object for method chaining
	 */
	default SampleConsumer acceptAll(final long... samples) {
		for (var sample : samples) {
			accept(sample);
		}
		return this;
	}

	/**
	 * Adding a new sample value.
	 *
	 * @param samples the sample values
	 * @return {@code this} samples object for method chaining
	 */
	default SampleConsumer acceptAll(final double... samples) {
		for (var sample : samples) {
			accept(sample);
		}
		return this;
	}

	/**
	 * Adding a new sample value.
	 *
	 * @param samples the sample values
	 * @return {@code this} samples object for method chaining
	 */
	default SampleConsumer acceptAll(final Iterable<? extends Number> samples) {
		samples.forEach(this::accept);
		return this;
	}

	/**
	 * Adding a new sample value.
	 *
	 * @param samples the sample values
	 * @return {@code this} samples object for method chaining
	 */
	default SampleConsumer acceptAll(final DoubleStream samples) {
		samples.forEach(this::accept);
		return this;
	}

	/**
	 * Adding a new sample value.
	 *
	 * @param samples the sample values
	 * @return {@code this} samples object for method chaining
	 */
	default SampleConsumer acceptAll(final Stream<? extends Number> samples) {
		samples.forEach(this::accept);
		return this;
	}

	/**
	 * Create a new sample consumer from the given double consumer.
	 *
	 * @param consumer the double consumer to be wrapped into a sample consumer
	 * @return a new sample consumer wrapping the given double consumer
	 */
	static SampleConsumer of(final DoubleConsumer consumer) {
		requireNonNull(consumer);
		return new SampleConsumer() {
			@Override
			public SampleConsumer accept(final double sample) {
				consumer.accept(sample);
				return this;
			}
		};
	}

}
