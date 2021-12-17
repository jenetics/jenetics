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
package io.jenetics;

import static java.util.Comparator.nullsFirst;

import java.util.Comparator;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class OptimizeTest {

	private static Phenotype<DoubleGene, Double> pt(double value) {
		return Phenotype.of(
			Genotype.of(DoubleChromosome.of(DoubleGene.of(value, 0, 10))),
			0, value
		);
	}

	@Test
	public void comparator() {
		Comparator<Phenotype<DoubleGene, Double>> comp =
			Optimize.MAXIMUM.<Phenotype<DoubleGene, Double>>descending();
		Assert.assertTrue(comp.compare(pt(2), pt(3)) > 0);
		Assert.assertEquals(comp.compare(pt(2), pt(2)), 0);
		Assert.assertTrue(comp.compare(pt(5), pt(3)) < 0);

		comp = Optimize.MINIMUM.<Phenotype<DoubleGene, Double>>descending();
		Assert.assertTrue(comp.compare(pt(4), pt(3)) > 0);
		Assert.assertEquals(comp.compare(pt(2), pt(2)), 0);
		Assert.assertTrue(comp.compare(pt(2), pt(3)) < 0);
	}

	@Test
	public void compare() {
		final Phenotype<DoubleGene, Double> pt1 = Phenotype.of(Genotype.of(
			DoubleChromosome.of(DoubleGene.of(5, 0, 10))),
			0, 5.0
		);
		final Phenotype<DoubleGene, Double> pt2 = Phenotype.of(Genotype.of(
			DoubleChromosome.of(DoubleGene.of(7, 0, 10))),
			0, 7.0
		);
		final Phenotype<DoubleGene, Double> pt3 = Phenotype.of(Genotype.of(
			DoubleChromosome.of(DoubleGene.of(7, 0, 10))),
			0, 7.0
		);

		Assert.assertTrue(Optimize.MINIMUM.compare(pt1, pt2) > 0);
		Assert.assertTrue(Optimize.MAXIMUM.compare(pt1, pt2) < 0);
		Assert.assertEquals(Optimize.MINIMUM.compare(pt3, pt2), 0);
		Assert.assertEquals(Optimize.MAXIMUM.compare(pt3, pt2), 0);
	}

	@Test
	public void best() {
		final Phenotype<DoubleGene, Double> pt1 = Phenotype.of(Genotype.of(
			DoubleChromosome.of(DoubleGene.of(5, 0, 10))),
			0, 5.0
		);
		final Phenotype<DoubleGene, Double> pt2 = Phenotype.of(Genotype.of(
			DoubleChromosome.of(DoubleGene.of(7, 0, 10))),
			0, 7.0
		);
		final Phenotype<DoubleGene, Double> pt3 = Phenotype.of(Genotype.of(
			DoubleChromosome.of(DoubleGene.of(7, 0, 10))),
			0, 7.0
		);

		Assert.assertSame(Optimize.MINIMUM.best(pt1, pt2), pt1);
		Assert.assertSame(Optimize.MAXIMUM.best(pt1, pt2), pt2);
		Assert.assertSame(Optimize.MINIMUM.best(pt2, pt3), pt2);
	}

	@Test
	public void worst() {
		final Phenotype<DoubleGene, Double> pt1 = Phenotype.of(Genotype.of(
			DoubleChromosome.of(DoubleGene.of(5, 0, 10))),
			0, 5.0
		);
		final Phenotype<DoubleGene, Double> pt2 = Phenotype.of(Genotype.of(
			DoubleChromosome.of(DoubleGene.of(7, 0, 10))),
			0, 7.0
		);
		final Phenotype<DoubleGene, Double> pt3 = Phenotype.of(Genotype.of(
			DoubleChromosome.of(DoubleGene.of(7, 0, 10))),
			0, 7.0
		);

		Assert.assertSame(Optimize.MINIMUM.worst(pt1, pt2), pt2);
		Assert.assertSame(Optimize.MAXIMUM.worst(pt1, pt2), pt1);
		Assert.assertSame(Optimize.MINIMUM.worst(pt2, pt3), pt2);
	}

	@Test
	public void nullCompareMaximum() {
		final Comparator<Integer> comparator = nullsFirst(Optimize.MAXIMUM::compare);

		Assert.assertEquals(comparator.compare(null, null), 0);
		Assert.assertEquals(comparator.compare(null, 4), -1);
		Assert.assertEquals(comparator.compare(4, null), 1);

		Assert.assertEquals(comparator.compare(4, 2), 1);
		Assert.assertEquals(comparator.compare(4, 7), -1);
		Assert.assertEquals(comparator.compare(4, 4), 0);
	}

	@Test
	public void nullCompareMinimum() {
		final Comparator<Integer> comparator = nullsFirst(Optimize.MINIMUM::compare);

		Assert.assertEquals(comparator.compare(null, null), 0);
		Assert.assertEquals(comparator.compare(null, 4), -1);
		Assert.assertEquals(comparator.compare(4, null), 1);

		Assert.assertEquals(comparator.compare(4, 2), -1);
		Assert.assertEquals(comparator.compare(4, 7), 1);
		Assert.assertEquals(comparator.compare(4, 4), 0);
	}

	@Test
	public void nullBestMaximum() {
		Assert.assertNull(Optimize.MAXIMUM.<Integer>best().apply(null, null));
		Assert.assertEquals(Optimize.MAXIMUM.<Integer>best().apply(null, 4), (Integer)4);
		Assert.assertEquals(Optimize.MAXIMUM.<Integer>best().apply(6, null), (Integer)6);

		Assert.assertEquals(Optimize.MAXIMUM.<Integer>best().apply(4, 4), (Integer)4);
		Assert.assertEquals(Optimize.MAXIMUM.<Integer>best().apply(6, 7), (Integer)7);
		Assert.assertEquals(Optimize.MAXIMUM.<Integer>best().apply(16, 7), (Integer)16);
	}

	@Test
	public void nullBestMinimum() {
		Assert.assertNull(Optimize.MINIMUM.<Integer>best().apply(null, null));
		Assert.assertEquals(Optimize.MINIMUM.<Integer>best().apply(null, 4), (Integer)4);
		Assert.assertEquals(Optimize.MINIMUM.<Integer>best().apply(6, null), (Integer)6);

		Assert.assertEquals(Optimize.MINIMUM.<Integer>best().apply(4, 4), (Integer)4);
		Assert.assertEquals(Optimize.MINIMUM.<Integer>best().apply(6, 7), (Integer)6);
		Assert.assertEquals(Optimize.MINIMUM.<Integer>best().apply(16, 7), (Integer)7);
	}

	@Test
	public void nullWorstMaximum() {
		Assert.assertNull(Optimize.MAXIMUM.<Integer>worst().apply(null, null));
		Assert.assertEquals(Optimize.MAXIMUM.<Integer>worst().apply(null, 4), (Integer)4);
		Assert.assertEquals(Optimize.MAXIMUM.<Integer>worst().apply(6, null), (Integer)6);

		Assert.assertEquals(Optimize.MAXIMUM.<Integer>worst().apply(4, 4), (Integer)4);
		Assert.assertEquals(Optimize.MAXIMUM.<Integer>worst().apply(6, 7), (Integer)6);
		Assert.assertEquals(Optimize.MAXIMUM.<Integer>worst().apply(16, 7), (Integer)7);
	}

	@Test
	public void nullWorstMinimum() {
		Assert.assertNull(Optimize.MINIMUM.<Integer>worst().apply(null, null));
		Assert.assertEquals(Optimize.MINIMUM.<Integer>worst().apply(null, 4), (Integer)4);
		Assert.assertEquals(Optimize.MINIMUM.<Integer>worst().apply(6, null), (Integer)6);

		Assert.assertEquals(Optimize.MINIMUM.<Integer>worst().apply(4, 4), (Integer)4);
		Assert.assertEquals(Optimize.MINIMUM.<Integer>worst().apply(6, 7), (Integer)7);
		Assert.assertEquals(Optimize.MINIMUM.<Integer>worst().apply(16, 7), (Integer)16);
	}

}
