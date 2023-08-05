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
package io.jenetics.ext.internal.util;

import static io.jenetics.CharacterGene.DEFAULT_CHARACTERS;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class EscaperTest {

	private static final Escaper ESCAPER = new Escaper('\\', 'a', 'b', 'c');

	@Test(dataProvider = "strings")
	public void escape(final String original, final String escaped) {
		final String esc = ESCAPER.escape(original);
		final String orig = ESCAPER.unescape(esc);

		Assert.assertEquals(esc, escaped);
		Assert.assertEquals(orig, original);
	}

	@DataProvider
	public Object[][] strings() {
		return new Object[][] {
			{"", ""},
			{"t", "t"},
			{"a", "\\a"},
			{"\\", "\\"}
		};
	}

	@Test(invocationCount = 10)
	public void escapeRandomStrings() {
		final String original = nextString(new Random());
		final String escaped = ESCAPER.escape(original);
		Assert.assertEquals(ESCAPER.unescape(escaped), original);
	}

	private static String nextString(final Random random) {
		return random.ints(0, DEFAULT_CHARACTERS.length())
			.limit(100)
			.mapToObj(DEFAULT_CHARACTERS::get)
			.map(Objects::toString)
			.collect(Collectors.joining());
	}

}
