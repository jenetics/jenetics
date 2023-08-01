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
package io.jenetics.incubator.beans.property;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.testng.annotations.Test;

import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.model.Author;
import io.jenetics.incubator.beans.model.Book;
import io.jenetics.incubator.beans.model.Library;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class PropertiesTest {

	public static final Author FRANZEN = new Author(
		"Jonathan",
		"Franzen",
		LocalDate.of(1959, 8, 17),
		new ArrayList<>()
	);

	public static final Author STEPHENSON = new Author(
		"Neal",
		"Stephenson",
		LocalDate.of(1959, 10, 31),
		new ArrayList<>()
	);

	public static final Book CORRECTIONS = new Book(
		"The Corrections",
		672,
		new ArrayList<>(List.of(FRANZEN))
	);

	public static final Book CROSSROADS = new Book(
		"Crossroads",
		832,
		new ArrayList<>(List.of(FRANZEN))
	);

	public static final Book SNOW_CRASH = new Book(
		"Snow Crash",
		576,
		new ArrayList<>(List.of(STEPHENSON))
	);

	public static final Book CRYPTONOMICON = new Book(
		"Cryptonomicon",
		931,
		new ArrayList<>(List.of(STEPHENSON))
	);

	public static final Book ANATHEM = new Book(
		"Anathem",
		981,
		new ArrayList<>(List.of(STEPHENSON))
	);

	public static final Library LIBRARY = new Library(
		"Private Books",
		new ArrayList<>(List.of(
			CORRECTIONS,
			CROSSROADS,
			SNOW_CRASH,
			CRYPTONOMICON,
			ANATHEM
		))
	);

	static {
		STEPHENSON.books().add(SNOW_CRASH);
		STEPHENSON.books().add(ANATHEM);
		STEPHENSON.books().add(CRYPTONOMICON);
	}

	@Test
	public void extractAuthor() {
		final var properties = Properties.extract(PathValue.of(FRANZEN))
			.sorted(Comparator.comparing(Property::path))
			.map(Property::toString)
			.toArray(String[]::new);

		final var expected = """
			SimpleProperty[path=birthDate, value=Immutable[value=1959-08-17, type=java.time.LocalDate, enclosureType=io.jenetics.incubator.beans.model.Author]]
			ListProperty[path=books, value=Immutable[value=[], type=java.util.List, enclosureType=io.jenetics.incubator.beans.model.Author]]
			SimpleProperty[path=forename, value=Immutable[value=Jonathan, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Author]]
			SimpleProperty[path=surname, value=Immutable[value=Franzen, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Author]]
			""".split("\n");

		assertThat(properties).isEqualTo(expected);
	}

	@Test
	public void extractBook() {
		final var properties = Properties.extract(PathValue.of(SNOW_CRASH))
			.sorted(Comparator.comparing(Property::path))
			.map(Property::toString)
			.toArray(String[]::new);

		final var expected = """
			ListProperty[path=authors, value=Immutable[value=[Author[Neal Stephenson]], type=java.util.List, enclosureType=io.jenetics.incubator.beans.model.Book]]
			SimpleProperty[path=pages, value=Immutable[value=576, type=int, enclosureType=io.jenetics.incubator.beans.model.Book]]
			SimpleProperty[path=title, value=Immutable[value=Snow Crash, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Book]]
			""".split("\n");

		assertThat(properties).isEqualTo(expected);
	}

	@Test
	public void walk() {
		final var properties = Properties.walk(LIBRARY)
			.sorted(Comparator.comparing(PathValue::path))
			.map(Property::toString)
			.peek(System.out::println)
			.toArray(String[]::new);

		final var expected = """
			ListProperty[path=books, value=Immutable[value=[Book[The Corrections], Book[Crossroads], Book[Snow Crash], Book[Cryptonomicon], Book[Anathem]], type=java.util.List, enclosureType=io.jenetics.incubator.beans.model.Library]]
			IndexProperty[path=books[0], value=Mutable[value=Book[The Corrections], type=io.jenetics.incubator.beans.model.Book, enclosureType=java.util.ArrayList]]
			ListProperty[path=books[0].authors, value=Immutable[value=[Author[Jonathan Franzen]], type=java.util.List, enclosureType=io.jenetics.incubator.beans.model.Book]]
			IndexProperty[path=books[0].authors[0], value=Mutable[value=Author[Jonathan Franzen], type=io.jenetics.incubator.beans.model.Author, enclosureType=java.util.ArrayList]]
			SimpleProperty[path=books[0].authors[0].birthDate, value=Immutable[value=1959-08-17, type=java.time.LocalDate, enclosureType=io.jenetics.incubator.beans.model.Author]]
			ListProperty[path=books[0].authors[0].books, value=Immutable[value=[], type=java.util.List, enclosureType=io.jenetics.incubator.beans.model.Author]]
			SimpleProperty[path=books[0].authors[0].forename, value=Immutable[value=Jonathan, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Author]]
			SimpleProperty[path=books[0].authors[0].surname, value=Immutable[value=Franzen, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Author]]
			SimpleProperty[path=books[0].pages, value=Immutable[value=672, type=int, enclosureType=io.jenetics.incubator.beans.model.Book]]
			SimpleProperty[path=books[0].title, value=Immutable[value=The Corrections, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Book]]
			IndexProperty[path=books[1], value=Mutable[value=Book[Crossroads], type=io.jenetics.incubator.beans.model.Book, enclosureType=java.util.ArrayList]]
			ListProperty[path=books[1].authors, value=Immutable[value=[Author[Jonathan Franzen]], type=java.util.List, enclosureType=io.jenetics.incubator.beans.model.Book]]
			IndexProperty[path=books[1].authors[0], value=Mutable[value=Author[Jonathan Franzen], type=io.jenetics.incubator.beans.model.Author, enclosureType=java.util.ArrayList]]
			SimpleProperty[path=books[1].pages, value=Immutable[value=832, type=int, enclosureType=io.jenetics.incubator.beans.model.Book]]
			SimpleProperty[path=books[1].title, value=Immutable[value=Crossroads, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Book]]
			IndexProperty[path=books[2], value=Mutable[value=Book[Snow Crash], type=io.jenetics.incubator.beans.model.Book, enclosureType=java.util.ArrayList]]
			ListProperty[path=books[2].authors, value=Immutable[value=[Author[Neal Stephenson]], type=java.util.List, enclosureType=io.jenetics.incubator.beans.model.Book]]
			IndexProperty[path=books[2].authors[0], value=Mutable[value=Author[Neal Stephenson], type=io.jenetics.incubator.beans.model.Author, enclosureType=java.util.ArrayList]]
			SimpleProperty[path=books[2].authors[0].birthDate, value=Immutable[value=1959-10-31, type=java.time.LocalDate, enclosureType=io.jenetics.incubator.beans.model.Author]]
			ListProperty[path=books[2].authors[0].books, value=Immutable[value=[Book[Snow Crash], Book[Anathem], Book[Cryptonomicon]], type=java.util.List, enclosureType=io.jenetics.incubator.beans.model.Author]]
			IndexProperty[path=books[2].authors[0].books[0], value=Mutable[value=Book[Snow Crash], type=io.jenetics.incubator.beans.model.Book, enclosureType=java.util.ArrayList]]
			IndexProperty[path=books[2].authors[0].books[1], value=Mutable[value=Book[Anathem], type=io.jenetics.incubator.beans.model.Book, enclosureType=java.util.ArrayList]]
			IndexProperty[path=books[2].authors[0].books[2], value=Mutable[value=Book[Cryptonomicon], type=io.jenetics.incubator.beans.model.Book, enclosureType=java.util.ArrayList]]
			SimpleProperty[path=books[2].authors[0].forename, value=Immutable[value=Neal, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Author]]
			SimpleProperty[path=books[2].authors[0].surname, value=Immutable[value=Stephenson, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Author]]
			SimpleProperty[path=books[2].pages, value=Immutable[value=576, type=int, enclosureType=io.jenetics.incubator.beans.model.Book]]
			SimpleProperty[path=books[2].title, value=Immutable[value=Snow Crash, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Book]]
			IndexProperty[path=books[3], value=Mutable[value=Book[Cryptonomicon], type=io.jenetics.incubator.beans.model.Book, enclosureType=java.util.ArrayList]]
			ListProperty[path=books[3].authors, value=Immutable[value=[Author[Neal Stephenson]], type=java.util.List, enclosureType=io.jenetics.incubator.beans.model.Book]]
			IndexProperty[path=books[3].authors[0], value=Mutable[value=Author[Neal Stephenson], type=io.jenetics.incubator.beans.model.Author, enclosureType=java.util.ArrayList]]
			SimpleProperty[path=books[3].pages, value=Immutable[value=931, type=int, enclosureType=io.jenetics.incubator.beans.model.Book]]
			SimpleProperty[path=books[3].title, value=Immutable[value=Cryptonomicon, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Book]]
			IndexProperty[path=books[4], value=Mutable[value=Book[Anathem], type=io.jenetics.incubator.beans.model.Book, enclosureType=java.util.ArrayList]]
			ListProperty[path=books[4].authors, value=Immutable[value=[Author[Neal Stephenson]], type=java.util.List, enclosureType=io.jenetics.incubator.beans.model.Book]]
			IndexProperty[path=books[4].authors[0], value=Mutable[value=Author[Neal Stephenson], type=io.jenetics.incubator.beans.model.Author, enclosureType=java.util.ArrayList]]
			SimpleProperty[path=books[4].pages, value=Immutable[value=981, type=int, enclosureType=io.jenetics.incubator.beans.model.Book]]
			SimpleProperty[path=books[4].title, value=Immutable[value=Anathem, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Book]]
			SimpleProperty[path=name, value=Immutable[value=Private Books, type=java.lang.String, enclosureType=io.jenetics.incubator.beans.model.Library]]
			""".split("\n");

		assertThat(properties).isEqualTo(expected);
	}

}
