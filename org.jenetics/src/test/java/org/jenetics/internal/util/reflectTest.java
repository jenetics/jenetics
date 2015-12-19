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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class reflectTest {

	@Test
	public void innerClasses() {
		reflect.innerClasses(reflectTest.class).forEach(System.out::println);
		final long count = reflect.innerClasses(reflectTest.class)
			.distinct()
			.count();

		Assert.assertEquals(count, 10);
	}

	private static final class _1 {
		private static final class _2 {
			private static final class _3 {}
			private static final class _4 {}
		}
		private static final class _5 {}
		public static final class _6 {}
	}

	private static final class _7 {
		private static final class _8 {}
		private static final class _9 {}
		private static final class _10 {}
	}

}
