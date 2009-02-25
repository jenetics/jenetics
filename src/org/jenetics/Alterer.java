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

import static org.jenetics.util.Validator.notNull;

import org.jenetics.util.Probability;

/**
 * Population based methods provide the possibility of incorporating a new set 
 * of moves based on combining solutions. This is referred to as crossover or 
 * recombination. 
 * 
 * The Alterer is responsible for the recombination of a Population. Alterers can
 * be chained by appending an new (component) alterers.
 * 
 * [code]
 *     GeneticAlgorithm<DoubleGene> ga = ...
 *     ga.setAlterer(
 *         new Crossover<DoubleGene>(Probability.valueOf(0.1)).append(
 *         new Mutation<DoubleGene>(Probability.valueOf(0.05))).append(
 *         new MeanAlterer<DoubleGene>(Probability.valueOf(0.2)))
 *     );
 * [/code]
 * 
 * The order of the alterer calls is: Crossover, Mutation and MeanAlterer.
 * 
 * @param <G> the gene type.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Alterer.java,v 1.12 2009-02-25 21:13:30 fwilhelm Exp $
 */
public abstract class Alterer<G extends Gene<?, G>> {


	/**
	 * The Alterer which is executed after <code>this</code> alterer.
	 */
	protected Alterer<G> _component;
	
	/**
	 * The altering probability. 
	 */
	protected final Probability _probability;
	
	/**
	 * Create a {@code Alterer} concatenating the given {@code Alterer}S. The
	 * default probability is set to 0.5.
	 * 
	 * @param component the {@code Alterer}S this {@code Alterer} consists.
	 * @throws NullPointerException if the {@code component} is {@code null}.     
	 */
	public Alterer(final Alterer<G> component) {
		this(Probability.valueOf(0.5), component);
	}
	
	/**
	 * Constucts an alterer with a given recombination probability.
	 * 
	 * @param probability The recombination probability.
	 * @throws NullPointerException if the {@code probability} is {@code null}. 
	 */
	public Alterer(final Probability probability) {
		notNull(probability, "Probability");
		_probability = probability;
	}
	
	/**
	 * Constructs an alterer with a given recombination probability. A
	 * second Alterer can be specified for a composite Alterer.
	 * 
	 * @param probability The recombination probability.
	 * @param component The composit Alterer.
	 * @throws NullPointerException if the {@code probability} or the
	 *         {@code component} is {@code null}. 
	 */
	public Alterer(final Probability probability, final Alterer<G> component) {
		notNull(probability, "Probability");
		notNull(component, "Alterer components");
		
		_probability = probability;
		_component = component;
	}
	
	/**
	 * Appends a additional Alterer at the end of the chain of Alterers.
	 * 
	 * @param alterer The Alterer to append.
	 * @throws NullPointerException if the {@code component} is {@code null}. 
	 */
	public Alterer<G> append(final Alterer<G> alterer) {
		notNull(alterer, "Alterer");

		if (_component == null) {
			_component = alterer;
		} else {
			_component.append(alterer);
		}
		
		return this;
	}
	
	/**
	 * Alters (recombinate) a given population. If the <code>population</code>
	 * is <code>null</code> or empty, nothing is altered.
	 * 
	 * @param population The Population to be altered. If the 
	 *        <code>population</code> is <code>null</code> or empty, nothing is 
	 *        altered.
	 * @param generation the date of birth (generation) of the altered phenotypes.
	 * @throws NullPointerException if the given {@code population} is 
	 *         {@code null}.
	 */
	public <C extends Comparable<C>> void alter(
		final Population<G, C> population, final int generation
	) {
		notNull(population, "Population");
		
		if (!population.isEmpty()) {
			change(population, generation);
	
			if (_component != null) {
				_component.alter(population, generation);
			}
		}
	}
	
	/**
	 * This template method performs the recombination in the implementing
	 * class. It is garuanteed that the given population is neither null, nor
	 * empty.
	 * 
	 * @param population the Population to be altered.
	 * @param generation the date of birth (generation) of the altered phenotypes.
	 */
	protected abstract <C extends Comparable<C>> void change(
		Population<G, C> population, int generation
	);
	
	/**
	 * Return the component alterer.
	 * 
	 * @return The component alterer, or {@code null} if this alterer has no
	 *         component alterer.
	 */
	public Alterer<G> getComponentAlterer() {
		return _component;
	}
	
	/**
	 * Return the recombination probability for this alterer.
	 * 
	 * @return The recombination probability.
	 */
	public Probability getProbability() {
		return _probability;
	}
	
}
