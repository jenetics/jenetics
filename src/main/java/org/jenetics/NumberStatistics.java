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

import static java.lang.Double.NaN;
import static java.lang.Double.doubleToLongBits;
import static java.lang.String.format;

import java.util.List;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.AccumulatorAdapter;
import org.jenetics.util.Accumulators;
import org.jenetics.util.Converter;
import org.jenetics.util.Accumulators.MinMax;
import org.jenetics.util.Accumulators.Variance;
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
	protected final double _standardError;
	
	protected NumberStatistics(final int generation) {
		this(generation, null, null, NaN, NaN, 0, NaN, NaN, NaN);
	}
	
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
		_standardError = errorOfMean;
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
		_standardError = errorOfMean;
	}

	public double getFitnessMean() {
		return _fitnessMean;
	}
	
	public double getFitnessVariance() {
		return _fitnessVariance;
	}
	
	public double getStandardError() {
		return _standardError;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode()*31 + 17;
		hash += (int)doubleToLongBits(_fitnessMean)*31 + 17;
		hash += (int)doubleToLongBits(_fitnessVariance)*31 + 17;
		hash += (int)doubleToLongBits(_standardError)*31 + 17;
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
			doubleToLongBits(statistics._standardError) == doubleToLongBits(_standardError);
	}
	
	@Override
	public String toString() {
		final String fpattern = "| %28s: %-26.11f|\n";
		
		final StringBuilder out = new StringBuilder();
		out.append(super.toString()).append("\n");
		out.append("+---------------------------------------------------------+\n");
		out.append("|  Fitness Statistics                                     |\n");
		out.append("+---------------------------------------------------------+\n");
		out.append(format(fpattern, "Fitness mean", _fitnessMean));
		out.append(format(fpattern, "Fitness variance", _fitnessVariance));
		out.append(format(fpattern, "Fitness error of mean", _standardError));
		out.append("+---------------------------------------------------------+");
		
		return out.toString();
	}
	
	@SuppressWarnings({ "unchecked" })
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
			xml.add(Float64.valueOf(s.getStandardError()), ERROR_OF_MEAN);
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
			final int generation,
			final Optimize opt
		) {
			NumberStatistics<G, R> statistics = new NumberStatistics<G, R>(generation);
			
			if (!population.isEmpty()) {
				// The properties we accumulate.
				final Converter<Phenotype<G, R>, Integer> age = Phenotype.age(generation);
				final Converter<Phenotype<G, R>, R> fitness = Phenotype.fitness();
				
				// The statistics accumulators.
				final MinMax<Phenotype<G, R>> minMax = new MinMax<Phenotype<G, R>>();
				final Variance<Integer> ageVariance = new Variance<Integer>();
				final Variance<R> fitnessVariance = new Variance<R>();
								
				Accumulators.accumulate(
						population, 
						minMax, 
						AccumulatorAdapter.valueOf(ageVariance, age),
						AccumulatorAdapter.valueOf(fitnessVariance, fitness)
					);

				statistics = new NumberStatistics<G, R>(
						generation,
						(opt == Optimize.MAXIMUM) ? minMax.getMax() : minMax.getMin(),
						(opt == Optimize.MAXIMUM) ? minMax.getMin() : minMax.getMax(),
						fitnessVariance.getMean(),
						fitnessVariance.getVariance(),
						population.size(),
						ageVariance.getMean(),
						ageVariance.getVariance(),
						fitnessVariance.getStandardError()
					);
			}
			
			return statistics;
		}
	}
	
//	public static <G extends Gene<?, G>, R extends Number & Comparable<R>> 
//	Calculator<G, R> calculator() {
//		return new Calculator<G, R>();
//	}
	
}



















