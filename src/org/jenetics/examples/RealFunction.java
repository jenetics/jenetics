/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics.examples;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static org.jenetics.ExponentialScaler.SQR_SCALER;

import org.jenetics.CompositeAlterer;
import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.util.Converter;
import org.jenetics.util.Factory;
import org.jscience.mathematics.number.Float64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class RealFunction {
	private static final class Function 
		implements FitnessFunction<Float64Gene, Float64>,
					Converter<Genotype<Float64Gene>, Float64>
	{
		private static final long serialVersionUID = 2793605351118238308L;
		
		@Override
		public Float64 evaluate(final Genotype<Float64Gene> genotype) {
			final double radians = toRadians(convert(genotype).doubleValue());
			return Float64.valueOf(acos(sin(radians)*cos(radians)));
		}

		@Override
		public Float64 convert(final Genotype<Float64Gene> value) {
			return value.getChromosome().getGene().getAllele();
		}
	}
	
	
	public static void main(String[] args) {
		final Factory<Genotype<Float64Gene>> gtf = Genotype.valueOf(new Float64Chromosome(0, 360));
		final Function ff = new Function();
		final GeneticAlgorithm<Float64Gene, Float64> ga = GeneticAlgorithm.valueOf(gtf, ff);
		
		ga.setFitnessScaler(SQR_SCALER);
		ga.setPopulationSize(1000);
		ga.setAlterer(new CompositeAlterer<Float64Gene>(
			new Mutator<Float64Gene>(0.03),
			new MeanAlterer<Float64Gene>(0.6)
		));
		ga.setSelectors(new RouletteWheelSelector<Float64Gene, Float64>());
		
		GAUtils.execute(ga, 50);
	}
	
}
