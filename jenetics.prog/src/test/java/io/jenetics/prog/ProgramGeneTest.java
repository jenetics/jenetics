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

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.IO;
import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.op.Const;
import io.jenetics.prog.op.MathOp;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.op.Var;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ProgramGeneTest {

	private static final ISeq<Op<Double>> OPERATIONS = ISeq.of(MathOp.values());
	private static final ISeq<Op<Double>> TERMINALS = ISeq.of(
		Var.of("x", 0),
		Var.of("y", 1),
		Var.of("c", 2),
		Const.of(1.0), Const.of(2.0)
	);

	private static final ProgramGene<Double> PROG = ProgramChromosome
		.of(5, OPERATIONS, TERMINALS)
		.getRoot();

	private static final TreeNode<Op<Double>> TREE = TreeNode.ofTree(PROG);

	@Test(dataProvider = "methods")
	public void methodResults(final Function<Tree<?, ?>, Object> method) {
		final Iterator<? extends Tree<?, ?>> it1 = PROG.iterator();
		final Iterator<? extends Tree<?, ?>> it2 = TREE.iterator();

		while (it1.hasNext()) {
			final Tree<?, ?> node1 = it1.next();
			final Tree<?, ?> node2 = it2.next();
			Assert.assertEquals(method.apply(node1), method.apply(node2));
		}
	}

	@DataProvider
	public Object[][] methods() {
		return new Object[][] {
			{(Function<Tree<?, ?>, Object>)Tree::toParenthesesString},
			{(Function<Tree<?, ?>, Object>)Tree::level},
			{(Function<Tree<?, ?>, Object>)Tree::childCount},
			{(Function<Tree<?, ?>, Object>)Tree::childPath},
			{(Function<Tree<?, ?>, Object>)Tree::isLeaf},
			{(Function<Tree<?, ?>, Object>)t -> t.getRoot().getValue()},
			{(Function<Tree<?, ?>, Object>)t -> t.getParent().map(t2 -> t2.getValue()).orElse(null)}
		};
	}

	@Test
	public void serialize() throws IOException {
		final ProgramGene<Double> object = new ProgramGene<>(
			OPERATIONS.get(0),
			0,
			OPERATIONS,
			TERMINALS
		);

		final byte[] data = IO.object.toByteArray(object);
		Assert.assertEquals(IO.object.fromByteArray(data), object);
	}

}
