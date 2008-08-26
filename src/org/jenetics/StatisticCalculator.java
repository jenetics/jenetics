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
 * @version $Id: StatisticCalculator.java,v 1.5 2008-08-26 22:29:35 fwilhelm Exp $
 */
public class StatisticCalculator {
	protected long _startEvaluationTime = 0;
	protected long _stopEvaluationTime = 0;
	
	public StatisticCalculator() {
	}
	
	public <T extends Gene<?>, C extends Comparable<C>> Statistic<T, C> evaluate(
		final List<? extends Phenotype<T, C>> population
	) {
		_startEvaluationTime = System.currentTimeMillis();
		try {
			if (population == null || population.isEmpty()) {
				return new Statistic<T, C>(null, null, null, null, 0.0, 0.0);
			} 
			
			Phenotype<T, C> bestPhenotype = null;
			Phenotype<T, C> worstPhenotype = null;
			
			double fitnessSum = 0.0;
			double fitnessSquareSum = 0.0;
			double minFitness = Double.MAX_VALUE;
			double maxFitness = -Double.MAX_VALUE;
			long ageSum = 0;
			long ageSquareSum = 0;
			
			C fitness = null;
			int age = 0;
			
			for (final Phenotype<T, C> phenotype : population) {
				fitness = phenotype.getFitness(); 
//				fitnessSum += fitness;
//				fitnessSquareSum += fitness*fitness;
				
				age = phenotype.getGeneration();
				ageSum += age;
				ageSquareSum += age*age;
	
//				if (minFitness > fitness) {
//					minFitness = fitness;
//					worstPhenotype = phenotype;
//				}
//				if (maxFitness < fitness) {
//					maxFitness = fitness;
//					bestPhenotype = phenotype;
//				}
			}
			
			final double meanFitness = fitnessSum/population.size();
			final double varianceFitness = fitnessSquareSum/population.size() - 
							meanFitness*meanFitness;
			final double meanAge = (double)ageSum/(double)population.size();
			final double varianceAge = (double)ageSquareSum/(double)population.size() - 
							meanAge*meanAge;
			
			final Statistic<T, C> statistic = null;/*new Statistic<T, C>(
				bestPhenotype, worstPhenotype, 
				meanFitness, varianceFitness,
				meanAge, varianceAge
			);*/
			statistic.setSamples(population.size());
			statistic.setAgeSum(ageSum);
			statistic.setAgeSquareSum(ageSquareSum);
			statistic.setFitnessSum(fitnessSum);
			statistic.setFitnessSquareSum(fitnessSquareSum);
			
			return statistic;
		} finally {
			_stopEvaluationTime = System.currentTimeMillis();
		}
	}
	
	public long getLastEvaluationTime() {
		return _stopEvaluationTime - _startEvaluationTime;
	}
	
}
