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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.annotations.Test;

import io.jenetics.incubator.property.Property.Path;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PropertyReaderTest {

	@Test
	public void filter() {
		final List<Property> properties = IntStream.range(0, 10)
			.mapToObj(i -> new SimpleProperty(
				"object" + 1,
				Path.of("path"+ i),
				String.class,
				"name" +i,
				"value" + i
			))
			.collect(Collectors.toUnmodifiableList());

		final PropertyReader reader = (basePath, object) -> properties.stream();

		assertThat(
			reader.read(null, null).toList()
		).isEqualTo(properties);

		assertThat(
			reader.filter(Objects::nonNull)
				.read(null, null)
				.toList()
		).isEqualTo(List.of());
	}

}
