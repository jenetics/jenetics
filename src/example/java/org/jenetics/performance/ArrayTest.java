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
package org.jenetics.performance;

import javax.measure.unit.SI;

import org.jenetics.util.Array;
import org.jenetics.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ArrayTest {

	static final class Getter {
		private static final int N = 1000;
		private static final int LOOPS = 1000000;
		final Array<Integer> array = new Array<Integer>(N);
		
		public void forloop() {
			
			final Timer timer = new Timer("for loop");
			
			for (int i = LOOPS; --i >= 0;) {
				timer.start();
				for (int j = N; --j >= 0;) {
					array.get(j);
				}
				timer.stop();
			}
			
			final double nanos = timer.getTime().doubleValue(SI.NANO(SI.SECOND));
			System.out.println(String.format(
					"Getter: %s ns", nanos/(N*LOOPS)
				));
		}
	}
	
	public static void main(String[] args) {
		final Getter getter = new Getter();
		getter.forloop();
	}
	
}
