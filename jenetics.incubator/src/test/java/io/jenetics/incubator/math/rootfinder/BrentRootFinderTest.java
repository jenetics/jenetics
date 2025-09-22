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
package io.jenetics.incubator.math.rootfinder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.DoubleUnaryOperator;

import org.assertj.core.data.Offset;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.DoubleRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class BrentRootFinderTest {

	private static final Offset<Double> DEFAULT_ABSOLUTE_ACCURACY =
		Offset.offset(1e-6);

	@Test(dataProvider = "testSinZeroParameters")
	void testSinZero(double result, DoubleRange interval) {
		assertThat(BrentRootFinder.DEFAULT.solve(Math::sin, interval))
			.isCloseTo(result, DEFAULT_ABSOLUTE_ACCURACY);
	}

	@DataProvider
	public Object[][] testSinZeroParameters() {
		return new Object[][] {
			{Math.PI, new DoubleRange(3, 4)},
			{Math.PI, new DoubleRange(1, 4)}
		};
	}

	@Test(dataProvider = "testQuinticZeroParameters")
	void testQuinticZero(double result, DoubleRange interval) {
		final DoubleUnaryOperator func = x -> (x - 1)*(x - 0.5)*x*(x + 0.5)*(x + 1);

		assertThat(BrentRootFinder.DEFAULT.solve(func, interval))
			.isCloseTo(result, DEFAULT_ABSOLUTE_ACCURACY);
	}

	@DataProvider
	public Object[][] testQuinticZeroParameters() {
		return new Object[][] {
			{0.0, new DoubleRange(-0.2, 0.2)},
			{0.0, new DoubleRange(-0.1, 0.3)},
			{0.0, new DoubleRange(-0.3, 0.45)},
			{0.5, new DoubleRange(0.3, 0.7)},
			{0.5, new DoubleRange(0.2, 0.6)},
			{0.5, new DoubleRange(0.05, 0.95)},
			{1.0, new DoubleRange(0.85, 1.25)},
			{1.0, new DoubleRange(0.8, 1.2)},
			{1.0, new DoubleRange(0.85, 1.75)},
			{1.0, new DoubleRange(0.55, 1.45)}
		};
	}

}
