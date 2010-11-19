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
package org.jenetics;

import org.jscience.mathematics.number.Float64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class Factories {

	private static final class Continous 
		implements FitnessFunction<Float64Gene, Float64> 
	{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Float64 evaluate(Genotype<Float64Gene> genotype) {
			return genotype.getChromosome().getGene().getAllele(); 
		}
	}
	
	public static final FitnessFunction<Float64Gene, Float64> FF = new Continous();
	
	
	public static Phenotype<Float64Gene, Float64> newFloat64Phenotype(final double value) {
		return Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(value, 0, 10))), FF, 0
			);
	}
	
	public static Phenotype<Float64Gene, Float64> newFloat64Phenotype() {
		return newFloat64Phenotype(Math.random()*10);
	}
	
	public static Population<Float64Gene, Float64> newFloat64Population(final int length) {
		final Population<Float64Gene, Float64> population = 
			new Population<Float64Gene, Float64>(length);
		
		for (int i = 0; i < length; ++i) {
			population.add(newFloat64Phenotype());
		}
		
		return population;
	}
	
}
