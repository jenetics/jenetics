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
package org.jenetix;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Plus<N extends Number> extends ProgramGene<N, Plus<N>> {

	@Override
	public N apply(final N... value) {
		return null;
	}

	@Override
	public int arity() {
		return 0;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public N getAllele() {
		return null;
	}

	@Override
	public Plus<N> newInstance() {
		return null;
	}

	@Override
	public Plus<N> newInstance(N value) {
		return null;
	}

	@Override
	public Optional<Plus<N>> getParent(Seq<? extends Plus<N>> genes) {
		return null;
	}

	@Override
	public Plus<N> getChild(int index, IntFunction<? extends Plus<N>> genes) {
		return null;
	}

	@Override
	public Stream<Plus<N>> children(Seq<? extends Plus<N>> genes) {
		return null;
	}

	@Override
	public int childCount() {
		return 0;
	}
}
