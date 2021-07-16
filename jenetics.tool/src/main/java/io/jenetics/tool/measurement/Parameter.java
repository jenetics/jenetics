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
package io.jenetics.tool.measurement;

import java.util.List;

/**
 * Represents the parameters needed for one measurement result.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Parameter {
	private final List<Object> values;

	/**
	 * Create a new parameter object with the given values.
	 *
	 * @param values the parameter values
	 */
	public Parameter(final List<Object> values) {
		this.values = List.copyOf(values);
	}

	/**
	 * Return the parameter values.
	 *
	 * @return the parameter values
	 */
	public List<Object> values() {
		return values;
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Parameter &&
			values.equals(((Parameter)obj).values);
	}

	@Override
	public String toString() {
		return values.toString();
	}

	public static Parameter of(final Object... values) {
		return new Parameter(List.of(values));
	}

}
