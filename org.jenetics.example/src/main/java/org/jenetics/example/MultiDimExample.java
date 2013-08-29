package org.jenetics.example;

import org.jscience.mathematics.number.Float64;

import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.Genotype;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;

public class MultiDimExample {

	/*
	 * The following Genotype factory defines a 4 dimensional search space with
	 * different extension for each dimension.
	 */

	final Factory<Genotype<Float64Gene>> gtf_1 = Genotype.valueOf(
		new Float64Chromosome(0.0, 10.0),  // x1 in [0, 10]
		new Float64Chromosome(-5.0, 0.0),  // x2 in [-5, 0]
		new Float64Chromosome(1.0, 10.0),  // x3 in [1, 10]
		new Float64Chromosome(0.0, 100.0)  // x4 in [0, 100]
	);

	final Function<Genotype<Float64Gene>, Float64> ff_1 = new Function<Genotype<Float64Gene>, Float64>() {
		@Override
		public Float64 apply(final Genotype<Float64Gene> gt) {
			final double x1 = gt.getChromosome(0).getGene().getAllele().doubleValue();
			final double x2 = gt.getChromosome(1).getGene().getAllele().doubleValue();
			final double x3 = gt.getChromosome(2).getGene().getAllele().doubleValue();
			final double x4 = gt.getChromosome(3).getGene().getAllele().doubleValue();

			// This assertions hold because of the defined structure of the
			// Genotype factory.
			assert(x1 >= 0 && x1 <= 10);
			assert(x2 >= -5 && x2 <= 0);
			assert(x3 >= 1 && x3 <= 10);
			assert(x4 >= 0 && x4 <= 100);


			return Float64.valueOf(f(x1, x2, x3, x4));
		}

		// Your fitness function implementation.
		private double f(double x1, double x2, double x3, double x4) {
			return x1+x2+x3+x4;
		}
	};

	/*
	 * This Genotype schema has the same extension for each dimension.
	 */

	final Factory<Genotype<Float64Gene>> gtf_2 = Genotype.valueOf(
		new Float64Chromosome(0.0, 10.0, 4)  // x1, x2, x3, x4 in [0, 10]
	);

	final Function<Genotype<Float64Gene>, Float64> ff_2 = new Function<Genotype<Float64Gene>, Float64>() {
		@Override
		public Float64 apply(final Genotype<Float64Gene> gt) {
			final double x1 = gt.getChromosome().getGene(0).getAllele().doubleValue();
			final double x2 = gt.getChromosome().getGene(1).getAllele().doubleValue();
			final double x3 = gt.getChromosome().getGene(2).getAllele().doubleValue();
			final double x4 = gt.getChromosome().getGene(3).getAllele().doubleValue();

			// This assertions hold because of the defined structure of the
			// Genotype factory.
			assert(x1 >= 0 && x1 <= 10);
			assert(x1 >= 0 && x1 <= 10);
			assert(x1 >= 0 && x1 <= 10);
			assert(x1 >= 0 && x1 <= 10);


			return Float64.valueOf(f(x1, x2, x3, x4));
		}

		// Your fitness function implementation.
		private double f(double x1, double x2, double x3, double x4) {
			return x1+x2+x3+x4;
		}
	};
}
