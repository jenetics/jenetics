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

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreeFormatterTest {

	private static final TreeNode<String> TREE = TreeNode
		.parse("mul(div(cos(1.0), cos(π)), sin(mul(1.0, z)))", String::trim);

	@Test
	public void toTreeString() {
		System.out.println(TreeFormatter.TREE.format(TREE));
	}

	@Test
	public void toParenthesesString() {
		System.out.println(TreeFormatter.PARENTHESES.format(TREE));
	}

	@Test
	public void toLispString() {
		System.out.println(TreeFormatter.LISP.format(TREE));
	}

	@Test
	public void toDottyString() {
		System.out.println(TreeFormatter.dot("TREE").format(TREE));
	}

}
