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

import java.util.List;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: StatisticCalculator.java,v 1.2 2008-04-21 21:29:08 fwilhelm Exp $
 */
public class StatisticCalculator {
	
	public StatisticCalculator() {
	}
	
	public <T extends Gene<?>> Statistic<T> evaluate(final List<? extends Phenotype<T>> population) {
		if (population == null || population.isEmpty()) {
			return new Statistic<T>(null, null, 0.0, 0.0, 0.0, 0.0);
		} 
		
		Phenotype<T> bestPhenotype = null;
		Phenotype<T> worstPhenotype = null;
		
		double fitnessSum = 0;
		double fitnessSumsq = 0;
		double minFitness = Double.MAX_VALUE;
		double maxFitness = -Double.MAX_VALUE;
		long ageSum = 0;
		long ageSumsq = 0;
		
		double fitness = 0;
		int age = 0;
		
		for (int i = 0, n = population.size(); i < n; ++i) {
			final Phenotype<T> phenotype = population.get(i);
			
			fitness = phenotype.getFitness(); 
			fitnessSum += fitness;
			fitnessSumsq += fitness*fitness;
			
			age = phenotype.getGeneration();
			ageSum += age;
			ageSumsq += age*age;

			if (minFitness > fitness) {
				minFitness = fitness;
				worstPhenotype = phenotype;
			}
			if (maxFitness < fitness) {
				maxFitness = fitness;
				bestPhenotype = phenotype;
			}
		}
		
		final double meanFitness = fitnessSum/population.size();
		final double varianceFitness = fitnessSumsq/population.size() - meanFitness*meanFitness;
		final double meanAge = (double)ageSum/(double)population.size();
		final double varianceAge = (double)ageSumsq/(double)population.size() - meanAge*meanAge;
		
		return new Statistic<T>(
			bestPhenotype, worstPhenotype, 
			meanFitness, varianceFitness,
			meanAge, varianceAge
		);
		
	}
	
}
