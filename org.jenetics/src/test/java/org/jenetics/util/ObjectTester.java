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

import static org.jenetics.util.MSeq.toMSeq;
import static org.jenetics.util.RandomRegistry.with;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.test.Retry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class ObjectTester<T> extends Retry {

	protected abstract Factory<T> factory();

	protected MSeq<T> newEqualObjects(final int length) {
		return Stream
			.generate(() -> with(new Random(589), r -> factory().newInstance()))
			.limit(length)
			.collect(toMSeq());
	}

	@Test
	public void equals() {
		final MSeq<T> same = newEqualObjects(5);

		final Object that = same.get(0);
		for (int i = 1; i < same.length(); ++i) {
			final Object other = same.get(i);

			Assert.assertEquals(other, other);
			Assert.assertEquals(other, that);
			Assert.assertEquals(that, other);
			Assert.assertEquals(that.hashCode(), other.hashCode());
		}
	}

	@Test
	public void notEquals() {
		for (int i = 0; i < 10; ++i) {
			final Object that = factory().newInstance();
			final Object other = factory().newInstance();

			if (that.equals(other)) {
				Assert.assertTrue(other.equals(that));
				Assert.assertEquals(that.hashCode(), other.hashCode());
			} else {
				Assert.assertFalse(other.equals(that));
				Assert.assertFalse(that.equals(other));
			}
		}
	}

	@Test
	public void notEqualsNull() {
		final Object that = factory().newInstance();
		Assert.assertFalse(that == null);
	}

	@Test
	public void notEqualsStringType() {
		final Object that = factory().newInstance();
		Assert.assertFalse(that.equals("__some_string__"));
	}

	@Test
	public void notEqualsClassType() {
		final Object that = factory().newInstance();
		Assert.assertFalse(that.equals(Class.class));
	}

	@Test
	public void hashCodeMethod() {
		final MSeq<T> same = newEqualObjects(5);

		final Object that = same.get(0);
		for (int i = 1; i < same.length(); ++i) {
			final Object other = same.get(i);

			Assert.assertEquals(that.hashCode(), other.hashCode());
		}
	}

	@Test
	public void cloneMethod() throws Exception {
		final Object that = factory().newInstance();

		if (that instanceof Cloneable) {
			final Method clone = that.getClass().getMethod("clone");
			final Object other = clone.invoke(that);

			Assert.assertEquals(other, that);
			Assert.assertNotSame(other, that);
		}
	}

	@Test
	public void copyMethod() {
		final Object that = factory().newInstance();
		if (that instanceof Copyable<?>) {
			final Object other = ((Copyable<?>)that).copy();
			if (other.getClass() == that.getClass()) {
				Assert.assertEquals(other, that);
				Assert.assertNotSame(other, that);
			}
		}
	}

	@Test
	public void toStringMethod() {
		final MSeq<T> same = newEqualObjects(5);

		final Object that = same.get(0);
		for (int i = 1; i < same.length(); ++i) {
			final Object other = same.get(i);

			Assert.assertEquals(that.toString(), other.toString());
			Assert.assertNotNull(other.toString());
		}
	}

	@Test
	public void isValid() {
		final T a = factory().newInstance();
		if (a instanceof Verifiable) {
			Assert.assertTrue(((Verifiable)a).isValid());
		}
	}

	@Test
	public void objectSerialize() throws Exception {
		final Object object = factory().newInstance();

		if (object instanceof Serializable) {
			for (int i = 0; i < 10; ++i) {
				final Serializable serializable =
					(Serializable)factory().newInstance();

				Serialize.object.test(serializable);
			}
		}
	}

}
