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

import static io.jenetics.ext.util.ParenthesesTrees.parseParenthesesString;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ParenthesesTreesTest {

	private static final String CHARS = "qwertzudsa(),\\WERTZUIO";

	private static String nextString(final int length, final Random random) {
		return IntStream.range(0, length)
			.mapToObj(i -> CHARS.charAt(random.nextInt(CHARS.length())))
			.map(Objects::toString)
			.collect(Collectors.joining());
	}

	@Test(dataProvider = "escapes")
	public void escaping(final String unescaped, final String escaped) {
		Assert.assertEquals(ParenthesesTrees.escape(unescaped), escaped);
	}

	@Test(dataProvider = "escapes")
	public void unescaping(final String unescaped, final String escaped) {
		Assert.assertEquals(ParenthesesTrees.unescape(escaped), unescaped);
	}

	@DataProvider(name = "escapes")
	public Object[][] escapes() {
		return new Object[][] {
			{"", ""},
			{"\\", "\\\\"},
			{"\\\\", "\\\\\\\\"},
			{"\\\\\\", "\\\\\\\\\\\\"},
			{"a(b(1,2),7,5)", "a\\(b\\(1\\,2\\)\\,7\\,5\\)"}
		};
	}

	@Test(invocationCount = 10)
	public void randomString() {
		final String unescaped = nextString(10_000, new Random());
		final String escaped = ParenthesesTrees.escape(unescaped);
		Assert.assertEquals(ParenthesesTrees.unescape(escaped), unescaped);
	}

	@Test(dataProvider = "tokens")
	public void tokenize(final String tree, final String[] tokens) {
		final String[] tokenize = ParenthesesTrees.tokenize(tree);
		Assert.assertEquals(tokenize, tokens);
	}

	@DataProvider(name = "tokens")
	public Object[][] tokens() {
		return new Object[][] {
			{"a", new String[]{"a"}},
			{"a\\", new String[]{"a"}},
			{"a\\\\", new String[]{"a\\"}},
			{"a(b)", new String[]{"a", "(", "b", ")"}},
			{"a(b,c)", new String[]{"a", "(", "b", ",", "c", ")"}},
			{"a(b\\))", new String[]{"a", "(", "b)", ")"}},
			{"a(\\(b\\),c\\,)", new String[]{"a", "(", "(b)", ",", "c,", ")"}}
		};
	}

	//@Test
	public void rootTree() {
		Assert.assertEquals(
			parseParenthesesString("a"),
			TreeNode.of("a")
		);
	}

	@Test
	public void oneLevelOneTree() {
		Assert.assertEquals(
			parseParenthesesString("a(b)"),
			TreeNode.of("a").attach("b")
		);
	}
	@Test
	public void oneLevelTwoTree() {
		Assert.assertEquals(
			parseParenthesesString("a(b,c)"),
			TreeNode.of("a").attach("b", "c")
		);
	}

	@Test(dataProvider = "validTrees")
	public void parseValid(final String string, final TreeNode<String> tree) {
		final TreeNode<String> node = parseParenthesesString(string);
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
		tree.setValue(Integer.toString(random.nextInt(10)*(level+1)));
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
		parseParenthesesString(invalid);
	}

	@DataProvider(name = "invalidTrees")
	public Object[][] invalidTrees() {
		return new Object[][] {
			{""},
			{"("},
			//{")"},
			{"a("},
			{"("}
		};
	}

}
