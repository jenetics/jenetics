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
package io.jenetics.util;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class BufferTest {

	@Test
	public void add() {
		final Buffer<Integer> buffer = Buffer.ofCapacity(10);
		buffer.add(1);
		buffer.add(2);
		buffer.add(3);

		Assert.assertEquals(buffer.toSeq(), ISeq.of(1, 2, 3));
		Assert.assertEquals(
			ISeq.of(buffer.toArray(Integer[]::new)),
			ISeq.of(1, 2, 3)
		);
	}

	@Test
	public void addAll() {
		final Buffer<Integer> buffer = Buffer.ofCapacity(10);
		buffer.addAll(1, 2, 3);

		Assert.assertEquals(buffer.toSeq(), ISeq.of(1, 2, 3));
		Assert.assertEquals(
			ISeq.of(buffer.toArray(Integer[]::new)),
			ISeq.of(1, 2, 3)
		);
	}

	@Test
	public void add1() {
		final Buffer<Integer> buffer = Buffer.ofCapacity(10);
		buffer.add(1);

		Assert.assertEquals(buffer.toSeq(), ISeq.of(1));
		Assert.assertEquals(
			ISeq.of(buffer.toArray(Integer[]::new)),
			ISeq.of(1)
		);
	}

	@Test
	public void addAll1() {
		final Buffer<Integer> buffer = Buffer.ofCapacity(10);
		buffer.addAll(1);

		Assert.assertEquals(buffer.toSeq(), ISeq.of(1));
	}

	@Test
	public void add2() {
		final Buffer<Integer> buffer = Buffer.ofCapacity(10);
		for (int i = 0; i < 7; ++i) {
			buffer.add(i);
		}
		Assert.assertEquals(buffer.toSeq(), ISeq.of(0, 1, 2, 3, 4, 5, 6));
		Assert.assertEquals(
			ISeq.of(buffer.toArray(Integer[]::new)),
			ISeq.of(0, 1, 2, 3, 4, 5, 6)
		);
	}

	@Test
	public void addAll2() {
		final Buffer<Integer> buffer = Buffer.ofCapacity(10);
		buffer.addAll(0, 1, 2, 3, 4, 5, 6);
		Assert.assertEquals(buffer.toSeq(), ISeq.of(0, 1, 2, 3, 4, 5, 6));

		buffer.addAll(7, 8);
		Assert.assertEquals(buffer.toSeq(), ISeq.of(0, 1, 2, 3, 4, 5, 6, 7, 8));

		buffer.addAll(9, 10, 11, 12, 13, 14);
		Assert.assertEquals(buffer.toSeq(), ISeq.of(5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
	}

	@Test
	public void add3() {
		final Buffer<Integer> buffer = Buffer.ofCapacity(10);
		for (int i = 0; i < 19; ++i) {
			buffer.add(i);
		}
		Assert.assertEquals(buffer.toSeq(), ISeq.of(9,10,11,12,13,14,15,16,17,18));
		Assert.assertEquals(
			ISeq.of(buffer.toArray(Integer[]::new)),
			ISeq.of(9,10,11,12,13,14,15,16,17,18)
		);
	}

	@Test
	public void addAll3() {
		final Buffer<Integer> buffer = Buffer.ofCapacity(10);
		buffer.addAll(0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18);
		Assert.assertEquals(buffer.toSeq(), ISeq.of(9,10,11,12,13,14,15,16,17,18));
	}

	@Test
	public void toArray() {
		final Buffer<Integer> buffer = Buffer.ofCapacity(5);
		final Object[] array = buffer.toArray(Integer[]::new);
		Assert.assertEquals(array.length, 0);
	}

}
