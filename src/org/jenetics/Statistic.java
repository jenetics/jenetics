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
import javolution.lang.Immutable;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.BitUtils;

/**
 * Data object which holds performance indicators of a given {@link Population}.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Statistic.java,v 1.12 2009-02-23 20:58:08 fwilhelm Exp $
 */
public class Statistic<G extends Gene<?, G>, C extends Comparable<C>> 
	implements Immutable, XMLSerializable 
{
	private static final long serialVersionUID = -8980979460645713414L;
	
	protected final Phenotype<G, C> _best;
	protected final Phenotype<G, C> _worst;
	protected final int _samples;
	protected final double _ageMean;
	protected final double _ageVariance;

	/**
	 * Evaluates statistic valus from a givem population
	 * 
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */ 
	protected Statistic(
		final Phenotype<G, C> best, final Phenotype<G, C> worst,
		final int samples, final double ageMean, final double ageVariance
	) {
		_best = best;
		_worst = worst;
		_samples = samples;
		_ageMean = ageMean;
		_ageVariance = ageVariance;
	}
	
	protected Statistic(final Statistic<G, C> other) {
		this(
			other.getBestPhenotype(), other.getWorstPhenotype(),
			other.getSamples(), other.getAgeMean(), other.getAgeVariance()
		);
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
		hash += (_best != null ? _best.hashCode() : 1)*37; 
		hash += (_worst != null ? _worst.hashCode() : 1)*37; 
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
		
		final Statistic<?, ?> statistic = (Statistic<?, ?>)obj;
		
		return 
		doubleToLongBits(statistic._ageMean) == doubleToLongBits(_ageMean) &&
		doubleToLongBits(statistic._ageVariance) == doubleToLongBits(_ageVariance) &&
		_best != null ? _best.equals(statistic._best) : statistic._best == null &&
		_worst != null ? _worst.equals(statistic._worst) : statistic._worst == null;
	}
	
	public boolean equals(final Statistic<G, C> statistic, final int ulps) {
		if (statistic == this) {
			return true;
		}
		
		return 
		equals(statistic._ageMean, _ageMean, ulps) &&
		equals(statistic._ageVariance, _ageVariance, ulps) &&
		_best != null ? _best.equals(statistic._best) : statistic._best == null &&
		_worst != null ? _worst.equals(statistic._worst) : statistic._worst == null;
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
	static final XMLFormat<Statistic> XML = new XMLFormat<Statistic>(Statistic.class) {
		@Override
		public Statistic newInstance(final Class<Statistic> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final double meanAge = xml.getAttribute("meanage", 1.0);
			final double varianceAge = xml.getAttribute("varianceage", 1.0);
			final int samples = xml.getAttribute("samples", 1);
			final Phenotype best = xml.getNext();
			final Phenotype worst = xml.getNext();
			return new Statistic(best, worst, samples, meanAge, varianceAge);

		}
		@Override
		public void write(final Statistic s, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("meanage", s._ageMean);
			xml.setAttribute("varianceage", s._ageVariance);
			xml.setAttribute("samples", s._samples);
			xml.add(s._best, "best");
			xml.add(s._worst, "worst");
		}
		@Override
		public void read(final InputElement xml, final Statistic p) 
			throws XMLStreamException 
		{
		}
		
	};
}



