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

import java.util.List;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.description.Descriptions;
import io.jenetics.incubator.beans.PathValue;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class DescriptionsSnippets {

	static void listType() {
		// @start region="list(Type)"
		record Author(String forename, String surname) { }
		record Book(String title, int pages, List<Author> authors) { }

		Descriptions.list(Book.class)
			.forEach(System.out::println);
		// @end
	}

	static void listPathValue() {
		// @start region="list(PathValue)"
		record Author(String forename, String surname) { }
		record Book(String title, int pages, List<Author> authors) { }

		Descriptions.list(PathValue.of(Path.of("book"), Book.class))
			.forEach(System.out::println);
		// @end
	}

	static void walkType() {
		// @start region="walk(Type)"
		record Author(String forename, String surname) { }
		record Book(String title, int pages, List<Author> authors) { }

		Descriptions.walk(Book.class)
			.forEach(System.out::println);
		// @end
	}

	static void walkPathValue() {
		// @start region="walk(PathValue)"
		record Author(String forename, String surname) { }
		record Book(String title, int pages, List<Author> authors) { }

		Descriptions.walk(PathValue.of(Path.of("library"), Book.class))
			.forEach(System.out::println);
		// @end
	}

	public static void main(String[] args) {
		listType();
	}

}
