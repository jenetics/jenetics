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
package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;

/**
 * Bean <em>property</em> implementation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
record MutableProperty(
	PropertyDesc desc,
	Object object,
	Path path,
	Object value
)
	implements Property
{

	MutableProperty {
		requireNonNull(desc);
		requireNonNull(object);
		requireNonNull(path);
	}

	@Override
	public Class<?> type() {
		return desc.type();
	}

	@Override
	public Path name() {
		return new Path(desc.name());
	}

	@Override
	public Object read() {
		return desc.read(object);
	}

	@Override
	public boolean write(final Object value) {
		return desc.write(object, value);
	}

	@Override
	public String toString() {
		return Property.toString(this);
	}

}
