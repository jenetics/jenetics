/* 
 * ConcurrentStatisticCalculator.java, @!identifier!@
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
 */
package org.jenetics;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * This {@link StatisticCalculator} calculates the fitness of a population in several
 * threads.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ConcurrentStatisticCalculator.java,v 1.1 2008-04-21 21:29:08 fwilhelm Exp $
 */
public class ConcurrentStatisticCalculator extends StatisticCalculator {
	private final int _numberOfThreads;
	private final ExecutorService _pool;
	
	/**
	 * Create a new ConcurentStatisticCalculator.
	 * 
	 * @param pool the 'thread pool'.
	 * @throws NullPointerException if the given {@code pool} is {@code null}.
	 */
	public ConcurrentStatisticCalculator(final int numberOfThreads, final ExecutorService pool) {
		Checker.checkNull(pool, "Thread pool");
		
		this._numberOfThreads = numberOfThreads;
		this._pool = pool;
	}
	
	@Override
	public <T extends Gene<?>> Statistic<T> evaluate(final List<? extends Phenotype<T>> population) {
		if (population == null || population.isEmpty()) {
			return new Statistic<T>(null, null, 0.0, 0.0, 0.0, 0.0);
		} 
		
		final int[] indexes = partition(population.size(), _numberOfThreads);
		final List<Callable<Statistic<T>>> tasks = new ArrayList<Callable<Statistic<T>>>(indexes.length - 1);
		for (int i = 0; i < indexes.length - 1; ++i) {
			final int idx = i;
			tasks.add(new Callable<Statistic<T>>() {
				@Override public Statistic<T> call() {
					return ConcurrentStatisticCalculator.super.evaluate(
						population.subList(indexes[idx], indexes[idx + 1])
					);
				}
			});
		}
		
		List<Future<Statistic<T>>> results = Collections.emptyList();
		try {
			results = _pool.invokeAll(tasks);
			_pool.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException ignore) {
		}
		
		return join(results);
	}
	
	private <T extends Gene<?>> Statistic<T> join(final List<Future<Statistic<T>>> results) {
//		if (results.isEmpty()) {
//			return new Statistic<T>(null, null, 0.0, 0.0, 0.0, 0.0);
//		} 
//		
//		Phenotype<T> bestPhenotype = null;
//		Phenotype<T> worstPhenotype = null;
//		
//		double fitnessSum = 0;
//		double fitnessSumsq = 0;
//		double minFitness = Double.MAX_VALUE;
//		double maxFitness = -Double.MAX_VALUE;
//		long ageSum = 0;
//		long ageSumsq = 0;
//		
//		double fitness = 0;
//		int age = 0;
//		
//		for (int i = 0, n = results.size(); i < n; ++i) {
//			final Statistic<T> statistic = results.get(i).get();
//			
//			fitness = statistic.getFitnessMean(); 
//			fitnessSum += fitness;
//			fitnessSumsq += fitness*fitness;
//			
//			age = statistic.getAgeMean();
//			ageSum += age;
//			ageSumsq += age*age;
//
//			if (minFitness > fitness) {
//				minFitness = fitness;
//				worstPhenotype = phenotype;
//			}
//			if (maxFitness < fitness) {
//				maxFitness = fitness;
//				bestPhenotype = phenotype;
//			}
//		}
//		
//		final double meanFitness = fitnessSum/population.size();
//		final double varianceFitness = fitnessSumsq/population.size() - meanFitness*meanFitness;
//		final double meanAge = (double)ageSum/(double)population.size();
//		final double varianceAge = (double)ageSumsq/(double)population.size() - meanAge*meanAge;
//		
//		return new Statistic<T>(
//			bestPhenotype, worstPhenotype, 
//			meanFitness, varianceFitness,
//			meanAge, varianceAge
//		);
		return null;
	}

	/**
	 * Return a array with the indexes of the partitions of an array with the given size.
	 * 
	 * 
	 * @param size the size of the array to partition.
	 * @param p the number of parts the (virtual) array should be partitioned.
	 * @return
	 */
	protected static int[] partition(final int size, final int p) {
		final int parts = min(size, p);
		final int[] partition = new int[parts + 1];
		
		final int bulk = size != 0 ? size/parts : 0;
		final int rest = size != 0 ? size%parts : 0;
		assert ((bulk*parts + rest) == size);
		
		for (int i = 0, n = parts - rest; i < n; ++i) {
			partition[i] = i*bulk;
		}
		for (int i = 0, n = rest + 1; i < n; ++i) {
			partition[parts - rest + i] = (parts - rest)*bulk + i*(bulk + 1);
		}
		
		return partition;
	}
	
	public int getNumberOfThreads() {
		return _numberOfThreads;
	}
}
