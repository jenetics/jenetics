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

import static java.lang.String.format;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreeMatcherTest {

	@Test(dataProvider = "patterns")
	public void matches(
		final String pattern,
		final String treeString,
		final boolean matches
	) {
		final TreePattern tp = TreePattern.compile(pattern);
		final Tree<String, ?> tree = TreeNode.parse(treeString);
		final TreeMatcher<String> matcher = tp.matcher(tree);

		Assert.assertEquals(
			matcher.matches(),
			matches,
			format("%s -> %s: %s", pattern, treeString, matches)
		);
	}

	@DataProvider
	public Object[][] patterns() {
		return new Object[][] {
			{"1", "1", true},
			{"1", "2", false},
			{"<x>", "2", true},
			{"<x>", "0(3,2,0(1,2),0(1,2))", true},
			{"0(1,2)", "0(1,2)", true},
			{"0(<x>,<x>)", "0(1,2)", false},
			{"0(<x>,<x>)", "0(1,1)", true},
			{"0(<x>,<x>)", "0(0(1,2),2)", false},
			{"0(<x>,<x>)", "0(0(1,2),0(1,2))", true},
			{"0(<x>,<x>)", "0(0(1,2),0(1,2,3))", false},
			{"0(<x>,<y>)", "0(0(1,2),0(1,2,3))", true},
			{"0(<x>,<y>,5)", "0(0(1,2),0(1,2,3),5)", true},
			{"0(<x>,<y>,5)", "0(0(1,2),0(1,2,3))", false},
			{"0(<x>,<y>,5)", "0(0(1,2),0(1,2,3),9)", false},
			{"0(1,2,<x>)", "0(1,2,3(4))", true},
			{"0(3,2,<x>)", "0(1,2)", false},
			{"0(3,2,<x>,<x>)", "0(3,2,0(1,2),0(1,2))", true},
			{"0(3,2,<x>,<x>)", "0(3,2,0(1,2),0(1,3))", false}
		};
	}

}
