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
package io.jenetics.ext.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionStreamable;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
abstract class EnginePool<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements EvolutionStreamable<G, C>
{

	final List<? extends EvolutionStreamable<G, C>> _engines;

	EnginePool(final List<? extends EvolutionStreamable<G, C>> engines) {
		engines.forEach(Objects::requireNonNull);
		_engines = new ArrayList<>(engines);
	}

}
