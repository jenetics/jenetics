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
package org.jenetics.engine;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.util.function.Function;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.AnyGene;
import org.jenetics.Chromosome;
import org.jenetics.DoubleGene;
import org.jenetics.EnumGene;
import org.jenetics.Genotype;
import org.jenetics.IntegerGene;
import org.jenetics.LongGene;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.LongRange;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class codecsTest {

	@Test(dataProvider = "intScalarData")
	public void ofIntScalar(final IntRange domain) {
		final Codec<Integer, IntegerGene> codec = codecs.ofScalar(domain);

		final Genotype<IntegerGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 1);
		Assert.assertEquals(gt.getChromosome().length(), 1);
		Assert.assertEquals(gt.getGene().getMin().intValue(), domain.getMin());
		Assert.assertEquals(gt.getGene().getMax().intValue(), domain.getMax());

		final Function<Genotype<IntegerGene>, Integer> f = codec.decoder();
		Assert.assertEquals(f.apply(gt).intValue(), gt.getGene().intValue());
	}

	@DataProvider(name = "intScalarData")
	public Object[][] intScalarData() {
		return new Object[][] {
			{IntRange.of(0, 1)},
			{IntRange.of(0, 10)},
			{IntRange.of(1, 2)},
			{IntRange.of(0, 100)},
			{IntRange.of(10, 1000)},
			{IntRange.of(1000, 10000)}
		};
	}

	@Test(dataProvider = "longScalarData")
	public void ofLongScalar(final LongRange domain) {
		final Codec<Long, LongGene> codec = codecs.ofScalar(domain);

		final Genotype<LongGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 1);
		Assert.assertEquals(gt.getChromosome().length(), 1);
		Assert.assertEquals(gt.getGene().getMin().longValue(), domain.getMin());
		Assert.assertEquals(gt.getGene().getMax().longValue(), domain.getMax());

		final Function<Genotype<LongGene>, Long> f = codec.decoder();
		Assert.assertEquals(f.apply(gt).longValue(), gt.getGene().longValue());
	}

	@DataProvider(name = "longScalarData")
	public Object[][] longScalarData() {
		return new Object[][] {
			{LongRange.of(0, 1)},
			{LongRange.of(0, 10)},
			{LongRange.of(1, 2)},
			{LongRange.of(0, 100)},
			{LongRange.of(10, 1000)},
			{LongRange.of(1000, 10000)}
		};
	}

	@Test(dataProvider = "doubleScalarData")
	public void ofDoubleScalar(final DoubleRange domain) {
		final Codec<Double, DoubleGene> codec = codecs.ofScalar(domain);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 1);
		Assert.assertEquals(gt.getChromosome().length(), 1);
		Assert.assertEquals(gt.getGene().getMin(), domain.getMin());
		Assert.assertEquals(gt.getGene().getMax(), domain.getMax());

		final Function<Genotype<DoubleGene>, Double> f = codec.decoder();
		Assert.assertEquals(f.apply(gt), gt.getGene().doubleValue());
	}

	@DataProvider(name = "doubleScalarData")
	public Object[][] doubleScalarData() {
		return new Object[][] {
			{DoubleRange.of(0, 1)},
			{DoubleRange.of(0, 10)},
			{DoubleRange.of(1, 2)},
			{DoubleRange.of(0, 100)},
			{DoubleRange.of(10, 1000)},
			{DoubleRange.of(1000, 10000)}
		};
	}

	@Test(dataProvider = "intVectorData")
	public void ofIntVector(final IntRange domain, final int length) {
		final Codec<int[], IntegerGene> codec = codecs.ofVector(domain, length);

		final Genotype<IntegerGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 1);
		Assert.assertEquals(gt.getChromosome().length(), length);
		for (IntegerGene gene : gt.getChromosome()) {
			Assert.assertEquals(gene.getMin().intValue(), domain.getMin());
			Assert.assertEquals(gene.getMax().intValue(), domain.getMax());
		}

		final Function<Genotype<IntegerGene>, int[]> f = codec.decoder();
		final int[] value = f.apply(gt);
		Assert.assertEquals(value.length, length);

		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(gt.get(0, i).intValue(), value[i]);
		}
	}

	@DataProvider(name = "intVectorData")
	public Object[][] intVectorData() {
		return new Object[][] {
			{IntRange.of(0, 1), 1},
			{IntRange.of(0, 10), 2},
			{IntRange.of(1, 2), 10},
			{IntRange.of(0, 100), 100},
			{IntRange.of(10, 1000), 3},
			{IntRange.of(1000, 10000), 100}
		};
	}

	@Test(dataProvider = "longVectorData")
	public void ofLongVector(final LongRange domain, final int length) {
		final Codec<long[], LongGene> codec = codecs.ofVector(domain, length);

		final Genotype<LongGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 1);
		Assert.assertEquals(gt.getChromosome().length(), length);
		for (LongGene gene : gt.getChromosome()) {
			Assert.assertEquals(gene.getMin().longValue(), domain.getMin());
			Assert.assertEquals(gene.getMax().longValue(), domain.getMax());
		}

		final Function<Genotype<LongGene>, long[]> f = codec.decoder();
		final long[] value = f.apply(gt);
		Assert.assertEquals(value.length, length);

		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(gt.get(0, i).longValue(), value[i]);
		}
	}

	@DataProvider(name = "longVectorData")
	public Object[][] longVectorData() {
		return new Object[][] {
			{LongRange.of(0, 1), 1},
			{LongRange.of(0, 10), 2},
			{LongRange.of(1, 2), 10},
			{LongRange.of(0, 100), 100},
			{LongRange.of(10, 1000), 3},
			{LongRange.of(1000, 10000), 100}
		};
	}

	@Test(dataProvider = "doubleVectorData")
	public void ofDoubleVector(final DoubleRange domain, final int length) {
		final Codec<double[], DoubleGene> codec = codecs.ofVector(domain, length);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 1);
		Assert.assertEquals(gt.getChromosome().length(), length);
		for (DoubleGene gene : gt.getChromosome()) {
			Assert.assertEquals(gene.getMin(), domain.getMin());
			Assert.assertEquals(gene.getMax(), domain.getMax());
		}

		final Function<Genotype<DoubleGene>, double[]> f = codec.decoder();
		final double[] value = f.apply(gt);
		Assert.assertEquals(value.length, length);

		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(gt.get(0, i).doubleValue(), value[i]);
		}
	}

	@DataProvider(name = "doubleVectorData")
	public Object[][] doubleVectorData() {
		return new Object[][] {
			{DoubleRange.of(0, 1), 1},
			{DoubleRange.of(0, 10), 2},
			{DoubleRange.of(1, 2), 10},
			{DoubleRange.of(0, 100), 100},
			{DoubleRange.of(10, 1000), 3},
			{DoubleRange.of(1000, 10000), 100}
		};
	}


	@Test(dataProvider = "intVectorDataVector")
	public void ofIntVectorVector(final IntRange[] domain) {
		final Codec<int[], IntegerGene> codec = codecs.ofVector(domain);

		final Genotype<IntegerGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), domain.length);

		for (int i = 0; i < gt.length(); ++i) {
			final Chromosome<IntegerGene> ch = gt.getChromosome(i);
			Assert.assertEquals(ch.length(), 1);

			final IntegerGene gene = ch.getGene();
			Assert.assertEquals(gene.getMin().intValue(), domain[i].getMin());
			Assert.assertEquals(gene.getMax().intValue(), domain[i].getMax());
		}

		final Function<Genotype<IntegerGene>, int[]> f = codec.decoder();
		final int[] value = f.apply(gt);
		Assert.assertEquals(value.length, domain.length);

		for (int i = 0; i < domain.length; ++i) {
			Assert.assertEquals(gt.get(i, 0).intValue(), value[i]);
		}
	}

	@DataProvider(name = "intVectorDataVector")
	public Object[][] intVectorDataVector() {
		return new Object[][] {
			{new IntRange[]{IntRange.of(0, 1)}},
			{new IntRange[]{IntRange.of(0, 10), IntRange.of(0, 1)}},
			{new IntRange[]{IntRange.of(1, 2), IntRange.of(1000, 10000), IntRange.of(1000, 10000)}},
			{new IntRange[]{IntRange.of(0, 100), IntRange.of(0, 1), IntRange.of(1000, 10000)}},
			{new IntRange[]{IntRange.of(10, 1000), IntRange.of(0, 1), IntRange.of(1000, 10000), IntRange.of(10, 100)}},
			{new IntRange[]{IntRange.of(1000, 10000), IntRange.of(0, 1)}}
		};
	}

	@Test(dataProvider = "longVectorDataVector")
	public void ofLongVectorVector(final LongRange[] domain) {
		final Codec<long[], LongGene> codec = codecs.ofVector(domain);

		final Genotype<LongGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), domain.length);

		for (int i = 0; i < gt.length(); ++i) {
			final Chromosome<LongGene> ch = gt.getChromosome(i);
			Assert.assertEquals(ch.length(), 1);

			final LongGene gene = ch.getGene();
			Assert.assertEquals(gene.getMin().longValue(), domain[i].getMin());
			Assert.assertEquals(gene.getMax().longValue(), domain[i].getMax());
		}

		final Function<Genotype<LongGene>, long[]> f = codec.decoder();
		final long[] value = f.apply(gt);
		Assert.assertEquals(value.length, domain.length);

		for (int i = 0; i < domain.length; ++i) {
			Assert.assertEquals(gt.get(i, 0).longValue(), value[i]);
		}
	}

	@DataProvider(name = "longVectorDataVector")
	public Object[][] longVectorDataVector() {
		return new Object[][] {
			{new LongRange[]{LongRange.of(0, 1)}},
			{new LongRange[]{LongRange.of(0, 10), LongRange.of(0, 1)}},
			{new LongRange[]{LongRange.of(1, 2), LongRange.of(1000, 10000), LongRange.of(1000, 10000)}},
			{new LongRange[]{LongRange.of(0, 100), LongRange.of(0, 1), LongRange.of(1000, 10000)}},
			{new LongRange[]{LongRange.of(10, 1000), LongRange.of(0, 1), LongRange.of(1000, 10000), LongRange.of(10, 100)}},
			{new LongRange[]{LongRange.of(1000, 10000), LongRange.of(0, 1)}}
		};
	}

	@Test(dataProvider = "doubleVectorDataVector")
	public void ofDoubleVectorVector(final DoubleRange[] domain) {
		final Codec<double[], DoubleGene> codec = codecs.ofVector(domain);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), domain.length);

		for (int i = 0; i < gt.length(); ++i) {
			final Chromosome<DoubleGene> ch = gt.getChromosome(i);
			Assert.assertEquals(ch.length(), 1);

			final DoubleGene gene = ch.getGene();
			Assert.assertEquals(gene.getMin(), domain[i].getMin());
			Assert.assertEquals(gene.getMax(), domain[i].getMax());
		}

		final Function<Genotype<DoubleGene>, double[]> f = codec.decoder();
		final double[] value = f.apply(gt);
		Assert.assertEquals(value.length, domain.length);

		for (int i = 0; i < domain.length; ++i) {
			Assert.assertEquals(gt.get(i, 0).doubleValue(), value[i]);
		}
	}

	@DataProvider(name = "doubleVectorDataVector")
	public Object[][] doubleVectorDataVector() {
		return new Object[][] {
			{new DoubleRange[]{DoubleRange.of(0, 1)}},
			{new DoubleRange[]{DoubleRange.of(0, 10), DoubleRange.of(0, 1)}},
			{new DoubleRange[]{DoubleRange.of(1, 2), DoubleRange.of(1000, 10000), DoubleRange.of(1000, 10000)}},
			{new DoubleRange[]{DoubleRange.of(0, 100), DoubleRange.of(0, 1), DoubleRange.of(1000, 10000)}},
			{new DoubleRange[]{DoubleRange.of(10, 1000), DoubleRange.of(0, 1), DoubleRange.of(1000, 10000), DoubleRange.of(10, 100)}},
			{new DoubleRange[]{DoubleRange.of(1000, 10000), DoubleRange.of(0, 1)}}
		};
	}

	@Test
	public void ofPermutation() {
		final Codec<ISeq<String>, EnumGene<String>> codec = codecs
			.ofPermutation(ISeq.of("foo", "bar", "zoo"));

		final Genotype<EnumGene<String>> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 1);

		final Function<Genotype<EnumGene<String>>, ISeq<String>> f = codec.decoder();
		final ISeq<String> value = f.apply(gt);
		Assert.assertEquals(value.length(), gt.getChromosome().length());

		for (int i = 0; i < value.length(); ++i) {
			Assert.assertEquals(value.get(i), gt.get(0, i).toString());
		}
	}


	@Test
	public void ofAffineTransform() {
		final DoubleRange sxr = DoubleRange.of(0, 100);
		final DoubleRange syr = DoubleRange.of(0, 200);
		final DoubleRange txr = DoubleRange.of(0, 50);
		final DoubleRange tyr = DoubleRange.of(0, 100);
		final DoubleRange thr = DoubleRange.of(0, 2*Math.PI);
		final DoubleRange kxr = DoubleRange.of(0, 10);
		final DoubleRange kyr = DoubleRange.of(0, 15);

		final Codec<AffineTransform, DoubleGene> codec = codecs.ofAffineTransform(
			sxr, syr, txr, tyr, thr, kxr, kyr
		);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		final double sx = gt.get(0, 0).doubleValue();
		final double sy = gt.get(1, 0).doubleValue();
		final double tx = gt.get(2, 0).doubleValue();
		final double ty = gt.get(3, 0).doubleValue();
		final double th = gt.get(4, 0).doubleValue();
		final double kx = gt.get(5, 0).doubleValue();
		final double ky = gt.get(6, 0).doubleValue();

		final double cos_th = cos(th);
		final double sin_th = sin(th);
		final double a11 = cos_th*sx + kx*sy*sin_th;
		final double a12 = cos_th*kx*sy - sx*sin_th;
		final double a21 = cos_th*ky*sx + sy*sin_th;
		final double a22 = cos_th*sy - ky*sx*sin_th;

		final AffineTransform eat = new AffineTransform(a11, a21, a12, a22, tx, ty);
		final AffineTransform at = codec.decoder().apply(gt);

		final double[] expectedMatrix = new double[6];
		final double[] matrix = new double[6];
		eat.getMatrix(expectedMatrix);
		at.getMatrix(matrix);

		for (int i = 0; i < matrix.length; ++i) {
			Assert.assertEquals(matrix[i], expectedMatrix[i], 0.0001);
		}
	}

	@Test
	public void ofAnyScalar() {
		final Codec<Integer, AnyGene<Integer>> codec = codecs.ofScalar(
			() -> RandomRegistry.getRandom().nextInt(1000),
			i -> i < 100
		);

		for (int i = 0; i < 1000; ++i) {
			final AnyGene<Integer> gene = codec.encoding()
				.newInstance().getGene();

			Assert.assertEquals(gene.isValid(), gene.getAllele() < 100);
			Assert.assertTrue(gene.getAllele() < 1000);
			Assert.assertTrue(gene.getAllele() >= 0);
		}
	}

	@Test
	public void ofAnyScalar2() {
		final Codec<Integer, AnyGene<Integer>> codec = codecs.ofScalar(
			() -> RandomRegistry.getRandom().nextInt(1000)
		);

		for (int i = 0; i < 1000; ++i) {
			final AnyGene<Integer> gene = codec.encoding()
				.newInstance().getGene();

			Assert.assertTrue(gene.isValid());
			Assert.assertTrue(gene.getAllele() < 1000);
			Assert.assertTrue(gene.getAllele() >= 0);
		}
	}

	@Test
	public void ofAnyVector() {
		final int length = 23;
		final Codec<ISeq<Integer>, AnyGene<Integer>> codec =
			codecs.ofVector(
				() -> RandomRegistry.getRandom().nextInt(1000),
				(Predicate<Integer>) i -> i < 100,
				length
			);

		for (int i = 0; i < 100; ++i) {
			final Chromosome<AnyGene<Integer>> ch = codec.encoding()
				.newInstance().getChromosome();

			Assert.assertEquals(ch.length(), length);

			for (AnyGene<Integer> gene : ch) {
				Assert.assertEquals(gene.isValid(), gene.getAllele() < 100);

				if (!gene.isValid()) {
					Assert.assertFalse(ch.isValid());
				}

				Assert.assertTrue(gene.getAllele() < 1000);
				Assert.assertTrue(gene.getAllele() >= 0);
			}
		}
	}

	@Test
	public void ofSubSet() {
		final Codec<ISeq<String>, EnumGene<String>> codec = codecs.ofSubSet(
			ISeq.of("1", "2", "3", "4", "5"), 3
		);

		for (int i = 0; i < 100; ++i) {
			final Genotype<EnumGene<String>> gt = codec.encoding().newInstance();
			final Chromosome<EnumGene<String>> ch = gt.getChromosome();

			Assert.assertEquals(ch.length(), 3);
			Assert.assertTrue(ch.isValid());

			final ISeq<String> permutation = codec.decoder().apply(gt);
			Assert.assertEquals(permutation.length(), 3);
		}
	}

}
