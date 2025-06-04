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
package io.jenetics.distassert.assertion;

import static java.util.Objects.requireNonNull;

import io.jenetics.distassert.distribution.Distribution;
import io.jenetics.distassert.observation.Histogram;

/**
 * Interface for statistical hypothesis testers. It checks if a given observation,
 * given as histogram, follows a given distribution, the zero-hypothesis.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
@FunctionalInterface
public interface HypothesisTester {

	/**
	 * The test result object.
	 */
	sealed interface Result {

		/**
		 * Return the zero-hypothesis.
		 *
		 * @return the zero-hypothesis
		 */
		Distribution hypothesis();

		/**
		 * Return the observation to be tested.
		 *
		 * @return the observation to be tested
		 */
		Histogram observation();

		/**
		 * Return the result message.
		 *
		 * @return the result message
		 */
		String message();
	}

	/**
	 * This object is returned if the hypothesis has been accepted.
	 *
	 * @param observation the observation to be tested
	 * @param hypothesis the zero-hypothesis
	 * @param message the result message
	 */
	record Accept(Distribution hypothesis, Histogram observation, String message)
		implements Result
	{
		public Accept {
			requireNonNull(hypothesis);
			requireNonNull(observation);
			requireNonNull(message);
		}
	}

	/**
	 * This object is returned if the hypothesis has been rejected.
	 *
	 * @param observation the observation to be tested
	 * @param hypothesis the zero-hypothesis
	 * @param message the result message
	 */
	record Reject(Distribution hypothesis, Histogram observation, String message)
		implements Result
	{
		public Reject {
			requireNonNull(hypothesis);
			requireNonNull(observation);
			requireNonNull(message);
		}
	}

	/**
	 * Testing an <em>observation</em> against a given zero-<em>hypothesis</em>.
	 *
	 * @param observation the observation to be tested
	 * @param hypothesis the zero-hypothesis
	 * @return the hypothesis test result
	 */
	Result test(final Histogram observation, final Distribution hypothesis);

}
