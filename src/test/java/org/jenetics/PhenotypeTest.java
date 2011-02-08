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

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class PhenotypeTest extends ObjectTester { 
    
	private static final class Function implements FitnessFunction<Float64Gene, Float64> {
		private static final long serialVersionUID = 2793605351118238308L;
		@Override public Float64 evaluate(final Genotype<Float64Gene> genotype) {
			final Float64Gene gene = genotype.getChromosome().getGene(0);
			return Float64.valueOf(sin(toRadians(gene.doubleValue())));
		}
	}	
	
	private final Factory<Genotype<Float64Gene>> _genotype = Genotype.valueOf(
			new Float64Chromosome(0, 1, 50), 
			new Float64Chromosome(0, 1, 500), 
			new Float64Chromosome(0, 1, 100),
			new Float64Chromosome(0, 1, 50)
		);
	private final FitnessFunction<Float64Gene, Float64> _ff = new Function();
	private final FitnessScaler<Float64> _scaler = IdentityScaler.<Float64>valueOf();
	private final Factory<?> _factory = new Factory<Phenotype<Float64Gene, Float64>>() {
		@Override public Phenotype<Float64Gene, Float64> newInstance() {
			return Phenotype.valueOf(_genotype.newInstance(), _ff, _scaler, 0);
		}
	};
	
	@Override protected Factory<?> getFactory() {
		return _factory;
	}
	
}
