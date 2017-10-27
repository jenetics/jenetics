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
package io.jenetics.ext.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class NodeTest {

	@Test
	public void treeToString() {
		final TreeNode<Integer> tree = TreeNode.of(0)
			.attach(TreeNode.of(1)
				.attach(4, 5))
			.attach(TreeNode.of(2)
				.attach(6))
			.attach(TreeNode.of(3)
				.attach(TreeNode.of(7)
					.attach(10, 11))
				.attach(8)
				.attach(9));

		//System.out.println(tree);
		//final FlatTreeNode<Integer> flat = FlatTreeNode.of(tree);
		//System.out.println(flat);
		//System.out.println(Tree.toString(flat));
		//System.out.println(Trees.toCompactString(tree));
		//System.out.println(Trees.toDottyString("number_tree", tree));
		//System.out.println(tree.depth());
		//System.out.println(Trees.toLispString(tree));
	}

	@Test
	public void serialize() throws Exception {
		final TreeNode<Integer> tree = TreeNode.of(0)
			.attach(TreeNode.of(1)
				.attach(4, 5))
			.attach(TreeNode.of(2)
				.attach(6))
			.attach(TreeNode.of(3)
				.attach(TreeNode.of(7)
					.attach(10, 11))
				.attach(TreeNode.of(8))
				.attach(TreeNode.of(9)));

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ObjectOutputStream oout = new ObjectOutputStream(out)) {
			oout.writeObject(tree);
		}

		final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		try (ObjectInputStream oin = new ObjectInputStream(in)) {
			@SuppressWarnings("unchecked")
			final TreeNode<Integer> object = (TreeNode<Integer>)oin.readObject();
			Assert.assertEquals(object, tree);
		}
	}

}
