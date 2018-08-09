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
public class SubTreeMatcherTest {

	@Test(dataProvider = "treePattern")
	public void subTrees(
		final String patternString,
		final String treeString,
		final boolean matches
	) {
		final Tree<String, ?> pattern = TreeNode.parse(patternString);
		final Matcher<String> matcher = SubTreeMatcher.of(pattern);

		final Tree<String, ?> tree = TreeNode.parse(treeString);
		Assert.assertEquals(matcher.matches(tree), matches);
	}

	@DataProvider(name = "treePattern")
	public Object[][] treePattern() {
		return new Object[][] {
			{"add(X,1)", "add(sub(1,2),1)", true},
			{"sub(X,X)", "add(sub(1,2),sub(1,2))", true},
			{"sub(X,X)", "add(sub(1,add(a,b)),sub(1,add(a,b)))", true},
			{"add(X,Y)", "add(sub(1,2),foo(a,v))", true},
			{"add(X,Y,Y)", "add(sub(1,2),foo(a,v))", false},
			{"add(X,Y,Y)", "add(sub(1,2),foo(a,v),foo(a,v))", true},
		};
	}

}
