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
package io.jenetics.incubator.beans.description;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;

import org.testng.annotations.Test;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.model.Author;
import io.jenetics.incubator.beans.model.Book;
import io.jenetics.incubator.beans.model.Library;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class DescriptionsTest {

	@Test
	public void extractLibrary() {
		final var descriptions = Descriptions
			.unapply(PathValue.of(Path.of("library"), Library.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=library.books, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Library]]
			Description[path=library.name, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Library]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractBook() {
		final var descriptions = Descriptions
			.unapply(PathValue.of(Book.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=authors, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Book]]
			Description[path=pages, value=Single[value=int, enclosure=io.jenetics.incubator.beans.model.Book]]
			Description[path=title, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Book]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractAuthor() {
		final var descriptions = Descriptions
			.unapply(PathValue.of(Author.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=birthDate, value=Single[value=java.time.LocalDate, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=books, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=forename, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=surname, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Author]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void walk() {
		final var descriptions = Descriptions
			.walk(PathValue.of(Path.of("library"), Library.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=library.books, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Library]]
			Description[path=library.books[0], value=Indexed[value=io.jenetics.incubator.beans.model.Book, enclosure=java.util.List]]
			Description[path=library.books[0].authors, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Book]]
			Description[path=library.books[0].authors[0], value=Indexed[value=io.jenetics.incubator.beans.model.Author, enclosure=java.util.List]]
			Description[path=library.books[0].authors[0].birthDate, value=Single[value=java.time.LocalDate, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=library.books[0].authors[0].books, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=library.books[0].authors[0].books[0], value=Indexed[value=io.jenetics.incubator.beans.model.Book, enclosure=java.util.List]]
			Description[path=library.books[0].authors[0].forename, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=library.books[0].authors[0].surname, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=library.books[0].pages, value=Single[value=int, enclosure=io.jenetics.incubator.beans.model.Book]]
			Description[path=library.books[0].title, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Book]]
			Description[path=library.name, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Library]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

}
