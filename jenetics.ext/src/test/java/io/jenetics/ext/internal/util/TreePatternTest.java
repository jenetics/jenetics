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
package io.jenetics.ext.internal.util;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.internal.util.TreePattern;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreePatternTest {

	@Test(dataProvider = "patterns")
	public void expand(
		final String pattern,
		final String[] trees,
		final String expanded
	) {
		final TreePattern tp = TreePattern.compile(pattern);
		final Map<String, Tree<String, ?>> vars = IntStream.range(0, trees.length)
			.mapToObj(i -> new Object() {
					final String name = Integer.toString(i + 1);
					final Tree<String, ?> tree = TreeNode.parse(trees[i]);
				})
			.collect(Collectors.toMap(o -> o.name, o -> o.tree));

		Assert.assertEquals(tp.expand(vars).toParenthesesString(), expanded);
	}

	@DataProvider
	public Object[][] patterns() {
		return new Object[][] {
			{"<1>", new String[]{"sin(4)"}, "sin(4)"},
			{"cos(<1>)", new String[]{"sin(4)"}, "cos(sin(4))"},
			{"cos(<1>,2,sin(x))", new String[]{"sin(4)"}, "cos(sin(4),2,sin(x))"},
			{"cos(<1>,<2>,sin(x))", new String[]{"sin(4)"}, "cos(sin(4),sin(x))"},
			{"cos(<1>,<2>,sin(x))", new String[]{"sin(4)", "exp(4,add(5))"}, "cos(sin(4),exp(4,add(5)),sin(x))"}
		};
	}

}
