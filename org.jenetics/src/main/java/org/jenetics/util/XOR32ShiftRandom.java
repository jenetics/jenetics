/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.util;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-09 $</em>
 * @since @__version__@
 */
public class XOR32ShiftRandom extends Random32 {

	private static final long serialVersionUID = 1L;

	private int _x = (int)math.random.seed();

	@Override
	public int nextInt() {
		/*
		long x = _seed;
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		_seed = x;
		x &= ((1L << 32) - 1);
		return (int)x;
		*/

		//_x ^= (_x << 13);
		//_x = (_x >> 17);
		//return (_x ^= (_x << 5));
		//return 1234;

		//yˆ=(y<<13); y=(y>>17); return (yˆ=(y<<5));


		//_x ^= _x << 13;
		//_x = _x >> 17;
		//return _x ^= _x << 5;


		return _x += (_x*_x | 5);
		//return (int)(_x >> 32);
		//return (int)math.random.seed();
	}

}
