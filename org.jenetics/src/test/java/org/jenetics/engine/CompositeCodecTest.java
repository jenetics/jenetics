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

import static java.lang.String.format;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Function;

import org.testng.annotations.Test;

import org.jenetics.AnyGene;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.LongChromosome;
import org.jenetics.LongGene;
import org.jenetics.Phenotype;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class CompositeCodecTest {

	@Test
	public void encoding() {
		final Codec<String, DoubleGene> codec = new CompositeCodec<>(
			ISeq.of(
				codecs.ofScalar(DoubleRange.of(0, 1)),
				codecs.ofVector(DoubleRange.of(10, 100), 3),
				codecs.ofScalar(DoubleRange.of(2, 3)),
				codecs.ofVector(DoubleRange.of(200, 500), DoubleRange.of(200, 500))
			),
			this::map
		);

		Genotype<DoubleGene> gt = codec.encoding().newInstance();
		System.out.println(gt);
		System.out.println(codec.decoder().apply(gt));
	}

	private String map(final Object[] values) {
		final Double v1 = (Double)values[0];
		final double[] v2 = (double[])values[1];
		final Double v3 = (Double)values[2];
		final double[] v4 = (double[])values[3];


		return format("%s:::%s:::%s:::%s", v1, Arrays.toString(v2), v3, Arrays.toString(v4));
 	}

	@Test
	public void example() {
		final Codec<LocalDate, LongGene> dateCodec1 = Codec.of(
			Genotype.of(LongChromosome.of(0, 10_000)),
			gt -> LocalDate.ofEpochDay(gt.getGene().longValue())
		);

		final Codec<LocalDate, LongGene> dateCodec2 = Codec.of(
			Genotype.of(LongChromosome.of(1_000_000, 10_000_000)),
			gt -> LocalDate.ofEpochDay(gt.getGene().longValue())
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
		System.out.println(pt);

		final Duration duration = durationCodec.decoder()
			.apply(pt.getGenotype());
		System.out.println(duration);
	}

}
