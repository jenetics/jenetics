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

import org.jenetics.util.Validator;
import org.jscience.mathematics.function.Function;
import org.jscience.mathematics.number.Float64;

/**
 * Defines the <i>domain</i>, <i>PDF</i> and <i>CDF</i> of a probability
 * distribution.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface Distribution<C extends Comparable<? super C>> {

	/**
	 * The domain of the distriibution.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Domain<C extends Comparable<? super C>> {
		private final C _min;
		private final C _max;

		/**
		 * Create a new domain object.
		 *
		 * @param min the minimum value of the domain.
		 * @param max the maximum value of the domain.
		 * @throws IllegalArgumentException if {@code min >= max}
		 * @throws NullPointerException if one of the arguments is {@code null}.
		 */
		public Domain(final C min, final C max) {
			if (min.compareTo(max) >= 0) {
				throw new IllegalArgumentException(String.format(
						"Min value must be smaller the max value: [%s, %s]", min, max
					));
			}
			_min = Validator.nonNull(min, "Minimum");
			_max = Validator.nonNull(max, "Maximum");
		}

		public C getMin() {
			return _min;
		}

		public C getMax() {
			return _max;
		}
	}

	/**
	 * Return the domain of this probability distribution.
	 *
	 * @return the domain of this probability distribution.
	 */
	public Domain<C> getDomain();

	/**
	 * Return the <i>Cumulative Distribution Function</i> (CDF).
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Cumulative_distribution_function">CDF</a>
	 *
	 * @return the <i>Cumulative Distribution Function</i>.
	 */
	public Function<C, Float64> cdf();

	/**
	 * Return the <i>Probability Density Function</i> (PDF).
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Probability_density_function">PDF</a>
	 *
	 * @return the <i>Probability Density Function</i>.
	 */
	public Function<C, Float64> pdf();
	
}
