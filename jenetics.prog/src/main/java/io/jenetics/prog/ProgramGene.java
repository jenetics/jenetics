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
package io.jenetics.prog;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.ext.AbstractTreeGene;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Program;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

/**
 * This gene represents a program, build upon an AST of {@link Op} functions.
 * Because of the tight coupling with the {@link ProgramChromosome}, a
 * {@code ProgramGene} can't be created directly. This reduces the the possible
 * <em>error space</em>. Since the {@code ProgramGene} also is a {@code Tree},
 * it can be easily used as result.
 *
 * <pre>{@code
 * final ProgramGene<Double> program = engine.stream()
 *     .limit(300)
 *     .collect(EvolutionResult.toBestGenotype())
 *     .getGene();
 *
 * final double result = program.eval(3.4);
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
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
		super(requireNonNull(get(op)), childOffset, op.arity());
		_operations = requireNonNull(operations);
		_terminals = requireNonNull(terminals);
	}

	private static <A> Op<A> get(final Op<A> op) {
		final Op<A> instance = op.get();
		if (instance != op && instance.arity() != op.arity()) {
			throw new IllegalArgumentException(format(
				"Original op and created op have different arity: %d != %d,",
				instance.arity(), op.arity()
			));
		}
		return instance;
	}

	/**
	 * Evaluates this program gene (recursively) with the given variable values.
	 *
	 * @see ProgramGene#eval(Object[])
	 * @see ProgramChromosome#eval(Object[])
	 *
	 * @param args the input variables
	 * @return the evaluated value
	 * @throws NullPointerException if the given variable array is {@code null}
	 */
	@Override
	public A apply(final A[] args) {
		checkTreeState();
		return Program.eval(this, args);
	}

	/**
	 * Convenient method, which lets you apply the program function without
	 * explicitly create a wrapper array.
	 *
	 * @see ProgramGene#apply(Object[])
	 * @see ProgramChromosome#eval(Object[])
	 *
	 * @param args the function arguments
	 * @return the evaluated value
	 * @throws NullPointerException if the given variable array is {@code null}
	 */
	@SafeVarargs
	public final A eval(final A... args) {
		return apply(args);
	}

	/**
	 * Return the allowed operations.
	 *
	 * @return the allowed operations
	 */
	public ISeq<? extends Op<A>> getOperations() {
		return _operations;
	}

	/**
	 * Return the allowed terminal operations.
	 *
	 * @return the allowed terminal operations
	 */
	public ISeq<? extends Op<A>> getTerminals() {
		return _terminals;
	}

	@Override
	public ProgramGene<A> newInstance() {
		final Random random = RandomRegistry.getRandom();

		Op<A> operation = getValue();
		if (isLeaf()) {
			operation = _terminals.get(random.nextInt(_terminals.length()));
		} else {
			final ISeq<Op<A>> operations = _operations.stream()
				.filter(op -> op.arity() == getValue().arity())
				.map(op -> (Op<A>)op)
				.collect(ISeq.toISeq());

			if (operations.length() > 1) {
				operation = operations.get(random.nextInt(operations.length()));
			}
		}

		return newInstance(operation);
	}

	/**
	 * Create a new program gene with the given operation.
	 *
	 * @param op the operation of the new program gene
	 * @return a new program gene with the given operation
	 * @throws NullPointerException if the given {@code op} is {@code null}
	 * @throws IllegalArgumentException if the arity of the given operation is
	 *         different from the arity of current operation. This restriction
	 *         ensures that only valid program genes are created by this method.
	 */
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

	/**
	 * Return a new program gene with the given operation and the <em>local</em>
	 * tree structure.
	 *
	 * @param op the new operation
	 * @param childOffset the offset of the first node child within the
	 *        chromosome
	 * @param childCount the number of children of the new tree gene
	 * @return a new tree gene with the given parameters
	 * @throws IllegalArgumentException  if the {@code childCount} is smaller
	 *         than zero
	 * @throws IllegalArgumentException if the operation arity is different from
	 *         the {@code childCount}.
	 * @throws NullPointerException if the given {@code op} is {@code null}
	 */
	@Override
	public ProgramGene<A> newInstance(
		final Op<A> op,
		final int childOffset,
		final int childCount
	) {
		if (op.arity() != childCount) {
			throw new IllegalArgumentException(format(
				"Operation arity and child count are different: %d, != %d",
				op.arity(), childCount
			));
		}

		return new ProgramGene<>(op, childOffset, _operations, _terminals);
	}

}
