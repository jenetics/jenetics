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
package io.jenetics.engine;

import io.jenetics.Gene;

/**
 * This interface represents a recipe for configuring (setup) a given
 * {@link Engine.Builder}. It is mainly used for grouping mutually dependent
 * engine configurations.
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface EngineSetup<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * Performs the setup of the given engine {@code builder}.
	 *
	 * @param builder the engine builder to setup (configure)
	 */
	void setup(final Engine.Builder<G, C> builder);

}
