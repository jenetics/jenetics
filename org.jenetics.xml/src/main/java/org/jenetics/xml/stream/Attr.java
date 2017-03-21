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
package org.jenetics.xml.stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Represents a XML attribute.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Attr {

	private final String _name;
	private final String _value;

	/**
	 * Create a new XML attribute with the given {@code name} and {@code value}.
	 *
	 * @param name the attribute name
	 * @param value the attribute value
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	private Attr(final String name, final Object value) {
		_name = requireNonNull(name);
		_value = requireNonNull(value).toString();
	}

	public String getName() {
		return _name;
	}

	public String getValue() {
		return _value;
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 17*Objects.hashCode(_name) + 31;
		hash += 17*Objects.hashCode(_value) + 31;
		return hash;
	}

	@Override
	public boolean equals(final Object object) {
		return object instanceof Attr &&
			((Attr)object)._name.equals(_name) &&
			((Attr)object)._value.equals(_value);
	}

	@Override
	public String toString() {
		return format("%s=%s", _name, _value);
	}

	/**
	 * Create a new XML attribute with the given {@code name} and {@code value}.
	 *
	 * @param name the attribute name
	 * @param value the attribute value
	 * @return a new XML attribute with the given {@code name} and {@code value}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Attr of(final String name, final Objects value) {
		return new Attr(name, value);
	}

}
