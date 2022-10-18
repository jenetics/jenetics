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
package io.jenetics.incubator.util;

public interface Matrix<T> {

	int rows();

	int columns();

	Matrix<T> copy();

}

class LU<T> {

	/**
	 * Array for internal storage of decomposition.
	 */
	private Matrix<T> LU;

	/**
	 * pivot sign.
	 */
	private int pivsign;

	/**
	 * Internal storage of pivot vector.
	 */
	private int[] piv;

	LU(final Matrix<T> A) {
		final int CUT_OFF = 10;
		// setup
		LU = A;

		int m = A.rows();
		int n = A.columns();

		// setup pivot vector
		if (piv == null || piv.length != m) {
			piv = new int[m];
		}
		for (int i = m; --i >= 0; ) piv[i] = i;
		pivsign = 1;

		if (m * n == 0) {
			//setLU(LU);
			return; // nothing to do
		}

//		//precompute and cache some views to avoid regenerating them time and again
//		DoubleMatrix1D[] LUrows = new DoubleMatrix1D[m];
//		for (int i = 0; i < m; i++) LUrows[i] = LU.viewRow(i);
//
//		cern.colt.list.IntArrayList nonZeroIndexes = new cern.colt.list.IntArrayList(); // sparsity
//		DoubleMatrix1D LUcolj = LU.viewColumn(0).like();  // blocked column j
//		cern.jet.math.Mult multFunction = cern.jet.math.Mult.mult(0);
//
//		// Outer loop.
//		for (int j = 0; j < n; j++) {
//			// blocking (make copy of j-th column to localize references)
//			LUcolj.assign(LU.viewColumn(j));
//
//			// sparsity detection
//			int maxCardinality = m / CUT_OFF; // == heuristic depending on speedup
//			LUcolj.getNonZeros(nonZeroIndexes, null, maxCardinality);
//			int cardinality = nonZeroIndexes.size();
//			boolean sparse = (cardinality < maxCardinality);
//
//			// Apply previous transformations.
//			for (int i = 0; i < m; i++) {
//				int kmax = Math.min(i, j);
//				double s;
//				if (sparse) {
//					s = LUrows[i].zDotProduct(LUcolj, 0, kmax, nonZeroIndexes);
//				} else {
//					s = LUrows[i].zDotProduct(LUcolj, 0, kmax);
//				}
//				double before = LUcolj.getQuick(i);
//				double after = before - s;
//				LUcolj.setQuick(i, after); // LUcolj is a copy
//				LU.setQuick(i, j, after);   // this is the original
//				if (sparse) {
//					if (before == 0 && after != 0) { // nasty bug fixed!
//						int pos = nonZeroIndexes.binarySearch(i);
//						pos = -pos - 1;
//						nonZeroIndexes.beforeInsert(pos, i);
//					}
//					if (before != 0 && after == 0) {
//						nonZeroIndexes.remove(nonZeroIndexes.binarySearch(i));
//					}
//				}
//			}
//
//			// Find pivot and exchange if necessary.
//			int p = j;
//			if (p < m) {
//				double max = Math.abs(LUcolj.getQuick(p));
//				for (int i = j + 1; i < m; i++) {
//					double v = Math.abs(LUcolj.getQuick(i));
//					if (v > max) {
//						p = i;
//						max = v;
//					}
//				}
//			}
//			if (p != j) {
//				LUrows[p].swap(LUrows[j]);
//				int k = piv[p];
//				piv[p] = piv[j];
//				piv[j] = k;
//				pivsign = -pivsign;
//			}
//
//			// Compute multipliers.
//			double jj;
//			if (j < m && (jj = LU.getQuick(j, j)) != 0.0) {
//				multFunction.multiplicator = 1 / jj;
//				LU.viewColumn(j).viewPart(j + 1, m - (j + 1)).assign(multFunction);
//			}
//
//		}
//		setLU(LU);
	}

}
