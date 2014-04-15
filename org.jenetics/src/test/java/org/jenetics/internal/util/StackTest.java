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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date: 2014-04-10 $</em>
 * @since s2.0
 */
public class StackTest {

	@Test
	public void list() {
		final Stack<String> list = new Stack<>();
		list.push("a");
		list.push("b");
		list.push("c");

		//for (int i = 0; i < 3; ++i) {
		//	System.out.println(list.peek());
		//}

		for (String value = list.pop(); value != null; value = list.pop()) {
			System.out.println(value);
		}
	}

}
