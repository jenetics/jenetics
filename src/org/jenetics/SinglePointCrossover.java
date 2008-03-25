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
import javolution.xml.stream.XMLStreamException;


/** 
 * <strong><p>Single point crossover</p></strong>
 * 
 * <p>
 * One or two children are created by taking two parent strings and cutting 
 * them at some randomly chosen site. E.g.
 * </p>
 * <div align="center">
 * 	<img src="doc-files/SinglePointCrossover.gif" >
 * </div>
 * <p>
 * If we create a child and its complement we preserving the total number of 
 * genes in the population, preventing any genetic drift.
 * Single-point crossover is the classic form of crossover. However, it produces
 * very slow mixing compared with multi-point crossover or uniform crossover. 
 * For problems where the site position has some intrinsic meaning to the 
 * problem single-point crossover can lead to small disruption than multi-point 
 * or uniform crossover.
 * </p>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: SinglePointCrossover.java,v 1.1 2008-03-25 18:31:56 fwilhelm Exp $
 */
public class SinglePointCrossover<T extends Gene<?>> extends Crossover<T> {
	private static final long serialVersionUID = -5901453762256113098L;

	public SinglePointCrossover() {
		this(Probability.valueOf(0.05));
	}
	
	public SinglePointCrossover(final Probability probability) {
		super(probability);
	}

	public SinglePointCrossover(final Probability probability, final Alterer<T> component) {
		super(probability, component);
	}
	
	@Override
	protected void crossover(T[] that, T[] other) {
		final Random random = RandomRegistry.getRandom();
		final int index = random.nextInt(that.length);
		
		for (int j = 0; j <= index; ++j) {
			final T temp = that[j];
			that[j] = other[j];
			other[j] = temp;
		}
	}
	
	@SuppressWarnings("unchecked")
	static final XMLFormat<SinglePointCrossover> XML = new XMLFormat<SinglePointCrossover>(SinglePointCrossover.class) {
		@Override
		public SinglePointCrossover newInstance(final Class<SinglePointCrossover> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final double p = xml.getAttribute("probability", 0.5);
			final boolean hasAlterer = xml.getAttribute("has-alterer", false);
			SinglePointCrossover alterer = null;
			
			if (hasAlterer) {
				Alterer component = xml.getNext();
				alterer = new SinglePointCrossover(Probability.valueOf(p), component);
			} else {
				alterer = new SinglePointCrossover(Probability.valueOf(p));
			}
			
			return alterer;
		}
		@Override
		public void write(final SinglePointCrossover a, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("probability", a._probability.doubleValue());
			xml.setAttribute("has-alterer", a._component != null);
			if (a._component != null) {
				xml.add(a._component);
			}
		}
		@Override
		public void read(final InputElement xml, final SinglePointCrossover a) {
		}
	};
	
}

