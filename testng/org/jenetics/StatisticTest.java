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

import org.jenetics.util.BitUtils;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: StatisticTest.java,v 1.2 2008-08-26 22:29:35 fwilhelm Exp $
 */
public class StatisticTest {

	@Test
	public void equals() {
		double a = 123.0;
		double b = a;
		
		assert a == b;
		assert Statistic.equals(a, b, 0);
		
		b = Math.nextUp(a);
		assert a != b;
		assert Statistic.equals(a, b, 1);
		assert Statistic.equals(a, b, 2);
		assert !Statistic.equals(a, b, 0);
		
		b = Math.nextUp(a);
		b = Math.nextUp(b);
		assert a != b;
		assert !Statistic.equals(a, b, 0);
		assert !Statistic.equals(a, b, 1);
		assert Statistic.equals(a, b, 2);
		
		a = Math.nextAfter(0.0, Double.POSITIVE_INFINITY);
		b = Math.nextAfter(0.0, Double.NEGATIVE_INFINITY);
		b = Math.nextAfter(b, Double.NEGATIVE_INFINITY);
		b = Math.nextAfter(b, Double.NEGATIVE_INFINITY);
		assert a != b;
		assert !Statistic.equals(a, b, 0);
		assert !Statistic.equals(a, b, 1);
		assert !Statistic.equals(a, b, 3);
		assert Statistic.equals(a, b, 4);
		
		a = 0.0;
		for (int i = 0; i < 10; ++i) {
			a = Math.nextAfter(a, Double.POSITIVE_INFINITY);
		}
		
		for (int i = 0; i < 19; ++i) {
			a = Math.nextAfter(a, Double.NEGATIVE_INFINITY);
			System.out.println(
				a + "\t" + 
				BitUtils.ulpPosition(a) + "\t" + 
				BitUtils.ulpDistance(0.0, a)
			);
		}
	}

}
