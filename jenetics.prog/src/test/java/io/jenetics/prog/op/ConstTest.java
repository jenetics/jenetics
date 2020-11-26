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
package io.jenetics.prog.op;

import static java.lang.String.format;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.io.IOException;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.IO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ConstTest {

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(Const.class)
			.withIgnoredFields("_name")
			.verify();
	}

	@Test(dataProvider = "floatEqualValues")
	public void floatEquals(final Float a, final Float b, final boolean equals) {
		final var ca = Const.of(a);
		final var cb = Const.of(b);

		Assert.assertEquals(
			ca.equals(cb), equals,
			format("Const[%s] == Const[%s] != %s", a, b, equals)
		);
	}

	@DataProvider
	public Object[][] floatEqualValues() {
		return new Object[][] {
			{null, null, true},
			{1.0f, null, false},
			{null, 1.0f, false},
			{1.0f, 1.0f, true},
			{Float.NaN, Float.NaN, true},
			{Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, true},
			{Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, true},
			{Float.NaN, Float.POSITIVE_INFINITY, false},
			{Float.NaN, Float.NEGATIVE_INFINITY, false},
			{Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, false},
			{Float.NaN, 1.0f, false}
		};
	}

	@Test(dataProvider = "doubleEqualValues")
	public void doubleEquals(final Double a, final Double b, final boolean equals) {
		final var ca = Const.of(a);
		final var cb = Const.of(b);

		Assert.assertEquals(
			ca.equals(cb), equals,
			format("Const[%s] == Const[%s] != %s", a, b, equals)
		);
	}

	@DataProvider
	public Object[][] doubleEqualValues() {
		return new Object[][] {
			{null, null, true},
			{1.0, null, false},
			{null, 1.0, false},
			{1.0, 1.0, true},
			{Double.NaN, Double.NaN, true},
			{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, true},
			{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true},
			{Double.NaN, Double.POSITIVE_INFINITY, false},
			{Double.NaN, Double.NEGATIVE_INFINITY, false},
			{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, false},
			{Double.NaN, 1.0, false}
		};
	}

	@Test
	public void serialize() throws IOException {
		final Const<Integer> object = Const.of("some name", new Random().nextInt());
		final byte[] data = IO.object.toByteArray(object);
		Assert.assertEquals(IO.object.fromByteArray(data), object);
	}

}
