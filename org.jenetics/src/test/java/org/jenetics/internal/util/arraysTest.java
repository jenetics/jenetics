package org.jenetics.internal.util;

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.io.Serializable;

import javax.measure.unit.SI;

import org.testng.annotations.Test;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.Timer;
import org.jenetics.util.functions;

public class arraysTest {


	private static final class FF
		implements Function<Genotype<DoubleGene>, Double>,
					Serializable
	{
		private static final long serialVersionUID = 2793605351118238308L;
		@Override public Double apply(final Genotype<DoubleGene> genotype) {
			final DoubleGene gene = genotype.getChromosome().getGene(0);
			return sin(toRadians(gene.doubleValue()));
		}
	}

	private final Factory<Genotype<DoubleGene>> _genotype = Genotype.valueOf(
		DoubleChromosome.of(0, 1, 50),
		DoubleChromosome.of(0, 1, 500),
		DoubleChromosome.of(0, 1, 100),
		DoubleChromosome.of(0, 1, 50),
		DoubleChromosome.of(0, 1, 50),
		DoubleChromosome.of(0, 1, 550),
		DoubleChromosome.of(0, 1, 350)
	);
	private final Function<Genotype<DoubleGene>, Double> _ff = new FF();
	private final Function<Double, Double> _scaler = functions.Identity();
	private final Factory<Phenotype<DoubleGene, Double>>
	_factory = new Factory<Phenotype<DoubleGene, Double>>() {
		@Override public Phenotype<DoubleGene, Double> newInstance() {
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










