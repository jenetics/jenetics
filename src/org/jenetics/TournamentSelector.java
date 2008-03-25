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

import java.util.Random;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

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
 * @version $Id: TournamentSelector.java,v 1.1 2008-03-25 18:31:55 fwilhelm Exp $
 */
public class TournamentSelector<T extends Gene<?>> implements Selector<T>, XMLSerializable {
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
		this._sampleSize = sampleSize;
	}

	/**
	 * @throws IllegalArgumentException if the sample size is bigger than the
	 *         population size.
	 * @throws NullPointerException if the <code>population</code> is 
	 *         <code>null</code>.
	 */
	@Override
	public Population<T> select(final Population<T> population, final int count) {
		Checker.checkNull(population, "Population");
		if (count < 0) {
			throw new IllegalArgumentException(
				"Selection count must be greater or equal then zero, but was " + count
			);
		}
		
		if (_sampleSize > population.size()) {
			throw new IllegalArgumentException(
				"Tournament size is greater than the population size! " +
				 _sampleSize + " > " + population.size()
			);
		}
		
		Population<T> pop = new Population<T>();
		if (count == 0) {
			return pop;
		}
		
		Phenotype<T> winner = null;
		Phenotype<T> selection = null;
		double bestFitness = -Double.MAX_VALUE;
		final int N = population.size();
		final Random random = RandomRegistry.getRandom();
		
		for (int i = 0; i < count; ++i) {
			bestFitness = -Double.MAX_VALUE;
			
			for (int j = 0; j < _sampleSize; ++j) {
				selection = population.get(random.nextInt(N));
				if (selection.getFitness() > bestFitness) {
					bestFitness = selection.getFitness();
					winner = selection;
				}
			}
			
			assert (winner != null);
			pop.add(winner);
		}
		
		return pop;
	}
	
	@SuppressWarnings("unchecked")
	static final XMLFormat<TournamentSelector> 
	XML = new XMLFormat<TournamentSelector>(TournamentSelector.class) {
		@Override
		public TournamentSelector newInstance(
			final Class<TournamentSelector> cls, final InputElement xml
		) throws XMLStreamException {
			final int sampleSize = xml.getAttribute("samplesize", 2);
			return new TournamentSelector(sampleSize);
		}
		@Override
		public void write(final TournamentSelector s, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("samplesize", s._sampleSize);
		}
		@Override
		public void read(final InputElement xml, final TournamentSelector s) 
			throws XMLStreamException 
		{
		}
	};

}




