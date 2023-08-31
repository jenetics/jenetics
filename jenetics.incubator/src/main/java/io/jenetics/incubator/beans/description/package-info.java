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
 * {@link io.jenetics.incubator.beans.description.Descriptions} object.
 *
 * {@snippet lang="java":
 * record Author(String forename, String surname) { }
 * record Book(String title, int pages, List<Author> authors) { }
 *
 * Descriptions.walk(PathEntry.of(Book.class))
 *     .forEach(System.out::println);
 * }
 *
 * The code snippet above will create the following output:
 *
 * {@snippet lang="java":
 * Description[path=authors, value=Single[value=java.util.List<Author>, enclosure=Book]]
 * Description[path=authors[0], value=Indexed[value=Author, enclosure=java.util.List]]
 * Description[path=authors[0].forename, value=Single[value=java.lang.String, enclosure=Author]]
 * Description[path=authors[0].surname, value=Single[value=java.lang.String, enclosure=Author]]
 * Description[path=pages, value=Single[value=int, enclosure=Book]]
 * Description[path=title, value=Single[value=java.lang.String, enclosure=Book]]
 * }
 */
package io.jenetics.incubator.beans.description;
