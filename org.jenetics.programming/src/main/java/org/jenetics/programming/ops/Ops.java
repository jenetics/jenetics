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
package org.jenetics.programming.ops;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Ops {
	private Ops() {
	}

	public static final Op<Double> ADD = Op.of("add", 2, v -> v[0] + v[1]);

	public static final Op<Double> SUB = Op.of("sub", 2, v -> v[0] - v[1]);

	public static final Op<Double> MUL = Op.of("mul", 2, v -> v[0]*v[1]);

	public static final Op<Double> DIV = Op.of("div", 2, v -> v[0]/v[1]);

	public static final Op<Double> EXP = Op.of("exp", 1, v -> Math.exp(v[0]));

	public static final Op<Double> SIN = Op.of("sin", 1, v -> Math.sin(v[0]));

	public static final Op<Double> COS = Op.of("cos", 1, v -> Math.cos(v[0]));



	public static Op<Double> fixed(final double value)  {
		return Op.of(Double.toString(value), 0, v -> value);
	}
}
