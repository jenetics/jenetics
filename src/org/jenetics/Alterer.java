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

import javolution.xml.XMLSerializable;

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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Alterer.java,v 1.3 2008-08-25 19:35:23 fwilhelm Exp $
 */
public abstract class Alterer<T extends Gene<?>> implements XMLSerializable {
	private static final long serialVersionUID = -675546015545758480L;

	/**
	 * The Alterer which is executed after <code>this</code> alterer.
	 */
	protected Alterer<T> _component;
	
	/**
	 * The altering probability. 
	 */
	protected final Probability _probability;
	
	/**
	 * Create a <code>Alterer</code> concatenating the given 
	 * <code>Alterer</code>S.
	 * 
	 * @param component the <code>Alterer</code>S this <code>Alterer</code>
	 *        consists.
	 */
	public Alterer(final Alterer<T> component) {
		this(Probability.ONE, component);
	}
	
	/**
	 * Constucts an alterer with a given recombination probability.
	 * 
	 * @param probability The recombination probability.
	 * @throws NullPointerException if the <code>probability</code> is 
	 * 		<code>null</code>.
	 */
	public Alterer(final Probability probability) {
		notNull(probability, "Probability");
		this._probability = probability;
	}
	
	/**
	 * Constructs an alterer with a given recombination probability. A
	 * second Alterer can be specified for a composite Alterer.
	 * 
	 * @param probability The recombination probability.
	 * @param component The composit Alterer.
	 * @throws NullPointerException if the <code>probability</code> or the
	 * 		<code>component</code> is <code>null</code>. 
	 */
	public Alterer(final Probability probability, final Alterer<T> component) {
		notNull(probability, "Probability");
		notNull(component, "Alterer components");
		
		this._probability = probability;
		this._component = component;
	}
	
	/**
	 * Appends a additional Alterer at the end of the chain of Alterers.
	 * 
	 * @param alterer The Alterer to append.
	 * @throws NullPointerException if the <code>alterer</code> is 
	 *         <code>null</code>.
	 */
	public Alterer<T> append(final Alterer<T> alterer) {
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
	 */
	public void alter(final Population<T> population) {
		if (population == null || population.isEmpty()) {
			return;
		}
		
		componentAlter(population);
		if (_component != null) {
			_component.alter(population);
		}
	}
	
	/**
	 * This template method performs the recombination in the implementing
	 * class. It is garuanteed that the given population is neither null, nor
	 * empty.
	 * 
	 * @param population the Population to be altered.
	 */
	protected abstract void componentAlter(Population<T> population);
	
	/**
	 * Return the component alterer.
	 * 
	 * @return The component alterer, or <code>null</code> if this alterer has no
	 *         component alterer.
	 */
	public Alterer<T> getComponentAlterer() {
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
