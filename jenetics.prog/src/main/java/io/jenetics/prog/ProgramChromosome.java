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
package io.jenetics.prog;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import io.jenetics.ext.AbstractTreeChromosome;
import io.jenetics.ext.util.FlatTreeNode;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Program;
import io.jenetics.util.ISeq;

/**
 * Holds the nodes of the operation tree.
 *
 * <pre>{@code
 * final int depth = 6;
 * final ISeq<Op<Double>> operations = ISeq.of(...);
 * final ISeq<Op<Double>> terminals = ISeq.of(...);
 * final ProgramChromosome<Double> ch = ProgramChromosome.of(
 *     depth,
 *     // If the program has more that 200 nodes, it is marked as "invalid".
 *     ch -> ch.length() <= 200,
 *     operations,
 *     terminals
 * );
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public class ProgramChromosome<A>
	extends AbstractTreeChromosome<Op<A>, ProgramGene<A>>
{

	private final Predicate<? super ProgramChromosome<A>> _validator;
	private final ISeq<? extends Op<A>> _operations;
	private final ISeq<? extends Op<A>> _terminals;

	/**
	 * Create a new program chromosome from the given program genes. This
	 * constructor assumes that the given {@code program} is valid. Since the
	 * program validation is quite expensive, the validity check is skipped in
	 * this constructor.
	 *
	 * @param program the program. During the program evolution, newly created
	 *        program trees has the same <em>depth</em> than this tree.
	 * @param validator the chromosome validator. A typical validator would
	 *        check the size of the tree and if the tree is too large, mark it
	 *        at <em>invalid</em>. The <em>validator</em> may be {@code null}.
	 * @param operations the allowed non-terminal operations
	 * @param terminals the allowed terminal operations
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IllegalArgumentException if either the {@code operations} or
	 *         {@code terminals} sequence is empty
	 */
	protected ProgramChromosome(
		final ISeq<ProgramGene<A>> program,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		super(program);
		_validator = requireNonNull(validator);
		_operations = requireNonNull(operations);
		_terminals = requireNonNull(terminals);

		if (operations.isEmpty()) {
			throw new IllegalArgumentException("No operations given.");
		}
		if (terminals.isEmpty()) {
			throw new IllegalArgumentException("No terminals given");
		}
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
	public boolean isValid() {
		if (_valid == null) {
			_valid = _validator.test(this);
		}

		return _valid;
	}

	private boolean isSuperValid() {
		return ProgramChromosome.super.isValid();
	}

	/**
	 * Evaluates the root node of this chromosome.
	 *
	 * @see ProgramGene#apply(Object[])
	 * @see ProgramChromosome#eval(Object[])
	 *
	 * @param args the input variables
	 * @return the evaluated value
	 * @throws NullPointerException if the given variable array is {@code null}
	 */
	public A apply(final A[] args) {
		return getRoot().apply(args);
	}

	/**
	 * Evaluates the root node of this chromosome.
	 *
	 * @see ProgramGene#eval(Object[])
	 * @see ProgramChromosome#apply(Object[])
	 *
	 * @param args the function arguments
	 * @return the evaluated value
	 * @throws NullPointerException if the given variable array is {@code null}
	 */
	@SafeVarargs
	public final A eval(final A... args) {
		return getRoot().eval(args);
	}

	@Override
	public ProgramChromosome<A> newInstance(final ISeq<ProgramGene<A>> genes) {
		return create(genes, _validator, _operations, _terminals);
	}

	@Override
	public ProgramChromosome<A> newInstance() {
		return create(getRoot().depth(), _validator, _operations, _terminals);
	}

	/**
	 * Create a new chromosome from the given operation tree (program).
	 *
	 * @param program the operation tree
	 * @param validator the chromosome validator. A typical validator would
	 *        check the size of the tree and if the tree is too large, mark it
	 *        at <em>invalid</em>. The <em>validator</em> may be {@code null}.
	 * @param operations the allowed non-terminal operations
	 * @param terminals the allowed terminal operations
	 * @param <A> the operation type
	 * @return a new chromosome from the given operation tree
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IllegalArgumentException if the given operation tree is invalid,
	 *         which means there is at least one node where the operation arity
	 *         and the node child count differ.
	 */
	public static <A> ProgramChromosome<A> of(
		final Tree<? extends Op<A>, ?> program,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		Program.check(program);
		checkOperations(operations);
		checkTerminals(terminals);

		return create(program, validator, operations, terminals);
	}

	// Create the chromosomes without checks.
	private static <A> ProgramChromosome<A> create(
		final Tree<? extends Op<A>, ?> program,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		final ISeq<ProgramGene<A>> genes = FlatTreeNode.of(program).stream()
			.map(n -> new ProgramGene<>(
				n.getValue(), n.childOffset(), operations, terminals))
			.collect(ISeq.toISeq());

		return new ProgramChromosome<>(genes, validator, operations, terminals);
	}

	private static void checkOperations(final ISeq<? extends Op<?>> operations) {
		final ISeq<?> terminals = operations.stream()
			.filter(op -> op.isTerminal())
			.map(op -> (Op<?>)op)
			.collect(ISeq.toISeq());

		if (!terminals.isEmpty()) {
			throw new IllegalArgumentException(format(
				"Operations must not contain terminals: %s",
				terminals.toString(",")
			));
		}
	}

	private static void checkTerminals(final ISeq<? extends Op<?>> terminals) {
		final ISeq<?> operations = terminals.stream()
			.filter(op -> !op.isTerminal())
			.map(op -> (Op<?>)op)
			.collect(ISeq.toISeq());

		if (!operations.isEmpty()) {
			throw new IllegalArgumentException(format(
				"Terminals must not contain operations: %s",
				operations.toString(",")
			));
		}
	}

	/**
	 * Create a new chromosome from the given operation tree (program).
	 *
	 * @param program the operation tree
	 * @param operations the allowed non-terminal operations
	 * @param terminals the allowed terminal operations
	 * @param <A> the operation type
	 * @return a new chromosome from the given operation tree
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IllegalArgumentException if the given operation tree is invalid,
	 *         which means there is at least one node where the operation arity
	 *         and the node child count differ.
	 */
	public static <A> ProgramChromosome<A> of(
		final Tree<? extends Op<A>, ?> program,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		return of(program, ProgramChromosome::isSuperValid, operations, terminals);
	}

	/**
	 * Create a new program chromosome with the defined depth. This method will
	 * create a <em>full</em> program tree.
	 *
	 * @param depth the depth of the created program tree
	 * @param validator the chromosome validator. A typical validator would
	 *        check the size of the tree and if the tree is too large, mark it
	 *        at <em>invalid</em>. The <em>validator</em> may be {@code null}.
	 * @param operations the allowed non-terminal operations
	 * @param terminals the allowed terminal operations
	 * @param <A> the operation type
	 * @return a new program chromosome from the given (flattened) program tree
	 * @throws NullPointerException if one of the parameters is {@code null}
	 * @throws IllegalArgumentException if the {@code depth} is smaller than zero
	 */
	public static <A> ProgramChromosome<A> of(
		final int depth,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		checkOperations(operations);
		checkTerminals(terminals);

		return create(depth, validator, operations, terminals);
	}

	private static <A> ProgramChromosome<A> create(
		final int depth,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		return create(
			Program.of(depth, operations, terminals),
			validator,
			operations,
			terminals
		);
	}

	/**
	 * Create a new program chromosome with the defined depth. This method will
	 * create a <em>full</em> program tree.
	 *
	 * @param depth the depth of the created (full) program tree
	 * @param operations the allowed non-terminal operations
	 * @param terminals the allowed terminal operations
	 * @param <A> the operation type
	 * @return a new program chromosome from the given (flattened) program tree
	 * @throws NullPointerException if one of the parameters is {@code null}
	 * @throws IllegalArgumentException if the {@code depth} is smaller than zero
	 */
	public static <A> ProgramChromosome<A> of(
		final int depth,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		return of(depth, ProgramChromosome::isSuperValid, operations, terminals);
	}

	/**
	 * Create a new program chromosome from the given (flattened) program tree.
	 * This method doesn't make any assumption about the validity of the given
	 * operation tree. If the tree is not valid, it will repair it. This
	 * behaviour allows the <em>safe</em> usage of all existing alterer.
	 *
	 * <pre>{@code
	 * final ProgramChromosome<Double> ch = ProgramChromosome.of(
	 *     genes,
	 *     // If the program has more that 200 nodes, it is marked as "invalid".
	 *     ch -> ch.length() <= 200,
	 *     operations,
	 *     terminals
	 * );
	 * }</pre>
	 *
	 * @param genes the program genes
	 * @param validator the chromosome validator to use
	 * @param operations the allowed non-terminal operations
	 * @param terminals the allowed terminal operations
	 * @param <A> the operation type
	 * @return a new program chromosome from the given (flattened) program tree
	 * @throws NullPointerException if one of the parameters is {@code null}
	 */
	public static <A> ProgramChromosome<A> of(
		final ISeq<ProgramGene<A>> genes,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		final TreeNode<Op<A>> program = Program.toTree(genes, terminals);
		return of(program, validator, operations, terminals);
	}

	private static <A> ProgramChromosome<A> create(
		final ISeq<ProgramGene<A>> genes,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		final TreeNode<Op<A>> program = Program.toTree(genes, terminals);
		return create(program, validator, operations, terminals);
	}

	public static <A> ProgramChromosome<A> of(
		final ISeq<ProgramGene<A>> genes,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		return of(genes, ProgramChromosome::isSuperValid, operations, terminals);
	}

}
