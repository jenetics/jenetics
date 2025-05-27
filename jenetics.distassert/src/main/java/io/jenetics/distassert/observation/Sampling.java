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

/**
 * This functional interface represents a sampling task. It is used for
 * separating generation of the sample points from the actual execution.
 * {@snippet class="ObservationSnippets" region="SamplingHistogram"}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Sampling {

	/**
	 * Writes {@code this} sampling to the given consumer.
	 *
	 * @param consumer the sample value <em>sink</em>
	 */
	void writeTo(final SampleConsumer consumer);

	/**
	 * Create a new sampling object which repeats the given sub{@code sampling}
	 *
	 * @param count number of times to repeat
	 * @param sampling the subsampling to be repeated
	 * @return a new sampling
	 */
	static Sampling repeat(final int count, final Sampling sampling) {
		requireNonNull(sampling);
		return consumer -> {
			for (int i = 0; i < count; ++i) {
				sampling.writeTo(consumer);
			}
		};
	}

}
