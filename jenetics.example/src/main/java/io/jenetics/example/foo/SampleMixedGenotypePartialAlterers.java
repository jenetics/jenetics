package io.jenetics.example.foo;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.stream.IntStream;

import io.jenetics.BitChromosome;
import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.MultiPointCrossover;
import io.jenetics.Mutator;
import io.jenetics.Optimize;
import io.jenetics.PartialAlterer;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.PermutationChromosome;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.NSGA2Selector;
import io.jenetics.ext.moea.Vec;
import io.jenetics.ext.moea.VecFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SampleMixedGenotypePartialAlterers {

	private static final Genotype ENCODING = Genotype.of(
		(Chromosome)PermutationChromosome.of(
			IntStream.rangeClosed(1,5).boxed()
				.collect(ISeq.toISeq())
		),
		(Chromosome)BitChromosome.of(4)
	);

	public static void main(String[] args) {
		final Engine engine = Engine
				.builder(SampleMixedGenotypePartialAlterers::fitness, ENCODING)
				.alterers(
					PartialAlterer.of(new PartiallyMatchedCrossover(0.4), 0),
					PartialAlterer.of(new MultiPointCrossover(0.4), 1),
					new Mutator<>(0.1))
				.survivorsSelector(NSGA2Selector.ofVec())
				.populationSize(200)
				.build();

		final ISeq<Phenotype> best = (ISeq<Phenotype>)engine.stream()
				.limit(300)
				.collect(MOEA.toParetoSet(IntRange.of(30, 50)));

		System.out.println(best);
	}

	public static final VecFactory<double[]> VEC_FACTORY =
			VecFactory.ofDoubleVec(
				Optimize.MAXIMUM,
				Optimize.MINIMUM,
				Optimize.MAXIMUM,
				Optimize.MINIMUM,
				Optimize.MAXIMUM
	);

	public static Vec<double[]> fitness(final Genotype genotype) {
		final double x = 3.2;
		final double y = 4.2;
		return VEC_FACTORY.newVec(new double[] {
			sin(x)*y,
			cos(y)*x,
			sin(x + y),
			cos(x + y)*x,
			x
		});
	}
}
