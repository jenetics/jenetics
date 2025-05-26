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
package io.jenetics.distassert;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.jenetics.ext.util.CsvSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ChiSquareTable {
	private final double[] pValues;
	private final int[] dof;
	private final double[][] chiSquare;

	private ChiSquareTable(double[] pValues, int[] dof, double[][] chiSquare) {
		this.pValues = requireNonNull(pValues);
		this.dof = requireNonNull(dof);
		this.chiSquare = requireNonNull(chiSquare);
	}

	public double[] pValues() {
		return pValues;
	}

	public int[] dof() {
		return dof;
	}

	public double chi2(final int pValueIndex, final int dofIndex) {
		return chiSquare[dofIndex][pValueIndex];
	}

	public static ChiSquareTable instance() {
		class Holder {
			static final ChiSquareTable INSTANCE;
			static {
				var csv = "/io/jenetics/incubator/stat/chi2.csv";
				try (var in = ChiSquareTable.class.getResourceAsStream(csv);
					var reader = new InputStreamReader(in))
				{
					final List<String[]> values = CsvSupport.readAllRows(reader);

					final double[] pv = Arrays.stream(values.getFirst()).skip(1)
							.mapToDouble(Double::parseDouble)
							.toArray();

					final var dof = new ArrayList<Integer>();

					double[][] c2 = values.stream().skip(1).map(row -> {
							dof.add(Integer.parseInt(row[0]));
							return Arrays.stream(row).skip(1)
								.mapToDouble(Double::parseDouble)
								.toArray();
						})
						.toArray(double[][]::new);

					INSTANCE = new ChiSquareTable(
						pv,
						dof.stream().mapToInt(i -> i).toArray(),
						c2
					);
				} catch (IOException e) {
					throw new AssertionError(e);
				}
			}
		}

		return Holder.INSTANCE;
	}

}
