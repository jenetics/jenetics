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

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import org.jenetics.Gene;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TreeGene<A>
	implements Gene<A, TreeGene<A>>
{

	private final A _value;
	private final Factory<A> _factory;
	private final int _parent;
	private final int[] _children;

	TreeGene(
		final A value,
		final Factory<A> factory,
		final int parent,
		final int[] children
	) {
		_value = value;
		_factory = requireNonNull(factory);
		_parent = parent;
		_children = requireNonNull(children);
	}

	public Optional<TreeGene<A>> getParent(final Seq<TreeGene<A>> genes) {
		return _parent < 0 || _parent >= genes.length()
			? Optional.empty()
			: Optional.of(genes.get(_parent));
	}

	public ISeq<TreeGene<A>> getChildren(final Seq<TreeGene<A>> genes) {
		return IntStream.of(_children)
			.filter(i -> i >= 0 && i < genes.length())
			.mapToObj(genes::get)
			.collect(ISeq.toISeq());
	}

	@Override
	public A getAllele() {
		return _value;
	}

	@Override
	public TreeGene<A> newInstance() {
		return newInstance(_factory.newInstance());
	}

	@Override
	public TreeGene<A> newInstance(final A value) {
		return new TreeGene<>(value, _factory, _parent, _children);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String toString() {
		return Objects.toString(_value);
	}

}
