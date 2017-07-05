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
package org.jenetix;

import org.testng.annotations.Test;

import org.jenetics.util.ISeq;

import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ProgramsTest {

	@Test
	public void creation() {
		final ISeq<Op<Double>> operations = ISeq.of(
			Ops.ADD, Ops.SUB, Ops.DIV, Ops.MUL
		);
		final ISeq<Op<Double>> terminals = ISeq.of(
			Ops.constant(1), Ops.constant(2),
			Ops.constant(3), Ops.constant(4)
		);

		final TreeNode<Op<Double>> tree = Programs.of(4, operations, terminals);
		System.out.println(tree);
	}


}
