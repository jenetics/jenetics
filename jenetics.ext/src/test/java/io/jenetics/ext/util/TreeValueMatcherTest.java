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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.util.TreeRewriter.Matcher;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreeValueMatcherTest {

	@Test(dataProvider = "subTreeMatches")
	public void subTrees(
		final String patternString,
		final String treeString,
		final boolean matches
	) {
		final Tree<String, ?> pattern = TreeNode.parse(patternString);
		final Matcher<Integer> matcher = TreeValueMatcher.of(pattern, Integer::parseInt);

		final Tree<Integer, ?> tree = TreeNode.parse(treeString, Integer::parseInt);
		Assert.assertEquals(matcher.matches(tree), matches);
	}

	@DataProvider(name = "subTreeMatches")
	public Object[][] subTreeMatches() {
		return new Object[][] {
			{"0(1,2)", "0(1,2)", true},
			{"0(1,2,X)", "0(1,2)", true},
			{"0(3,2,X)", "0(1,2)", false}
		};
	}

}
