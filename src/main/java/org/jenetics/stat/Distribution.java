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
package org.jenetics.stat;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Function;
import org.jenetics.util.Range;

/**
 * Defines the <i>domain</i>, <i>PDF</i> and <i>CDF</i> of a probability
 * distribution.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version $Id$
 */
public interface Distribution<C extends Comparable<? super C>> {

	/**
	 * Return the domain of this probability distribution.
	 *
	 * @return the domain of this probability distribution.
	 */
	public Range<C> getDomain();

	/**
	 * Return a new instance of the <i>Cumulative Distribution Function</i> (CDF).
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Cumulative_distribution_function">CDF</a>
	 *
	 * @return the <i>Cumulative Distribution Function</i>.
	 */
	public Function<C, Float64> getCDF();

	/**
	 * Return a new instance of the <i>Probability Density Function</i> (PDF).
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Probability_density_function">PDF</a>
	 *
	 * @return the <i>Probability Density Function</i>.
	 */
	public Function<C, Float64> getPDF();

}
