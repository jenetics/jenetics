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
package snippets;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.model.Author;
import io.jenetics.incubator.beans.model.Book;
import io.jenetics.incubator.beans.property.Properties;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PropertiesSnippets {

	static void listObject() {
		// @start region="list(Object)"
		var franzen = new Author(
			"Jonathan",
			"Franzen",
			LocalDate.of(1959, 8, 17),
			new ArrayList<>()
		);

		Properties.list(franzen)
			.forEach(System.out::println);
		// @end
	}

	static void listPathValue() {
		// @start region="list(PathValue)"
		var franzen = new Author(
			"Jonathan",
			"Franzen",
			LocalDate.of(1959, 8, 17),
			new ArrayList<>()
		);

		Properties.list(PathValue.of(Path.of("author"), franzen))
			.forEach(System.out::println);
		// @end
	}

	static void walkObject() {
		// @start region="walk(Object)"
		var franzen = new Author(
			"Jonathan",
			"Franzen",
			LocalDate.of(1959, 8, 17),
			List.of()
		);
		var crossroads = new Book(
			"Crossroads",
			832,
			List.of(franzen)
		);

		Properties.walk(crossroads)
			.forEach(System.out::println);
		// @end
	}

	public static void main(String[] args) {
		walkObject();
	}

}
