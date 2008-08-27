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

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static org.jenetics.PowerScaler.SQR_SCALER;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.GenotypeFactory;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutation;
import org.jenetics.util.Probability;
import org.jscience.mathematics.number.Float64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: RealFunction.java,v 1.6 2008-08-27 20:30:31 fwilhelm Exp $
 */
public class RealFunction {
	private static final class Function implements FitnessFunction<DoubleGene, Float64> {
		private static final long serialVersionUID = 2793605351118238308L;
		
		public Float64 evaluate(final Genotype<DoubleGene> genotype) {
			final DoubleGene gene = genotype.getChromosome().getGene(0);
			return Float64.valueOf(sin(toRadians(gene.doubleValue())));
		}
	}
	
	
	public static void main(String[] args) {
		final GenotypeFactory<DoubleGene> gtf = Genotype.valueOf(new DoubleChromosome(0, 360));
		final Function ff = new Function();
		final GeneticAlgorithm<DoubleGene, Float64> ga = GeneticAlgorithm.valueOf(gtf, ff);
		
		ga.setFitnessScaler(SQR_SCALER);
		ga.setPopulationSize(100);
		ga.setAlterer(
			new Mutation<DoubleGene>(Probability.valueOf(0.1)).append(
			new MeanAlterer<DoubleGene>(Probability.valueOf(0.1)))
		);
		
		ga.setup();		
		for (int i = 0; i < 10; ++i) {
			ga.evolve();
			System.out.println(
				Integer.toString(i) + ":" + ga.getBestPhenotype() + "-->" + 
				ga.getBestPhenotype().getFitness()
			);
		}		 
	}
	
}
