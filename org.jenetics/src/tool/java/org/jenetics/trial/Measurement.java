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
package org.jenetics.trial;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Measurement<T> {

	private final String _name;
	private final String _description;

	private final ISeq<T> _parameters;
	private final ISeq<Data> _data;

	public Measurement(
		final String name,
		final String description,
		final ISeq<T> parameters,
		final ISeq<Data> data
	) {
		_name = name;
		_description = description;
		_parameters = requireNonNull(parameters);
		_data = requireNonNull(data);
	}

	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	public ISeq<T> getParameters() {
		return _parameters;
	}

	public void execute(final Function<T, double[]> function) {
		final T param = _parameters.get(_data.get(0).next().nextIndex());
		final double[] results = function.apply(param);

		for (int i = 0; i < results.length; ++i) {
			_data.get(i).next().add(results[i]);
		}
	}



	public static void main(final String[] args) {
		final Measurement<String> measurement = null;
		measurement.execute(Measurement::foo);
	}

	static double[] foo(final String parameter) {
		return null;
	}


	static final class Model {

	}

}
