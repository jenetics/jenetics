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
 * {@snippet class="PropertySnippets" region="walk(Object)"}
 *
 * The code snippet above will create the following output
 * <pre>{@code
 * SimpleProperty[path=title, value=Crossroads, mutable=false, type=java.lang.String, enclosure=Book]
 * SimpleProperty[path=pages, value=832, mutable=false, type=int, enclosure=Book]
 * ListProperty[path=authors, value=[Author[Jonathan Franzen]], mutable=false, type=java.util.List, enclosure=Book]
 * IndexProperty[path=authors[0], value=Author[Jonathan Franzen], mutable=true, type=Author, enclosure=java.util.ImmutableCollections$List12]
 * SimpleProperty[path=authors[0].forename, value=Jonathan, mutable=false, type=java.lang.String, enclosure=Author]
 * SimpleProperty[path=authors[0].surname, value=Franzen, mutable=false, type=java.lang.String, enclosure=Author]
 * SimpleProperty[path=authors[0].birthDate, value=1959-08-17, mutable=false, type=java.time.LocalDate, enclosure=Author]
 * ListProperty[path=authors[0].books, value=[], mutable=false, type=java.util.List, enclosure=Author]
 * }</pre>
 */
package io.jenetics.incubator.beans.property;
