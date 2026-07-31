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
import io.jenetics.incubator.structural.StructureView;
import io.jenetics.incubator.structural.Structures;
import io.swagger.v3.oas.models.media.ObjectSchema;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.tools.ToolProvider;

import org.testng.annotations.Test;

public class StructuralTypeBuilderTest {

	@Test
	public void structuralTypeHasCompatibleBuilderInterface() throws Exception {
		try (var classes = compileStructuralTypes()) {
			final var ticket = classes.loadClass(
				"io.jenetics.incubator.test.Ticket"
			);
			final var event = classes.loadClass(
				"io.jenetics.incubator.test.Event"
			);
			final var ticketBuilder = classes.loadClass(
				"io.jenetics.incubator.test.Ticket$Builder"
			);
			final var eventBuilder = classes.loadClass(
				"io.jenetics.incubator.test.Event$Builder"
			);

			Structures.check(ticket);
			Structures.check(event);
			Structures.Builders.check(ticketBuilder);
			Structures.Builders.check(eventBuilder);

			final var store = new LinkedHashMap<String, Object>();
			final var builder = StructureView.of(store, ticketBuilder);

			ticketBuilder.getMethod("ticketId", String.class)
				.invoke(builder, "T-123");
			ticketBuilder.getMethod("ticketDate", LocalDate.class)
				.invoke(builder, LocalDate.of(2026, 7, 31));
			ticketBuilder.getMethod("ticketType", String.class)
				.invoke(builder, "VIP");
			ticketBuilder.getMethod("event", Consumer.class)
				.invoke(builder, (Consumer<Object>)nested -> {
					try {
						eventBuilder.getMethod("id", Long.class)
							.invoke(nested, 123L);
						eventBuilder.getMethod("name", String.class)
							.invoke(nested, "Concert");
					} catch (ReflectiveOperationException e) {
						throw new AssertionError(e);
					}
				});

			assertThat(store)
				.containsEntry("ticketId", "T-123")
				.containsEntry("ticketDate", LocalDate.of(2026, 7, 31))
				.containsEntry("ticketType", "VIP")
				.containsEntry("event", Map.of(
					"id", 123L,
					"name", "Concert"
				));
		}
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

	private static CompiledTypes compileStructuralTypes() throws Exception {
		final var dir = Files.createTempDirectory("structural-type-builder-test");
		try {
			final var model = new JCodeModel();
			new StructuralTypeBuilder()
				.name("io.jenetics.incubator.test.Event")
				.component("id", Long.class.getName())
				.component("name", String.class.getName())
				.build(model);

			new StructuralTypeBuilder()
				.name("io.jenetics.incubator.test.Ticket")
				.component("ticketId", String.class.getName())
				.component("ticketDate", LocalDate.class.getName())
				.component("ticketType", String.class.getName())
				.component("event", new ObjectSchema()
					.name("io.jenetics.incubator.test.Event"))
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
				dir.resolve("io/jenetics/incubator/test/Event.java").toString(),
				dir.resolve("io/jenetics/incubator/test/Ticket.java").toString()
			);

			assertThat(result).isZero();

			final var loader = URLClassLoader.newInstance(
				new java.net.URL[]{ dir.toUri().toURL() },
				StructuralTypeBuilderTest.class.getClassLoader()
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
