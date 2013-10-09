package org.jenetics.internal.util;

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.io.Serializable;

import javax.measure.unit.SI;

import org.jscience.mathematics.number.Float64;
import org.testng.annotations.Test;

import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.Timer;
import org.jenetics.util.functions;

public class arraysTest {


	private static final class FF
		implements Function<Genotype<Float64Gene>, Float64>,
					Serializable
	{
		private static final long serialVersionUID = 2793605351118238308L;
		@Override public Float64 apply(final Genotype<Float64Gene> genotype) {
			final Float64Gene gene = genotype.getChromosome().getGene(0);
			return Float64.valueOf(sin(toRadians(gene.doubleValue())));
		}
	}

	private final Factory<Genotype<Float64Gene>> _genotype = Genotype.valueOf(
		new Float64Chromosome(0, 1, 50),
		new Float64Chromosome(0, 1, 500),
		new Float64Chromosome(0, 1, 100),
		new Float64Chromosome(0, 1, 50),
		new Float64Chromosome(0, 1, 50),
		new Float64Chromosome(0, 1, 550),
		new Float64Chromosome(0, 1, 350)
	);
	private final Function<Genotype<Float64Gene>, Float64> _ff = new FF();
	private final Function<Float64, Float64> _scaler = functions.Identity();
	private final Factory<Phenotype<Float64Gene, Float64>>
	_factory = new Factory<Phenotype<Float64Gene, Float64>>() {
		@Override public Phenotype<Float64Gene, Float64> newInstance() {
			return Phenotype.valueOf(_genotype.newInstance(), _ff, _scaler, 0);
		}
	};

	@Test
	public void fill() {
		final Object[] data = new Object[2001];

		for (int i = 1; i <= data.length; i += 10) {
			//arrays.MIN_BULK_SIZE = i;

			final Timer timer = new Timer();
			for (int j = 0; j < 5; ++j) {
				timer.start();
				arrays.fill(data, _factory);
				timer.stop();
			}
			System.out.println(String.format("%d\t%s", i, timer.getTime().doubleValue(SI.SECOND)));
		}

	}

}










