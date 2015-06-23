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
package org.jenetics.optimizer;

import java.util.Optional;
import java.util.function.Function;

import org.jenetics.Gene;
import org.jenetics.Selector;
import org.jenetics.TruncationSelector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TruncationSelectorProxy<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
	>
	implements Proxy<Selector<G, C>>
{

	private final double _probability;

	public TruncationSelectorProxy(final double probability) {
		_probability = probability;
	}

	@Override
	public Function<double[], Optional<Selector<G, C>>> factory() {
		return args -> args[0] <= _probability
			? Optional.of(new TruncationSelector<>())
			: Optional.empty();
	}

	@Override
	public int argsLength() {
		return 1;
	}
}
