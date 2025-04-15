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
package io.jenetics.incubator.metamodel.property;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.testng.annotations.Test;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.PathValue;
import io.jenetics.incubator.metamodel.model.Author;
import io.jenetics.incubator.metamodel.model.Book;
import io.jenetics.incubator.metamodel.model.Library;

import io.jenetics.jpx.GPX;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
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
		final var properties = Properties.list(PathValue.of(FRANZEN))
			.sorted(Comparator.comparing(Property::path))
			.map(Property::toString)
			.toArray(String[]::new);

		final var expected = """
			SimpleProperty[path=birthDate, value=1959-08-17, mutable=false, type=java.time.LocalDate, enclosure=io.jenetics.incubator.metamodel.model.Author]
			ListProperty[path=books, value=[], mutable=false, type=java.util.List, enclosure=io.jenetics.incubator.metamodel.model.Author]
			SimpleProperty[path=forename, value=Jonathan, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Author]
			SimpleProperty[path=surname, value=Franzen, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Author]
			""".split("\n");

		assertThat(properties).isEqualTo(expected);
	}

	@Test
	public void extractBook() {
		final var properties = Properties.list(PathValue.of(SNOW_CRASH))
			.sorted(Comparator.comparing(Property::path))
			.map(Property::toString)
			.toArray(String[]::new);

		final var expected = """
			ListProperty[path=authors, value=[Author[Neal Stephenson]], mutable=false, type=java.util.List, enclosure=io.jenetics.incubator.metamodel.model.Book]
			SimpleProperty[path=pages, value=576, mutable=false, type=int, enclosure=io.jenetics.incubator.metamodel.model.Book]
			SimpleProperty[path=title, value=Snow Crash, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Book]
			""".split("\n");

		assertThat(properties).isEqualTo(expected);
	}

	@Test
	public void walLibrary() {
		final var properties = Properties.walk(LIBRARY)
			.sorted(Comparator.comparing(Property::path))
			.map(Property::toString)
			.toArray(String[]::new);

		final var expected = """
			ListProperty[path=books, value=[Book[The Corrections], Book[Crossroads], Book[Snow Crash], Book[Cryptonomicon], Book[Anathem]], mutable=false, type=java.util.List, enclosure=io.jenetics.incubator.metamodel.model.Library]
			IndexProperty[path=books[0], value=Book[The Corrections], mutable=true, type=io.jenetics.incubator.metamodel.model.Book, enclosure=java.util.ArrayList]
			ListProperty[path=books[0].authors, value=[Author[Jonathan Franzen]], mutable=false, type=java.util.List, enclosure=io.jenetics.incubator.metamodel.model.Book]
			IndexProperty[path=books[0].authors[0], value=Author[Jonathan Franzen], mutable=true, type=io.jenetics.incubator.metamodel.model.Author, enclosure=java.util.ArrayList]
			SimpleProperty[path=books[0].authors[0].birthDate, value=1959-08-17, mutable=false, type=java.time.LocalDate, enclosure=io.jenetics.incubator.metamodel.model.Author]
			ListProperty[path=books[0].authors[0].books, value=[], mutable=false, type=java.util.List, enclosure=io.jenetics.incubator.metamodel.model.Author]
			SimpleProperty[path=books[0].authors[0].forename, value=Jonathan, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Author]
			SimpleProperty[path=books[0].authors[0].surname, value=Franzen, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Author]
			SimpleProperty[path=books[0].pages, value=672, mutable=false, type=int, enclosure=io.jenetics.incubator.metamodel.model.Book]
			SimpleProperty[path=books[0].title, value=The Corrections, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Book]
			IndexProperty[path=books[1], value=Book[Crossroads], mutable=true, type=io.jenetics.incubator.metamodel.model.Book, enclosure=java.util.ArrayList]
			ListProperty[path=books[1].authors, value=[Author[Jonathan Franzen]], mutable=false, type=java.util.List, enclosure=io.jenetics.incubator.metamodel.model.Book]
			IndexProperty[path=books[1].authors[0], value=Author[Jonathan Franzen], mutable=true, type=io.jenetics.incubator.metamodel.model.Author, enclosure=java.util.ArrayList]
			SimpleProperty[path=books[1].pages, value=832, mutable=false, type=int, enclosure=io.jenetics.incubator.metamodel.model.Book]
			SimpleProperty[path=books[1].title, value=Crossroads, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Book]
			IndexProperty[path=books[2], value=Book[Snow Crash], mutable=true, type=io.jenetics.incubator.metamodel.model.Book, enclosure=java.util.ArrayList]
			ListProperty[path=books[2].authors, value=[Author[Neal Stephenson]], mutable=false, type=java.util.List, enclosure=io.jenetics.incubator.metamodel.model.Book]
			IndexProperty[path=books[2].authors[0], value=Author[Neal Stephenson], mutable=true, type=io.jenetics.incubator.metamodel.model.Author, enclosure=java.util.ArrayList]
			SimpleProperty[path=books[2].authors[0].birthDate, value=1959-10-31, mutable=false, type=java.time.LocalDate, enclosure=io.jenetics.incubator.metamodel.model.Author]
			ListProperty[path=books[2].authors[0].books, value=[Book[Snow Crash], Book[Anathem], Book[Cryptonomicon]], mutable=false, type=java.util.List, enclosure=io.jenetics.incubator.metamodel.model.Author]
			IndexProperty[path=books[2].authors[0].books[0], value=Book[Snow Crash], mutable=true, type=io.jenetics.incubator.metamodel.model.Book, enclosure=java.util.ArrayList]
			IndexProperty[path=books[2].authors[0].books[1], value=Book[Anathem], mutable=true, type=io.jenetics.incubator.metamodel.model.Book, enclosure=java.util.ArrayList]
			ListProperty[path=books[2].authors[0].books[1].authors, value=[Author[Neal Stephenson]], mutable=false, type=java.util.List, enclosure=io.jenetics.incubator.metamodel.model.Book]
			IndexProperty[path=books[2].authors[0].books[1].authors[0], value=Author[Neal Stephenson], mutable=true, type=io.jenetics.incubator.metamodel.model.Author, enclosure=java.util.ArrayList]
			SimpleProperty[path=books[2].authors[0].books[1].pages, value=981, mutable=false, type=int, enclosure=io.jenetics.incubator.metamodel.model.Book]
			SimpleProperty[path=books[2].authors[0].books[1].title, value=Anathem, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Book]
			IndexProperty[path=books[2].authors[0].books[2], value=Book[Cryptonomicon], mutable=true, type=io.jenetics.incubator.metamodel.model.Book, enclosure=java.util.ArrayList]
			ListProperty[path=books[2].authors[0].books[2].authors, value=[Author[Neal Stephenson]], mutable=false, type=java.util.List, enclosure=io.jenetics.incubator.metamodel.model.Book]
			IndexProperty[path=books[2].authors[0].books[2].authors[0], value=Author[Neal Stephenson], mutable=true, type=io.jenetics.incubator.metamodel.model.Author, enclosure=java.util.ArrayList]
			SimpleProperty[path=books[2].authors[0].books[2].pages, value=931, mutable=false, type=int, enclosure=io.jenetics.incubator.metamodel.model.Book]
			SimpleProperty[path=books[2].authors[0].books[2].title, value=Cryptonomicon, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Book]
			SimpleProperty[path=books[2].authors[0].forename, value=Neal, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Author]
			SimpleProperty[path=books[2].authors[0].surname, value=Stephenson, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Author]
			SimpleProperty[path=books[2].pages, value=576, mutable=false, type=int, enclosure=io.jenetics.incubator.metamodel.model.Book]
			SimpleProperty[path=books[2].title, value=Snow Crash, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Book]
			IndexProperty[path=books[3], value=Book[Cryptonomicon], mutable=true, type=io.jenetics.incubator.metamodel.model.Book, enclosure=java.util.ArrayList]
			IndexProperty[path=books[4], value=Book[Anathem], mutable=true, type=io.jenetics.incubator.metamodel.model.Book, enclosure=java.util.ArrayList]
			SimpleProperty[path=name, value=Private Books, mutable=false, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Library]
			""".split("\n");

		assertThat(properties).isEqualTo(expected);
	}

	@Test
	public void walkGPX() throws IOException {
		final GPX gpx = GPX.Reader.DEFAULT.read(
			PropertiesTest.class
				.getResourceAsStream("/io/jenetics/incubator/metamodel/Austria.gpx")
		);

		final var expected =
			new String(
				Properties.class
					.getResourceAsStream("/io/jenetics/incubator/metamodel/Austria.txt")
					.readAllBytes()
			)
			.split("\n");

		final var properties = Properties
			.walk(PathValue.of(Path.of("austria"), gpx))
			.map(Objects::toString)
			.toArray(String[]::new);

		Arrays.stream(properties).forEach(System.out::println);
		assertThat(properties).isEqualTo(expected);
	}

}
