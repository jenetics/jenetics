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
import javolution.lang.Reference;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.BitUtils;
import org.jscience.mathematics.number.Float64;

/**
 * Data object which holds performance indicators of a given {@link Population}.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Statistics.java,v 1.11 2010-01-14 14:53:10 fwilhelm Exp $
 */
public class Statistics<G extends Gene<?, G>, C extends Comparable<C>> 
	implements Immutable, XMLSerializable 
{
	private static final long serialVersionUID = -8980979460645713414L;
	
	protected final Phenotype<G, C> _best;
	protected final Phenotype<G, C> _worst;
	protected final int _samples;
	protected final double _ageMean;
	protected final double _ageVariance;
		
	private final Final<Time> _times = new Final<Time>(new Time());

	/**
	 * Evaluates statistic values from a given population. The given phenotypes
	 * may be {@code null}
	 * 
	 */ 
	protected Statistics(
		final Phenotype<G, C> best, final Phenotype<G, C> worst,
		final int samples, final double ageMean, final double ageVariance
	) {
		_best = best;
		_worst = worst;
		_samples = samples;
		_ageMean = ageMean;
		_ageVariance = ageVariance;
	}
	
	protected Statistics(final Statistics<G, C> other) {
		this(
			other.getBestPhenotype(), other.getWorstPhenotype(),
			other.getSamples(), other.getAgeMean(), other.getAgeVariance()
		);
	}
	
	/**
	 * Return the time statistic object which contains the durations of the
	 * different GA execution steps.
	 * 
	 * @return the time statistic object.
	 */
	public Time getTimes() {
		return _times.get();
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
	
	public int getSamples() {
		return _samples;
	}
	
	public double getAgeMean() {
		return _ageMean;
	}
	
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

		out.append("Samples:         ").append(_samples).append("\n");
		out.append("Best Phenotype:  ").append(getBestPhenotype()).append("\n");
		out.append("Worst Phenotype: ").append(getWorstPhenotype()).append("\n");
		
		return out.toString();
	}
	
	@SuppressWarnings("unchecked")
	protected static final XMLFormat<Statistics> XML = 
		new XMLFormat<Statistics>(Statistics.class) 
	{
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
			final int samples = xml.getAttribute(SAMPLES, 1);
			final Float64 meanAge = xml.get(AGE_MEAN);
			final Float64 varianceAge = xml.get(AGE_VARIANCE);
			final Phenotype best = xml.get(BEST_PHENOTYPE);
			final Phenotype worst = xml.get(WORST_PHENOTYPE);
			
			final Statistics statistics = new Statistics(
					best, worst, samples, 
					meanAge.doubleValue(), 
					varianceAge.doubleValue()
				);
			statistics._times.set(xml.get(STATISITCS_TIME));
			
			return statistics;

		}
		@Override
		public void write(final Statistics s, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(SAMPLES, s._samples);
			xml.add(Float64.valueOf(s._ageMean), AGE_MEAN);
			xml.add(Float64.valueOf(s._ageVariance), AGE_VARIANCE);
			xml.add(s._best, BEST_PHENOTYPE);
			xml.add(s._worst, WORST_PHENOTYPE);
			xml.add(s._times.get(), STATISITCS_TIME);
		}
		@Override
		public void read(final InputElement xml, final Statistics p) 
			throws XMLStreamException 
		{
		}
	};
	
	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id: Statistics.java,v 1.11 2010-01-14 14:53:10 fwilhelm Exp $
	 */
	public static class Calculator<G extends Gene<?, G>, C extends Comparable<C>> {
		protected long _startEvaluationTime = 0;
		protected long _stopEvaluationTime = 0;
		
		public Calculator() {
		}
		
		public Statistics<G, C> evaluate(final List<? extends Phenotype<G, C>> population) {
			_startEvaluationTime = System.currentTimeMillis();
			
			Statistics<G, C> statistic = new Statistics<G, C>(null, null, 0, 0.0, 0.0);
			final int size = population.size();
			
			Phenotype<G, C> best = null;
			Phenotype<G, C> worst = null;
			long ageSum = 0;
			long ageSquareSum = 0;
			int start = 0;
			
			if (size%2 == 0 && size > 0) {
				start = 2;
				if (population.get(0).compareTo(population.get(1)) < 0) {
					worst = population.get(0);
					best = population.get(1);
				} else {
					worst = population.get(1);
					best = population.get(0);
				}
				
				ageSum += best.getGeneration() + worst.getGeneration();
				ageSquareSum += best.getGeneration()*best.getGeneration();
				ageSquareSum += worst.getGeneration()*worst.getGeneration();
			} else if (size%2 == 1) {
				start = 1;
				worst = population.get(0);
				best = population.get(0);
				
				ageSum = best.getGeneration();
				ageSquareSum = best.getGeneration()*best.getGeneration();
			}
			
			for (int i = start; i < size; i += 2) {
				final Phenotype<G, C> first = population.get(i);
				final Phenotype<G, C> second = population.get(i + 1);
				
				if (first.compareTo(second) < 0) {
					if (first.compareTo(worst) < 0) {
						worst = first;
					}
					if (second.compareTo(best) > 0) {
						best = second;
					}
				} else {
					if (second.compareTo(worst) < 0) {
						worst = second;
					}
					if (first.compareTo(best) > 0) {
						best = first;
					}
				}
				
				assert best != null;
				assert worst != null;
				ageSum += best.getGeneration() + worst.getGeneration();
				ageSquareSum += best.getGeneration()*best.getGeneration();
				ageSquareSum += worst.getGeneration()*worst.getGeneration();
			}
			
			if (size > 0) {		
				final double meanAge = (double)ageSum/(double)size;
				final double varianceAge = (double)ageSquareSum/(double)size - meanAge*meanAge;
				
				statistic = new Statistics<G, C>(best, worst, size, meanAge, varianceAge);
			}
			
			_stopEvaluationTime = System.currentTimeMillis();
			return statistic;
		}
		
		public Measurable<Duration> getEvaluationTime() {
			return Measure.valueOf(_stopEvaluationTime - _startEvaluationTime, SI.MILLI(SI.SECOND));
		}

	}
	
	public static final class Time implements XMLSerializable {
		private static final long serialVersionUID = -4947801435156551911L;

		private static final Measurable<Duration> ZERO = Measure.valueOf(
				0, SI.MILLI(SI.SECOND)
			);
		
		/**
		 * The overall execution time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final Reference<Measurable<Duration>> 
			execution = new Final<Measurable<Duration>>(ZERO);
		
		/**
		 * The selection time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final Reference<Measurable<Duration>> 
			selection = new Final<Measurable<Duration>>(ZERO);
		
		/**
		 * The alter time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final Reference<Measurable<Duration>> 
			alter = new Final<Measurable<Duration>>(ZERO);
		
		/**
		 * The evaluation time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final Reference<Measurable<Duration>> 
			evaluation = new Final<Measurable<Duration>>(ZERO);
		
		/**
		 * The statistics time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final Reference<Measurable<Duration>> 
			statistics = new Final<Measurable<Duration>>(ZERO);
		
		
		@Override
		public int hashCode() {
			int hash = 17;
			hash += alter.hashCode()*37;
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
				evaluation.equals(time.evaluation) &&
				execution.equals(time.execution) &&
				selection.equals(time.selection) &&
				statistics.equals(time.statistics);
		}
		
		@SuppressWarnings("unchecked")
		protected static final XMLFormat<Statistics.Time> XML = 
			new XMLFormat<Statistics.Time>(Statistics.Time.class) 
		{
			private static final String ALTER_TIME = "alter-time";
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
	
	private static final class Final<T> implements Reference<T> {
		private T _value = null;
		private boolean _initialized = false;

		public Final(final T value) {
			_value = value;
		}
		
		@Override
		public void set(final T value) {
			if (_initialized) {
				throw new IllegalStateException("Value is already initialized.");
			}
			_value = value;
			_initialized = true;
		}
		
		@Override
		public T get() {
			return _value;
		}
		
		@Override
		public int hashCode() {
			return _value != null ? _value.hashCode() : 0;
		}
		
		@Override
		public boolean equals(final Object object) {
			if (object == this) {
				return true;
			}
			if (!(object instanceof Final<?>)) {
				return false;
			}
			
			final Final<?> f = (Final<?>)object;
			return f._value != null ? f._value.equals(_value) : _value == null;
		}
		
		@Override
		public String toString() {
			return _value != null ? _value.toString() : "null";
		}
		
	}
}



