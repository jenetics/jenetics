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

import java.io.Serializable;
import java.util.Random;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Validator;

/**
 * In tournament selection the best {@link Phenotype} from a random sample of s 
 * individuals is chosen for the next generation. The samples are drawn 
 * (in this class) without replacement. An individual will win a tournament 
 * onnly if its fitness is greater than the fitness of the other s-1 
 * competitors. Note that the worst {@link Phenotype} individual never survives, 
 * and the best {@link Phenotype} individual wins in all the tournaments it 
 * participates.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: TournamentSelector.java,v 1.8 2009-02-23 20:58:08 fwilhelm Exp $
 */
public class TournamentSelector<G extends Gene<?, G>, C extends Comparable<C>> 
	implements Selector<G, C>, Serializable 
{
	private static final long serialVersionUID = -5342297228328820942L;
	
	private final int _sampleSize;

	/**
	 * Create a tournament selector with sample size two.
	 */
	public TournamentSelector() {
		this(2);
	}
	
	/**
	 * Create a tournament selector with the give sample size. The sample size 
	 * must be greater than one.
	 * 
	 * @throws IllegalArgumentException if the sample size is smaller than two.
	 */
	public TournamentSelector(final int sampleSize) {
		if (sampleSize < 2) {
			throw new IllegalArgumentException(
				"Sample size must be greater than one, but was " + sampleSize
			);
		}
		_sampleSize = sampleSize;
	}

	/**
	 * @throws IllegalArgumentException if the sample size is bigger than the
	 *         population size.
	 * @throws NullPointerException if the <code>population</code> is 
	 *         <code>null</code>.
	 */
	@Override
	public Population<G, C> select(final Population<G, C> population, final int count) {
		Validator.notNull(population, "Population");
		if (count < 0) {
			throw new IllegalArgumentException(
				"Selection count must be greater or equal then zero, but was " + count
			);
		}
		
		if (_sampleSize > population.size()) {
			throw new IllegalArgumentException(String.format(
				"Tournament size is greater than the population size! %d > %d.",
				 _sampleSize, population.size()
			));
		}
		
		final Population<G, C> pop = new Population<G, C>(count);
		if (count == 0) {
			return pop;
		}
		
		Phenotype<G, C> winner = null;
		C bestFitness = null;
		
		final int N = population.size();
		final Random random = RandomRegistry.getRandom();
		
		for (int i = 0; i < count; ++i) {
			winner = population.get(random.nextInt(N));
			bestFitness = winner.getFitness();
			
			for (int j = 0; j < _sampleSize; ++j) {
				final Phenotype<G, C> selection = population.get(random.nextInt(N));
				if (selection.getFitness().compareTo(bestFitness) > 0) {
					bestFitness = selection.getFitness();
					winner = selection;
				}
			}
			
			pop.add(winner);
		}
		
		return pop;
	}
	
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>> 
	TournamentSelector<SG, SC> valueOf(final int sampleSize) {
		return new TournamentSelector<SG, SC>(sampleSize);
	}
	
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>> 
	TournamentSelector<SG, SC> valueOf() {
		return new TournamentSelector<SG, SC>();
	}

}





