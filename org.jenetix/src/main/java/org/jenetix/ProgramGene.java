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

import java.lang.reflect.Array;
import java.util.Random;

import org.jenetics.Gene;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ProgramGene<A>
	extends AbstractTreeGene<Op<A>, ProgramGene<A>>
	implements Gene<Op<A>, ProgramGene<A>>
{

	private final ISeq<? extends Op<A>> _ops;

	ProgramGene(
		final Op<A> op,
		final ISeq<? extends Op<A>> ops,
		final int childStartIndex
	) {
		super(op, childStartIndex);
		_ops = requireNonNull(ops);
	}

	public ProgramGene(final Op<A> op, final ISeq<? extends Op<A>> ops) {
		this(op, ops, -1);
	}

	/**
	 * Evaluates the actual operation of the program gene.
	 *
	 * @param values the operation values
	 * @return the evaluated operation value
	 * @throws NullPointerException if the given values array is {@code null}
	 */
	public A apply(final A[] values) {
		requireNonNull(values);
		return getAllele().apply(values);
	}

	/**
	 * Evaluates this program gene (recursively) with the given variable values.
	 *
	 * @param variables the variables
	 * @return the evaluated value
	 * @throws NullPointerException if the given variable array is {@code null}
	 */
	public A eval(final A[] variables) {
		requireNonNull(variables);
		checkTreeState();

		@SuppressWarnings("unchecked")
		final A[] values = (A[])Array.newInstance(
			variables.getClass().getComponentType(),
			childCount()
		);

		for (int i = 0; i < childCount(); ++i) {
			final ProgramGene<A> child = getChild(i);
			if (child.getAllele() instanceof Var<?>) {
				values[i] = child.getAllele().apply(variables);
			} else {
				values[i] = child.eval(variables);
			}
		}

		return apply(values);
	}

	@Override
	public int childCount() {
		return getAllele().arity();
	}

	public ISeq<? extends Op<A>> getOps() {
		return _ops;
	}

	@Override
	public ProgramGene<A> newInstance() {
		final Random random = RandomRegistry.getRandom();
		final int index = random.nextInt(_ops.length());
		return newInstance(_ops.get(index));
	}

	@Override
	public ProgramGene<A> newInstance(final Op<A> value) {
		return new ProgramGene<>(value, _ops);
	}
}
