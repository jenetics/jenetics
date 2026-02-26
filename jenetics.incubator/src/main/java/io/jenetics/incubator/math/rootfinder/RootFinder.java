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
package io.jenetics.incubator.math.rootfinder;

import java.util.function.DoubleUnaryOperator;

import io.jenetics.incubator.math.iterative.Estimate;
import io.jenetics.util.DoubleRange;

/**
 * Root finder function.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface RootFinder {

	/**
	 * Represents the found root value plus error estimation and performed
	 * iterations.
	 *
	 * @param value the found root value
	 * @param error the error estimate
	 * @param iterations the performed iterations
	 */
	record Root(double value, double error, long iterations)
		implements Estimate
	{
	}

	/**
	 * Finds the <em>x</em>, where the function value of {@code fn} becomes
	 * zero, or near zero.
	 *
	 * @param fn the function to find the root value for
	 * @param interval the <em>search</em> interval
	 * @return the root value for the given function {@code fn}
	 */
	Root solve(DoubleUnaryOperator fn, DoubleRange interval);

}
