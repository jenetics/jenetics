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
package io.jenetics.incubator.property;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PropertyTest {

	private final RootObject root = new RootObject(
		"root", 0,
		List.of(
			new SubObject(
				"sub_1", 1,
				new SubObject("sub2", 2, null)
			),
			new SubObject(
				"sub_3", 3,
				new SubObject("sub4", 4, null)
			),
			new SubObject(
				"sub_5", 5,
				new SubObject("sub6", 6, null)
			)
		)
	);

	@Test
	public void walk() {
		final List<Property> properties = Properties
			.stream(root, "io.jenetics")
			.toList();

		assertThat(properties.size()).isEqualTo(24);
	}

	@Test
	public void filter() {
		final var properties = Properties
			.stream(root, "io.jenetics")
			.filter(Property.pathMatcher("**index"))
			.map(Property::value)
			.toList();

		assertThat(properties).isEqualTo(List.of(0, 1, 2, 3, 4, 5, 6));
	}

	@Test
	public void propertyWrite() {
		final var root = new MutableObject();
		root.setIndex(0);
		root.setName("name");

		final List<Property> properties = Properties
			.stream(root, "io.jenetics")
			.toList();

		final var wp = (WriteableProperty)properties.get(0);
		wp.write(10);
		assertThat(wp.value()).isEqualTo(0);
		assertThat(wp.read()).isEqualTo(10);
	}

}
