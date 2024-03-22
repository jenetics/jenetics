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

import java.util.Comparator;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PropertyTest {

	static final class Data {
		private String name;
		private Integer value;

		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public Integer getValue() { return value; }
		public void setValue(Integer value) { this.value = value; }
	}

	@Test
	public void writeProperty() {
		final var data = new Data();
		final var properties = Properties.list(data)
			.sorted(Comparator.comparing(Property::path))
			.toList();

		assertThat(properties.get(0).value()).isNull();
		assertThat(properties.get(1).value()).isNull();

		properties.get(0).writer().ifPresent(writer -> writer.write("my_name"));
		assertThat(properties.get(0).value()).isNull();
		assertThat(properties.get(0).read()).isEqualTo("my_name");

		properties.get(1).writer().ifPresent(writer -> writer.write(12345));
		assertThat(properties.get(1).value()).isNull();
		assertThat(properties.get(1).read()).isEqualTo(12345);
	}

}
