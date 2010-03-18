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
 * In tournament selection the best {@link Phenotype} from a random sample of 
 * <i>s</i> individuals is chosen for the next generation. The samples are drawn 
 * (in this class) without replacement. An individual will win a tournament 
 * only if its fitness is greater than the fitness of the other <i>s-1</i> 
 * competitors. Note that the worst {@link Phenotype} individual never survives, 
 * and the best {@link Phenotype} individual wins in all the tournaments it 
 * participates.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Tournament_selection">Tournament selection</a>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: TournamentSelector.java 330 2010-02-16 12:48:21Z fwilhelm $
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
	 * @throws IllegalArgumentException if the sample size is greater than the
	 *         population size or {@code count} is greater the the population 
	 *         size or the _sampleSize is greater the the population size.
	 * @throws NullPointerException if the {@code population} is {@code null}.
	 */
	@Override
	public Population<G, C> select(final Population<G, C> population, final int count) {
		Validator.nonNull(population, "Population");
		if (count < 0) {
			throw new IllegalArgumentException(String.format(
				"Selection count must be greater or equal then zero, but was %s",
				count
			));
		}
		if (count > population.size()) {
			throw new IllegalArgumentException(String.format(
				"Selection size greater than population size: %s > %s",
				count, population.size()
			));
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
		
		final int N = population.size();
		final Random random = RandomRegistry.getRandom();
		
		for (int i = 0; i < count; ++i) {
			winner = population.get(random.nextInt(N));
			
			for (int j = 0; j < _sampleSize; ++j) {
				final Phenotype<G, C> selection = population.get(random.nextInt(N));
				if (selection.compareTo(winner) > 0) {
					winner = selection;
				}
			}
			
			assert (winner != null);
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





