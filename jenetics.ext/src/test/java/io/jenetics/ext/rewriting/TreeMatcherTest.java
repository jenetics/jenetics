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

import static java.lang.String.format;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreeMatcherTest {

	@Test(dataProvider = "patterns")
	public void matches(
		final String patternString,
		final String treeString,
		final boolean matches
	) {
		final TreePattern<String> pattern = TreePattern.compile(patternString);
		final Tree<String, ?> tree = TreeNode.parse(treeString);
		final TreeMatcher<String> matcher = pattern.matcher(tree);

		Assert.assertEquals(
			matcher.matches(),
			matches,
			format("%s -> %s: %s", patternString, treeString, matches)
		);
	}

	@DataProvider
	public Object[][] patterns() {
		return new Object[][] {
			{"1", "1", true},
			{"1", "2", false},
			{"$x", "2", true},
			{"$x", "R(3,2,R(1,2),R(1,2))", true},
			{"R(1,2)", "R(1,2)", true},
			{"R($x,$x)", "R(1,2)", false},
			{"R($x,$x)", "R(1,1)", true},
			{"R($x,$x)", "R(R(1,2),2)", false},
			{"R($x,$x)", "R(R(1,2),R(1,2))", true},
			{"R($x,$x)", "R(R(1,2),R(1,2,3))", false},
			{"R($x,$y)", "R(R(1,2),R(1,2,3))", true},
			{"R($x,$y,5)", "R(R(1,2),R(1,2,3),5)", true},
			{"R($x,$y,5)", "R(R(1,2),R(1,2,3))", false},
			{"R($x,$y,5)", "R(R(1,2),R(1,2,3),9)", false},
			{"R(1,2,$x)", "R(1,2,3(4))", true},
			{"R(3,2,$x)", "R(1,2)", false},
			{"R(3,2,$x,$x)", "R(3,2,R(1,2),R(1,2))", true},
			{"R(3,2,$x,$x)", "R(3,2,R(1,2),R(1,3))", false}
		};
	}

	@Test(dataProvider = "matchResults")
	public void results(
		final String patternString,
		final String treeString,
		final String[] results
	) {
		final TreePattern<String> pattern = TreePattern.compile(patternString);
		final Tree<String, ?> tree = TreeNode.parse(treeString);
		final String[] matches = pattern.matcher(tree).results()
			.map(t -> t.tree().toParenthesesString())
			.toArray(String[]::new);

		Assert.assertEquals(matches, results);
	}

	@DataProvider
	public Object[][] matchResults() {
		return new Object[][] {
			{"1", "R(1,1)", new String[]{"1", "1"}},
			{"O(2,3)", "R(1,O(2,3),5,O(2,3))", new String[]{"O(2,3)", "O(2,3)"}}
		};
	}

}
