package io.jenetics.example;

import java.util.stream.IntStream;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Codec;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.IntRange;

public class IntRangeCodec {

	record Ranges(int[] ivalues, double dvalue) {
	}

	static Codec<Ranges, DoubleGene> codec(IntRange[] iranges, DoubleRange drange) {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(DoubleRange.of(0, 1), iranges.length + 1)),
			gt -> {
				final var ch = gt.chromosome();
				return new Ranges(
					IntStream.range(0, iranges.length)
						.map(i -> (int)(ch.get(i).doubleValue()*iranges[i].size()) + iranges[i].min())
						.toArray(),
					ch.get(iranges.length).doubleValue()*(drange.max() - drange.min()) + drange.min()
				);
			}
		);
	}

}
