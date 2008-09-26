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

import javolution.context.ConcurrentContext;

import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Validator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: FitnessEvaluators.java,v 1.2 2008-09-26 18:41:39 fwilhelm Exp $
 */
public class FitnessEvaluators {

	/**
	 * Fitness evaluator which calculates one fitness value after another.
	 */
	public static final FitnessEvaluator SERIAL = new Serial();
	
	/**
	 * Fitness evaluator which calculates the fitness values of an population
	 * concurrently. 
	 */
	public static final FitnessEvaluator CONCURRENT = new Concurrent();
	
	private FitnessEvaluators() {
	}
	
	
	private static final class Serial implements FitnessEvaluator {
		
		public <G extends Gene<?>, C extends Comparable<C>> 
		void evaluate(final List<Phenotype<G, C>> population)
		{
			Validator.notNull(population, "Population");
			
			for (int i = population.size(); --i >= 0;) {
				population.get(i).evaluate();
			}
		}
		
	}
	
	
	private static final class Concurrent implements FitnessEvaluator {
		public <G extends Gene<?>, C extends Comparable<C>> 
		void evaluate(final List<Phenotype<G, C>> population) 
		{
			Validator.notNull(population, "Population");
			
			ConcurrentContext.enter();
			try {
				final int[] parts = ArrayUtils.partition(
					population.size(), ConcurrentContext.getConcurrency() + 1
				);
				for (int i = 0; i < parts.length - 1; ++i) {
					final int part = i;
					ConcurrentContext.execute(new Runnable() {
						@Override public void run() {
							for (int j = parts[part + 1]; --j >= parts[part];) {
								population.get(j).evaluate();
							}
						}
					});
				}
			} finally {
				ConcurrentContext.exit();
			}
		}
	}
	
}
