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
package io.jenetics.incubator.web.openapi.modelbuilder;

import static org.assertj.core.api.Assertions.assertThat;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Function;

import javax.tools.ToolProvider;

import org.testng.annotations.Test;

public class TypedValueBuilderTest {

	@Test
	public void typedValueHasValueComponent() throws IOException {
		final var model = new JCodeModel();
		new TypedValueBuilder()
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
		new TypedValueBuilder()
			.name("io.jenetics.incubator.test.Count")
			.type("int")
			.build(model);

		assertThat(source(model))
			.contains("record Count(int value)")
			.doesNotContain("requireNonNull(value);");
	}

	@Test
	public void typedValueFactoryIsAnnotatedNullable() throws IOException {
		final var model = new JCodeModel();
		new TypedValueBuilder()
			.name("io.jenetics.incubator.test.TicketId")
			.type(UUID.class.getName())
			.build(model);
		new TypedValueBuilder()
			.name("io.jenetics.incubator.test.Count")
			.type("int")
			.build(model);

		assertThat(source(model))
			.contains("import org.jspecify.annotations.Nullable;")
			.contains("@Nullable\n    public static TicketId of(@Nullable UUID value)")
			.contains("@Nullable\n    public static Count of(@Nullable Integer value)");
	}

	@Test
	public void typedValueWithMethodIsAnnotatedNullable() throws IOException {
		final var model = new JCodeModel();
		new TypedValueBuilder()
			.name("io.jenetics.incubator.test.TicketId")
			.type(UUID.class.getName())
			.build(model);
		new TypedValueBuilder()
			.name("io.jenetics.incubator.test.Count")
			.type("int")
			.build(model);

		assertThat(source(model))
			.contains("import java.util.function.Function;")
			.contains(
				"@Nullable\n" +
				"    public TicketId with(Function<? super UUID, ? extends @Nullable UUID> fn)"
			)
			.contains(
				"@Nullable\n" +
				"    public Count with(Function<? super Integer, ? extends @Nullable Integer> fn)"
			);
	}

	@Test
	public void typedValueFactoryIsNullSafe() throws Exception {
		try (var classes = compileTypedValues()) {
			final var ticketId = classes.loadClass(
				"io.jenetics.incubator.test.TicketId"
			);
			final var of = ticketId.getMethod("of", UUID.class);
			final var id = UUID.fromString("3b344c14-5a7f-4c6b-9a9d-c696355ca79d");

			final var value = of.invoke(null, id);

			assertThat(value).isInstanceOf(ticketId);
			assertThat(ticketId.getMethod("value").invoke(value)).isEqualTo(id);
			assertThat(of.invoke(null, (Object)null)).isNull();
		}
	}

	@Test
	public void typedValueWithMethodMapsValue() throws Exception {
		try (var classes = compileTypedValues()) {
			final var ticketId = classes.loadClass(
				"io.jenetics.incubator.test.TicketId"
			);
			final var of = ticketId.getMethod("of", UUID.class);
			final var with = ticketId.getMethod("with", Function.class);
			final var id = UUID.fromString("3b344c14-5a7f-4c6b-9a9d-c696355ca79d");
			final var mappedId = UUID.fromString(
				"6b5e1e9c-5856-4da6-829b-3fdd8c4b8c8e"
			);

			final var value = of.invoke(null, id);
			final var mapped = with.invoke(
				value,
				(Function<Object, Object>)ignored -> mappedId
			);

			assertThat(mapped).isInstanceOf(ticketId);
			assertThat(ticketId.getMethod("value").invoke(mapped))
				.isEqualTo(mappedId);
			assertThat(with.invoke(
				value,
				(Function<Object, Object>)ignored -> null
			)).isNull();
		}
	}

	@Test
	public void primitiveTypedValueWithMethodUsesBoxedFunctionTypes()
		throws Exception
	{
		try (var classes = compileTypedValues()) {
			final var count = classes.loadClass(
				"io.jenetics.incubator.test.Count"
			);
			final var of = count.getMethod("of", Integer.class);
			final var with = count.getMethod("with", Function.class);

			final var value = of.invoke(null, 123);
			final var mapped = with.invoke(
				value,
				(Function<Object, Object>)current -> (Integer)current + 1
			);

			assertThat(mapped).isInstanceOf(count);
			assertThat(count.getMethod("value").invoke(mapped)).isEqualTo(124);
			assertThat(with.invoke(
				value,
				(Function<Object, Object>)ignored -> null
			)).isNull();
		}
	}

	@Test
	public void primitiveTypedValueFactoryUsesBoxedParameter() throws Exception {
		try (var classes = compileTypedValues()) {
			final var count = classes.loadClass(
				"io.jenetics.incubator.test.Count"
			);
			final var of = count.getMethod("of", Integer.class);

			final var value = of.invoke(null, 123);

			assertThat(value).isInstanceOf(count);
			assertThat(count.getMethod("value").invoke(value)).isEqualTo(123);
			assertThat(of.invoke(null, (Object)null)).isNull();
		}
	}

	private static String source(final JCodeModel model) throws IOException {
		final var out = new ByteArrayOutputStream();
		final var writer = new JCMWriter(model);
		writer.build(
			new OutputStreamCodeWriter(out, StandardCharsets.UTF_8)
		);
		return out.toString(StandardCharsets.UTF_8);
	}

	private record CompiledTypes(URLClassLoader loader, Path dir)
		implements AutoCloseable
	{
		Class<?> loadClass(final String name) throws ClassNotFoundException {
			return loader.loadClass(name);
		}

		@Override
		public void close() throws IOException {
			loader.close();
			delete(dir);
		}
	}

	private static CompiledTypes compileTypedValues() throws Exception {
		final var dir = Files.createTempDirectory("typed-value-builder-test");
		try {
			final var model = new JCodeModel();
			new TypedValueBuilder()
				.name("io.jenetics.incubator.test.TicketId")
				.type(UUID.class.getName())
				.build(model);
			new TypedValueBuilder()
				.name("io.jenetics.incubator.test.Count")
				.type("int")
				.build(model);

			final var writer = new JCMWriter(model);
			writer.setCharset(Charset.defaultCharset());
			writer.build(dir.toFile());

			final var compiler = ToolProvider.getSystemJavaCompiler();
			final var result = compiler.run(
				null,
				null,
				null,
				"-d",
				dir.toString(),
				dir.resolve("io/jenetics/incubator/test/TicketId.java").toString(),
				dir.resolve("io/jenetics/incubator/test/Count.java").toString()
			);

			assertThat(result).isZero();

			final var loader = URLClassLoader.newInstance(
				new java.net.URL[]{ dir.toUri().toURL() },
				TypedValueBuilderTest.class.getClassLoader()
			);

			return new CompiledTypes(loader, dir);
		} catch (Exception e) {
			delete(dir);
			throw e;
		}
	}

	private static void delete(final Path dir) throws IOException {
		if (Files.exists(dir)) {
			try (var files = Files.walk(dir)) {
				files
					.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(File::delete);
			}
		}
	}

}
