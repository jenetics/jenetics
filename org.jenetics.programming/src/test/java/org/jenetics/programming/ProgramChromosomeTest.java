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

import org.jenetics.programming.ops.Const;
import org.jenetics.programming.ops.Program;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.programming.ops.Op;
import org.jenetics.programming.ops.MathOp;
import org.jenetics.programming.ops.Var;
import org.jenetics.util.ISeq;

import org.jenetix.util.FlatTreeNode;
import org.jenetix.util.Tree;
import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ProgramChromosomeTest {

	private static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
		MathOp.ADD,
		MathOp.SUB,
		MathOp.MUL,
		MathOp.DIV,
		MathOp.EXP,
		MathOp.SIN,
		MathOp.COS
	);

	private static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of("x", 0),
		Var.of("y", 1),
		Var.of("z", 2),
		Const.of("π", Math.PI),
		Const.of(1.0)
	);

	@Test(invocationCount = 10)
	public void createFromTree() {
		final TreeNode<Op<Double>> tree = Program.of(
			6,
			OPERATIONS,
			TERMINALS
		);

		final ProgramChromosome<Double> chromosome = ProgramChromosome
			.of(tree, OPERATIONS, TERMINALS);

		Assert.assertTrue(Tree.equals(tree, chromosome.getRoot()));
	}

	@Test(invocationCount = 10)
	public void createFromSeq() {
		final TreeNode<Op<Double>> tree = Program.of(
			6,
			OPERATIONS,
			TERMINALS
		);
		final ProgramChromosome<Double> ch1 =
			ProgramChromosome.of(tree, OPERATIONS, TERMINALS);

		final ISeq<ProgramGene<Double>> genes = ch1.toSeq();
		final ProgramChromosome<Double> ch2 =
			ProgramChromosome.of(genes, OPERATIONS, TERMINALS);

		Assert.assertEquals(ch2, ch1);
	}

	@Test
	public void createTreeFromChromosome() {
		final TreeNode<Op<Double>> tree = Program.of(
			6,
			OPERATIONS,
			TERMINALS
		);
		final ProgramChromosome<Double> ch =
			ProgramChromosome.of(tree, OPERATIONS, TERMINALS);

		final TreeNode<Op<Double>> node = TreeNode.of(ch.getGene());
		System.out.println(node);
	}

	//@Test
	public void treeToFlatTree() {
		final TreeNode<Op<Double>> tree = Program.of(
			6,
			OPERATIONS,
			TERMINALS
		);

		System.out.println(tree);

		final FlatTreeNode<Op<Double>> flat = FlatTreeNode.of(tree);
		System.out.println(Tree.toString(flat));

		flat.stream().forEach(System.out::println);
	}

	@Test
	public void toCompactString() {
		final TreeNode<Op<Double>> tree = Program.of(
			3,
			OPERATIONS,
			TERMINALS
		);

		System.out.println(tree);
		System.out.println(Tree.toCompactString(tree));
	}

}
