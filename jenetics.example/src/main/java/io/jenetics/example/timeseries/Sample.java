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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Sample {
	private final double[] _arguments;
	private final double _result;

	private Sample(final double[] arguments, final double result) {
		_arguments = arguments;
		_result = result;
	}

	public int arity() {
		return _arguments.length;
	}

	public double[] arguments() {
		return _arguments;
	}

	public double result() {
		return _result;
	}

	public static Sample of(final double[] arguments, final double result) {
		return new Sample(arguments, result);
	}
}
