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
package io.jenetics.incubator.web.openapi.structural;

import static org.assertj.core.api.Assertions.assertThat;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.testng.annotations.Test;

public class TypedValueGeneratorTest {

	@Test
	public void typedValueHasValueComponent() throws IOException {
		final var model = new JCodeModel();
		new TypedValueGenerator()
			.name("io.jenetics.incubator.test.Price")
			.type("java.math.BigDecimal")
			.build(model);

		assertThat(source(model))
			.contains("import java.math.BigDecimal;")
			.contains("record Price(BigDecimal value)")
			.contains("requireNonNull(value);");
	}

	@Test
	public void primitiveTypedValueHasNoNullCheck() throws IOException {
		final var model = new JCodeModel();
		new TypedValueGenerator()
			.name("io.jenetics.incubator.test.Count")
			.type("int")
			.build(model);

		assertThat(source(model))
			.contains("record Count(int value)")
			.doesNotContain("requireNonNull(value);");
	}

	private static String source(final JCodeModel model) throws IOException {
		final var out = new ByteArrayOutputStream();
		final var writer = new JCMWriter(model);
		writer.build(
			new OutputStreamCodeWriter(out, StandardCharsets.UTF_8)
		);
		return out.toString(StandardCharsets.UTF_8);
	}

}
