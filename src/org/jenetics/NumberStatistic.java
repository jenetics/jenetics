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
import org.jscience.mathematics.structure.Ring;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: NumberStatistic.java,v 1.3 2008-10-02 19:40:17 fwilhelm Exp $
 */
class NumberStatistic<G extends Gene<?>, R extends Ring<R> & Comparable<R>>
	extends Statistic<G, R> 
{
	private static final long serialVersionUID = -7468324436514041360L;
	
	protected final R _fitnessMean;
	protected final R _fitnessVariance;
	
	protected NumberStatistic(
		final Phenotype<G, R> best, final Phenotype<G, R> worst, 
		final R fitnessMean, final R fitnessVariance,
		final int samples, final double ageMean, final double ageVariance
	) {
		super(best, worst, samples, ageMean, ageVariance);
		
		Validator.notNull(fitnessMean, "Fitness mean value");
		Validator.notNull(fitnessVariance, "Fitness variance");
		
		_fitnessMean = fitnessMean;
		_fitnessVariance = fitnessVariance;
	}
	
	protected NumberStatistic(
		final Statistic<G, R> other, final R fitnessMean, final R fitnessVariance
	) {
		super(other);
		_fitnessMean = fitnessMean;
		_fitnessVariance = fitnessVariance;
	}

	public R getFitnessMean() {
		return _fitnessMean;
	}
	
	public R getFitnessVariance() {
		return _fitnessVariance;
	}

}















