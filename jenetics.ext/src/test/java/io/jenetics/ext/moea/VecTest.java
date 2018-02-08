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
package io.jenetics.ext.moea;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class VecTest {

	@Test
	public void length() {
		Assert.assertEquals(Vec.of(new double[1]).length(), 1);
		Assert.assertEquals(Vec.of(new double[2]).length(), 2);
		Assert.assertEquals(Vec.of(new double[5]).length(), 5);
		Assert.assertEquals(Vec.of(new double[10]).length(), 10);
		Assert.assertEquals(Vec.of(new double[20]).length(), 20);
		Assert.assertEquals(Vec.of(new double[100]).length(), 100);
	}

	@Test
	public void comparator() {
		final double[] d1 = new Random().doubles(10).toArray();
		final double[] d2 = new Random().doubles(10).toArray();
		final Vec<double[]> v1 = Vec.of(d1);
		final Vec<double[]> v2 = Vec.of(d2);


		for (int i = 0; i < d1.length; ++i) {
			Assert.assertEquals(Double.compare(d1[i], d2[i]), v1.compare(v2, i));
		}
	}

	@Test
	public void distance() {
		final double[] d1 = new Random().doubles(10).toArray();
		final double[] d2 = new Random().doubles(10).toArray();
		final Vec<double[]> v1 = Vec.of(d1);
		final Vec<double[]> v2 = Vec.of(d2);


		for (int i = 0; i < d1.length; ++i) {
			Assert.assertEquals(d1[i] - d2[i], v1.distance(v2, i));
		}
	}

	@Test
	public void dominance() {
		final double[] d1 = new Random().doubles(10).toArray();
		final double[] d2 = new Random().doubles(10).toArray();
		final Vec<double[]> v1 = Vec.of(d1);
		final Vec<double[]> v2 = Vec.of(d2);

		Assert.assertEquals(v1.dominance(v2), Pareto.dominance(d1, d2));
	}

}
