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

import org.testng.annotations.Test;

import org.jenetics.programming.ops.Op;
import org.jenetics.programming.ops.Ops;
import org.jenetics.programming.ops.Program;
import org.jenetics.programming.ops.Programs;
import org.jenetics.programming.ops.Var;
import org.jenetics.util.ISeq;

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

}
