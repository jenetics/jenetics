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

import javax.measure.Measurable;
import javax.measure.Measure;
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
 * @version $Id: Statistics.java,v 1.4 2009-02-28 14:53:09 fwilhelm Exp $
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
		hash += _best.hashCode()*37; 
		hash += _worst.hashCode()*37; 
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Statistics)) {
			return false;
		}
		
		final Statistics<?, ?> statistic = (Statistics<?, ?>)obj;
		
		return 
			doubleToLongBits(statistic._ageMean) == doubleToLongBits(_ageMean) &&
			doubleToLongBits(statistic._ageVariance) == doubleToLongBits(_ageVariance) &&
			 _best.equals(statistic._best) &&
			_worst.equals(statistic._worst);
	}
	
	public boolean equals(final Statistics<G, C> statistic, final int ulps) {
		if (statistic == this) {
			return true;
		}
		
		return 
			equals(statistic._ageMean, _ageMean, ulps) &&
			equals(statistic._ageVariance, _ageVariance, ulps) &&
			_best.equals(statistic._best) &&
			_worst.equals(statistic._worst);
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
		
		out.append("Samples:         " + _samples + "\n");
		out.append("Best Phenotype:  " + getBestPhenotype() + "\n");
		out.append("Worst Phenotype: " + getWorstPhenotype() + "\n");
		
		return out.toString();
	}
	
	@SuppressWarnings("unchecked")
	protected static final XMLFormat<Statistics> XML = new XMLFormat<Statistics>(Statistics.class) {
		@Override
		public Statistics newInstance(final Class<Statistics> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final Float64 meanAge = xml.get("meanage");
			final Float64 varianceAge = xml.get("varianceage");
			final int samples = xml.getAttribute("samples", 1);
			final Phenotype best = xml.get("best-phenotype");
			final Phenotype worst = xml.get("worst-phenotype");
			
			final Statistics statistics = new Statistics(
					best, worst, samples, 
					meanAge.doubleValue(), varianceAge.doubleValue()
				);
			statistics._times.set(xml.get("statistics-time"));
			
			return statistics;

		}
		@Override
		public void write(final Statistics s, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("samples", s._samples);
			xml.add(Float64.valueOf(s._ageMean), "age-mean");
			xml.add(Float64.valueOf(s._ageVariance), "age-variance");
			xml.add(s._best, "best-phenotype");
			xml.add(s._worst, "worst-phenotype");
			xml.add(s._times, "statisitcs-time");
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
			@Override
			public Statistics.Time newInstance(final Class<Statistics.Time> cls, final InputElement xml) 
				throws XMLStreamException 
			{
				final Statistics.Time time = new Statistics.Time();

				time.setAlterTime((Measurable<Duration>)xml.get("alter-time"));
				time.setEvaluationTime((Measurable<Duration>)xml.get("evaluation-time"));
				time.setExecutionTime((Measurable<Duration>)xml.get("execution-time"));
				time.setSelectionTime((Measurable<Duration>)xml.get("selection-time"));
				time.setStatisticTime((Measurable<Duration>)xml.get("statistics-time"));

				return time;

			}
			@Override
			public void write(final Statistics.Time s, final OutputElement xml) 
				throws XMLStreamException 
			{
				xml.add(s.getAlterTime(), "alter-time");
				xml.add(s.getEvaluationTime(), "evaluation-time");
				xml.add(s.getExecutionTime(), "execution-time");
				xml.add(s.getSelectionTime(), "selection-time");
				xml.add(s.getStatisticsTime(), "statistics-time");
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
		
		public Final() {
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



