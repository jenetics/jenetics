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
package io.jenetics.engine;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.AnyGene;
import io.jenetics.Chromosome;
import io.jenetics.DoubleGene;
import io.jenetics.EnumGene;
import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import io.jenetics.LongGene;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.LongRange;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CodecsTest {

	@Test(dataProvider = "intScalarData")
	public void ofIntScalar(final IntRange domain) {
		final Codec<Integer, IntegerGene> codec = Codecs.ofScalar(domain);

		final Genotype<IntegerGene> gt = codec.encoding().newInstance();
		assertEquals(gt.length(), 1);
		assertEquals(gt.chromosome().length(), 1);
		assertEquals(gt.gene().min().intValue(), domain.min());
		assertEquals(gt.gene().max().intValue(), domain.max());

		final Function<Genotype<IntegerGene>, Integer> f = codec.decoder();
		assertEquals(f.apply(gt).intValue(), gt.gene().intValue());
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
		final Codec<Long, LongGene> codec = Codecs.ofScalar(domain);

		final Genotype<LongGene> gt = codec.encoding().newInstance();
		assertEquals(gt.length(), 1);
		assertEquals(gt.chromosome().length(), 1);
		assertEquals(gt.gene().min().longValue(), domain.min());
		assertEquals(gt.gene().max().longValue(), domain.max());

		final Function<Genotype<LongGene>, Long> f = codec.decoder();
		assertEquals(f.apply(gt).longValue(), gt.gene().longValue());
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
		final Codec<Double, DoubleGene> codec = Codecs.ofScalar(domain);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		assertEquals(gt.length(), 1);
		assertEquals(gt.chromosome().length(), 1);
		assertEquals(gt.gene().min().doubleValue(), domain.min());
		assertEquals(gt.gene().max().doubleValue(), domain.max());

		final Function<Genotype<DoubleGene>, Double> f = codec.decoder();
		assertEquals(f.apply(gt).doubleValue(), gt.gene().doubleValue());
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
		final Codec<int[], IntegerGene> codec = Codecs.ofVector(domain, length);

		final Genotype<IntegerGene> gt = codec.encoding().newInstance();
		assertEquals(gt.length(), 1);
		assertEquals(gt.chromosome().length(), length);
		for (IntegerGene gene : gt.chromosome()) {
			assertEquals(gene.min().intValue(), domain.min());
			assertEquals(gene.max().intValue(), domain.max());
		}

		final Function<Genotype<IntegerGene>, int[]> f = codec.decoder();
		final int[] value = f.apply(gt);
		assertEquals(value.length, length);

		for (int i = 0; i < length; ++i) {
			assertEquals(gt.get(0).get(i).intValue(), value[i]);
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
		final Codec<long[], LongGene> codec = Codecs.ofVector(domain, length);

		final Genotype<LongGene> gt = codec.encoding().newInstance();
		assertEquals(gt.length(), 1);
		assertEquals(gt.chromosome().length(), length);
		for (LongGene gene : gt.chromosome()) {
			assertEquals(gene.min().longValue(), domain.min());
			assertEquals(gene.max().longValue(), domain.max());
		}

		final Function<Genotype<LongGene>, long[]> f = codec.decoder();
		final long[] value = f.apply(gt);
		assertEquals(value.length, length);

		for (int i = 0; i < length; ++i) {
			assertEquals(gt.get(0).get(i).longValue(), value[i]);
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
		final Codec<double[], DoubleGene> codec = Codecs.ofVector(domain, length);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		assertEquals(gt.length(), 1);
		assertEquals(gt.chromosome().length(), length);
		for (DoubleGene gene : gt.chromosome()) {
			assertEquals(gene.min().doubleValue(), domain.min());
			assertEquals(gene.max().doubleValue(), domain.max());
		}

		final Function<Genotype<DoubleGene>, double[]> f = codec.decoder();
		final double[] value = f.apply(gt);
		assertEquals(value.length, length);

		for (int i = 0; i < length; ++i) {
			assertEquals(gt.get(0).get(i).doubleValue(), value[i]);
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
		final Codec<int[], IntegerGene> codec = Codecs.ofVector(domain);

		final Genotype<IntegerGene> gt = codec.encoding().newInstance();
		assertEquals(gt.length(), domain.length);

		for (int i = 0; i < gt.length(); ++i) {
			final Chromosome<IntegerGene> ch = gt.get(i);
			assertEquals(ch.length(), 1);

			final IntegerGene gene = ch.gene();
			assertEquals(gene.min().intValue(), domain[i].min());
			assertEquals(gene.max().intValue(), domain[i].max());
		}

		final Function<Genotype<IntegerGene>, int[]> f = codec.decoder();
		final int[] value = f.apply(gt);
		assertEquals(value.length, domain.length);

		for (int i = 0; i < domain.length; ++i) {
			assertEquals(gt.get(i).get(0).intValue(), value[i]);
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
		final Codec<long[], LongGene> codec = Codecs.ofVector(domain);

		final Genotype<LongGene> gt = codec.encoding().newInstance();
		assertEquals(gt.length(), domain.length);

		for (int i = 0; i < gt.length(); ++i) {
			final Chromosome<LongGene> ch = gt.get(i);
			assertEquals(ch.length(), 1);

			final LongGene gene = ch.gene();
			assertEquals(gene.min().longValue(), domain[i].min());
			assertEquals(gene.max().longValue(), domain[i].max());
		}

		final Function<Genotype<LongGene>, long[]> f = codec.decoder();
		final long[] value = f.apply(gt);
		assertEquals(value.length, domain.length);

		for (int i = 0; i < domain.length; ++i) {
			assertEquals(gt.get(i).get(0).longValue(), value[i]);
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
		final Codec<double[], DoubleGene> codec = Codecs.ofVector(domain);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		assertEquals(gt.length(), domain.length);

		for (int i = 0; i < gt.length(); ++i) {
			final Chromosome<DoubleGene> ch = gt.get(i);
			assertEquals(ch.length(), 1);

			final DoubleGene gene = ch.gene();
			assertEquals(gene.min().doubleValue(), domain[i].min());
			assertEquals(gene.max().doubleValue(), domain[i].max());
		}

		final Function<Genotype<DoubleGene>, double[]> f = codec.decoder();
		final double[] value = f.apply(gt);
		assertEquals(value.length, domain.length);

		for (int i = 0; i < domain.length; ++i) {
			assertEquals(gt.get(i).get(0).doubleValue(), value[i]);
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
	public void ofIntMatrix() {
		final int rows = 10;
		final int cols = 15;
		final Codec<int[][], IntegerGene> codec = Codecs.ofMatrix(
			IntRange.of(0, 1_000),
			rows, cols
		);

		final Genotype<IntegerGene> gt = codec.encoding().newInstance();
		final int[][] matrix = codec.decode(gt);

		assertEquals(matrix.length, rows);
		assertEquals(matrix[0].length, cols);

		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col) {
				assertEquals(matrix[row][col], gt.get(row).get(col).intValue());
			}
		}
	}

	@Test
	public void ofLongMatrix() {
		final int rows = 10;
		final int cols = 15;
		final Codec<long[][], LongGene> codec = Codecs.ofMatrix(
			LongRange.of(0, 1_000),
			rows, cols
		);

		final Genotype<LongGene> gt = codec.encoding().newInstance();
		final long[][] matrix = codec.decode(gt);

		assertEquals(matrix.length, rows);
		assertEquals(matrix[0].length, cols);

		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col) {
				assertEquals(matrix[row][col], gt.get(row).get(col).longValue());
			}
		}
	}

	@Test
	public void ofDoubleMatrix() {
		final int rows = 10;
		final int cols = 15;
		final Codec<double[][], DoubleGene> codec = Codecs.ofMatrix(
			DoubleRange.of(0, 1_000),
			rows, cols
		);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		final double[][] matrix = codec.decode(gt);

		assertEquals(matrix.length, rows);
		assertEquals(matrix[0].length, cols);

		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col) {
				assertEquals(matrix[row][col], gt.get(row).get(col).doubleValue());
			}
		}
	}

	@Test
	public void ofPermutation() {
		final Codec<ISeq<String>, EnumGene<String>> codec = Codecs
			.ofPermutation(ISeq.of("foo", "bar", "zoo"));

		final Genotype<EnumGene<String>> gt = codec.encoding().newInstance();
		assertEquals(gt.length(), 1);

		final Function<Genotype<EnumGene<String>>, ISeq<String>> f = codec.decoder();
		final ISeq<String> value = f.apply(gt);
		assertEquals(value.length(), gt.chromosome().length());

		for (int i = 0; i < value.length(); ++i) {
			assertEquals(value.get(i), gt.get(0).get(i).toString());
		}
	}

	@Test
	public void ofMapping1() {
		final ISeq<Integer> numbers = ISeq.of(1, 2, 3, 4, 5);
		final ISeq<String> chars = ISeq.of("A", "B", "C");

		final Codec<Map<Integer, String>, EnumGene<Integer>> codec =
			Codecs.ofMapping(numbers, chars);

		final Function<Map<Integer, String>, Integer> ff = map ->
			map.keySet().stream().mapToInt(Integer::intValue).sum();

		Engine<EnumGene<Integer>, Integer> engine = Engine.builder(ff, codec)
			.build();

		final Map<Integer, String> best = codec.decode(
			engine.stream()
				.limit(100)
				.collect(EvolutionResult.toBestGenotype())
		);

		assertTrue(best.containsKey(3));
		assertTrue(best.containsKey(4));
		assertTrue(best.containsKey(5));
	}

	@Test
	public void ofMapping2() {
		final ISeq<Integer> numbers = ISeq.of(1, 2, 3, 4, 5);
		final ISeq<String> chars = ISeq.of("A", "B", "C");

		final Codec<Map<String, Integer>, EnumGene<Integer>> codec =
			Codecs.ofMapping(chars, numbers);

		final Function<Map<String, Integer>, Integer> ff = map ->
			map.values().stream().mapToInt(Integer::intValue).sum();

		Engine<EnumGene<Integer>, Integer> engine = Engine.builder(ff, codec)
			.build();

		final Map<String, Integer> best = codec.decode(
			engine.stream()
				.limit(100)
				.collect(EvolutionResult.toBestGenotype())
		);

		assertTrue(best.containsValue(3));
		assertTrue(best.containsValue(4));
		assertTrue(best.containsValue(5));
	}


//	@Test
//	public void ofAffineTransform() {
//		final DoubleRange sxr = DoubleRange.of(0, 100);
//		final DoubleRange syr = DoubleRange.of(0, 200);
//		final DoubleRange txr = DoubleRange.of(0, 50);
//		final DoubleRange tyr = DoubleRange.of(0, 100);
//		final DoubleRange thr = DoubleRange.of(0, 2*Math.PI);
//		final DoubleRange kxr = DoubleRange.of(0, 10);
//		final DoubleRange kyr = DoubleRange.of(0, 15);
//
//		final Codec<AffineTransform, DoubleGene> codec = Codecs.ofAffineTransform(
//			sxr, syr, txr, tyr, thr, kxr, kyr
//		);
//
//		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
//		final double sx = gt.get(0, 0).doubleValue();
//		final double sy = gt.get(1, 0).doubleValue();
//		final double tx = gt.get(2, 0).doubleValue();
//		final double ty = gt.get(3, 0).doubleValue();
//		final double th = gt.get(4, 0).doubleValue();
//		final double kx = gt.get(5, 0).doubleValue();
//		final double ky = gt.get(6, 0).doubleValue();
//
//		final double cos_th = cos(th);
//		final double sin_th = sin(th);
//		final double a11 = cos_th*sx + kx*sy*sin_th;
//		final double a12 = cos_th*kx*sy - sx*sin_th;
//		final double a21 = cos_th*ky*sx + sy*sin_th;
//		final double a22 = cos_th*sy - ky*sx*sin_th;
//
//		final AffineTransform eat = new AffineTransform(a11, a21, a12, a22, tx, ty);
//		final AffineTransform at = codec.decoder().apply(gt);
//
//		final double[] expectedMatrix = new double[6];
//		final double[] matrix = new double[6];
//		eat.getMatrix(expectedMatrix);
//		at.getMatrix(matrix);
//
//		for (int i = 0; i < matrix.length; ++i) {
//			Assert.assertEquals(matrix[i], expectedMatrix[i], 0.0001);
//		}
//	}

	@Test
	public void ofAnyScalar() {
		final Codec<Integer, AnyGene<Integer>> codec = Codecs.ofScalar(
			() -> RandomRegistry.random().nextInt(1000),
			i -> i < 100
		);

		for (int i = 0; i < 1000; ++i) {
			final AnyGene<Integer> gene = codec.encoding()
				.newInstance().gene();

			assertEquals(gene.isValid(), gene.allele() < 100);
			assertTrue(gene.allele() < 1000);
			assertTrue(gene.allele() >= 0);
		}
	}

	@Test
	public void ofAnyScalar2() {
		final Codec<Integer, AnyGene<Integer>> codec = Codecs.ofScalar(
			() -> RandomRegistry.random().nextInt(1000)
		);

		for (int i = 0; i < 1000; ++i) {
			final AnyGene<Integer> gene = codec.encoding()
				.newInstance().gene();

			assertTrue(gene.isValid());
			assertTrue(gene.allele() < 1000);
			assertTrue(gene.allele() >= 0);
		}
	}

	@Test
	public void ofAnyVector() {
		final int length = 23;
		final Codec<ISeq<Integer>, AnyGene<Integer>> codec =
			Codecs.ofVector(
				() -> RandomRegistry.random().nextInt(1000),
				i -> i < 100,
				length
			);

		for (int i = 0; i < 100; ++i) {
			final Chromosome<AnyGene<Integer>> ch = codec.encoding()
				.newInstance().chromosome();

			assertEquals(ch.length(), length);

			for (AnyGene<Integer> gene : ch) {
				assertEquals(gene.isValid(), gene.allele() < 100);

				if (!gene.isValid()) {
					Assert.assertFalse(ch.isValid());
				}

				assertTrue(gene.allele() < 1000);
				assertTrue(gene.allele() >= 0);
			}
		}
	}

	@Test
	public void ofSubSet() {
		final Codec<ISeq<String>, EnumGene<String>> codec = Codecs.ofSubSet(
			ISeq.of("1", "2", "3", "4", "5"), 3
		);

		for (int i = 0; i < 100; ++i) {
			final Genotype<EnumGene<String>> gt = codec.encoding().newInstance();
			final Chromosome<EnumGene<String>> ch = gt.chromosome();

			assertEquals(ch.length(), 3);
			assertTrue(ch.isValid());

			final ISeq<String> permutation = codec.decoder().apply(gt);
			assertEquals(permutation.length(), 3);
		}
	}

	@Test(dataProvider = "invertibleCodecs")
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void inversion(final InvertibleCodec codec, final boolean unique) {
		final Genotype gt1 = (Genotype)codec.encoding().newInstance();
		assertNotNull(gt1);

		final Object v1 = codec.decode(gt1);
		assertNotNull(v1);

		final Genotype gt2 = codec.encode(v1);
		if (unique) {
			assertEquals(gt2, gt1);
		}

		final Object v2 = codec.decode(gt2);
		assertEquals(v2, v1);
	}

	@DataProvider
	public Object[][] invertibleCodecs() {
		return new Object[][] {
			{Codecs.ofScalar(IntRange.of(10, 10_000)), true},
			{Codecs.ofScalar(LongRange.of(10, 100_000)), true},
			{Codecs.ofScalar(DoubleRange.of(10, 10_000)), true},

			{Codecs.ofVector(IntRange.of(10, 10_000), 10), true},
			{Codecs.ofVector(LongRange.of(10, 100_000), 10), true},
			{Codecs.ofVector(DoubleRange.of(10, 10_000), 10), true},

			{Codecs.ofVector(IntRange.of(10, 10_000), IntRange.of(60, 100), IntRange.of(1, 10)), true},
			{Codecs.ofVector(LongRange.of(10, 10_000), LongRange.of(60, 100), LongRange.of(1, 10)), true},
			{Codecs.ofVector(DoubleRange.of(10, 10_000), DoubleRange.of(60, 100), DoubleRange.of(1, 10)), true},

			{Codecs.ofPermutation(100), true},
			{Codecs.ofPermutation(ISeq.of("a", "b", "c", "d", "e", "f", "end")), true},

			{Codecs.ofMatrix(IntRange.of(10, 10_000), 10, 100), true},
			{Codecs.ofMatrix(LongRange.of(10, 10_000), 10, 100), true},
			{Codecs.ofMatrix(DoubleRange.of(10, 10_000), 10, 100), true},

			{Codecs.ofMapping(ISeq.of("A", "B", "C", "D"), ISeq.of(1, 2, 3, 4)), true},
			{Codecs.ofMapping(ISeq.of("A", "B", "C", "D", "E", "F", "G"), ISeq.of(1, 2, 3, 4)), true},
			{Codecs.ofMapping(ISeq.of("A", "B", "C", "D", "E"), ISeq.of(1, 2, 3, 4, 5, 6, 7, 8, 9)), false},

			{Codecs.ofSubSet(ISeq.of("A", "B", "C", "D", "E", "F", "G")), true},
			{Codecs.ofSubSet(ISeq.of("A", "B", "C", "D", "E", "F", "G"), 3), true}
		};
	}

	@Test(
		dataProvider = "invalidSubSets",
		expectedExceptions = IllegalArgumentException.class
	)
	public void subSetEncodeError(final String[] subset) {
		final ISeq<String> basicSet = IntStream.range(0, 20)
			.mapToObj(String::valueOf)
			.collect(ISeq.toISeq());

		final var codec = Codecs.ofSubSet(basicSet);
		codec.encode(ISeq.of(subset));
	}

	@DataProvider
	public Object[][] invalidSubSets() {
		return new Object[][] {
			{"1","2","3","7","5","6","4","8","9"},
			{"1","2","3","7","5"},
			{"1","2","3","5", "59"}
		};
	}

}
