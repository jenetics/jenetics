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

import java.util.function.DoubleConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * This functional interface represents a data sample. It is used for
 * separating generation of the sample points from the actual execution.
 * {@snippet class="ObservationSnippets" region="SamplingHistogram"}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Sample {

	/**
	 * Writes {@code this} sampling to the given consumer.
	 *
	 * @param consumer the sample value <em>sink</em>
	 */
	void writeTo(final Consumer consumer);

	/**
	 * Create a new sampling object which repeats the given sub{@code sampling}
	 *
	 * @param count number of times to repeat
	 * @param sample the subsampling to be repeated
	 * @return a new sampling
	 */
	static Sample repeat(final int count, final Sample sample) {
		requireNonNull(sample);
		return consumer -> {
			for (int i = 0; i < count; ++i) {
				sample.writeTo(consumer);
			}
		};
	}

	/**
	 * This functional interface serves as a sink for sample values.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	@FunctionalInterface
	interface Consumer {

		/**
		 * Adding a new sample value.
		 *
		 * @param sample the sample value
		 * @return {@code this} samples object for method chaining
		 */
		Consumer accept(double sample);

		/**
		 * Adding a new sample value.
		 *
		 * @param sample the sample value
		 * @return {@code this} samples object for method chaining
		 */
		default Consumer accept(final Number sample) {
			accept(sample.doubleValue());
			return this;
		}

		/**
		 * Adding a new sample value.
		 *
		 * @param samples the sample values
		 * @return {@code this} samples object for method chaining
		 */
		default Consumer acceptAll(final int... samples) {
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
		default Consumer acceptAll(final long... samples) {
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
		default Consumer acceptAll(final double... samples) {
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
		default Consumer acceptAll(final Iterable<? extends Number> samples) {
			samples.forEach(this::accept);
			return this;
		}

		/**
		 * Adding a new sample value.
		 *
		 * @param samples the sample values
		 * @return {@code this} samples object for method chaining
		 */
		default Consumer acceptAll(final DoubleStream samples) {
			samples.forEach(this::accept);
			return this;
		}

		/**
		 * Adding a new sample value.
		 *
		 * @param samples the sample values
		 * @return {@code this} samples object for method chaining
		 */
		default Consumer acceptAll(final Stream<? extends Number> samples) {
			samples.forEach(this::accept);
			return this;
		}

		/**
		 * Create a new sample consumer from the given double consumer.
		 *
		 * @param consumer the double consumer to be wrapped into a sample consumer
		 * @return a new sample consumer wrapping the given double consumer
		 */
		static Consumer of(final DoubleConsumer consumer) {
			requireNonNull(consumer);
			return new Consumer() {
				@Override
				public Consumer accept(final double sample) {
					consumer.accept(sample);
					return this;
				}
			};
		}

	}
}
