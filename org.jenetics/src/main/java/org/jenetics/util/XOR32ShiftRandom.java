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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jenetics.internal.util.DieHarder;

/**
 * http://www.jstatsoft.org/v08/i14/paper
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-15 $</em>
 * @since @__version__@
 */
public class XOR32ShiftRandom extends Random32 {
	private static final long serialVersionUID = 1L;

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version @__version__@ &mdash; <em>$Date: 2014-01-15 $</em>
	 * @since @__version__@
	 */
	public static final class Param {
		public final int a;
		public final int b;
		public final int c;

		/**
		 * Valid values for a, b, c:
		 * <pre>
		 * +--------------------------------------------------------------------------------+
		 * | 1, 3,10| 1, 5,16| 1, 5,19| 1, 9,29| 1,11, 6| 1,11,16| 1,19, 3| 1,21,20| 1,27,27|
		 * | 2, 5,15| 2, 5,21| 2, 7, 7| 2, 7, 9| 2, 7,25| 2, 9,15| 2,15,17| 2,15,25| 2,21, 9|
		 * | 3, 1,14| 3, 3,26| 3, 3,28| 3, 3,29| 3, 5,20| 3, 5,22| 3, 5,25| 3, 7,29| 3,13, 7|
		 * | 3,23,25| 3,25,24| 3,27,11| 4, 3,17| 4, 3,27| 4, 5,15| 5, 3,21| 5, 7,22| 5, 9,7 |
		 * | 5, 9,28| 5, 9,31| 5,13, 6| 5,15,17| 5,17,13| 5,21,12| 5,27, 8| 5,27,21| 5,27,25|
		 * | 5,27,28| 6, 1,11| 6, 3,17| 6,17, 9| 6,21, 7| 6,21,13| 7, 1, 9| 7, 1,18| 7, 1,25|
		 * | 7,13,25| 7,17,21| 7,25,12| 7,25,20| 8, 7,23| 8,9,23 | 9, 5,1 | 9, 5,25| 9,11,19|
		 * | 9,21,16|10, 9,21|10, 9,25|11, 7,12|11, 7,16|11,17,13|11,21,13|12, 9,23|13, 3,17|
		 * |13, 3,27|13, 5,19|13,17,15|14, 1,15|14,13,15|15, 1,29|17,15,20|17,15,23|17,15,26|
		 * +--------------------------------------------------------------------------------+
		 * </pre>
		 *
		 * @param a
		 * @param b
		 * @param c
		 */
		public Param(final int a, final int b, final int c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Override
		public String toString() {
			return String.format("Param[%-3d, %-3d, %-3d]", a, b, c);
		}
	}


	private final Param _param;

	private int _x = (int)math.random.seed();

	public XOR32ShiftRandom(final Param param) {
		_param = param;
	}

	public XOR32ShiftRandom() {
		this(new Param(13, 17, 15));
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

	public static void main(final String[] args) throws Exception {
		final ExecutorService executor = Executors.newFixedThreadPool(3);
		for (int[] p : BASE_PARAMS) {
			test(p, executor);
		}
	}

	private static void test(final int[] p, final ExecutorService executor)
		throws InterruptedException, ExecutionException
	{
		final List<TestTask> tasks = Arrays.asList(
			new TestTask(new Param(p[0], p[1], p[2])),
			new TestTask(new Param(p[0], p[2], p[1])),
			new TestTask(new Param(p[1], p[0], p[2])),
			new TestTask(new Param(p[1], p[2], p[0])),
			new TestTask(new Param(p[2], p[1], p[0])),
			new TestTask(new Param(p[2], p[0], p[1]))
		);

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

}
