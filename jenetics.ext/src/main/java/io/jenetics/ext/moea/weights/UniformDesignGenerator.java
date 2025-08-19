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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.ext.moea.weights;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record UniformDesignGenerator(int objectives, int size)
	implements Generator
{

	private static final int[] PRIMES = new int[] {
		2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67,
		71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149,
		151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229,
		233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313,
		317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409,
		419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499,
		503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601,
		607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691,
		701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809,
		811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907,
		911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997, 1009, 1013,
		1019, 1021, 1031, 1033, 1039, 1049, 1051, 1061, 1063, 1069, 1087, 1091, 1093,
		1097, 1103, 1109, 1117, 1123, 1129, 1151, 1153, 1163, 1171, 1181, 1187, 1193,
		1201, 1213, 1217, 1223, 1229, 1231, 1237, 1249, 1259, 1277, 1279, 1283, 1289,
		1291, 1297, 1301, 1303, 1307, 1319, 1321, 1327, 1361, 1367, 1373, 1381, 1399,
		1409, 1423, 1427, 1429, 1433, 1439, 1447, 1451, 1453, 1459, 1471, 1481, 1483,
		1487, 1489, 1493, 1499, 1511, 1523, 1531, 1543, 1549, 1553, 1559, 1567, 1571,
		1579, 1583
	};

	@Override
	public Weights next() {
		// Generate uniform design using Hammersley method.
		final var designs = new ArrayList<double[]>();
		final var primes = Arrays.copyOfRange(PRIMES, 0, objectives - 2);

		for (int i = 0; i < size; i++) {
			final var design = new double[objectives - 1];
			design[0] = (2.0*(i + 1) - 1.0)/(2.0*size);

			for (int j = 1; j < objectives - 1; ++j) {
				double f = 1.0/primes[j - 1];
				int d = i + 1;
				design[j] = 0.0;

				while (d > 0) {
					design[j] += f*(d%primes[j - 1]);
					d /= primes[j - 1];
					f /= primes[j - 1];
				}
			}

			designs.add(design);
		}

		// Transform designs into weight vectors (sum to 1).
		final var weights = new ArrayList<double[]>();

		for (double[] design : designs) {
			double[] weight = new double[objectives];

			for (int i = 1; i <= objectives; ++i) {
				if (i == objectives) {
					weight[i - 1] = 1.0;
				} else {
					weight[i - 1] = 1.0 - Math.pow(design[i - 1], 1.0/(objectives - i));
				}

				for (int j = 1; j <= i-1; j++) {
					weight[i - 1] *= Math.pow(design[j-1], 1.0/(objectives - j));
				}
			}

			weights.add(weight);
		}

		return Weights.of(weights);
	}

}
