package org.jenetics.examples;

import java.io.File;

import org.jscience.mathematics.number.Float64;

import org.jenetics.Chromosome;
import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.util.Function;
import org.jenetics.util.IO;

public class Serial {

	static class Id implements Function<Genotype<Float64Gene>, Float64> {
		@Override
		public Float64 apply(final Genotype<Float64Gene> value) {
			double result = 0;
			for (Chromosome<Float64Gene> c : value) {
				for (Float64Gene g : c) {
					result += Math.sin(g.getAllele().doubleValue());
				}
			}
			return Float64.valueOf(result);
		}
	}
	
	static class Scaler implements Function<Float64, Float64> {
		@Override
		public Float64 apply(Float64 value) {
			return value.times(Math.PI);
		}
	}
	
	public static void main(final String[] args) throws Exception {
		final Genotype<Float64Gene> genotype = Genotype.valueOf(
			    new Float64Chromosome(0.0, 1.0, 8),
			    new Float64Chromosome(1.0, 2.0, 10),
			    new Float64Chromosome(0.0, 10.0, 9),
			    new Float64Chromosome(0.1, 0.9, 5)
			);
		
		final GeneticAlgorithm<Float64Gene, Float64> ga = new GeneticAlgorithm<>(genotype, new Id(), new Scaler());
		ga.setPopulationSize(5);
		ga.setup();
		ga.evolve(5);
		
		IO.xml.write(ga.getPopulation(), new File("/home/fwilhelm/population.xml"));
		
	}
	
}




