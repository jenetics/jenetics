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

import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;
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

	private static String nextString(final int length, final RandomGenerator random) {
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
			{"a", "a"},
			{"ab", "ab"},
			{"abc", "abc"},
			{"\\a", "\\a"},
			{"\\", "\\"},
			{"(", "\\("},
			{"\\(", "\\\\("},
			{"\\\\", "\\\\"},
			{"\\\\\\", "\\\\\\"},
			{"a(b(1,2),7,5)", "a\\(b\\(1\\,2\\)\\,7\\,5\\)"}
		};
	}

	@Test(invocationCount = 10)
	public void randomString() {
		final String unescaped = nextString(10_000, new Random());
		final String escaped = ParenthesesTrees.escape(unescaped);
		Assert.assertEquals(ParenthesesTrees.unescape(escaped), unescaped);
	}

}
