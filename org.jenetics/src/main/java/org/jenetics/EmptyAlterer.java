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
package org.jenetics;

import static java.util.Objects.requireNonNull;

/**
 * Alterer implementation which does nothing.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class EmptyAlterer<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Alterer<G, C>
{

	static final Alterer<?, ?> INSTANCE = new EmptyAlterer<DoubleGene, Double>();

	private EmptyAlterer() {
	}

	@SuppressWarnings("unchecked")
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Alterer<G, C> instance() {
		return (Alterer<G, C>)INSTANCE;
	}

	@Override
	public int alter(
		final Population<G, C> population,
		final long generation
	) {
		return 0;
	}

	@Override
	public Alterer<G, C> compose(final Alterer<G, C> before) {
		return requireNonNull(before);
	}

	@Override
	public Alterer<G, C> andThen(final Alterer<G, C> after) {
		return requireNonNull(after);
	}

	@Override
	public String toString() {
		return "EmptyAlterer";
	}

}
