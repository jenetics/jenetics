/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetix.util;

import org.jscience.mathematics.number.LargeInteger;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class LargeIntegerRandomTest {

	@Test
	public void random() {
		final NumberRandom<LargeInteger> random = new LargeIntegerRandom();
		final LargeInteger min = LargeInteger.valueOf(0);
		final LargeInteger max = LargeInteger.ONE.times2pow(256).minus(-1);
		//final LargeInteger max = LargeInteger.valueOf("100000000999999999990000000000000000000000000");

		for (int i = 0; i < 10; ++i) {
			System.out.println(random.next(min, max));
		}
	}

}
