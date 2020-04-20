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

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Measurement {
	private final String _name;
	private final ZonedDateTime _createdAt;
	private final List<Parameter> _parameters;

	public Measurement(
		final String name,
		final ZonedDateTime createdAt,
		final List<Parameter> parameters
	) {
		_name = requireNonNull(name);
		_createdAt = requireNonNull(createdAt);
		_parameters = List.copyOf(parameters);
	}

	public String name() {
		return _name;
	}

	public ZonedDateTime createdAt() {
		return _createdAt;
	}

	public List<Parameter> parameters() {
		return _parameters;
	}

}
