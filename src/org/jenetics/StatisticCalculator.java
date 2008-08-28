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
 * @version $Id: StatisticCalculator.java,v 1.8 2008-08-28 21:46:50 fwilhelm Exp $
 */
public class StatisticCalculator<G extends Gene<?>, C extends Comparable<C>> {
	protected long _startEvaluationTime = 0;
	protected long _stopEvaluationTime = 0;
	
	public StatisticCalculator() {
	}
	
	public Statistic<G, C> evaluate(final List<? extends Phenotype<G, C>> population) {
		_startEvaluationTime = System.currentTimeMillis();
		
		Statistic<G, C> statistic = new Statistic<G, C>(null, null, 0, 0.0, 0.0);
		final int size = population.size();
		
		Phenotype<G, C> best = null;
		Phenotype<G, C> worst = null;
		long ageSum = 0;
		long ageSquareSum = 0;
		int start = 0;
		
		if (size%2 == 0 && size > 0) {
			start = 2;
			if (population.get(0).compareTo(population.get(1)) < 0) {
				worst = population.get(0);
				best = population.get(1);
			} else {
				worst = population.get(1);
				best = population.get(0);
			}
			
			ageSum += best.getGeneration() + worst.getGeneration();
			ageSquareSum += best.getGeneration()*best.getGeneration();
			ageSquareSum += worst.getGeneration()*worst.getGeneration();
		} else if (size%2 == 1) {
			start = 1;
			worst = population.get(0);
			best = population.get(0);
			
			ageSum = best.getGeneration();
			ageSquareSum = best.getGeneration()*best.getGeneration();
		}
		
		for (int i = start; i < size; i += 2) {
			final Phenotype<G, C> first = population.get(i);
			final Phenotype<G, C> second = population.get(i + 1);
			
			if (first.compareTo(second) < 0) {
				if (first.compareTo(worst) < 0) {
					worst = first;
				}
				if (second.compareTo(best) > 0) {
					best = second;
				}
			} else {
				if (second.compareTo(worst) < 0) {
					worst = second;
				}
				if (first.compareTo(best) > 0) {
					best = first;
				}
			}
			
			ageSum += best.getGeneration() + worst.getGeneration();
			ageSquareSum += best.getGeneration()*best.getGeneration();
			ageSquareSum += worst.getGeneration()*worst.getGeneration();
		}
		
		if (size > 0) {		
			final double meanAge = (double)ageSum/(double)size;
			final double varianceAge = (double)ageSquareSum/(double)size - meanAge*meanAge;
			
			statistic = new Statistic<G, C>(best, worst, size, meanAge, varianceAge);
		}

		_stopEvaluationTime = System.currentTimeMillis();
		return statistic;
	}
	
	public long getLastEvaluationTime() {
		return _stopEvaluationTime - _startEvaluationTime;
	}
	
	
	
}
