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

import static java.lang.Double.POSITIVE_INFINITY;

import java.util.Random;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CrowdingDistanceTest {

	@Test
	public void calculate() {
		final Random random = new Random(5345);
		final int objectives = 2;
		final ISeq<Vec<double[]>> points = IntStream.range(0, 50)
			.mapToObj(i -> {
				final double k = random.nextInt(10_000);
				final double l = random.nextInt(10_000);
				return Vec.of(k, l);
			})
			.collect(ISeq.toISeq());

		final double[] distance = CrowdingDistance
			.<double[]>ofVec(objectives)
			.apply(points);

		Assert.assertEquals(
			distance,
			new double[]{
				0.07870822748764089, POSITIVE_INFINITY, 0.04223935440208857,
				0.0734723931549105, 0.08262933375836108, 0.07080599996446849,
				0.1280772521691561, 0.07500782380601785, 0.10346165319126902,
				0.08739273743069301, 0.09371414325750799, 0.09677297469253897,
				0.06860068967311245, 0.1556309291729891, 0.044762588039068914,
				0.05301158371826927, 0.05807157942534296, 0.12285056425606283,
				0.05444377867925762, 0.07798119564333192, 0.08084558556530863,
				0.05994420808040598, 0.13953549396293413, 0.08340532220133795,
				POSITIVE_INFINITY, 0.030426551909450487, 0.09843817081818175,
				0.12123391133159023, 0.04392629523611162, 0.02599473437004262,
				0.091243421437676, 0.11746678529383461, 0.08712213776362061,
				0.033935252836441665, 0.04480182979654032, 0.06176829353429881,
				0.10228229213988838, 0.07835188917954063, 0.040138923055904996,
				0.14922803236443535, 0.062381971784731975, 0.03538877910831255,
				POSITIVE_INFINITY, 0.15121414896756094, 0.11176206518424872,
				0.10671055446081582, 0.10338412049057095, 0.06070200703348302,
				0.10065784667258369, POSITIVE_INFINITY}
		);
	}

}
