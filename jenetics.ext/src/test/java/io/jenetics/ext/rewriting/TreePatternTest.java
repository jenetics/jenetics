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
package io.jenetics.ext.rewriting;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.IO;

import io.jenetics.ext.rewriting.TreePattern.Val;
import io.jenetics.ext.rewriting.TreePattern.Var;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreePatternTest {

	private static final String[] VARS = {"a", "b", "c"};

	@Test(dataProvider = "patterns")
	public void expand(
		final String pattern,
		final String[] trees,
		final String expanded
	) {
		final TreePattern<String> tp = TreePattern.compile(pattern);
		final Map<Var<String>, Tree<String, ?>> vars = IntStream.range(0, trees.length)
			.mapToObj(i -> new Object() {
					final Var<String> name = new Var<>(VARS[i]);
					final Tree<String, ?> tree = TreeNode.parse(trees[i]);
				})
			.collect(Collectors.toMap(o -> o.name, o -> o.tree));

		Assert.assertEquals(tp.expand(vars).toParenthesesString(), expanded);
	}

	@DataProvider
	public Object[][] patterns() {
		return new Object[][] {
			{"$a", new String[]{"sin(4)"}, "sin(4)"},
			{"cos($a)", new String[]{"sin(4)"}, "cos(sin(4))"},
			{"cos($a,2,sin(x))", new String[]{"sin(4)"}, "cos(sin(4),2,sin(x))"},
			{"cos($a,$b,sin(x))", new String[]{"sin(4)"}, "cos(sin(4),sin(x))"},
			{"cos($a,$b,sin(x))", new String[]{"sin(4)", "exp(4,add(5))"}, "cos(sin(4),exp(4,add(5)),sin(x))"},
			{"mul(2,$a)", new String[]{"1"}, "mul(2,1)"},
			{"mul(\\$d,$a)", new String[]{"1"}, "mul($d,1)"},
			{"mul(\\$d sdf,$a)", new String[]{"1"}, "mul($d sdf,1)"},
			{"mul(\\\\$d,$a)", new String[]{"1"}, "mul(\\$d,1)"}
		};
	}

	@Test(dataProvider = "erroneousPatterns", expectedExceptions = IllegalArgumentException.class)
	public void compileError(final String pattern) {
		TreePattern.compile(pattern);
	}

	@DataProvider
	public Object[][] erroneousPatterns() {
		return new Object[][] {
			{"cos($a:b)"},
			{"cos($a b)"},
			{"cos($1)"}
		};
	}

	@Test
	public void expand() {
		final TreePattern<String> pattern = TreePattern.compile("add($x,$y,1)");
		final Map<Var<String>, Tree<String, ?>> vars = new HashMap<>();
		vars.put(new Var<>("x"), TreeNode.parse("sin(x)"));
		vars.put(new Var<>("y"), TreeNode.parse("sin(y)"));

		final Tree<String, ?> tree = pattern.expand(vars);
		Assert.assertEquals(tree.toParenthesesString(), "add(sin(x),sin(y),1)");
	}

	@Test
	public void map() {
		final TreePattern<String> pattern = TreePattern.compile("3($x,$y,1)");
		final TreePattern<Integer> ipattern = pattern.map(Integer::parseInt);

		Assert.assertEquals(ipattern.pattern().root().value(), new Val<>(3));
		Assert.assertEquals(ipattern.toString(), "3($x,$y,1)");
	}

	@Test
	public void serialize() throws IOException {
		final TreePattern<String> pattern = TreePattern.compile("add($x,$y,1)");
		final byte[] data = IO.object.toByteArray(pattern);
		Assert.assertEquals(IO.object.fromByteArray(data), pattern);
	}
}
