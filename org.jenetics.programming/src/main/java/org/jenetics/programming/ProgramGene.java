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
import java.util.stream.Stream;

import org.jenetics.Gene;
import org.jenetics.programming.ops.Op;
import org.jenetics.programming.ops.Program;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

import org.jenetix.AbstractTreeGene;

/**
 * This gene represents a program, build upon an AST of {@link Op} functions.
 * Because of the tight coupling with the {@link ProgramChromosome}, a
 * {@code ProgramGene} can't be created directly. This reduces the the possible
 * <em>error space</em>. Since the {@code ProgramGene} also is a
 * {@code Tree<? extends Op<T>, ?>}, it can be easily directly used as result.
 *
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
		super(requireNonNull(op), childOffset, op.arity());
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

	public ISeq<? extends Op<A>> getOperations() {
		return _operations;
	}

	public ISeq<? extends Op<A>> getTerminals() {
		return _terminals;
	}

	@Override
	public ProgramGene<A> newInstance() {
		final Random random = RandomRegistry.getRandom();

		Op<A> operation = getValue();
		if (isLeaf()) {
			final ISeq<? extends Op<A>> terminals = _terminals.stream()
				.filter(op -> op.arity() == getValue().arity())
				.collect(ISeq.toISeq());

			if (terminals.length() > 1) {
				operation = terminals.get(random.nextInt(terminals.length()));
			}
		} else {
			final ISeq<? extends Op<A>> operations = _operations.stream()
				.filter(op -> op.arity() == getValue().arity())
				.collect(ISeq.toISeq());

			if (operations.length() > 1) {
				operation = operations.get(random.nextInt(operations.length()));
			}
		}

		return newInstance(operation);
	}

	@Override
	public ProgramGene<A> newInstance(final Op<A> op) {
		if (getValue().arity() != op.arity()) {
			throw new IllegalArgumentException(format(
				"New operation must have same arity: %s[%d] != %s[%d]",
				getValue().name(), getValue().arity(), op.name(), op.arity()
			));
		}
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
