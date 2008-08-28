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

import org.jenetics.util.Validator;
import org.jscience.mathematics.number.Number;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: NumberStatistic.java,v 1.2 2008-08-28 21:21:13 fwilhelm Exp $
 */
class NumberStatistic<G extends Gene<?>, N extends Number<N>>
	extends Statistic<G, N> 
{
	private static final long serialVersionUID = -7468324436514041360L;
	
	protected final N _fitnessMean;
	protected final N _fitnessVariance;
	
	protected NumberStatistic(
		final Phenotype<G, N> best, final Phenotype<G, N> worst, 
		final N fitnessMean, final N fitnessVariance,
		final int samples, final double ageMean, final double ageVariance
	) {
		super(best, worst, samples, ageMean, ageVariance);
		
		Validator.notNull(fitnessMean, "Fitness mean value");
		Validator.notNull(fitnessVariance, "Fitness variance");
		
		_fitnessMean = fitnessMean;
		_fitnessVariance = fitnessVariance;
	}
	
	protected NumberStatistic(
		final Statistic<G, N> other, final N fitnessMean, final N fitnessVariance
	) {
		super(other);
		_fitnessMean = fitnessMean;
		_fitnessVariance = fitnessVariance;
	}

	public N getFitnessMean() {
		return _fitnessMean;
	}
	
	public N getFitnessVariance() {
		return _fitnessVariance;
	}

}















