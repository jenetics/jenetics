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

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Random;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

/**
 * <p>
 * The {@code PartiallyMatchedCrossover} (PMX) guarantees that all {@link Gene}s 
 * are found exactly once in each chromosome. No gene is duplicated by this 
 * crossover. The PMX can be applied usefully in the TSP or other permutation
 * problem encodings. Permutation encoding is useful for all problems where the
 * fitness only depends on the ordering of the genes within the chromosome. This
 * is the case in many combinatorial optimization problems. Other crossover
 * operators for combinatorial optimization are:
 * <ul type="square">
 *     <li>order crossover</li>
 *     <li>cycle crossover</li>
 *     <li>edge recombination crossover</li>
 *     <li>edge assembly crossover</li>
 * </ul>
 * </p>
 * The PMX is similar to the two-point crossover. A crossing region is choosen
 * by selecting two crossing points.
 * <pre>
 *     C1 = 012|345|6789
 *     C2 = 987|654|3210
 * </pre>
 * After performing the crossover we normally got two invalid chromosomes.
 * <pre>
 *     C1 = 012|654|6789
 *     C2 = 987|345|3210
 * </pre>
 * Chromosome {@code C1} contains the value 6  twice and misses the value
 * 3. On  the other side chromosome {@code C2} contains the value 3 twice and 
 * misses the value 6. We can observe that this crossover is equivalent 
 * to the exchange of the values 3 -> 6, 4 -> 5 and 5 -> 4. To repair the two 
 * chromosomes we have to apply this exchange outside the crossing region.
 * <pre>
 *     C1 = 012|654|3789
 *     C2 = 987|345|6210
 * </pre>
 * 
 * @see PermutationChromosome
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: PartiallyMatchedCrossover.java,v 1.3 2008-07-05 20:28:12 fwilhelm Exp $
 */
public class PartiallyMatchedCrossover<T extends Gene<?>> extends Crossover<T> {
	private static final long serialVersionUID = 4100745364870900673L;

	public PartiallyMatchedCrossover(final Alterer<T> component) {
		super(component);
	}

	public PartiallyMatchedCrossover(final Probability probability, final Alterer<T> component) {
		super(probability, component);
	}

	public PartiallyMatchedCrossover(final Probability probability) {
		super(probability);
	}
	
	@Override 
	protected void crossover(Array<T> that, Array<T> other) {
		final Random random = RandomRegistry.getRandom();
		int index1 = random.nextInt(that.length());
		int index2 = random.nextInt(other.length());
		index1 = min(index1, index2);
		index2 = max(index1, index2) + 1;
		
		final Array<T> thatGenes = Array.newInstance(index2 - index1);
		final Array<T> otherGenes = Array.newInstance(index2 - index1);
		
		//Swap the gene range.
		for (int i = index1; i < index2; ++i) {
			final int index = i - index1;
			
			thatGenes.set(index, that.get(i));
			otherGenes.set(index, other.get(i));
			
			that.set(i, otherGenes.get(index));
			other.set(i, thatGenes.get(index));
		}
		
		//Repare the chromosomes.
		for (int i = 0; i < (index2 - index1); ++i) {
			int thatIndex = indexOf(that, index1, index2, otherGenes.get(i));
			int otherIndex = indexOf(other, index1, index2, thatGenes.get(i));
			
			that.set(thatIndex, thatGenes.get(i));
			other.set(otherIndex, otherGenes.get(i));
		}
		
	}
	
	private static <A extends Gene<?>> int indexOf(
		final Array<A> genes, final int idx1, final int idx2, final A gene
	) {
		int index = -1;
		for (int i = 0; index == -1 && i < idx1; ++i) {
			if (genes.get(i).equals(gene)) {
				index = i;
			}
		}
		for (int i = idx2; index == -1 && i < genes.length(); ++i) {
			if (genes.get(i).equals(gene)) {
				index = i;
			}
		}
		for (int i = idx1; index == -1 && i < idx2; ++i) {
			if (genes.get(i).equals(gene)) {
				index = i;
			}
		}
		return index;
	}
	
	
	@SuppressWarnings("unchecked")
	static final XMLFormat<PartiallyMatchedCrossover> 
	XML = new XMLFormat<PartiallyMatchedCrossover>(PartiallyMatchedCrossover.class) {
		@Override
		public PartiallyMatchedCrossover newInstance(
			final Class<PartiallyMatchedCrossover> cls, final InputElement xml
		) throws XMLStreamException {
			final double p = xml.getAttribute("probability", 0.5);
			final boolean hasAlterer = xml.getAttribute("has-alterer", false);
			PartiallyMatchedCrossover alterer = null;
			
			if (hasAlterer) {
				Alterer component = xml.getNext();
				alterer = new PartiallyMatchedCrossover(Probability.valueOf(p), component);
			} else {
				alterer = new PartiallyMatchedCrossover(Probability.valueOf(p));
			}
			
			return alterer;
		}
		@Override
		public void write(final PartiallyMatchedCrossover a, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("probability", a._probability.doubleValue());
			xml.setAttribute("has-alterer", a._component != null);
			if (a._component != null) {
				xml.add(a._component);
			}
		}
		@Override
		public void read(final InputElement xml, final PartiallyMatchedCrossover a) 
			throws XMLStreamException 
		{
		}
	};

}



