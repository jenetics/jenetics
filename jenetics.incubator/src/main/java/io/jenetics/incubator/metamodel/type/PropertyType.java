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
package io.jenetics.incubator.metamodel.type;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.metamodel.internal.Methods.toGetter;
import static io.jenetics.incubator.metamodel.internal.Methods.toSetter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import io.jenetics.incubator.metamodel.access.Access;

/**
 * Represents a <em>property</em>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public final class PropertyType {
	private final String name;
	private final StructType enclosure;
	private final Type type;
	private final Method getter;
	private final Method setter;

	PropertyType(
		final String name,
		final StructType enclosure,
		final Type type,
		final Method getter,
		final Method setter
	) {
		this.name = requireNonNull(name);
		this.enclosure = requireNonNull(enclosure);
		this.type = requireNonNull(type);
		this.getter = requireNonNull(getter);
		this.setter = setter;
	}

	public String name() {
		return name;
	}

	public StructType enclosure() {
		return enclosure;
	}

	public Type type() {
		return type;
	}

	public Access access() {
		return setter != null
			? new Access.Writable(toGetter(getter), toSetter(setter))
			: new Access.Readonly(toGetter(getter));
	}

}
