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
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-06-14 $</em>
 */
public class objectTest {

	@Test
	public void rangeCheckPredicate1() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		array.forEach(CheckRange(0, 100));
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void rangeCheckPredicate2() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		array.set(45, null);
		array.forEach(CheckRange(0, 100));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void rangeCheckPredicate3() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		array.set(45, 333);
		array.forEach(CheckRange(0, 100));
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

		array.forEach(NonNull);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void nonNullPredicate2() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		array.set(45, null);
		array.forEach(NonNull);
	}

}





