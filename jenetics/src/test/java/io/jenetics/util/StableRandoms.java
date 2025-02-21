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
package io.jenetics.util;

import java.util.random.RandomGeneratorFactory;

/**
 * Helper class for making reproducible randomized tests.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class StableRandoms {

	private StableRandoms() {
	}

	/**
	 * Executes the given {@code task} with a registered random generator with
	 * the given {@code seed}. This will make randomized tests reproducible.
	 *
	 * @param seed the seed used for the random generator
	 * @param task the test task
	 */
	public static void using(final long seed, final Runnable task) {
		RandomRegistry.using(
			RandomGeneratorFactory.getDefault().create(seed),
			r -> task.run()
		);
	}

}
