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

import java.text.ParseException;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.MeasureFormat;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import javolution.lang.Immutable;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.BitUtils;
import org.jenetics.util.FinalReference;
import org.jscience.mathematics.number.Float64;

/**
 * Data object which holds performance indicators of a given {@link Population}.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Statistics.java,v 1.23 2010-01-30 14:41:07 fwilhelm Exp $
 */
public class Statistics<G extends Gene<?, G>, C extends Comparable<C>> 
	implements Immutable, XMLSerializable 
{
	private static final long serialVersionUID = -8980979460645713414L;
	
	protected final int _generation;
	protected final Phenotype<G, C> _best;
	protected final Phenotype<G, C> _worst;
	protected final int _samples;
	protected final double _ageMean;
	protected final double _ageVariance;
		
	private final FinalReference<Time> _time = new FinalReference<Time>(new Time());

	/**
	 * Evaluates statistic values from a given population. The given phenotypes
	 * may be {@code null}
	 */ 
	protected Statistics(
		final int generation,
		final Phenotype<G, C> best, 
		final Phenotype<G, C> worst,
		final int samples, 
		final double ageMean, 
		final double ageVariance
	) {
		_generation = generation;
		_best = best;
		_worst = worst;
		_samples = samples;
		_ageMean = ageMean;
		_ageVariance = ageVariance;
	}
	
	protected Statistics(final Statistics<G, C> other) {
		this(
			other.getGeneration(),
			other.getBestPhenotype(), other.getWorstPhenotype(),
			other.getSamples(), other.getAgeMean(), other.getAgeVariance()
		);
	}
	
	public int getGeneration() {
		return _generation;
	}
	
	/**
	 * Return the time statistic object which contains the durations of the
	 * different GA execution steps.
	 * 
	 * @return the time statistic object.
	 */
	public Time getTime() {
		return _time.get();
	}
	
	/**
	 * Return the best population Phenotype.
	 * 
	 * @return The best population Phenotype.
	 */
	public Phenotype<G, C> getBestPhenotype() {
		return _best;
	}
	
	/**
	 * Return the worst population Phenotype.
	 * 
	 * @return The worst population Phenotype.
	 */
	public Phenotype<G, C> getWorstPhenotype() {
		return _worst;
	}
	
	/**
	 * Return the best population fitness.
	 * 
	 * @return The best population fitness.
	 */
	public C getBestFitness() {
		C fitness = null;
		if (_best != null) {
			fitness = _best.getFitness();
		}
		return fitness; 
	}
	
	/**
	 * Return the worst population fitness.
	 * 
	 * @return The worst population fitness.
	 */
	public C getWorstFitness() {
		C fitness = null;
		if (_worst != null) {
			fitness = _worst.getFitness();
		}
		return fitness;
	}
	
	/**
	 * Return the number of samples this statistics has aggregated.
	 * 
	 * @return the number of samples this statistics has aggregated.
	 */
	public int getSamples() {
		return _samples;
	}
	
	/**
	 * Return the average (mean) age of the individuals of the aggregated 
	 * population.
	 * 
	 * @return the average population age.
	 */
	public double getAgeMean() {
		return _ageMean;
	}
	
	/**
	 * Return the age variance of the individuals of the aggregated population.
	 * 
	 * @return the age variance of the individuals of the aggregated population.
	 */
	public double getAgeVariance() {
		return _ageVariance;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash += (int)doubleToLongBits(_ageMean)*37;
		hash += (int)doubleToLongBits(_ageVariance)*37;
		hash += _best != null ?_best.hashCode()*37 : 3; 
		hash += _worst != null ? _worst.hashCode()*37 : 3; 
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Statistics<?, ?>)) {
			return false;
		}
		
		final Statistics<?, ?> statistics = (Statistics<?, ?>)obj;
		
		return 
			doubleToLongBits(statistics._ageMean) == doubleToLongBits(_ageMean) &&
			doubleToLongBits(statistics._ageVariance) == doubleToLongBits(_ageVariance) &&
			_best != null ? _best.equals(statistics._best) : statistics._best == null &&
			_worst != null ?_worst.equals(statistics._worst) : statistics._worst == null;
	}
	
	public boolean equals(final Statistics<G, C> statistics, final int ulps) {
		return statistics == this ||
			   (equals(statistics._ageMean, _ageMean, ulps) &&
				equals(statistics._ageVariance, _ageVariance, ulps) &&
				_best != null ? _best.equals(statistics._best) : statistics._best == null &&
				_worst != null ? _worst.equals(statistics._worst) : statistics._worst == null);
		
	}
	
	static boolean equals(final double a, final double b, final int ulpDistance) {
		if (Double.isNaN(a) || Double.isNaN(b)) {
			return false;
		}
		
		boolean equals = false;
		try {
			equals = Math.abs(BitUtils.ulpDistance(a, b)) <= ulpDistance;
		} catch (ArithmeticException e) {}
		return equals;
	}

	
	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();

		append(out, "Age mean", _ageMean, "");
		append(out, "Age variance", _ageVariance, "");
		append(out, "Samples", Integer.toString(_samples));
		append(out, "Best fitness", getBestFitness().toString());
		append(out, "Worst fitness", getWorstFitness().toString());
		
		out.append("Best Phenotype:  ").append("(Generation " + Integer.toString(_generation) + "):" + 
				getBestPhenotype()).append("\n");
		out.append("Worst Phenotype: ").append("(Generation " + Integer.toString(_generation) + "):" +
				getWorstPhenotype());
		
		return out.toString();
	}
	
	static void append(
		final StringBuilder out, 
		final String name, 
		final double value,
		final String unit
	) {
		out.append(String.format("%30s: %20.11f %s \n", name, value, unit));
	}
	
	static void append(
		final StringBuilder out, 
		final String name, 
		final String value
	) {
		out.append(String.format("%30s: %20s   \n", name, value));
	}
	
	@SuppressWarnings("unchecked")
	protected static final XMLFormat<Statistics> XML = 
		new XMLFormat<Statistics>(Statistics.class) 
	{
		private static final String GENERATION = "generation";
		private static final String SAMPLES = "samples";
		private static final String AGE_MEAN = "age-mean";
		private static final String AGE_VARIANCE = "age-variance";
		private static final String BEST_PHENOTYPE = "best-phenotype";
		private static final String WORST_PHENOTYPE = "worst-phenotype";
		private static final String STATISITCS_TIME = "statistics-time";
		
		@Override
		public Statistics newInstance(final Class<Statistics> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final int generation = xml.getAttribute(GENERATION, 0);
			final int samples = xml.getAttribute(SAMPLES, 1);
			final Float64 meanAge = xml.get(AGE_MEAN);
			final Float64 varianceAge = xml.get(AGE_VARIANCE);
			final Phenotype best = xml.get(BEST_PHENOTYPE);
			final Phenotype worst = xml.get(WORST_PHENOTYPE);
			
			final Statistics statistics = new Statistics(
					generation,
					best, worst, samples, 
					meanAge.doubleValue(), 
					varianceAge.doubleValue()
				);
			statistics._time.set(xml.get(STATISITCS_TIME));
			
			return statistics;

		}
		@Override
		public void write(final Statistics s, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(GENERATION, s._generation);
			xml.setAttribute(SAMPLES, s._samples);
			xml.add(Float64.valueOf(s._ageMean), AGE_MEAN);
			xml.add(Float64.valueOf(s._ageVariance), AGE_VARIANCE);
			xml.add(s._best, BEST_PHENOTYPE);
			xml.add(s._worst, WORST_PHENOTYPE);
			xml.add(s._time.get(), STATISITCS_TIME);
		}
		@Override
		public void read(final InputElement xml, final Statistics p) 
			throws XMLStreamException 
		{
		}
	};
	
	
	/**
	 * Class which holds time statistic values.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id: Statistics.java,v 1.23 2010-01-30 14:41:07 fwilhelm Exp $
	 */
	public static final class Time implements XMLSerializable {
		private static final long serialVersionUID = -4947801435156551911L;

		private static final Measurable<Duration> ZERO = Measure.valueOf(
				0, SI.MILLI(SI.SECOND)
			);
		
		/**
		 * Create a new time object with zero time values. The time references
		 * can only be set once. If you try to set the values twice an
		 * {@link IllegalStateException} is thrown.
		 */
		public Time() {
		}
		
		/**
		 * The overall execution time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			execution = new FinalReference<Measurable<Duration>>(ZERO);
		
		/**
		 * The selection time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			selection = new FinalReference<Measurable<Duration>>(ZERO);
		
		/**
		 * The alter time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			alter = new FinalReference<Measurable<Duration>>(ZERO);
		
		/**
		 * Combination time between offsprings and survivors.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			combine = new FinalReference<Measurable<Duration>>(ZERO);
		
		/**
		 * The evaluation time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			evaluation = new FinalReference<Measurable<Duration>>(ZERO);
		
		/**
		 * The statistics time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			statistics = new FinalReference<Measurable<Duration>>(ZERO);
		
		
		@Override
		public int hashCode() {
			int hash = 17;
			hash += alter.hashCode()*37;
			hash += combine.hashCode()*37;
			hash += evaluation.hashCode()*37;
			hash += execution.hashCode()*37;
			hash += selection.hashCode()*37;
			hash += statistics.hashCode()*37;
			return hash;
		}
		
		@Override
		public boolean equals(final Object object) {
			if (object == this) {
				return true;
			}
			if (object == null || object.getClass() != getClass()) {
				return false;
			}
			
			final Statistics.Time time = (Statistics.Time)object;
			return 
				alter.equals(time.alter) &&
				combine.equals(time.combine) &&
				evaluation.equals(time.evaluation) &&
				execution.equals(time.execution) &&
				selection.equals(time.selection) &&
				statistics.equals(time.statistics);
		}
		
		@Override
		public String toString() {
			final StringBuilder out = new StringBuilder();
			
			append(out, "Select time", selection.get().doubleValue(SI.SECOND), " s");
			append(out, "Alter time", alter.get().doubleValue(SI.SECOND), " s");
			append(out, "Combine time", combine.get().doubleValue(SI.SECOND), " s");
			append(out, "Fitness calculation time", evaluation.get().doubleValue(SI.SECOND), " s");
			append(out, "Statistics calculation time", statistics.get().doubleValue(SI.SECOND), " s");
			append(out, "Overall execution time", execution.get().doubleValue(SI.SECOND), " s");
			
			return out.toString();
		}
		
		@SuppressWarnings("unchecked")
		protected static final XMLFormat<Statistics.Time> XML = 
			new XMLFormat<Statistics.Time>(Statistics.Time.class) 
		{
			private static final String ALTER_TIME = "alter-time";
			private static final String COMBINE_TIME = "combine-time";
			private static final String EVALUATION_TIME = "evaluation-time";
			private static final String EXECUTION_TIME = "execution-time";
			private static final String SELECTION_TIME = "selection-time";
			private static final String STATISTICS_TIME = "statistics-time";
			
			@Override
			public Statistics.Time newInstance(
				final Class<Statistics.Time> cls, final InputElement xml
			) 
				throws XMLStreamException 
			{
				final MeasureFormat format = MeasureFormat.getInstance();
				final Statistics.Time time = new Statistics.Time();

				try {
					time.alter.set((Measurable<Duration>)format.parseObject(
							(String)xml.get(ALTER_TIME)
						));
					time.combine.set((Measurable<Duration>)format.parseObject(
							(String)xml.get(COMBINE_TIME)
						));					
					time.evaluation.set((Measurable<Duration>)format.parseObject(
							(String)xml.get(EVALUATION_TIME)
						));
					time.execution.set((Measurable<Duration>)format.parseObject(
							(String)xml.get(EXECUTION_TIME)
						));
					time.selection.set((Measurable<Duration>)format.parseObject(
							(String)xml.get(SELECTION_TIME)
						));
					time.statistics.set((Measurable<Duration>)format.parseObject(
							(String)xml.get(STATISTICS_TIME)
						));
				} catch (ParseException e) {
					throw new XMLStreamException(e);
				}
				return time;

			}
			@Override
			public void write(final Statistics.Time s, final OutputElement xml) 
				throws XMLStreamException 
			{
				final MeasureFormat format = MeasureFormat.getInstance();
				
				xml.add(format.format(s.alter.get()), ALTER_TIME);
				xml.add(format.format(s.combine.get()), COMBINE_TIME);
				xml.add(format.format(s.evaluation.get()), EVALUATION_TIME);
				xml.add(format.format(s.execution.get()), EXECUTION_TIME);
				xml.add(format.format(s.selection.get()), SELECTION_TIME);
				xml.add(format.format(s.statistics.get()), STATISTICS_TIME);
			}
			@Override
			public void read(final InputElement xml, final Statistics.Time p) 
				throws XMLStreamException 
			{
			}
		};
	}
	
	
	/**
	 * Class for calculating the statistics. This class serves also as factory
	 * for the Statistics class.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id: Statistics.java,v 1.23 2010-01-30 14:41:07 fwilhelm Exp $
	 */
	public static class Calculator<G extends Gene<?, G>, C extends Comparable<C>> {
		
		/**
		 * Create a new calculator object.
		 */
		public Calculator() {
		}
		
		/**
		 * Create a new statistics object from the given {@code population} at
		 * the given {@code generation}.
		 * 
		 * @param population the population to aggregate.
		 * @param generation the current GA generation.
		 * @return a new statistics object generated from the given arguments.
		 */
		public Statistics<G, C> evaluate(
			final List<? extends Phenotype<G, C>> population,
			final int generation
		) {	
			Statistics<G, C> statistic = new Statistics<G, C>(
					generation, null, null, 0, 0.0, 0.0
				);
			
			if (!population.isEmpty()) {	
				final int size = population.size();
				final double N = size;
				
				Phenotype<G, C> best = population.get(0);
				Phenotype<G, C> worst = population.get(0);
				
				double sum = 0;
				for (int i = 0; i < size; ++i) {
					final Phenotype<G, C> pt = population.get(i);
					
					// Finding best/worst phenotype
					if (pt.compareTo(worst) < 0) {
						worst = pt;
					}
					if (pt.compareTo(best) > 0) {
						best = pt;
					}
					
					sum += pt.getAge(generation);
				}
				
				final double mean = sum/N;
	
				sum = 0;
				for (int i = 0; i < size; ++i) {
					final Phenotype<G, C> pt = population.get(i);
					final double diff = pt.getAge(generation) - mean;
					 sum += diff*diff; 
				}
				
				final double variance = N > 1 ? sum/(N - 1) : sum;
			
				
				statistic = new Statistics<G, C>(generation, best, worst, size, mean, variance);
			}
			
			return statistic;
		}

	}
	
	
}



