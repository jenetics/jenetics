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
package org.jenetics.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.math.random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class CharSeqTest extends ObjectTester<CharSeq> {

	@Override
	protected Factory<CharSeq> factory() {
		return () -> {
			final Random r = RandomRegistry.getRandom();
			return new CharSeq(random.nextString(r, r.nextInt(200) + 100));
		};
	}

	@Test
	public void distinct() {
		final CharSeq cs1 = new CharSeq("abcdeaafg");
		final CharSeq cs2 = new CharSeq("gfedcbabb");
		Assert.assertEquals(cs1, cs2);
	}

	@Test
	public void distinct1() {
		CharSeq set = new CharSeq("".toCharArray());
		Assert.assertEquals(set.toString(), "");

		set = new CharSeq("1".toCharArray());
		Assert.assertEquals(set.toString(), "1");

		set = new CharSeq("11".toCharArray());
		Assert.assertEquals(set.toString(), "1");

		set = new CharSeq("142321423456789".toCharArray());
		Assert.assertEquals(set.toString(), "123456789");

		set = new CharSeq("0000000000000000000000000".toCharArray());
		Assert.assertEquals(set.toString(), "0");

		set = new CharSeq("0111111111111111111111111112".toCharArray());
		Assert.assertEquals(set.toString(), "012");

		set = new CharSeq("111111111111111112".toCharArray());
		Assert.assertEquals(set.toString(), "12");

		set = new CharSeq("1222222222222222222".toCharArray());
		Assert.assertEquals(set.toString(), "12");

		set = new CharSeq("000000987654321111111111".toCharArray());
		Assert.assertEquals(set.toString(), "0123456789");
	}

	@Test
	public void distinct2() {
		CharSeq set = new CharSeq("");
		Assert.assertEquals(set.toString(), "");

		set = new CharSeq("1");
		Assert.assertEquals(set.toString(), "1");

		set = new CharSeq("11");
		Assert.assertEquals(set.toString(), "1");

		set = new CharSeq("1223345667899");
		Assert.assertEquals(set.toString(), "123456789");

		set = new CharSeq("0000000000000000000000000");
		Assert.assertEquals(set.toString(), "0");

		set = new CharSeq("0111111111111111111111111112");
		Assert.assertEquals(set.toString(), "012");

		set = new CharSeq("111111111111111112");
		Assert.assertEquals(set.toString(), "12");

		set = new CharSeq("1222222222222222222");
		Assert.assertEquals(set.toString(), "12");

		set = new CharSeq("000000987654321111111111");
		Assert.assertEquals(set.toString(), "0123456789");
	}

	@Test(dataProvider = "randomString")
	public void distinctRandomStrings(final String value) {
		final CharSeq seq = new CharSeq(value);
		final Set<Character> set = new HashSet<>();
		for (int i = value.length(); --i >= 0;) {
			set.add(value.charAt(i));
		}

		Assert.assertEquals(seq.length(), set.size());
		for (Character c : seq) {
			Assert.assertTrue(set.contains(c), "Set must contain " + c);
		}
	}

	@DataProvider(name = "randomString")
	public Object[][] randomString() {
		final Random random = new Random(123);
		final Object[][] strings = new Object[25][1];
		for (int i = 0; i < strings.length; ++i) {
			strings[i][0] = nextString(random, 25);
		}

		return strings;
	}

	private static String nextString(final Random random, final int length) {
		final char[] chars = new char[length];
		for (int i = 0; i < chars.length; ++i) {
			chars[i] = (char) org.jenetics.internal.math.random.nextInt(random, 'a', 'k');
		}

		return new String(chars);
	}

	@Test
	public void expand1() {
		String value = CharSeq.expand('a', 'z');
		Assert.assertEquals(value.length(), 26);
		Assert.assertEquals(value, "abcdefghijklmnopqrstuvwxyz");

		value = CharSeq.expand('A', 'Z');
		Assert.assertEquals(value.length(), 26);
		Assert.assertEquals(value, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

		value = CharSeq.expand('0', '9');
		Assert.assertEquals(value.length(), 10);
		Assert.assertEquals(value, "0123456789");
	}

	@Test
	public void expand2() {
		String value = CharSeq.expand("a-z");
		Assert.assertEquals(value.length(), 26);
		Assert.assertEquals(value, "abcdefghijklmnopqrstuvwxyz");

		value = CharSeq.expand("a-z\\-");
		Assert.assertEquals(value.length(), 27);
		Assert.assertEquals(value, "abcdefghijklmnopqrstuvwxyz-");

		value = CharSeq.expand("a-z\\\\xx");
		Assert.assertEquals(value.length(), 29);
		Assert.assertEquals(value, "abcdefghijklmnopqrstuvwxyz\\xx");

		value = CharSeq.expand("A-Z");
		Assert.assertEquals(value.length(), 26);
		Assert.assertEquals(value, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

		value = CharSeq.expand("0-9");
		Assert.assertEquals(value.length(), 10);
		Assert.assertEquals(value, "0123456789");

		value = CharSeq.expand("0-9yxcvba-z");
		Assert.assertEquals(value.length(), 41);
		Assert.assertEquals(value, "0123456789yxcvbabcdefghijklmnopqrstuvwxyz");

		value = CharSeq.expand("0-9a-zA-Z");
		Assert.assertEquals(value.length(), 10 + 26 + 26);
		Assert.assertEquals(value, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
	}

	@Test(expectedExceptions = PatternSyntaxException.class)
	public void expand3() {
		CharSeq.expand("a-z-");
	}

	@Test(expectedExceptions = PatternSyntaxException.class)
	public void expand4() {
		CharSeq.expand("-az");
	}

	@Test
	public void contains() {
		final CharSeq set = new CharSeq(CharSeq.expand("a-z"));
		Assert.assertTrue(set.contains('t'));
		Assert.assertTrue(set.contains('a'));
		Assert.assertTrue(set.contains('z'));
		Assert.assertFalse(set.contains('T'));
		Assert.assertFalse(set.contains('1'));
		Assert.assertFalse(set.contains('Z'));
	}

	@Test
	public void iterate() {
		final CharSeq set = new CharSeq(CharSeq.expand("a-z"));
		final String values = CharSeq.expand("a-z");
		final Iterator<Character> it = set.iterator();
		for (int i = 0; i < values.length(); ++i) {
			Assert.assertTrue(it.hasNext());
			Assert.assertEquals(it.next(), Character.valueOf(values.charAt(i)));
		}
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void subSequence() {
		final CharSeq set = new CharSeq(CharSeq.expand("a-z"));
		final CharSeq sub = set.subSequence(3, 10);
		Assert.assertEquals(sub.length(), 7);
		Assert.assertEquals(sub.toString(), "defghij");
	}

}
