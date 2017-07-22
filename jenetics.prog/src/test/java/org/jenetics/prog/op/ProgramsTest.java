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
package org.jenetics.prog.op;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.ext.util.FlatTreeNode;
import org.jenetics.ext.util.TreeNode;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ProgramsTest {

	private static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
		MathOps.ADD,
		MathOps.SUB,
		MathOps.MUL,
		MathOps.DIV,
		MathOps.EXP,
		MathOps.SIN,
		MathOps.COS
	);

	private static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of("x", 0),
		Var.of("y", 1),
		Var.of("z", 2),
		MathOps.PI,
		Const.of(1.0)
	);

	@Test
	public void program() {
		final TreeNode<Op<Double>> tree = Program.of(
			5,
			OPERATIONS,
			TERMINALS
		);

		final TreeNode<Op<Double>> formula = TreeNode.of(MathOps.ADD);
		final Program<Double> program = new Program<>("foo", tree);

		final Double result = program.eval(1.0, 2.0, 3.0);
		System.out.println("Arity: " + program.arity());
		System.out.println(result);
		System.out.flush();
	}

	@Test(invocationCount = 5)
	public void toTree() {
		final TreeNode<Op<Double>> tree = Program.of(
			6,
			OPERATIONS,
			TERMINALS
		);

		final ISeq<FlatTreeNode<Op<Double>>> seq = FlatTreeNode.of(tree).flattenedNodes();

		Assert.assertEquals(
			Program.toTree(seq, TERMINALS),
			tree
		);
	}

	@Test
	public void toTreeAndRepair() {
		final TreeNode<Op<Double>> tree1 = Program.of(
			3,
			OPERATIONS,
			TERMINALS
		);
		final TreeNode<Op<Double>> tree2 = Program.of(
			3,
			OPERATIONS,
			TERMINALS
		);

		final ISeq<FlatTreeNode<Op<Double>>> seq1 = FlatTreeNode.of(tree1).flattenedNodes();
		final ISeq<FlatTreeNode<Op<Double>>> seq2 = FlatTreeNode.of(tree2).flattenedNodes();

		final ISeq<FlatTreeNode<Op<Double>>> seq3 = ISeq
			.of(seq1.subSeq(0, seq1.length()/2))
			.append(seq2.subSeq(0, seq2.length()/2));

		final TreeNode<Op<Double>> tree3 = Program.toTree(seq3, TERMINALS);
	}

	@Test
	public void offsets() {
		final TreeNode<Op<Double>> tree = Program.of(
			6,
			OPERATIONS,
			TERMINALS
		);
		final ISeq<FlatTreeNode<Op<Double>>> seq = FlatTreeNode.of(tree).flattenedNodes();

		final int[] expected = seq.stream()
			.mapToInt(FlatTreeNode::childOffset)
			.toArray();

		final int[] offsets = Program.offsets(seq);
		Assert.assertEquals(offsets, expected);
	}

}
