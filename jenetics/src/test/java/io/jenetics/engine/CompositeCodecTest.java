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

import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.LongChromosome;
import io.jenetics.LongGene;
import io.jenetics.Phenotype;
import io.jenetics.internal.math.DoubleAdder;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CompositeCodecTest {

	@Test(dataProvider = "scalarCodecCount")
	public void minimalScalarCodec(final Integer scalars) {
		final ISeq<Codec<Double,DoubleGene>> seq = Stream
			.generate(() -> Codecs.ofScalar(DoubleRange.of(0, 1)))
			.limit(scalars)
			.collect(ISeq.toISeq());

		final Codec<Double, DoubleGene> codec = new CompositeCodec<>(
			seq, values -> 10.0
		);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), scalars.intValue());
	}

	@DataProvider(name = "scalarCodecCount")
	public Object[][] scalarCodecCount() {
		return new Object[][] {
			{1}, {2}, {5}, {10}, {100}
		};
	}

	@Test
	public void minimalScalarVectorCodec() {
		final Codec<Double, DoubleGene> codec = new CompositeCodec<>(
			ISeq.of(
				Codecs.ofScalar(DoubleRange.of(0, 1)),
				Codecs.ofVector(DoubleRange.of(10, 100), 3),
				Codecs.ofScalar(DoubleRange.of(2, 3)),
				Codecs.ofVector(DoubleRange.of(200, 500), DoubleRange.of(200, 500))
			),
			values -> 10.0
		);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();
		Assert.assertEquals(gt.length(), 5);
	}

	@Test
	public void minimalNestedCodec() {
		final Codec<Double, DoubleGene> codec1 = new CompositeCodec<>(
			ISeq.of(
				Codecs.ofScalar(DoubleRange.of(0, 1)),
				Codecs.ofVector(DoubleRange.of(10, 100), 3),
				Codecs.ofVector(DoubleRange.of(200, 500), DoubleRange.of(200, 500))
			),
			values ->  {
				final Double v1 = (Double)values[0];
				final double[] v2 = (double[])values[1];
				final double[] v3 = (double[])values[2];

				return v1 + DoubleAdder.sum(v2) + DoubleAdder.sum(v3);
			}
		);

		final Codec<Double, DoubleGene> codec2 = new CompositeCodec<>(
			ISeq.of(
				Codecs.ofVector(DoubleRange.of(10, 100), 3),
				Codecs.ofScalar(DoubleRange.of(0, 1)),
				Codecs.ofVector(DoubleRange.of(200, 500), DoubleRange.of(200, 500))
			),
			values ->  {
				final double[] v1 = (double[])values[0];
				final Double v2 = (Double)values[1];
				final double[] v3 = (double[])values[2];

				return DoubleAdder.sum(v1) + v2 + DoubleAdder.sum(v3);
			}
		);

		final Codec<Double, DoubleGene> codec3 = new CompositeCodec<>(
			ISeq.of(codec1, codec2),
			values ->  {
				final Double v1 = (Double)values[0];
				final Double v2 = (Double)values[1];

				return v1 + v2;
			}
		);

		final Genotype<DoubleGene> gt3 = codec3.encoding().newInstance();
		Assert.assertEquals(gt3.length(), 8);

		final Codec<Double, DoubleGene> codec4 = new CompositeCodec<>(
			ISeq.of(codec3),
			values -> (double)values[0]
		);

		final Genotype<DoubleGene> gt4 = codec3.encoding().newInstance();
		Assert.assertEquals(gt4.length(), 8);

		final double sum = gt4.stream()
			.mapToDouble(c -> c.stream()
				.mapToDouble(DoubleGene::doubleValue)
				.sum())
			.sum();

		Assert.assertEquals(sum, codec4.decoder().apply(gt4), 0.000001);
	}

	@Test
	public void encoding() {
		final Codec<Double, DoubleGene> codec = new CompositeCodec<>(
			ISeq.of(
				Codecs.ofScalar(DoubleRange.of(0, 1)),
				Codecs.ofVector(DoubleRange.of(10, 100), 3),
				Codecs.ofScalar(DoubleRange.of(2, 3)),
				Codecs.ofVector(DoubleRange.of(200, 500), DoubleRange.of(200, 500))
			),
			values -> {
				final Double v1 = (Double)values[0];
				final double[] v2 = (double[])values[1];
				final Double v3 = (Double)values[2];
				final double[] v4 = (double[])values[3];

				return v1 + DoubleAdder.sum(v2) + v3 + DoubleAdder.sum(v4);
			}
		);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();

		final double sum = gt.stream()
			.mapToDouble(c -> c.stream()
				.mapToDouble(DoubleGene::doubleValue)
				.sum())
			.sum();

		Assert.assertEquals(sum, codec.decoder().apply(gt), 0.000001);
	}

	@Test(invocationCount = 10)
	public void constrainedEncoding() {
		final Constraint<DoubleGene, Double> constraint = RetryConstraint.of(
			pt -> pt.genotype().gene().doubleValue() < 0.5,
			100
		);

		ISeq<Codec<?, DoubleGene>> codecs = ISeq
			.of(
				Codecs.ofScalar(DoubleRange.of(0.1, 0.9)),
				Codecs.ofScalar(DoubleRange.of(0.3, 0.7)),
				Codecs.ofVector(DoubleRange.of(0.3, 1.7), DoubleRange.of(0.3, 0.7)))
			.map(codec -> constraint.constrain(codec));

		final Codec<Double, DoubleGene> codec = new CompositeCodec<>(
			codecs,
			values -> {
				final Double v1 = (Double)values[0];
				final Double v2 = (Double)values[1];
				final double[] v3 = (double[])values[2];

				Assert.assertTrue(v1 < 0.5, "v1: " + v1);
				Assert.assertTrue(v2 < 0.5, "v2: " + v2);
				Assert.assertTrue(v3[0] < 0.5, "v3[0]: " + v3[0]);
				return v1 + v2 + v3[0] + v3[1];
			}
		);

		final Genotype<DoubleGene> gt = codec.encoding().newInstance();

		final double sum = gt.stream()
			.mapToDouble(c -> c.stream()
				.mapToDouble(DoubleGene::doubleValue)
				.sum())
			.sum();

		Assert.assertEquals(sum, codec.decoder().apply(gt), 0.000001);
	}

	@Test
	public void example() {
		final Codec<LocalDate, LongGene> dateCodec1 = Codec.of(
			Genotype.of(LongChromosome.of(0, 10_000)),
			gt -> LocalDate.ofEpochDay(gt.gene().longValue())
		);

		final Codec<LocalDate, LongGene> dateCodec2 = Codec.of(
			Genotype.of(LongChromosome.of(1_000_000, 10_000_000)),
			gt -> LocalDate.ofEpochDay(gt.gene().longValue())
		);

		final Codec<Duration, LongGene> durationCodec = Codec.of(
			dateCodec1,
			dateCodec2,
			(d1, d2) -> Duration.ofDays(d2.toEpochDay() - d1.toEpochDay())
		);

		final Engine<LongGene, Long> engine = Engine
			.builder(Duration::toMillis, durationCodec)
			.build();

		final Phenotype<LongGene, Long> pt = engine.stream()
			.limit(100)
			.collect(EvolutionResult.toBestPhenotype());
		//System.out.println(pt);

		final Duration duration = durationCodec.decoder()
			.apply(pt.genotype());
		//System.out.println(duration);
	}

	@Test
	public void example2() {
		final Codec<LocalDate, LongGene> dateCodec = Codec.of(
			Genotype.of(LongChromosome.of(0, 10_000)),
			gt -> LocalDate.ofEpochDay(gt.gene().longValue())
		);

		final Codec<Duration, LongGene> durationCodec = Codec.of(
			ISeq.of(dateCodec, dateCodec, dateCodec),
			dates -> {
				final LocalDate ld1 = (LocalDate)dates[0];
				final LocalDate ld2 = (LocalDate)dates[1];
				final LocalDate ld3 = (LocalDate)dates[2];

				return Duration.ofDays(
					ld1.toEpochDay() + ld2.toEpochDay() - ld3.toEpochDay()
				);
			}
		);

		final Engine<LongGene, Long> engine = Engine
			.builder(Duration::toMillis, durationCodec)
			.build();

		final Phenotype<LongGene, Long> pt = engine.stream()
			.limit(100)
			.collect(EvolutionResult.toBestPhenotype());
		//System.out.println(pt);

		final Duration duration = durationCodec.decoder()
			.apply(pt.genotype());
		//System.out.println(duration);
	}

}
