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
package org.jenetics.example.tsp.gpx;

import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public enum Fix {

	NONE("none"),
	DIM_2("2d"),
	DIM_3("3d"),
	DGPS("dgps"),
	PPS("pps");

	private final String _value;

	private Fix(final String value) {
		_value = requireNonNull(value);
	}

	public String getValue() {
		return _value;
	}

	public static Fix of(final String value) {
		switch (value) {
			case "none": return Fix.NONE;
			case "2d": return Fix.DIM_2;
			case "3d": return Fix.DIM_3;
			case "dgps": return Fix.DGPS;
			case "pps": return Fix.PPS;
			default: throw new IllegalArgumentException(value);
		}
	}

}
