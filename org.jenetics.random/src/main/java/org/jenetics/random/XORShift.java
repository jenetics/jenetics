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
package org.jenetics.random;

import org.jenetics.random.XOR32ShiftRandom.Param;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
interface XORShift {

	int shift(int x, XOR32ShiftRandom.Param param);

	public static int nextInt1(int x, XOR32ShiftRandom.Param param) {
		x ^= x << param.a; x ^= x >> param.b; return x << param.c;
	}

	public static int nextInt2(int x, XOR32ShiftRandom.Param param) {
		x ^= x << param.c; x ^= x >> param.b; return x << param.a;
	}

	public static int nextInt3(int x, XOR32ShiftRandom.Param param) {
		x ^= x >> param.a; x ^= x << param.b; return x >> param.c;
	}

	public static int nextInt4(int x, XOR32ShiftRandom.Param param) {
		x ^= x >> param.c; x ^= x << param.b; return x >> param.a;
	}

	public static int nextInt5(int x, XOR32ShiftRandom.Param param) {
		x ^= x << param.a; x ^= x << param.c; return x >> param.b;
	}

	public static int nextInt6(int x, XOR32ShiftRandom.Param param) {
		x ^= x << param.c; x ^= x << param.a; return x >> param.b;
	}

	public static int nextIn7(int x, XOR32ShiftRandom.Param param) {
		x ^= x >> param.a; x ^= x >> param.c; return x << param.b;
	}

	public static int nextInt8(int x, XOR32ShiftRandom.Param param) {
		x ^= x >> param.c; x ^= x >> param.a; return x << param.b;
	}

}
