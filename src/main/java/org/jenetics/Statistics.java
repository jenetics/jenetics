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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 	 
 */
package org.jenetics;

import static java.lang.Double.NaN;
import static java.lang.String.format;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.MeasureFormat;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.UnitFormat;

import javolution.lang.Immutable;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

import org.jenetics.stat.Variance;
import org.jenetics.util.accumulators;
import org.jenetics.util.accumulators.MinMax;
import org.jenetics.util.FinalReference;

/**
 * Data object which holds performance indicators of a given {@link Population}.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Statistics<G extends Gene<?, G>, C extends Comparable<? super C>> 
	implements 
		Immutable, 
		XMLSerializable 
{
	
	/**
	 * Builder for the Statistics class.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Builder<
		G extends Gene<?, G>, 
		C extends Comparable<? super C>
	> 
	{
		protected Optimize _optimize = Optimize.MAXIMUM;
		protected int _generation = 0;
		protected Phenotype<G, C> _best = null;
		protected Phenotype<G, C> _worst = null;
		protected int _samples = 0;
		protected double _ageMean = NaN;
		protected double _ageVariance = NaN;
		protected int _killed = 0;
		protected int _invalid = 0;
		
		/**
		 * Create a new Statistics builder.
		 */
		public Builder() {
		}
		
		/**
		 * Set the values of this builder with the values of the given 
		 * {@code statistics}.
		 * 
		 * @param statistics the statistics values. If the {@code statistics}
		 *        is {@code null} nothing is set.
		 * @return this builder.
		 */
		public Builder<G, C> statistics(final Statistics<G, C> statistics) {
			if (statistics != null) {
				_optimize = statistics._optimize;
				_generation = statistics._generation;
				_best = statistics._best;
				_worst = statistics._worst;
				_samples = statistics._samples;
				_ageMean = statistics._ageMean;
				_ageVariance = statistics._ageVariance;
				_killed = statistics._killed;
				_invalid = statistics._invalid;
			}
			return this;
		}
		
		public Builder<G, C> optimize(final Optimize optimize) {
			_optimize = nonNull(optimize, "Optimize strategy");
			return this;
		}
		
		/**
		 * @see Statistics#getGeneration()
		 */
		public Builder<G, C> generation(final int generation) {
			_generation = generation;
			return this;
		}
		
		/**
		 * @see Statistics#getBestPhenotype()
		 */
		public Builder<G, C> bestPhenotype(final Phenotype<G, C> best) {
			_best = best;
			return this;
		}
		
		/**
		 * @see Statistics#getWorstPhenotype()
		 */
		public Builder<G, C> worstPhenotype(final Phenotype<G, C> worst) {
			_worst = worst;
			return this;
		}
		
		/**
		 * @see Statistics#getSamples()
		 */
		public Builder<G, C> samples(final int samples) {
			_samples = samples;
			return this;
		}
		
		/**
		 * @see Statistics#getAgeMean()
		 */
		public Builder<G, C> ageMean(final double ageMean) {
			_ageMean = ageMean;
			return this;
		}
		
		/**
		 * @see Statistics#getAgeVariance()
		 */
		public Builder<G, C> ageVariance(final double ageVariance) {
			_ageVariance = ageVariance;
			return this;
		}
		
		/**
		 * @see Statistics#getInvalid()
		 */
		public Builder<G, C> invalid(final int invalid) {
			_invalid = invalid;
			return this;
		}
		
		/**
		 * @see Statistics#getKilled()
		 */
		public Builder<G, C> killed(final int killed) {
			_killed = killed;
			return this;
		}
		
		/**
		 * Return a new Statistics object with the builder values.
		 * 
		 * @return new Statistics object with the builder values.
		 */
		public Statistics<G, C> build() {
			return new Statistics<>(
					_optimize,
					_generation,
					_best,
					_worst,
					_samples,
					_ageMean,
					_ageVariance,
					_killed,
					_invalid
				);
		}
	}
	
	private static final long serialVersionUID = 2L;
		
	protected final Optimize _optimize;
	protected final int _generation;
	protected final Phenotype<G, C> _best;
	protected final Phenotype<G, C> _worst;
	protected final int _samples;
	protected final double _ageMean;
	protected final double _ageVariance;
	protected final int _killed;
	protected final int _invalid;
			
	private final FinalReference<Time> _time = new FinalReference<>(new Time());
	
	
	/**
	 * Evaluates statistic values from a given population. The given phenotypes
	 * may be {@code null}
	 */ 
	protected Statistics(
		final Optimize optimize,
		final int generation,
		final Phenotype<G, C> best, 
		final Phenotype<G, C> worst,
		final int samples, 
		final double ageMean, 
		final double ageVariance,
		final int killed,
		final int invalid
	) {
		_optimize = optimize;
		_generation = generation;
		_best = best;
		_worst = worst;
		_samples = samples;
		_ageMean = ageMean;
		_ageVariance = ageVariance;
		_killed = killed;
		_invalid = invalid;
	}
	
	/**
	 * Return the optimize strategy of the GA.
	 * 
	 * @return the optimize strategy of the GA.
	 */
	public Optimize getOptimize() {
		return _optimize;
	}
	
	/**
	 * Return the generation of this statistics.
	 * 
	 * @return the generation of this statistics.
	 */
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
		return _best != null ? _best.getFitness() : null;
	}
	
	/**
	 * Return the worst population fitness.
	 * 
	 * @return The worst population fitness.
	 */
	public C getWorstFitness() {
		return _worst != null ? _worst.getFitness() : null;
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
	
	/**
	 * Return the number of invalid individuals.
	 * 
	 * @return the number of invalid individuals.
	 */
	public int getInvalid() {
		return _invalid;
	}
	
	/**
	 * Return the number of killed individuals.
	 * 
	 * @return the number of killed individuals.
	 */
	public int getKilled() {
		return _killed;
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).
				and(_optimize).
				and(_generation).
				and(_ageMean).
				and(_ageVariance).
				and(_best).
				and(_worst).
				and(_invalid).
				and(_samples).
				and(_killed).value();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		final Statistics<?, ?> statistics = (Statistics<?, ?>)obj;
		return eq(_optimize, statistics._optimize) &&
				eq(_generation, statistics._generation) &&
 				eq(_ageMean, statistics._ageMean) &&
				eq(_ageVariance, statistics._ageVariance) &&
				eq(_best, statistics._best) &&
				eq(_worst, statistics._worst) &&
				eq(_invalid, statistics._invalid) &&
				eq(_samples, statistics._samples) &&
				eq(_killed, statistics._killed);
	}

	@Override
	public String toString() {
		final String spattern = "| %28s: %-26s|\n";
		final String fpattern = "| %28s: %-26.11f|\n";
		final String ipattern = "| %28s: %-26d|\n";
		
		final StringBuilder out = new StringBuilder();
		out.append("+---------------------------------------------------------+\n");
		out.append("|  Population Statistics                                  |\n");
		out.append("+---------------------------------------------------------+\n");
		out.append(format(fpattern, "Age mean", _ageMean));
		out.append(format(fpattern, "Age variance", _ageVariance));
		out.append(format(ipattern, "Samples", _samples));
		out.append(format(spattern, "Best fitness", getBestFitness()));
		out.append(format(spattern, "Worst fitness", getWorstFitness()));
		out.append("+---------------------------------------------------------+");
		
		return out.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static final XMLFormat<Statistics> XML = 
		new XMLFormat<Statistics>(Statistics.class) 
	{
		private static final String OPTIMIZE = "optimize";
		private static final String GENERATION = "generation";
		private static final String SAMPLES = "samples";
		private static final String AGE_MEAN = "age-mean";
		private static final String AGE_VARIANCE = "age-variance";
		private static final String BEST_PHENOTYPE = "best-phenotype";
		private static final String WORST_PHENOTYPE = "worst-phenotype";
		private static final String STATISITCS_TIME = "statistics-time";
		private static final String INVALID = "invalid";
		private static final String KILLED = "killed";
		
		@Override
		public Statistics newInstance(final Class<Statistics> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final Optimize optimize = Optimize.valueOf(
						xml.getAttribute(OPTIMIZE, Optimize.MAXIMUM.name())
					);
			final int generation = xml.getAttribute(GENERATION, 0);
			final int samples = xml.getAttribute(SAMPLES, 1);
			final Float64 meanAge = xml.get(AGE_MEAN);
			final Float64 varianceAge = xml.get(AGE_VARIANCE);
			final Integer64 invalid = xml.get(INVALID);
			final Integer64 killed = xml.get(KILLED);
			final Phenotype best = xml.get(BEST_PHENOTYPE);
			final Phenotype worst = xml.get(WORST_PHENOTYPE);
			
			final Statistics statistics = new Statistics(
					optimize,
					generation,
					best, 
					worst, 
					samples, 
					meanAge.doubleValue(), 
					varianceAge.doubleValue(),
					killed.intValue(),
					invalid.intValue()
				);
			statistics._time.set(xml.get(STATISITCS_TIME));
			
			return statistics;

		}
		@Override
		public void write(final Statistics s, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(OPTIMIZE, s._optimize.name());
			xml.setAttribute(GENERATION, s._generation);
			xml.setAttribute(SAMPLES, s._samples);
			xml.add(Float64.valueOf(s._ageMean), AGE_MEAN);
			xml.add(Float64.valueOf(s._ageVariance), AGE_VARIANCE);
			xml.add(Integer64.valueOf(s._invalid), INVALID);
			xml.add(Integer64.valueOf(s._killed), KILLED);
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
	 * @version $Id$
	 */
	public static final class Time implements XMLSerializable {
		private static final long serialVersionUID = 1L;

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
			execution = new FinalReference<>(ZERO);
		
		/**
		 * The selection time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			selection = new FinalReference<>(ZERO);
		
		/**
		 * The alter time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			alter = new FinalReference<>(ZERO);
		
		/**
		 * Combination time between offsprings and survivors.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			combine = new FinalReference<>(ZERO);
		
		/**
		 * The evaluation time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			evaluation = new FinalReference<>(ZERO);
		
		/**
		 * The statistics time.
		 * The time can be set only once, otherwise an IllegalArgumentException
		 * is thrown.
		 */
		public final FinalReference<Measurable<Duration>> 
			statistics = new FinalReference<>(ZERO);
		
		
		@Override
		public int hashCode() {
			return hashCodeOf(getClass()).
					and(alter).
					and(combine).
					and(evaluation).
					and(execution).
					and(selection).
					and(statistics).value();
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
			return eq(alter, time.alter) &&
					eq(combine, time.combine) &&
					eq(evaluation, time.evaluation) &&
					eq(execution, time.execution) &&
					eq(selection, time.selection) &&
					eq(statistics, time.statistics);
		}
		
		@Override
		public String toString() {
			final String pattern = "| %28s: %-26.11f|\n";
			
			final StringBuilder out = new StringBuilder();
			out.append("+---------------------------------------------------------+\n");
			out.append("|  Time Statistics                                        |\n");
			out.append("+---------------------------------------------------------+\n");
			out.append(format(pattern, "Select time", selection.get().doubleValue(SI.SECOND)));
			out.append(format(pattern, "Alter time", alter.get().doubleValue(SI.SECOND)));
			out.append(format(pattern, "Combine time", combine.get().doubleValue(SI.SECOND)));
			out.append(format(pattern, "Fitness calculation time", evaluation.get().doubleValue(SI.SECOND)));
			out.append(format(pattern, "Statistics calculation time", statistics.get().doubleValue(SI.SECOND)));
			out.append(format(pattern, "Overall execution time", execution.get().doubleValue(SI.SECOND)));
			out.append("+---------------------------------------------------------+");
			
			return out.toString();
		}
		
		@SuppressWarnings("unchecked")
		static final XMLFormat<Statistics.Time> XML = 
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
				final MeasureFormat format = getMeasureFormat();
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
				final MeasureFormat format = getMeasureFormat();

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
			
			private MeasureFormat getMeasureFormat() {
				final NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
				nf.setMinimumFractionDigits(25);
				final UnitFormat uf = UnitFormat.getInstance(Locale.ENGLISH);
				
				return MeasureFormat.getInstance(nf, uf);
			}
		};
	}
	
	
	/**
	 * Class for calculating the statistics. This class serves also as factory
	 * for the Statistics class.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Calculator<
		G extends Gene<?, G>, 
		C extends Comparable<? super C>
	>
	{
		
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
		 * @param opt the optimization <i>direction</i>.
		 * @return a new statistics object generated from the given arguments.
		 */
		public Statistics.Builder<G, C> evaluate(
			final Iterable<? extends Phenotype<G, C>> population,
			final int generation,
			final Optimize opt
		) {	
			final Builder<G, C> builder = new Builder<>();
			builder.generation(generation);
			builder.optimize(opt);
			
			final MinMax<Phenotype<G, C>> minMax = new MinMax<>();
			final Variance<Integer> age = new Variance<>();
							
			accumulators.<Phenotype<G, C>>accumulate(
					population, 
					minMax, 
					age.map(Phenotype.Age(generation))
				);
			
			builder.bestPhenotype(opt.best(minMax.getMax(), minMax.getMin()));
			builder.worstPhenotype(opt.worst(minMax.getMax(), minMax.getMin()));
			builder.samples((int)minMax.getSamples());
			builder.ageMean(age.getMean());
			builder.ageVariance(age.getVariance());
			
			return builder;
		}

	}
	
}



