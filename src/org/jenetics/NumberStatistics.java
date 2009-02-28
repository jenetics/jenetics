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

import static java.lang.Double.doubleToLongBits;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Float64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: NumberStatistics.java,v 1.2 2009-02-28 14:53:09 fwilhelm Exp $
 */
public class NumberStatistics<G extends Gene<?, G>, R extends Number & Comparable<R>>
	extends Statistics<G, R> 
{
	private static final long serialVersionUID = -7468324436514041360L;
	
	protected final double _fitnessMean;
	protected final double _fitnessVariance;
	
	protected NumberStatistics(
		final Phenotype<G, R> best, final Phenotype<G, R> worst, 
		final double fitnessMean, final double fitnessVariance,
		final int samples, final double ageMean, final double ageVariance
	) {
		super(best, worst, samples, ageMean, ageVariance);
		
		_fitnessMean = fitnessMean;
		_fitnessVariance = fitnessVariance;
	}
	
	protected NumberStatistics(
		final Statistics<G, R> other, 
		final double fitnessMean, final double fitnessVariance
	) {
		super(other);
		_fitnessMean = fitnessMean;
		_fitnessVariance = fitnessVariance;
	}

	public double getFitnessMean() {
		return _fitnessMean;
	}
	
	public double getFitnessVariance() {
		return _fitnessVariance;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode()*37;
		hash += (int)doubleToLongBits(_fitnessMean)*37;
		hash += (int)doubleToLongBits(_fitnessVariance)*37;
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof NumberStatistics)) {
			return false;
		}
		
		final NumberStatistics<?, ?> statistics = (NumberStatistics<?, ?>)obj;
		
		return 
			doubleToLongBits(statistics._fitnessMean) == doubleToLongBits(_fitnessMean) &&
			doubleToLongBits(statistics._fitnessVariance) == doubleToLongBits(_fitnessVariance);
	}
	
	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();

		out.append(super.toString() + "\n");
		out.append("Mean:            " + _fitnessMean + "\n");
		out.append("Variance:        " + _fitnessVariance);
		
		return out.toString();
	}
	
	@SuppressWarnings("unchecked")
	protected static final XMLFormat<NumberStatistics> XML = 
		new XMLFormat<NumberStatistics>(NumberStatistics.class) 
	{
		@Override
		public NumberStatistics newInstance(final Class<NumberStatistics> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final Statistics stats = Statistics.XML.newInstance(Statistics.class, xml);
			final Float64 fitnessMean = xml.get("fitness-mean");
			final Float64 fitnessVariance = xml.get("fitness-variance");
			
			return new NumberStatistics(
				stats, fitnessMean.doubleValue(), fitnessVariance.doubleValue()
			);

		}
		@Override
		public void write(final NumberStatistics s, final OutputElement xml) 
			throws XMLStreamException 
		{
			Statistics.XML.write(s, xml);
			xml.add(Float64.valueOf(s.getFitnessMean()), "fitness-mean");
			xml.add(Float64.valueOf(s.getFitnessVariance()), "fitness-variance");
		}
		@Override
		public void read(final InputElement xml, final NumberStatistics p) 
			throws XMLStreamException 
		{
		}
	};
	
}



















