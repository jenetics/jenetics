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

import java.util.ListIterator;
import java.util.Random;

import org.jenetics.util.Probability;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;


/**
 * This class is for mutating a chromosomoes of an given population. There are 
 * two distinct roles mutation plays
 * <ul>
 * 	<li>Exploring the search space. By making small moves mutation allows a
 * 	population to explore the search space. This exploration is often slow
 * 	compared to crossover, but in problems where crossover is disruptive this
 * 	can be an important way to explore the landscape.
 * 	</li>
 * 	<li>Maintaining diversity. Mutation prevents a population from
 * 	correlating. Even if most of the search is being performed by crossover,
 * 	mutation can be vital to provide the diversity which crossover needs.
 * 	</li>
 * </ul>
 * 
 * The mutation rate is the parameter that must be optimized. The optimal value 
 * of the mutation rate depends on the role mutation plays. If mutation is the 
 * only source of exploration (if there is no crossover) then the mutation rate 
 * should be set so that a reasonable neighbourhood of solutions is explored. 
 * Typically, this involves changing around one variable in the string, 
 * thus a mutation rate of 1/L is commonly used. Where L is the length of the
 * string, respectively the length of the 
 * {@link Chromosome#length()} or {@link Genotype}. 
 * That means, the probability that a given chromosome is mutate is
 * <pre>
 * 	probability/L,
 * </pre>
 * where the <code>probability</code> is the given mutation probability.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Mutation.java,v 1.4 2008-08-29 21:18:16 fwilhelm Exp $
 */
public class Mutation<G extends Gene<?>> extends Alterer<G> {	
	private static final long serialVersionUID = -7012689808565856577L;

	/**
	 * Default constructor, with probability = 0.01.
	 */
	public Mutation() {
		this(Probability.valueOf(0.01));
	}
	
	/**
	 * Construct a Mutation object which a given mutation probability.
	 * 
	 * @param probability Mutation probability. The given probability is
	 * 	  devided by the number of chromosomes of the genotype to form
	 * 	  the concrete mutation probability.
	 */
	public Mutation(final Probability probability) {
		super(probability);
	}

	/**
	 * Construct a Mutation object which a given mutation probability
	 * 
	 * @param probability Mutation probability. The given probability is
	 *        devided by the number of chromosomes of the genotype to form
	 *        the concrete mutation probability.
	 * @param component The next Alterers in Alterer-Chain.
	 */
	public Mutation(final Probability probability, final Alterer<G> component) {
		super(probability, component);
	}
 
	/**
	 * Concrete implementation of the alter method.
	 */
	@Override
	protected <C extends Comparable<C>> void componentAlter(final Population<G, C> population) {
		assert(population != null) : "Not null is guaranteed from base class.";
		
		final double prop = _probability.doubleValue()/
								population.get(0).getGenotype().length();
		
		final Random random = RandomRegistry.getRandom();
		for (ListIterator<Phenotype<G, C>> it = population.listIterator(); it.hasNext();) { 
			final Phenotype<G, C> pt = it.next();
			
			if (random.nextDouble() < prop) {
				final Genotype<G> gt = pt.getGenotype(); 
				final int chIndex = random.nextInt(gt.chromosomes());
				final Chromosome<G> ch = gt.getChromosome(chIndex);
				
				final int geneIndex = random.nextInt(ch.length());
				ch.mutate(geneIndex);
				
				it.set(pt.newInstance(gt));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	static final XMLFormat<Mutation> XML = new XMLFormat<Mutation>(Mutation.class) {
		@Override
		public Mutation newInstance(final Class<Mutation> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final double p = xml.getAttribute("probability", 0.5);
			final boolean hasAlterer = xml.getAttribute("has-alterer", false);
			Mutation alterer = null;
			
			if (hasAlterer) {
				Alterer component = xml.getNext();
				alterer = new Mutation(Probability.valueOf(p), component);
			} else {
				alterer = new Mutation(Probability.valueOf(p));
			}
			
			return alterer;
		}
		@Override
		public void write(final Mutation a, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("probability", a._probability.doubleValue());
			xml.setAttribute("has-alterer", a._component != null);
			if (a._component != null) {
				xml.add(a._component);
			}
		}
		@Override
		public void read(final InputElement xml, final Mutation a) 
			throws XMLStreamException 
		{
		}
	};

}




