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

import static org.jenetics.ArrayUtils.partition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * This {@link StatisticCalculator} calculates the fitness of a population in several
 * threads.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ConcurrentStatisticCalculator.java,v 1.5 2008-04-23 19:20:20 fwilhelm Exp $
 */
public class ConcurrentStatisticCalculator extends StatisticCalculator {
	private final int _numberOfPartitions;
	private final ExecutorService _pool;
	
	/**
	 * Create a new ConcurentStatisticCalculator.
	 * 
	 * @param pool the 'thread pool'.
	 * @param numberOfPartitions the number of partitions.
	 * @throws NullPointerException if the given {@code pool} is {@code null}.
	 * @throws IllegalArgumentException if the {@code numberOfPartitions} is smaller than one.
	 */
	public ConcurrentStatisticCalculator(final int numberOfPartitions, final ExecutorService pool) {
		Checker.checkNull(pool, "Thread pool");
		if (numberOfPartitions < 1) {
			throw new IllegalArgumentException("Number of partitions is " + numberOfPartitions);
		}
		
		this._numberOfPartitions = numberOfPartitions;
		this._pool = pool;
	}
	
	/**
	 * Return the number of threads.
	 * 
	 * @return the number of threads.
	 */
	public int getNumberOfPartitions() {
		return _numberOfPartitions;
	}
	
	@Override
	public <T extends Gene<?>> Statistic<T> evaluate(final List<? extends Phenotype<T>> population) {
		_startEvaluationTime = System.currentTimeMillis();
		try {
			if (population == null || population.isEmpty()) {
				return new Statistic<T>(null, null, 0.0, 0.0, 0.0, 0.0);
			} 
			
			final int[] indexes = partition(population.size(), _numberOfPartitions);
			final List<Callable<Statistic<T>>> tasks = 
				new ArrayList<Callable<Statistic<T>>>(indexes.length - 1);
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
			
			try {
				return join(_pool.invokeAll(tasks));
			} catch (InterruptedException ignore) {
				Thread.currentThread().interrupt();
			} catch (ExecutionException never) {
				if (never.getCause() instanceof RuntimeException) {
					throw (RuntimeException)never.getCause();
				}
				assert (false) : "The execution task never throws.";
			}
			
			return new Statistic<T>(null, null, 0.0, 0.0, 0.0, 0.0);
		} finally {
			_stopEvaluationTime = System.currentTimeMillis();
		}
	}
	
	private static <T extends Gene<?>> Statistic<T> join(final List<Future<Statistic<T>>> results) 
		throws InterruptedException, ExecutionException 
	{
		if (results.isEmpty()) {
			return new Statistic<T>(null, null, 0.0, 0.0, 0.0, 0.0);
		} 
		
		Phenotype<T> bestPhenotype = null;
		Phenotype<T> worstPhenotype = null;
		
		double fitnessSum = 0;
		double fitnessSquareSum = 0;
		double minFitness = Double.MAX_VALUE;
		double maxFitness = -Double.MAX_VALUE;
		long ageSum = 0;
		long ageSquareSum = 0;
		int samples = 0;
		
		for (int i = 0, n = results.size(); i < n; ++i) {
			final Statistic<T> statistic = results.get(i).get();
			
			samples += statistic.getSamples();
			fitnessSum += statistic.getFitnessSum();
			fitnessSquareSum += statistic.getFitnessSquareSum();
			
			ageSum += statistic.getAgeSum();
			ageSquareSum += statistic.getAgeSquareSum();

			if (minFitness > statistic.getWorstPhenotype().getFitness()) {
				minFitness = statistic.getWorstPhenotype().getFitness();
				worstPhenotype = statistic.getWorstPhenotype();
			}
			if (maxFitness < statistic.getBestPhenotype().getFitness()) {
				maxFitness = statistic.getBestPhenotype().getFitness();
				bestPhenotype = statistic.getBestPhenotype();
			}
		}
		
		final double meanFitness = fitnessSum/samples;
		final double varianceFitness = fitnessSquareSum/samples - meanFitness*meanFitness;
		final double meanAge = (double)ageSum/(double)samples;
		final double varianceAge = (double)ageSquareSum/(double)samples - meanAge*meanAge;
		
		return new Statistic<T>(
			bestPhenotype, worstPhenotype, 
			meanFitness, varianceFitness,
			meanAge, varianceAge
		);
	}

}
