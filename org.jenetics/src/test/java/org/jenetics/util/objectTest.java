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

import static org.jenetics.util.functions.not;
import static org.jenetics.util.object.CheckRange;
import static org.jenetics.util.object.NonNull;
import static org.jenetics.util.object.Verify;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class objectTest {

	@Test(dataProvider = "byteStrData")
	public void byteStr(final byte[] data, final String result) {
		Assert.assertEquals(object.str(data), result);
	}

	@DataProvider(name = "byteStrData")
	public Object[][] byteStrData() {
		return new Object[][] {
			{ new byte[]{(byte)0}, "00000000" },
			{ new byte[]{(byte)1}, "00000001" },
			{ new byte[]{(byte)2}, "00000010" },
			{ new byte[]{(byte)4}, "00000100" },
			{ new byte[]{(byte)0xFF}, "11111111" },

			{ new byte[]{(byte)0, (byte)0}, "00000000|00000000" },
			{ new byte[]{(byte)1, (byte)0}, "00000000|00000001" },
			{ new byte[]{(byte)0, (byte)1}, "00000001|00000000" },
			{ new byte[]{(byte)1, (byte)1}, "00000001|00000001" }
		};
	}

	@Test
	public void rangeCheckPredicate1() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		array.foreach(CheckRange(0, 100));
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void rangeCheckPredicate2() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		array.set(45, null);
		array.foreach(CheckRange(0, 100));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void rangeCheckPredicate3() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		array.set(45, 333);
		array.foreach(CheckRange(0, 100));
	}

	@Test
	public void validPredicate() {
		final Array<Verifiable> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, new Verifiable() {
				@Override public boolean isValid() {
					return true;
				}
			});
		}
		Assert.assertEquals(array.indexWhere(not(Verify)), -1);

		array.set(77, new Verifiable() {
			@Override public boolean isValid() {
				return false;
			}
		});
		Assert.assertEquals(array.indexWhere(not(Verify)), 77);
	}

	@Test
	public void nonNullPredicate1() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}

		array.foreach(NonNull);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void nonNullPredicate2() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		array.set(45, null);
		array.foreach(NonNull);
	}

	@Test
	public void nonNull1() {
		object.nonNull("df");
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void nonNull2() {
		object.nonNull(null);
	}

}





