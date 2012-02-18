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

import org.jenetics.util.Function;

/**
 * Some default GA termination strategies.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class Until {

	private Until() {
		throw new AssertionError("Don't create an 'Until' instance.");
	}
	
	static class SteadyFitness<C extends Comparable<? super C>>
		implements Function<Statistics<?, C>, Boolean>
	{
		private final int _genenerations;
		
		private C _fitness;
		private int _stableGenerations = 0;
		
		public SteadyFitness(final int generations) {
			_genenerations = generations;
		}
		
		@Override
		public Boolean apply(final Statistics<?, C> statistics) {
			boolean proceed = true;
			
			if (_fitness == null) {
				_fitness = statistics.getBestFitness();
				_stableGenerations = 1;
			} else {
				final Optimize opt = statistics.getOptimize();
				if (opt.compare(_fitness, statistics.getBestFitness()) >= 0) {
					proceed = ++_stableGenerations <= _genenerations;
				} else {
					_fitness = statistics.getBestFitness();
					_stableGenerations = 1;
				}
			}
			
			return proceed ? Boolean.TRUE : Boolean.FALSE;
		}	
	}
	
	/**
	 * Create a <i>terminator</i> which returns {@code false} if the fitness
	 * hasn't improved for a given number of generations.
	 *
	 * @param <C> the fitness type.
	 * @param generation the number of generations the fitness don't have been
	 *        improved.
	 * @return the GA terminator.
	 */
	public static <C extends Comparable<? super C>>
	Function<Statistics<?, C>, Boolean> SteadyFitness(final int generation) {
		return new SteadyFitness<>(generation);
	}
	
	static class Generation implements Function<Statistics<?, ?>, Boolean> {
		private final int _generation;
		
		public Generation(final int generation) {
			_generation = generation;
		}
		
		@Override
		public Boolean apply(final Statistics<?, ?> statistics) {
			return statistics.getGeneration() < _generation ? Boolean.TRUE : Boolean.FALSE;
		}		
	}
	
	/**
	 * Return a <i>termination predicate</i> which returns {@code false} if the
	 * current GA generation is {@code >=} as the given {@code generation}.
	 *
	 * [code]
	 * final GeneticAlgortihm<Float64Gene, Float64> ga = ...
	 * ga.evolve(Until.Generation(100));
	 * [/code]
	 *
	 * @param generation the maximal GA generation.
	 * @return the termination predicate.
	 */
	public static Function<Statistics<?, ?>, Boolean> Generation(final int generation) {
		return new Generation(generation);
	}
	
}
