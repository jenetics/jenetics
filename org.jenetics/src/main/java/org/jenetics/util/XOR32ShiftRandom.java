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

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jenetics.internal.util.DieHarder;

/**
 * Implementation of the XOR shift PRNG.
 *
 * @see <a href="http://www.jstatsoft.org/v08/i14/paper">
 *      Xorshift RNGs, George Marsaglia</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-20 $</em>
 * @since @__version__@
 */
public class XOR32ShiftRandom extends Random32 {
	private static final long serialVersionUID = 1L;

	/**
	 * Parameter class for the {@code XOR32ShiftRandom} generator. The three
	 * integer parameters are used in the PRNG as follows:
	 *
	 * [code]
	 * y ^= y << a;
	 * y ^= y >> b;
	 * return y ^= y << c;
	 * [/code]
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version @__version__@ &mdash; <em>$Date: 2014-01-20 $</em>
	 * @since @__version__@
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * Contains a list of the parameters with the highest 'dieharder'
		 * scores.
		 */
		public static final Param[] PARAMS = {
			new Param(12, 21, 5),  // p=104, w=5, f=5
			new Param(5, 21, 12),  // p=104, w=4, f=6
			new Param(5, 19, 13),  // p=103, w=4, f=7
			new Param(10, 21, 9),  // p=103, w=2, f=9
			new Param(5, 17, 13),  // p=102, w=5, f=7
			new Param(3, 13, 7),   // p=102, w=3, f=9
			new Param(9, 17, 6),   // p=102, w=3, f=9
			new Param(17, 11, 13), // p=102, w=3, f=9
			new Param(25, 9, 5),   // p=102, w=3, f=9
			new Param(7, 13, 3),   // p=101, w=7, f=6
			new Param(25, 13, 7),  // p=101, w=4, f=9
			new Param(25, 9, 10),  // p=101, w=4, f=9
			new Param(6, 17, 9),   // p=100, w=6, f=8
			new Param(13, 17, 5),  // p=100, w=5, f=9
			new Param(7, 21, 6),   // p=100, w=5, f=9
			new Param(9, 21, 2),   // p=100, w=4, f=10
			new Param(8, 23, 7),   // p=100, w=4, f=10
			new Param(19, 9, 11),  // p=100, w=4, f=10
			new Param(6, 21, 7),   // p=100, w=2, f=12
			new Param(20, 5, 3),   // p=99, w=6, f=9
			new Param(13, 17, 11), // p=99, w=5, f=10
			new Param(16, 7, 11),  // p=99, w=5, f=10
			new Param(4, 5, 15),   // p=99, w=4, f=11
			new Param(17, 13, 3),  // p=99, w=4, f=11
			new Param(5, 6, 13),   // p=99, w=2, f=13
			new Param(2, 15, 9),   // p=98, w=7, f=9
			new Param(12, 25, 7),  // p=98, w=7, f=9
			new Param(5, 7, 9),    // p=98, w=6, f=10
			new Param(9, 5, 7),    // p=98, w=6, f=10
			new Param(7, 25, 13),  // p=98, w=5, f=11
			new Param(6, 5, 13),   // p=98, w=5, f=11
			new Param(22, 5, 3),   // p=98, w=5, f=11
			new Param(2, 9, 7),    // p=98, w=5, f=11
			new Param(9, 12, 23),  // p=98, w=4, f=12
			new Param(11, 17, 13), // p=97, w=9, f=8
			new Param(7, 23, 8),   // p=97, w=6, f=11
			new Param(23, 12, 9),  // p=97, w=6, f=11
			new Param(13, 6, 5),   // p=97, w=6, f=11
			new Param(6, 13, 21),  // p=97, w=5, f=12
			new Param(7, 25, 12),  // p=97, w=5, f=12
			new Param(21, 13, 6),  // p=97, w=4, f=13
			new Param(13, 11, 17), // p=97, w=4, f=13
			new Param(7, 9, 2),    // p=97, w=4, f=13
			new Param(13, 5, 6),   // p=97, w=4, f=13
			new Param(21, 7, 6),   // p=97, w=3, f=14
			new Param(5, 25, 3),   // p=97, w=2, f=15
			new Param(11, 19, 9),  // p=97, w=10, f=7
			new Param(2, 15, 5),   // p=97, w=1, f=16
			new Param(9, 21, 10),  // p=96, w=8, f=10
			new Param(7, 5, 9),    // p=96, w=8, f=10
			new Param(11, 6, 1),   // p=96, w=8, f=10
			new Param(2, 21, 9),   // p=96, w=6, f=12
			new Param(1, 6, 11),   // p=96, w=6, f=12
			new Param(11, 9, 19),  // p=96, w=5, f=13
			new Param(1, 16, 11),  // p=96, w=4, f=14
			new Param(15, 5, 4),   // p=96, w=4, f=14
			new Param(23, 9, 12),  // p=96, w=4, f=14
			new Param(10, 9, 25),  // p=96, w=4, f=14
			new Param(5, 9, 25),   // p=96, w=3, f=15
			new Param(2, 7, 9),    // p=95, w=9, f=10
			new Param(13, 19, 5),  // p=95, w=8, f=11
			new Param(11, 7, 16),  // p=95, w=7, f=12
			new Param(11, 21, 13), // p=95, w=7, f=12
			new Param(25, 5, 9),   // p=95, w=6, f=13
			new Param(21, 5, 3),   // p=95, w=6, f=13
			new Param(9, 15, 2),   // p=95, w=5, f=14
			new Param(16, 11, 7),  // p=95, w=5, f=14
			new Param(9, 7, 2),    // p=95, w=4, f=15
			new Param(1, 9, 5),    // p=95, w=3, f=16
			new Param(6, 17, 3),   // p=95, w=1, f=18
			new Param(15, 17, 20), // p=95, w=0, f=19
			new Param(15, 9, 2),   // p=94, w=8, f=12
			new Param(25, 7, 13),  // p=94, w=8, f=12
			new Param(8, 27, 5),   // p=94, w=7, f=13
			new Param(12, 9, 23),  // p=94, w=6, f=14
			new Param(28, 9, 5),   // p=94, w=6, f=14
			new Param(7, 11, 16),  // p=94, w=4, f=16
			new Param(9, 23, 8),   // p=94, w=4, f=16
			new Param(23, 15, 17), // p=94, w=4, f=16
			new Param(27, 8, 5),   // p=94, w=4, f=16
			new Param(19, 5, 13),  // p=94, w=4, f=16
			new Param(9, 10, 25),  // p=94, w=4, f=16
			new Param(17, 4, 3),   // p=94, w=2, f=18
			new Param(13, 25, 7),  // p=93, w=9, f=12
			new Param(3, 13, 17),  // p=93, w=7, f=14
			new Param(8, 23, 9),   // p=93, w=7, f=14
			new Param(5, 27, 8),   // p=93, w=7, f=14
			new Param(9, 21, 16),  // p=93, w=3, f=18
			new Param(16, 21, 9),  // p=93, w=3, f=18
			new Param(20, 15, 17), // p=93, w=2, f=19
			new Param(13, 5, 19),  // p=93, w=1, f=20
			new Param(7, 13, 25),  // p=92, w=8, f=14
			new Param(3, 5, 20),   // p=92, w=7, f=15
			new Param(19, 5, 1),   // p=92, w=7, f=15
			new Param(3, 17, 4),   // p=92, w=4, f=18
			new Param(5, 2, 15),   // p=92, w=4, f=18
			new Param(3, 17, 6),   // p=92, w=3, f=19
			new Param(17, 3, 6),   // p=92, w=3, f=19
			new Param(25, 5, 3),   // p=92, w=11, f=11
			new Param(5, 9, 1),    // p=91, w=6, f=17
			new Param(3, 25, 5),   // p=91, w=6, f=17
			new Param(27, 3, 13),  // p=91, w=4, f=19
			new Param(13, 7, 25),  // p=91, w=2, f=21
			new Param(27, 5, 8),   // p=91, w=11, f=12
			new Param(17, 6, 3),   // p=90, w=8, f=16
			new Param(17, 6, 9),   // p=90, w=8, f=16
			new Param(25, 7, 12),  // p=90, w=8, f=16
			new Param(25, 10, 9),  // p=90, w=8, f=16
			new Param(15, 2, 5),   // p=90, w=7, f=17
			new Param(7, 25, 20),  // p=90, w=5, f=19
			new Param(21, 2, 5),   // p=90, w=4, f=20
			new Param(23, 8, 7),   // p=90, w=10, f=14
			new Param(5, 27, 25),  // p=90, w=1, f=23
		};

		public static final Param DEFAULT = PARAMS[0];

		public final int a;
		public final int b;
		public final int c;

		/**
		 *
		 * @param a first shift parameter
		 * @param b second shift parameter
		 * @param c third shift parameter
		 */
		public Param(final int a, final int b, final int c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(new int[]{a, b, c});
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof Param)) {
				return false;
			}

			final Param param = (Param)other;
			return a == param.a && b == param.b && c == param.c;
		}

		@Override
		public String toString() {
			return String.format("Param[%d, %d, %d]", a, b, c);
		}
	}


	private final Param _param;
	private final long _seed;

	private int _x = 0;

	public XOR32ShiftRandom(final long seed, final Param param) {
		_param = requireNonNull(param, "PRNG param must not be null.");
		_seed = seed;

		_x = (int)_seed;
	}

	public XOR32ShiftRandom(final long seed) {
		this(seed, Param.DEFAULT);
	}

	public XOR32ShiftRandom(final Param param) {
		this(math.random.seed(), Param.DEFAULT);
	}

	public XOR32ShiftRandom() {
		this(math.random.seed(), Param.DEFAULT);
	}

	@Override
	public int nextInt() {
		_x ^= _x << _param.a; _x ^= _x >> _param.b; return _x ^= _x << _param.c;
//		_x ^= _x << _param.c; _x ^= _x >> _param.b; return _x ^= _x << _param.a;
//		_x ^= _x >> _param.a; _x ^= _x << _param.b; return _x ^= _x >> _param.c;
//		_x ^= _x >> _param.c; _x ^= _x << _param.b; return _x ^= _x >> _param.a;
//		_x ^= _x << _param.a; _x ^= _x << _param.c; return _x ^= _x >> _param.b;
//		_x ^= _x << _param.c; _x ^= _x << _param.a; return _x ^= _x >> _param.b;
//		_x ^= _x >> _param.a; _x ^= _x >> _param.c; return _x ^= _x << _param.b;
//		_x ^= _x >> _param.c; _x ^= _x >> _param.a; return _x ^= _x << _param.b;
	}

	// https://bugs.webkit.org/attachment.cgi?id=191670&action=prettypatch
	/*
	int nextInt_1() {
		return (int)((_x += (_x*_x | 5)) >> 32);
	}
	*/

	@Override
	public String toString() {
		return String.format("XOR32ShiftRandom[%s]", _param);
	}

	/*
	public static void main(final String[] args) throws Exception {
		final ExecutorService executor = Executors.newFixedThreadPool(3);
		final Set<Param> params = new HashSet<>();
		for (int[] p : BASE_PARAMS) {
			params.add(new Param(p[0], p[1], p[2]));
			params.add(new Param(p[0], p[2], p[1]));
			params.add(new Param(p[1], p[0], p[2]));
			params.add(new Param(p[1], p[2], p[0]));
			params.add(new Param(p[2], p[1], p[0]));
			params.add(new Param(p[2], p[0], p[1]));
		}

		final List<TestTask> tasks = new ArrayList<>();
		for (Param param : params) {
			tasks.add(new TestTask(param));
		}

		for (Future<?> future : executor.invokeAll(tasks)) {
			future.get();
		}
	}

	private static final class TestTask implements Callable<Void> {
		private final Param _param;

		TestTask(final Param param) {
			_param = param;
		}

		@Override
		public Void call() throws Exception {
			test(_param);
			return null;
		}

	}

	private static void test(final Param param)
		throws IOException, InterruptedException
	{
		final File reportFile = new File(
			"/home/fwilhelm/tmp/random_reports", String.format(
			"%s_%d_%d_%d.report", XOR32ShiftRandom.class.getSimpleName(), param.a, param.b, param.c
		));

		try (FileWriter writer = new FileWriter(reportFile)) {
			final Random random = new XOR32ShiftRandom(param);
			final DieHarder test = new DieHarder(random, writer, true);
			test.run();
			System.out.println(String.format(
				"%s --> %s", param, test
			));
		}

	}

	private static final int[][] BASE_PARAMS = {
		{1, 3, 10},
		{1, 5, 16},
		{1, 5, 19},
		{1, 9, 29},
		{1, 11, 6},
		{1, 11, 16},
		{1, 19, 3},
		{1, 21, 20},
		{1, 27, 27},
		{2, 5, 15},
		{2, 5, 21},
		{2, 7, 7},
		{2, 7, 9},
		{2, 7, 25},
		{2, 9, 15},
		{2, 15, 17},
		{2, 15, 25},
		{2, 21, 9},
		{3, 1, 14},
		{3, 3, 26},
		{3, 3, 28},
		{3, 3, 29},
		{3, 5, 20},
		{3, 5, 22},
		{3, 5, 25},
		{3, 7, 29},
		{3, 13, 7},
		{3, 23, 25},
		{3, 25, 24},
		{3, 27, 11},
		{4, 3, 17},
		{4, 3, 27},
		{4, 5, 15},
		{5, 3, 21},
		{5, 7, 22},
		{5, 9, 7},
		{5, 9, 28},
		{5, 9, 31},
		{5, 13, 6},
		{5, 15, 17},
		{5, 17, 13},
		{5, 21, 12},
		{5, 27, 8},
		{5, 27, 21},
		{5, 27, 25},
		{5, 27, 28},
		{6, 1, 11},
		{6, 3, 17},
		{6, 17, 9},
		{6, 21, 7},
		{6, 21, 13},
		{7, 1, 9},
		{7, 1, 18},
		{7, 1, 25},
		{7, 13, 25},
		{7, 17, 21},
		{7, 25, 12},
		{7, 25, 20},
		{8, 7, 23},
		{8, 9, 23},
		{9, 5, 1},
		{9, 5, 25},
		{9, 11, 19},
		{9, 21, 16},
		{10, 9, 21},
		{10, 9, 25},
		{11, 7, 12},
		{11, 7, 16},
		{11, 17, 13},
		{11, 21, 13},
		{12, 9, 23},
		{13, 3, 17},
		{13, 3, 27},
		{13, 5, 19},
		{13, 17, 15},
		{14, 1, 15},
		{14, 13, 15},
		{15, 1, 29},
		{17, 15, 20},
		{17, 15, 23},
		{17, 15, 26}
	};
	*/

}
