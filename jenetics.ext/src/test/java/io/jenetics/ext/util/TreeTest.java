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

import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TreeTest {

	private static final Tree<String, ?> TREE = TreeNode.of("1")
		.attach("2")
		.attach(TreeNode.of("3")
			.attach(TreeNode.of("4")
				.attach("5")
				.attach("6"))
			.attach(TreeNode.of("7")
				.attach("8")
				.attach("9")))
		.attach(TreeNode.of("10"));

	@Test(dataProvider = "paths")
	public void childByPath(final int[] path, final String result)  {
		Assert.assertEquals(
			TREE.childAtPath(path).map(t -> t.getValue()),
			Optional.ofNullable(result)
		);
	}

	@DataProvider(name = "paths")
	public Object[][] paths() {
		return new Object[][] {
			{new int[0], "1"},
			{new int[]{0}, "2"},
			{new int[]{1}, "3"},
			{new int[]{1, 0}, "4"},
			{new int[]{1, 1}, "7"},
			{new int[]{1, 1, 0}, "8"},
			{new int[]{1, 1, 1}, "9"},
			{new int[]{2}, "10"},
			{new int[]{10}, null},
			{new int[]{0, 0, 0, 0}, null}
		};
	}

}
