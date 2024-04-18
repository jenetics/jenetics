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

import io.jenetics.incubator.beans.reflect.ArrayType;
import io.jenetics.incubator.beans.reflect.BeanType;
import io.jenetics.incubator.beans.reflect.ElementType;
import io.jenetics.incubator.beans.reflect.ListType;
import io.jenetics.incubator.beans.reflect.OptionalType;
import io.jenetics.incubator.beans.reflect.PropertyType;
import io.jenetics.incubator.beans.reflect.RecordType;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
final class ReflectSnippets {

	static class ReflectPackageSnippet {

		public static void main(String[] args) {
			// @start region="PropertyType"
			record Author(String forename, String surname) { }

			final var type = switch (PropertyType.of(Author.class)) {
				case ElementType t -> "ElementType";
				case RecordType t -> "RecordType";
				case BeanType t -> "BeanType";
				case OptionalType t -> "OptionalType";
				case ArrayType t -> "ArrayType";
				case ListType t -> "ListType";
			};

			System.out.println(type);
			// > RecordType
			// @end
		}

	}

}
