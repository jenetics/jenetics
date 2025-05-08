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

import io.jenetics.incubator.metamodel.access.Accessor;
import io.jenetics.incubator.metamodel.access.Carried;

/**
 * An enclosed type is embedded in another type. This allows accessing (read
 * and write) the value of this type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public sealed interface EnclosedType
	extends MetaModelType
	permits PropertyType, IndexType
{

	/**
	 * Return the enclosing structure type of the property.
	 *
	 * @return the enclosing structure type of the property
	 */
	MetaModelType enclosure();

	/**
	 * Return the access object for accessing (read and write) the value.
	 *
	 * @return the access object for accessing (read and write) the value
	 */
	Carried<Accessor> accessor();

}
