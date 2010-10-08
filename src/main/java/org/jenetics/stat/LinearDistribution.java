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

import org.jscience.mathematics.function.Function;
import org.jscience.mathematics.number.Float64;

/**
 * 
 * <embed src="doc-files/linear-distribution.svg" type="image/svg+xml"/>
 * 
 * <img src="doc-files/linear-kxd1.gif"
 *      alt="y = k \cdot x + d"
 * />
 * 
 * <img src="doc-files/linear-kxd2.gif"
 *      alt="y = 
 *      \\underset{k} {\\underbrace {\frac{y_2-y_1}{x_2-x_1}}} \cdot x + 
 *               \\underset{d}{\\underbrace {y_1-\frac{y_2-y_1}{x_2-x_1}\cdot x_1}}"
 * />
 * 
 * <img src="doc-files/linear-precondition.gif"
 *      alt="\int_{x_1}^{x_2}\left( 
 *           \frac{y_2-y_1}{x_2-x_1}\cdot x + y_1-
 *           \frac{y_2-y_1}{x_2-x_1}\cdot x_1
 *           \right)\mathrm{d}x = 1"
 *  />
 *  
 *  <img src="doc-files/linear-precondition-y2.gif"
 *       alt="y_2 = \frac{(x_2-x_1)\cdot y_1 - 2}{x_2-x_1}"
 *  />
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class LinearDistribution<
	N extends Number & Comparable<? super N>
>
	implements Distribution<N> 
{

	@Override
	public Domain<N> getDomain() {
		return null;
	}

	@Override
	public Function<N, Float64> cdf() {
		return null;
	}

	@Override
	public Function<N, Float64> pdf() {
		return null;
	}

}
