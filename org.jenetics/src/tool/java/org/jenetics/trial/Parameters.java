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

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Parameters<T> {

	private final String _name;
	private final ISeq<T> _params;

	private Parameters(final String name, final ISeq<T> params) {
		_name = requireNonNull(name);
		_params = requireNonNull(params);
	}

	public String getName() {
		return _name;
	}

	public ISeq<T> getParams() {
		return _params;
	}

	public static <T> Parameters<T> of(
		final String name,
		final ISeq<T> params
	) {
		return new Parameters<>(name, params);
	}

}
