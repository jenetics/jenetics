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

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.programming.ops.Op;
import org.jenetics.programming.ops.Ops;
import org.jenetics.programming.ops.Program;
import org.jenetics.programming.ops.Programs;
import org.jenetics.programming.ops.Var;
import org.jenetics.util.ISeq;

import org.jenetix.util.FlatTreeNode;
import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ProgramsTest {

	private static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
		Ops.ADD,
		Ops.SUB,
		Ops.MUL,
		Ops.DIV,
		Ops.EXP,
		Ops.SIN,
		Ops.COS
	);

	private static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of("x", 0),
		Var.of("y", 1),
		Var.of("z", 2),
		Ops.fixed(Math.PI),
		Ops.fixed(1)
	);

	@Test
	public void program() {
		final TreeNode<Op<Double>> tree = Programs.of(
			5,
			OPERATIONS,
			TERMINALS
		);

		final TreeNode<Op<Double>> formula = TreeNode.of(Ops.ADD);

		System.out.println(tree);

		final Program<Double> program = new Program<>("foo", tree);
		final Double result = program.apply(new Double[]{1.0, 2.0, 3.0});
		System.out.println("Arity: " + program.arity());
		System.out.println(result);
		System.out.flush();
	}

	@Test(invocationCount = 1)
	public void toTree() {
		final TreeNode<Op<Double>> tree = Programs.of(
			3,
			OPERATIONS,
			TERMINALS
		);

		final ISeq<FlatTreeNode<Op<Double>>> seq = FlatTreeNode.of(tree).nodes();

		Assert.assertEquals(
			Programs.toTree(seq, OPERATIONS, TERMINALS),
			tree
		);
	}

	@Test
	public void toTreeAndRepair() {
		final TreeNode<Op<Double>> tree1 = Programs.of(
			3,
			OPERATIONS,
			TERMINALS
		);
		final TreeNode<Op<Double>> tree2 = Programs.of(
			3,
			OPERATIONS,
			TERMINALS
		);

		System.out.println(tree1);
		System.out.println(tree2);
		System.out.println(Arrays.toString(Programs.offsets(FlatTreeNode.of(tree1).nodes())));


		/*
		final ISeq<FlatTreeNode<Op<Double>>> seq1 = FlatTreeNode.of(tree1).nodes();
		final ISeq<FlatTreeNode<Op<Double>>> seq2 = FlatTreeNode.of(tree2).nodes();

		final ISeq<FlatTreeNode<Op<Double>>> seq3 = ISeq
			.of(seq1.subSeq(0, seq1.length()/2))
			.append(seq2.subSeq(0, seq2.length()/2));

		final TreeNode<Op<Double>> tree3 = Programs.toTree(seq3, OPERATIONS, TERMINALS);
		System.out.println(tree3);
		*/
	}

}
