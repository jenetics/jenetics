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
package org.jenetics.stat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * It would be more elegant to calculate the inverse cumulative probability and
 * not reading it from a file. But for now it is better than a single magic
 * number.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public final class ChiSquare {
	private static final String CHI = "/org/jenetics/stat/chi.txt";

	private static final double[] PROPS = {
			0.9, 0.95, 0.975, 0.99, 0.995, 0.999, 0.9999
		};

	private static final double[][] TABLE = new double[1000][PROPS.length];

	static {
		final InputStream in = ChiSquare.class.getResourceAsStream(CHI);
		try {
			final BufferedReader reader =
				new BufferedReader(new InputStreamReader(in));

			int index = 0;
			String line = null;
			while ((line = readLine(reader)) != null) {
				final String[] parts = line.split("\\s");
				assert (parts.length == PROPS.length + 1);

				for (int i = 0; i < PROPS.length; ++i) {
					TABLE[index][i] = Double.parseDouble(parts[i + 1]);
				}

				++index;
			}
		} finally {
			try { in.close(); } catch (Exception ignore) {}
		}
	}

	private ChiSquare() {
		throw new AssertionError();
	}

	public static void main(String[] args) {
		System.out.println(chi_9(10));
	}

	private static String readLine(final BufferedReader reader) {
		try {
			String line = reader.readLine();
			while (line != null && line.startsWith("#")) {
				line = reader.readLine();
			}

			return line;
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public static double chi_9(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][0];
	}

	public static double chi_95(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][1];
	}

	public static double chi_975(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][2];
	}

	public static double chi_99(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][3];
	}

	public static double chi_995(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][4];
	}

	public static double chi_999(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][5];
	}

	public static double chi_9999(final int degreeOfFreedom) {
		return TABLE[degreeOfFreedom - 1][6];
	}

}
