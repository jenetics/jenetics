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
package org.jenetics.programming;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.function.Function;

import org.jenetics.Gene;
import org.jenetics.programming.ops.Op;
import org.jenetics.programming.ops.Program;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

import org.jenetix.AbstractTreeGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ProgramGene<A>
	extends AbstractTreeGene<Op<A>, ProgramGene<A>>
	implements Gene<Op<A>, ProgramGene<A>>, Function<A[], A>
{

	private final ISeq<? extends Op<A>> _operations;
	private final ISeq<? extends Op<A>> _terminals;

	ProgramGene(
		final Op<A> op,
		final int childOffset,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		super(op, childOffset, op.arity());
		_operations = requireNonNull(operations);
		_terminals = requireNonNull(terminals);
	}

	/**
	 * Evaluates this program gene (recursively) with the given variable values.
	 *
	 * @param variables the variables
	 * @return the evaluated value
	 * @throws NullPointerException if the given variable array is {@code null}
	 */
	@Override
	public A apply(final A[] variables) {
		checkTreeState();
		return Program.eval(this, variables);
	}

	void checkTreeState() {
		if (genes() == null) {
			throw new IllegalStateException(
				"Gene is not attached to a chromosome."
			);
		}
	}

	public ISeq<? extends Op<A>> getOperations() {
		return _operations;
	}

	public ISeq<? extends Op<A>> getTerminals() {
		return _terminals;
	}

	@Override
	public ProgramGene<A> newInstance() {
		final Random random = RandomRegistry.getRandom();
		return newInstance(
			isLeaf()
				? _terminals.get(random.nextInt(_terminals.length()))
				: _operations.get(random.nextInt(_operations.length()))
		);
	}

	@Override
	public ProgramGene<A> newInstance(final Op<A> op) {
		return new ProgramGene<>(op, childOffset(), _operations, _terminals);
	}

	@Override
	public ProgramGene<A> newInstance(
		final Op<A> allele,
		final int childOffset,
		final int childCount
	) {
		if (allele.arity() != childCount) {
			throw new IllegalArgumentException(format(
				"Operation arity and child count are different: %d, != %d",
				allele.arity(), childCount
			));
		}

		return new ProgramGene<>(allele, childOffset, _operations, _terminals);
	}
}
