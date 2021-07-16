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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Represents one sample result for a given parameter.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Sample {
	private final List<Number> values;

	/**
	 * Create a new measurement sample with the given numeric values
	 *
	 * @param values the values of the sample
	 */
	public Sample(final List<Number> values) {
		this.values = List.copyOf(values);
	}

	/**
	 * Return the values of this sample.
	 *
	 * @return the values of this sample
	 */
	public List<Number> values() {
		return values;
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Sample &&
				values.equals(((Sample)obj).values);
	}

	@Override
	public String toString() {
		return values.toString();
	}

	public static Sample of(final double... values) {
		final Double[] array = new Double[values.length];
		for (int i = 0; i < values.length; ++i) {
			array[i] = values[i];
		}
		return new Sample(List.of(array));
	}



}
