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

import static java.lang.Math.exp;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * In this <code>Selector</code>, the probability for selection is defined as:
 * <p/>
 * 
 * <pre>
 *         exp[beta*f_i]
 *  p_i = ---------------,
 *              Z
 * </pre>
 * 
 * where <pre>beta</pre> controls the selection intensity, and<p/>
 * 
 * <pre>
 *     Z = Sum[exp[beta*f_j], j = 1, n].
 * </pre>
 * 
 * f_j denotes the fitness value of the jth individium.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: BoltzmannSelector.java,v 1.2 2008-07-08 18:58:09 fwilhelm Exp $
 */
public class BoltzmannSelector<T extends Gene<?>> extends ProbabilitySelector<T> 
	implements XMLSerializable
{
	private static final long serialVersionUID = 4785987330242283796L;
	
	private final double _beta;

	/**
	 * Create a new BolzmanSelecter with a default beta of 0.2.
	 */
	public BoltzmannSelector() {
		this(0.2);
	}
	
	public BoltzmannSelector(final double beta) {
		this._beta = beta;
	}

	@Override
	protected double[] probabilities(final Population<T> population, final int count) {
		assert (population != null) : "Population must not be null. ";
		assert (count >= 0) : "Population to select must be greater than zero. ";
		
		final double[] props = new double[population.size()];
		
		double z = 0;
		for (Phenotype<T> pt : population) {
			z += exp(_beta*pt.getFitness());
		}
		
		for (int i = 0, n = population.size(); i < n; ++i) {
			props[i] = exp(_beta*population.get(i).getFitness())/z;
		}
	
		return props;
	}
	
	@SuppressWarnings("unchecked")
	static final XMLFormat<BoltzmannSelector> 
	XML = new XMLFormat<BoltzmannSelector>(BoltzmannSelector.class) {
		@Override
		public BoltzmannSelector newInstance(
			final Class<BoltzmannSelector> cls, final InputElement xml
		) throws XMLStreamException 
		{
			final double beta = xml.getAttribute("beta", 0.2);
			return new BoltzmannSelector(beta);
		}
		@Override
		public void write(final BoltzmannSelector s, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("beta", s._beta);
		}
		@Override
		public void read(final InputElement xml, final BoltzmannSelector s) {
		}
	};

}




