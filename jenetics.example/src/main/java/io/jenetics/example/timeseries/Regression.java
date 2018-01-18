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
package io.jenetics.example.timeseries;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.Op;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Regression {

	private final double[][] _data;
	private final DoubleBinaryOperator _error;


	public Regression(final double[][] data, final DoubleBinaryOperator error) {
		_data = requireNonNull(data);
		_error = requireNonNull(error);
	}

	public Regression(final double[][] data) {
		this(data, (a, b) -> (a - b)*(a - b));
	}

	public double error(final Tree<? extends Op<Double>, ?> expr) {
		return Arrays.stream(_data)
			.mapToDouble(sample -> _error
				.applyAsDouble(eval(expr, sample), sample[sample.length - 1]))
			.sum();
	}

	private double eval(
		final Tree<? extends Op<Double>, ?> expr,
		final double[] point
	) {
		final double[] args = new double[point.length - 1];
		System.arraycopy(point, 1, args, 0, args.length);
		return MathExpr.eval(expr, args);
	}

}
