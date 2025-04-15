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

/**
 * This package contains class for reading <em>statically</em> bean information.
 * The main entry point of this package is the
 * {@link io.jenetics.incubator.metamodel.description.Descriptions} object.
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
 */
package io.jenetics.incubator.metamodel.description;
