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

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * This class represents the <em>static</em> description of a <em>performance</em>
 * measure. It is immutable and cannot be changed after creation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Measurement {
	private final String _name;
	private final Instant _createdAt;
	private final List<Parameter> _parameters;
	private final int _sampleCount;

	private final Environment _environment;

	/**
	 * Create a new measurement object with the given parameters.
	 *
	 * @param name the name of the measurement
	 * @param createdAt the creation date of the measurement
	 * @param parameters the parameters to be measured
	 * @param sampleCount the desired number of calculated samples per parameter
	 * @param environment the environment information
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public Measurement(
		final String name,
		final Instant createdAt,
		final List<Parameter> parameters,
		final int sampleCount,
		final Environment environment
	) {
		_name = requireNonNull(name);
		_createdAt = requireNonNull(createdAt);
		_environment = requireNonNull(environment);
		_parameters = List.copyOf(parameters);
		_sampleCount = sampleCount;
	}

	/**
	 * Return the name of the measurement.
	 *
	 * @return the name of the measurement
	 */
	public String name() {
		return _name;
	}

	/**
	 * Return the creation time of the measurement.
	 *
	 * @return the creation time of the measurement
	 */
	public Instant createdAt() {
		return _createdAt;
	}

	/**
	 * Return the environment the measurement is executed at.
	 *
	 * @return the environment the measurement is executed at
	 */
	public Environment environment() {
		return _environment;
	}

	/**
	 * Return the parameters the measurement should vary.
	 *
	 * @return the parameters the measurement shoudl vary
	 */
	public List<Parameter> parameters() {
		return _parameters;
	}

	/**
	 * Return the desired number of samples per parameter.
	 *
	 * @return the desired number of samples per parameter
	 */
	public int sampleCount() {
		return _sampleCount;
	}

}
