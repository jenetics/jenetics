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
 * This package contains class for reading <em>runtime</em> bean information.
 * The main entry point of this package is the
 * {@link io.jenetics.incubator.beans.property.Properties} object.
 *
 * <pre>{@code
 * record Author(String forename, String surname) { }
 * record Book(String title, int pages, List<Author> authors) { }
 *
 * final var object = new Book(
 *     "Oliver Twist",
 *     366,
 *     List.of(new Author("Charles", "Dickens"))
 * );
 *
 * Properties.walk(PathEntry.of(object))
 *     .forEach(System.out::println);
 * }</pre>
 *
 * The code snippet above will create the following output:
 *
 * <pre>{@code
 * ListProperty[path=authors, value=Immutable[value=[Author[forename=Charles, surname=Dickens]], type=java.util.List, enclosureType=Book]]
 * IndexProperty[path=authors[0], value=Mutable[value=Author[forename=Charles, surname=Dickens], type=Author, enclosureType=java.util.ImmutableCollections$List12]]
 * SimpleProperty[path=authors[0].forename, value=Immutable[value=Charles, type=java.lang.String, enclosureType=Author]]
 * SimpleProperty[path=authors[0].surname, value=Immutable[value=Dickens, type=java.lang.String, enclosureType=Author]]
 * SimpleProperty[path=pages, value=Immutable[value=366, type=int, enclosureType=Book]]
 * SimpleProperty[path=title, value=Immutable[value=Oliver Twist, type=java.lang.String, enclosureType=Book]]
 * }</pre>
 */
package io.jenetics.incubator.beans.property;
