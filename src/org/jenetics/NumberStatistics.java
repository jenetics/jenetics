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
import static java.lang.Math.sqrt;

import java.util.List;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Float64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class NumberStatistics<G extends Gene<?, G>, R extends Number & Comparable<R>>
	extends Statistics<G, R> 
{
	private static final long serialVersionUID = -7468324436514041360L;
	
	protected final double _fitnessMean;
	protected final double _fitnessVariance;
	protected final double _errorOfMean;
	
	protected NumberStatistics(
		final int generation,
		final Phenotype<G, R> best, 
		final Phenotype<G, R> worst, 
		final double fitnessMean, 
		final double fitnessVariance,
		final int samples, 
		final double ageMean, 
		final double ageVariance,
		final double errorOfMean
	) {
		super(generation, best, worst, samples, ageMean, ageVariance);
		
		_fitnessMean = fitnessMean;
		_fitnessVariance = fitnessVariance;
		_errorOfMean = errorOfMean;
	}
	
	protected NumberStatistics(
		final Statistics<G, R> other, 
		final double fitnessMean, 
		final double fitnessVariance,
		final double errorOfMean
	) {
		super(other);
		_fitnessMean = fitnessMean;
		_fitnessVariance = fitnessVariance;
		_errorOfMean = errorOfMean;
	}

	public double getFitnessMean() {
		return _fitnessMean;
	}
	
	public double getFitnessVariance() {
		return _fitnessVariance;
	}
	
	public double getErrorOfMean() {
		return _errorOfMean;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode()*31 + 17;
		hash += (int)doubleToLongBits(_fitnessMean)*31 + 17;
		hash += (int)doubleToLongBits(_fitnessVariance)*31 + 17;
		hash += (int)doubleToLongBits(_errorOfMean)*31 + 17;
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof NumberStatistics<?, ?>)) {
			return false;
		}
		
		final NumberStatistics<?, ?> statistics = (NumberStatistics<?, ?>)obj;
		
		return 
			doubleToLongBits(statistics._fitnessMean) == doubleToLongBits(_fitnessMean) &&
			doubleToLongBits(statistics._fitnessVariance) == doubleToLongBits(_fitnessVariance) &&
			doubleToLongBits(statistics._errorOfMean) == doubleToLongBits(_errorOfMean);
	}
	
	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();

		out.append(super.toString()).append("\n");
		out.append("Mean:            ").append(_fitnessMean).append("\n");
		out.append("Variance:        ").append(_fitnessVariance).append("\n");
		out.append("Error of mean:   ").append(_errorOfMean);
		
		return out.toString();
	}
	
	@SuppressWarnings("unchecked")
	static final XMLFormat<NumberStatistics> 
	XML = new XMLFormat<NumberStatistics>(NumberStatistics.class) 
	{
		private static final String FITNESS_MEAN = "fitness-mean";
		private static final String FITNESS_VARIANCE = "fitness-variance";
		private static final String ERROR_OF_MEAN = "error-of-mean";
		
		@Override
		public NumberStatistics newInstance(final Class<NumberStatistics> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final Statistics stats = Statistics.XML.newInstance(Statistics.class, xml);
			final Float64 fitnessMean = xml.get(FITNESS_MEAN);
			final Float64 fitnessVariance = xml.get(FITNESS_VARIANCE);
			final Float64 errorOfMean = xml.get(ERROR_OF_MEAN);
			
			return new NumberStatistics(
				stats, fitnessMean.doubleValue(), 
				fitnessVariance.doubleValue(),
				errorOfMean.doubleValue()				
			);

		}
		@Override
		public void write(final NumberStatistics s, final OutputElement xml) 
			throws XMLStreamException 
		{
			Statistics.XML.write(s, xml);
			xml.add(Float64.valueOf(s.getFitnessMean()), FITNESS_MEAN);
			xml.add(Float64.valueOf(s.getFitnessVariance()), FITNESS_VARIANCE);
			xml.add(Float64.valueOf(s.getErrorOfMean()), ERROR_OF_MEAN);
		}
		@Override
		public void read(final InputElement xml, final NumberStatistics p) 
			throws XMLStreamException 
		{
		}
	};
	
	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Calculator<G extends Gene<?, G>, R extends Number & Comparable<R>>
		extends Statistics.Calculator<G, R>
	{

		public Calculator() {
		}
		
		@Override
		public NumberStatistics<G, R> evaluate(
			final List<? extends Phenotype<G, R>> population,
			final int generation
		) {
			final Statistics<G, R> s = super.evaluate(population, generation);
			NumberStatistics<G, R> statistics = new NumberStatistics<G, R>(s, 0, 0, 0);
			
			if (!population.isEmpty()) {
				final int size = population.size();
				final double N = population.size();
				
				double sum = 0;
				for (int i = 0; i < size; ++i) {
					final Phenotype<G, R> phenotype = population.get(i); 
					final double fitness = phenotype.getFitness().doubleValue();
					sum += fitness;
				}
				
				final double mean = sum/N;
				
				sum = 0;
				for (int i = 0; i < size; ++i) {
					final Phenotype<G, R> phenotype = population.get(i); 
					final double fitness = phenotype.getFitness().doubleValue();
					final double diff = fitness - mean;
					sum += diff*diff;
				}
				
				final double n = N > 1 ? N - 1 : 1.0;
				final double variance = sum/n;
				final double error = sqrt(variance/n);
				
				statistics = new NumberStatistics<G, R>(s, mean, variance, error);
			}
			
			return statistics;
		}

	}
	
}



















