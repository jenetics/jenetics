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
package io.jenetics.incubator.metamodel.description;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.PathValue;
import io.jenetics.incubator.metamodel.internal.Dtor;
import io.jenetics.incubator.metamodel.internal.PreOrderIterator;
import io.jenetics.incubator.metamodel.type.ElementType;
import io.jenetics.incubator.metamodel.type.EnclosedType;
import io.jenetics.incubator.metamodel.type.EnclosingType;
import io.jenetics.incubator.metamodel.type.MetaModelType;
import io.jenetics.incubator.metamodel.type.StructType;

/**
 * This class contains methods for extracting the <em>static</em> bean property
 * information from a given object. It is the main entry point for the extracting
 * properties from an object graph.
 * {@snippet class="DescriptionSnippets" region="walk(Type)"}
 *
 * The code snippet above will create the following output
 * <pre>{@code
 * Description[path=title, type=java.lang.String, enclosure=Book]
 * Description[path=pages, type=int, enclosure=Book]
 * Description[path=authors, type=java.util.List<Author>, enclosure=Book]
 * Description[path=authors[0], type=Author, enclosure=java.util.List]
 * Description[path=authors[0].forename, type=java.lang.String, enclosure=Author]
 * Description[path=authors[0].surname, type=java.lang.String, enclosure=Author]
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class Descriptions {

	private Descriptions() {
	}



}
