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

import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.function.Predicate;

import org.jenetics.programming.ops.Op;
import org.jenetics.programming.ops.Program;
import org.jenetics.programming.ops.Programs;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

import org.jenetix.AbstractTreeChromosome;
import org.jenetix.util.FlatTreeNode;
import org.jenetix.util.Tree;
import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ProgramChromosome<A> extends AbstractTreeChromosome<Op<A>, ProgramGene<A>> {

	private final Predicate<? super ProgramChromosome<A>> _validator;
	private final ISeq<? extends Op<A>> _operations;
	private final ISeq<? extends Op<A>> _terminals;

	ProgramChromosome(
		final ISeq<ProgramGene<A>> genes,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		super(genes);
		_validator = validator;
		_operations = requireNonNull(operations);
		_terminals = requireNonNull(terminals);
	}

	public ISeq<? extends Op<A>> getOperations() {
		return _operations;
	}

	public ISeq<? extends Op<A>> getTerminals() {
		return _terminals;
	}

	@Override
	public boolean isValid() {
		if (_validator != null) {
			if (_valid == null) {
				_valid = _validator.test(this);
			}
		} else {
			_valid = super.isValid();
		}

		return _valid;
	}

	@Override
	public ProgramChromosome<A> newInstance(final ISeq<ProgramGene<A>> genes) {
		return of(genes, _validator, _operations, _terminals);
	}

	@Override
	public ProgramChromosome<A> newInstance() {
		return of(getRoot().depth(), _validator, _operations, _terminals);
	}

	public static <A> ProgramChromosome<A> of(
		final Tree<? extends Op<A>, ?> program,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		Program.check(program);

		final ISeq<ProgramGene<A>> genes = FlatTreeNode.of(program).stream()
			.map(n -> new ProgramGene<>(
				n.getValue(), n.childOffset(), operations, terminals))
			.collect(ISeq.toISeq());

		return new ProgramChromosome<>(genes, validator, operations, terminals);
	}

	public static <A> ProgramChromosome<A> of(
		final Tree<? extends Op<A>, ?> program,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		return of(program, null, operations, terminals);
	}

	public static <A> ProgramChromosome<A> of(
		final int depth,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		final TreeNode<Op<A>> root = TreeNode.of();
		fill(depth, root, operations, terminals, RandomRegistry.getRandom());
		return of(root, validator, operations, terminals);
	}

	public static <A> ProgramChromosome<A> of(
		final int depth,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		return of(depth, null, operations, terminals);
	}

	private static <A> void fill(
		final int depth,
		final TreeNode<Op<A>> tree,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals,
		final Random random
	) {
		final Op<A> op = operations.get(random.nextInt(operations.size()));
		tree.setValue(op);

		if (depth > 1) {
			for (int i = 0; i < op.arity(); ++i) {
				final TreeNode<Op<A>> node = TreeNode.of();
				fill(depth - 1, node, operations, terminals, random);
				tree.attach(node);
			}
		} else {
			for (int i = 0; i < op.arity(); ++i) {
				final Op<A> term = terminals.get(random.nextInt(terminals.size()));
				final TreeNode<Op<A>> node = TreeNode.of(term);
				tree.attach(node);
			}
		}
	}

	public static <A> ProgramChromosome<A> of(
		final ISeq<ProgramGene<A>> genes,
		final Predicate<? super ProgramChromosome<A>> validator,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		final TreeNode<Op<A>> program = Programs.toTree(genes, terminals);
		return of(program, validator, operations, terminals);
	}

	public static <A> ProgramChromosome<A> of(
		final ISeq<ProgramGene<A>> genes,
		final ISeq<? extends Op<A>> operations,
		final ISeq<? extends Op<A>> terminals
	) {
		return of(genes, null, operations, terminals);
	}

}
