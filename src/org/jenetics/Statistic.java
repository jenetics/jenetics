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
import static org.jenetics.Checker.checkNull;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * Data object which holds performance indicators of a given {@link Population}.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Statistic.java,v 1.3 2008-04-22 15:25:36 fwilhelm Exp $
 */
public class Statistic<T extends Gene<?>> implements XMLSerializable {
	private static final long serialVersionUID = -8980979460645713414L;
	
	private final Phenotype<T> _bestPhenotype;
	private final Phenotype<T> _worstPhenotype;
	
	private final double _fitnessMean;
	private final double _fitnessVariance;
	private final double _ageMean;
	private final double _ageVariance;
	
	private int _samples = 0;
	private double _fitnessSum = 0.0;
	private double _fitnessSquareSum = 0.0;
	private long _ageSum = 0;
	private long _ageSquareSum = 0;

	/**
	 * Evaluates statistic valus from a givem population
	 * 
	 * @param population The population to calculate the statistic.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */ 
	public Statistic(
		final Phenotype<T> best, final Phenotype<T> worst, 
		final double fitnessMean, final double fitnessVariance,
		final double ageMean, final double ageVariance
	) {
		checkNull(best, "Best phenotype");
		checkNull(worst, "Worst phenotype");
		
		this._bestPhenotype = best;
		this._worstPhenotype = worst;
		this._fitnessMean = fitnessMean;
		this._fitnessVariance = fitnessVariance;
		this._ageMean = ageMean;
		this._ageVariance = ageVariance;
	}
	
	/**
	 * Return the best population Phenotype.
	 * 
	 * @return The best population Phenotype.
	 */
	public Phenotype<T> getBestPhenotype() {
		return _bestPhenotype;
	}
	
	/**
	 * Return the worst population Phenotype.
	 * 
	 * @return The worst population Phenotype.
	 */
	public Phenotype<T> getWorstPhenotype() {
		return _worstPhenotype;
	}
	
	/**
	 * Return the best population fitness.
	 * 
	 * @return The best population fitness.
	 */
	public double getBestFitness() {
		if (_bestPhenotype == null) {
			return 0;
		}
		return _bestPhenotype.getFitness(); 
	}
	
	/**
	 * Return the worst population fitness.
	 * 
	 * @return The worst population fitness.
	 */
	public double getWorstFitness() {
		if (_worstPhenotype == null) {
			return 0;
		}
		return _worstPhenotype.getFitness();
	}
	
	/**
	 * Return the average population fitness.
	 * 
	 * @return The average population fitness.
	 */
	public double getFitnessMean() {
		return _fitnessMean;
	}
	
	/**
	 * Return the variance of the population fitness.
	 * 
	 * @return The variance of the population fitness.
	 */
	public double getFitnessVariance() {
		return _fitnessVariance;
	}
	
	public double getAgeMean() {
		return _ageMean;
	}
	
	public double getAgeVariance() {
		return _ageVariance;
	}
	
	/**
	 * The selection strength measures how strongly the fitter individuals are 
	 * selected over the less fit individuals. One measure would be
	 * <pre>
	 *                             increase in average fitness
	 * 	selection strength = ---------------------------------------
	 *                         standard deviation of the population 
	 * </pre>
	 * 
	 * 
	 * @param previous The Statistic of the previous population. If the given
	 *        Statistic is null or the current variance is zero, the returned 
	 *        selection strength is zero.
	 * @return The selection strength.
	 */
	public double selectionStrength(final Statistic<T> previous) {
		if (previous == null) {
			return 0.0;
		}
		
		final double variance = getFitnessVariance();
		if (variance == 0.0) {
			return 0.0;
		}
		
		return (getFitnessMean() - previous.getFitnessMean())/sqrt(getFitnessVariance());
	}
	
	int getSamples() {
		return _samples;
	}

	void setSamples(final int samples) {
		this._samples = samples;
	}

	double getFitnessSum() {
		return _fitnessSum;
	}

	void setFitnessSum(final double sum) {
		_fitnessSum = sum;
	}

	double getFitnessSquareSum() {
		return _fitnessSquareSum;
	}

	void setFitnessSquareSum(final double squareSum) {
		_fitnessSquareSum = squareSum;
	}

	long getAgeSum() {
		return _ageSum;
	}

	void setAgeSum(final long sum) {
		_ageSum = sum;
	}

	long getAgeSquareSum() {
		return _ageSquareSum;
	}

	void setAgeSquareSum(final long squareSum) {
		_ageSquareSum = squareSum;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash += (int)doubleToLongBits(_fitnessMean)*37;
		hash += (int)doubleToLongBits(_fitnessVariance)*37;
		hash += (int)doubleToLongBits(_ageMean)*37;
		hash += (int)doubleToLongBits(_ageVariance)*37;
		hash += (_bestPhenotype != null ? _bestPhenotype.hashCode() : 1)*37; 
		hash += (_worstPhenotype != null ? _worstPhenotype.hashCode() : 1)*37; 
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Statistic)) {
			return false;
		}
		
		final Statistic<?> statistic = (Statistic<?>)obj;
		return doubleToLongBits(statistic._fitnessMean) == doubleToLongBits(_fitnessMean) &&
			doubleToLongBits(statistic._fitnessVariance) == doubleToLongBits(_fitnessVariance) &&
			doubleToLongBits(statistic._ageMean) == doubleToLongBits(_ageMean) &&
			doubleToLongBits(statistic._ageVariance) == doubleToLongBits(_ageVariance) &&
			_bestPhenotype != null ? _bestPhenotype.equals(statistic._bestPhenotype) : statistic._bestPhenotype == null &&
			_worstPhenotype != null ? _worstPhenotype.equals(statistic._worstPhenotype) : statistic._worstPhenotype == null;
	}
	
	public boolean equals(final Statistic<T> statistic, final int ulps) {
		if (statistic == this) {
			return true;
		}
		
		return equals(statistic._fitnessMean, _fitnessMean, ulps) &&
				equals(statistic._fitnessVariance, _fitnessVariance, ulps) &&
				equals(statistic._ageMean, _ageMean, ulps) &&
				equals(statistic._ageVariance, _ageVariance, ulps) &&
				_bestPhenotype != null ? _bestPhenotype.equals(statistic._bestPhenotype) : statistic._bestPhenotype == null &&
				_worstPhenotype != null ? _worstPhenotype.equals(statistic._worstPhenotype) : statistic._worstPhenotype == null;
	}
	
	static boolean equals(final double a, final double b, final int ulpDistance) {
		if (Double.isNaN(a) || Double.isNaN(b)) {
			return false;
		}
		
		return BitUtils.ulpDistance(a, b) <= ulpDistance;
	}

	@Override
	public String toString() {
		String ret = "Mean          : " + getFitnessMean() + "\n" +
					 "Var           : " + getFitnessVariance() + "\n" +
					 "Best Phenotype: " + getBestPhenotype();
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	static final XMLFormat<Statistic> XML = new XMLFormat<Statistic>(Statistic.class) {
		@Override
		public Statistic newInstance(final Class<Statistic> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final double meanFitness = xml.getAttribute("meanfitness", 1.0);
			final double varianceFitness = xml.getAttribute("variancefitness", 1.0);
			final double meanAge = xml.getAttribute("meanage", 1.0);
			final double varianceAge = xml.getAttribute("varianceage", 1.0);
			final Phenotype best = xml.getNext();
			final Phenotype worst = xml.getNext();
			return new Statistic(best, worst, meanFitness, varianceFitness, meanAge, varianceAge);

		}
		@Override
		public void write(final Statistic s, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("meanfitness", s._fitnessMean);
			xml.setAttribute("variancefitness", s._fitnessVariance);
			xml.setAttribute("meanage", s._ageMean);
			xml.setAttribute("varianceage", s._ageVariance);
			xml.add(s._bestPhenotype, "best");
			xml.add(s._worstPhenotype, "worst");
		}
		@Override
		public void read(final InputElement xml, final Statistic p) 
			throws XMLStreamException 
		{
		}
		
	};
}



