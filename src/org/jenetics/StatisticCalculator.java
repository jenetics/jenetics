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
 * @version $Id: StatisticCalculator.java,v 1.6 2008-08-27 20:30:28 fwilhelm Exp $
 */
public class StatisticCalculator<G extends Gene<?>, C extends Comparable<C>> {
	protected long _startEvaluationTime = 0;
	protected long _stopEvaluationTime = 0;
	
	public StatisticCalculator() {
	}
	
	public Statistic<G, C> evaluate(final List<? extends Phenotype<G, C>> population) {
		_startEvaluationTime = System.currentTimeMillis();
		
		Statistic<G, C> statistic = new Statistic<G, C>(null, null, 0, 0.0, 0.0);
		if (!population.isEmpty()) {		
			int age = 0;
			
			Phenotype<G, C> bestPhenotype = null;
			Phenotype<G, C> worstPhenotype = null;
			
			C fitness = null;
			C minFitness = null;
			C maxFitness = null;
			long ageSum = 0;
			long ageSquareSum = 0;
			
			for (final Phenotype<G, C> phenotype : population) {
				fitness = phenotype.getFitness(); 
				
				age = phenotype.getGeneration();
				ageSum += age;
				ageSquareSum += age*age;
	
				if (minFitness == null || minFitness.compareTo(fitness) > 0) {
					minFitness = fitness;
					worstPhenotype = phenotype;
				}
				if (maxFitness == null || maxFitness.compareTo(fitness) < 0) {
					maxFitness = fitness;
					bestPhenotype = phenotype;
				}
			}
			
			final double meanAge = 
				(double)ageSum/(double)population.size();
			final double varianceAge = 
				(double)ageSquareSum/(double)population.size() - meanAge*meanAge;
			
			statistic = new Statistic<G, C>(
				bestPhenotype, worstPhenotype, 
				population.size(), meanAge, varianceAge
			);
		}

		_stopEvaluationTime = System.currentTimeMillis();
		return statistic;
	}
	
	public C median(final List<? extends Phenotype<G, C>> population) {
		return null;
	}
	
	public long getLastEvaluationTime() {
		return _stopEvaluationTime - _startEvaluationTime;
	}
	
	
	
}
