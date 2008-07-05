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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: MeanAlterer.java,v 1.2 2008-07-05 20:28:11 fwilhelm Exp $
 */
public class MeanAlterer<T extends Gene<?> & Mean<T>> extends Alterer<T> {
	private static final long serialVersionUID = 4680966822655548466L;

	public MeanAlterer() {
		this(Probability.valueOf(0.05));
	}
	
	public MeanAlterer(final Probability probability) {
		super(probability);
	}

	public MeanAlterer(final Probability probability, final Alterer<T> component) {
		super(probability, component);
	}

	@Override
	protected void componentAlter(Population<T> population) {
		assert(population != null) : "Not null is guaranteed from base class.";
		
		final Random random = RandomRegistry.getRandom();
		for (int i = 0, size = population.size(); i < size; ++i) {
			if (random.nextDouble() < _probability.doubleValue()) {
				final int pt2Index = random.nextInt(population.size());
				final Phenotype<T> pt1 = population.get(i);
				final Phenotype<T> pt2 = population.get(pt2Index);
				final Genotype<T> gt1 = pt1.getGenotype();
				final Genotype<T> gt2 = pt2.getGenotype();
				
				final int chIndex = random.nextInt(gt1.chromosomes());
				final Array<Chromosome<T>> chromosomes1 = gt1.getChromosomes();
				final Array<Chromosome<T>> chromosomes2 = gt2.getChromosomes();
				final Array<T> genes1 = chromosomes1.get(chIndex).getGenes();
				final Array<T> genes2 = chromosomes2.get(chIndex).getGenes();
				
				final int geneIndex = random.nextInt(genes1.length());
				
				genes1.set(geneIndex, genes1.get(geneIndex).mean(genes2.get(geneIndex)));
				genes2.set(geneIndex, genes1.get(geneIndex));
				chromosomes1.set(chIndex, chromosomes1.get(chIndex).newChromosome(genes1));
				chromosomes2.set(chIndex, chromosomes2.get(chIndex).newChromosome(genes2));
				
				population.set(i, pt1.newInstance(Genotype.valueOf(chromosomes1)));
				population.set(pt2Index, pt2.newInstance(Genotype.valueOf(chromosomes2))); 
			}
		}
	}

	@SuppressWarnings("unchecked")
	static final XMLFormat<MeanAlterer> XML = new XMLFormat<MeanAlterer>(MeanAlterer.class) {
		@Override
		public MeanAlterer newInstance(final Class<MeanAlterer> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final double p = xml.getAttribute("probability", 0.5);
			final boolean hasAlterer = xml.getAttribute("has-alterer", false);
			MeanAlterer alterer = null;
			
			if (hasAlterer) {
				Alterer component = xml.getNext();
				alterer = new MeanAlterer(Probability.valueOf(p), component);
			} else {
				alterer = new MeanAlterer(Probability.valueOf(p));
			}
			
			return alterer;
		}
		@Override
		public void write(final MeanAlterer a, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("probability", a._probability.doubleValue());
			xml.setAttribute("has-alterer", a._component != null);
			if (a._component != null) {
				xml.add(a._component);
			}
		}
		@Override
		public void read(final InputElement xml, final MeanAlterer a) {
		}
	};
}


