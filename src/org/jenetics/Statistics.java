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
import org.jenetics.util.Validator;
import org.jscience.mathematics.number.Float64;

/**
 * Data object which holds performance indicators of a given {@link Population}.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Statistics.java,v 1.8 2009-07-02 17:47:57 fwilhelm Exp $
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
	 * Evaluates statistic valus from a givem population. The given pheontypes
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
	
	
	public static final class Time implements XMLSerializable {
		private static final long serialVersionUID = -4947801435156551911L;

		private static final Measurable<Duration> ZERO = Measure.valueOf(
				0, SI.MILLI(SI.SECOND)
			);
		
		private final Final<Measurable<Duration>> 
		_executionTime = new Final<Measurable<Duration>>(ZERO);
		
		private final Final<Measurable<Duration>> 
		_selectionTime = new Final<Measurable<Duration>>(ZERO);
		
		private final Final<Measurable<Duration>> 
		_alterTime = new Final<Measurable<Duration>>(ZERO);
		
		private final Final<Measurable<Duration>> 
		_evaluationTime = new Final<Measurable<Duration>>(ZERO);
		
		private final Final<Measurable<Duration>> 
		_statisticTime = new Final<Measurable<Duration>>(ZERO);
		
		
		/**
		 * Set the overall execution time.
		 * 
		 * @param time the overall execution time.
		 * @throws NullPointerException if the given {@code time} is {@code null}.
		 * @throws IllegalStateException if you try to set the {@code time}
		 *         twice.
		 */
		public void setExecutionTime(final Measurable<Duration> time) {
			Validator.notNull(time, "Execution time");
			_executionTime.set(time);
		}
		
		/**
		 * Return the overall execution time.
		 * 
		 * @return the overall execution time.
		 */
		public Measurable<Duration> getExecutionTime() {
			return _executionTime.get();
		}
		
		/**
		 * Set the time needed for selecting the survivors and offsprings.
		 * 
		 * @param time the selection time.
		 * @throws NullPointerException if the given {@code time} is {@code null}.
		 * @throws IllegalStateException if you try to set the {@code time}
		 *         twice.
		 */
		public void setSelectionTime(final Measurable<Duration> time) {
			Validator.notNull(time, "Selection time");
			_selectionTime.set(time);
		}
		
		/**
		 * Return the time needed for selecting the survivors and offsprings.
		 * 
		 * @return the selection time.
		 */
		public Measurable<Duration> getSelectionTime() {
			return _selectionTime.get();
		}
		
		/**
		 * Set the time needed for altering the population.
		 *
		 * @param time the alter time.
		 * @throws NullPointerException if the given {@code time} is {@code null}.
		 * @throws IllegalStateException if you try to set the {@code time}
		 *         twice.
		 */
		public void setAlterTime(final Measurable<Duration> time) {
			Validator.notNull(time, "Alter time");
			_alterTime.set(time);
		}
		
		/**
		 * Return the time needed for altering the population.
		 * 
		 * @return the alter time.
		 */
		public Measurable<Duration> getAlterTime() {
			return _alterTime.get();
		}
		
		/**
		 * Set the time needed for evaluating the fitness function.
		 * 
		 * @param time the fitness function evaluation time.
		 * @throws NullPointerException if the given {@code time} is {@code null}.
		 * @throws IllegalStateException if you try to set the {@code time}
		 *         twice.
		 */
		public void setEvaluationTime(final Measurable<Duration> time) {
			Validator.notNull(time, "Evaluation time");
			_evaluationTime.set(time);
		}
		
		/**
		 * Return the time needed for evaluating the fitness function.
		 * 
		 * @return the fitness function evaluation time.
		 */
		public Measurable<Duration> getEvaluationTime() {
			return _evaluationTime.get();
		}
		
		/**
		 * Set the time needed for evaluating the statistic.
		 * 
		 * @param time the statistic evaluation time.
		 * @throws NullPointerException if the given {@code time} is {@code null}.
		 * @throws IllegalStateException if you try to set the {@code time}
		 *         twice.
		 */
		public void setStatisticTime(final Measurable<Duration> time) {
			Validator.notNull(time, "Statistic time");
			_statisticTime.set(time);
		}
		
		/**
		 * Return the time needed for evaluating the statisitc function.
		 * 
		 * @return the statisitc evaluation time.
		 */
		public Measurable<Duration> getStatisticsTime() {
			return _statisticTime.get();
		}
		
		@Override
		public int hashCode() {
			int hash = 17;
			hash += getAlterTime().hashCode()*37;
			hash += getEvaluationTime().hashCode()*37;
			hash += getExecutionTime().hashCode()*37;
			hash += getSelectionTime().hashCode()*37;
			hash += getStatisticsTime().hashCode()*37;
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
				getAlterTime().equals(time.getAlterTime()) &&
				getEvaluationTime().equals(time.getEvaluationTime()) &&
				getExecutionTime().equals(time.getExecutionTime()) &&
				getSelectionTime().equals(time.getSelectionTime()) &&
				getStatisticsTime().equals(time.getStatisticsTime());
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
					time.setAlterTime((Measurable<Duration>)format.parseObject(
							(String)xml.get(ALTER_TIME)
						));
					time.setEvaluationTime((Measurable<Duration>)format.parseObject(
							(String)xml.get(EVALUATION_TIME)
						));
					time.setExecutionTime((Measurable<Duration>)format.parseObject(
							(String)xml.get(EXECUTION_TIME)
						));
					time.setSelectionTime((Measurable<Duration>)format.parseObject(
							(String)xml.get(SELECTION_TIME)
						));
					time.setStatisticTime((Measurable<Duration>)format.parseObject(
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
				
				xml.add(format.format(s.getAlterTime()), ALTER_TIME);
				xml.add(format.format(s.getEvaluationTime()), EVALUATION_TIME);
				xml.add(format.format(s.getExecutionTime()), EXECUTION_TIME);
				xml.add(format.format(s.getSelectionTime()), SELECTION_TIME);
				xml.add(format.format(s.getStatisticsTime()), STATISTICS_TIME);
			}
			@Override
			public void read(final InputElement xml, final Statistics.Time p) 
				throws XMLStreamException 
			{
			}
		};
	}
	
	private static final class Final<T> {
		private T _value = null;
		private boolean _initialized = false;

		public Final(final T value) {
			_value = value;
		}
		
		public void set(final T value) {
			if (_initialized) {
				throw new IllegalStateException("Value is already initialized.");
			}
			_value = value;
			_initialized = true;
		}
		
		public T get() {
			return _value;
		}
		
	}
}



