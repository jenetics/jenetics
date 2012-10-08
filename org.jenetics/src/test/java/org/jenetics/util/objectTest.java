/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
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
 */
public class objectTest {

	@Test(dataProvider = "byteStrData")
	public void byteStr(final byte[] data, final String result) {
		Assert.assertEquals(object.str((byte[])data), result);
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





