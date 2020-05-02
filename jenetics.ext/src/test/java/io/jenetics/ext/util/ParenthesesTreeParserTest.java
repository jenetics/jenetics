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

import static io.jenetics.ext.util.ParenthesesTreeParser.parse;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.util.ParenthesesTreeParser.Token;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ParenthesesTreeParserTest {

	@Test(dataProvider = "tokens")
	public void tokenize(final String tree, final String[] tokens) {
		final List<Token> tokenize = ParenthesesTreeParser.tokenize(tree);
		Assert.assertEquals(
			tokenize.stream()
				.map(t -> t.seq)
				.toArray(String[]::new),
			tokens
		);
	}

	@DataProvider(name = "tokens")
	public Object[][] tokens() {
		return new Object[][] {
			{"a", new String[]{"a"}},
			{"a\\", new String[]{"a\\"}},
			{"a\\\\", new String[]{"a\\\\"}},
			{"a(b)", new String[]{"a", "(", "b", ")"}},
			{"a(b,c)", new String[]{"a", "(", "b", ",", "c", ")"}},
			{"a(b\\))", new String[]{"a", "(", "b)", ")"}},
			{"a(\\(b\\),c\\,)", new String[]{"a", "(", "(b)", ",", "c,", ")"}}
		};
	}


	@Test
	public void emptyTree() {
		Assert.assertEquals(
			parse("", Function.identity()),
			TreeNode.of()
		);
	}

	@Test
	public void rootTree() {
		Assert.assertEquals(
			parse("a", Function.identity()),
			TreeNode.of("a")
		);
	}

	@Test
	public void oneLevelOneTree() {
		Assert.assertEquals(
			parse("a(b)", Function.identity()),
			TreeNode.of("a").attach("b")
		);
	}
	@Test
	public void oneLevelTwoTree() {
		Assert.assertEquals(
			parse("a(b,c)", Function.identity()),
			TreeNode.of("a").attach("b", "c")
		);
	}

	@Test
	public void oneLevelTwoThreeTree() {
		Assert.assertEquals(
			parse("a(b,c,d)", Function.identity()),
			TreeNode.of("a").attach("b", "c", "d")
		);
	}

	@Test
	public void oneLevelThreeThreeTree() {
		Assert.assertEquals(
			parse("a  ( b , c  ,  d ( 1 , 2  )  )", String::trim),
			TreeNode.of("a")
				.attach("b", "c")
				.attach(TreeNode.of("d")
					.attach("1", "2"))
		);
	}

	@Test(dataProvider = "validTrees")
	public void parseValid(final String string, final TreeNode<String> tree) {
		final TreeNode<String> node = parse(string, Function.identity());
		final String nodeString = node.toParenthesesString();

		Assert.assertEquals(nodeString, string);
		Assert.assertEquals(node, tree);
	}

	@DataProvider(name = "validTrees")
	public Object[][] validTrees() {
		final Random random = new Random();

		return IntStream.range(0, 10)
			.mapToObj(i -> of(7, random))
			.map(o -> new Object[]{o.toParenthesesString(), o})
			.toArray(Object[][]::new);
	}

	public static TreeNode<String> of(final int depth, final Random random) {
		final TreeNode<String> root = TreeNode.of("R");
		fill(depth, root, random);
		return root;
	}

	private static void fill(
		final int level,
		final TreeNode<String> tree,
		final Random random
	) {
		tree.value(Integer.toString(random.nextInt(10)*(level+1)));
		if (level > 1) {
			for (int i = 0, n = random.nextInt(4) + 1; i < n; ++i) {
				final TreeNode<String> node = TreeNode.of();
				fill(level - 1, node, random);
				tree.attach(node);
			}
		}
	}

	@Test(dataProvider = "invalidTrees", expectedExceptions = IllegalArgumentException.class)
	public void parseInvalid(final String invalid) {
		final Object obj = parse(invalid, Function.identity());
		System.out.println(obj);
	}

	@DataProvider(name = "invalidTrees")
	public Object[][] invalidTrees() {
		return new Object[][] {
			{"("},
			{")"},
			{"a("},
			{"())"},
			{","},
			{"a(b(c,d)"},
			{"a(b(c,d)))"},
			{"a(b(c,d)),"},
			{"()()"},
			{"())"},
			{"()("},
			{"a(b,c)("},
			{"a(b,c)d("},
			{"a(b,c)d(e,f)"},
			{"a(b,c),d(e,f)"}
		};
	}

}
