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
		Ops.DIV,
		Ops.MUL,
		Ops.DIV
	);

	private static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of(0), Var.of(1), Var.of(2)
	);

	@Test
	public void program() {
		final TreeNode<Op<Double>> program = Programs.of(
			3,
			OPERATIONS,
			TERMINALS
		);

		System.out.println(program);
	}

}
