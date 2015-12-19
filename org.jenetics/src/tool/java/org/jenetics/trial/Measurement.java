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
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Measurement {

	private final String _name;
	private final String _description;

	private final ISeq<Parameter> _parameters;
	private final Map<String, Data> _data = new HashMap<>();

	public Measurement(
		final String name,
		final String description,
		final ISeq<Parameter> parameters
	) {
		_name = name;
		_description = description;
		_parameters = requireNonNull(parameters);
	}

	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	public ISeq<Parameter> getParameters() {
		return _parameters;
	}

	public void execute(final Function<Parameter, Optional<double[]>> function) {

	}

	public static void main(final String[] args) {
		final Measurement measurement = null;
		measurement.execute(Measurement::foo);
	}

	static Optional<double[]> foo(final Parameter parameter) {
		return Optional.empty();
	}


	static final class Model {

	}

}
