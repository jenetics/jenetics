package io.jenetics.incubator.web.openapi.structural;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

import javax.tools.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumBuilderTest {

	@Test
	public void enumConstantsKeepOriginalStringValue() throws IOException {
		final var model = new JCodeModel();

		new EnumBuilder(model, "io.jenetics.incubator.test.TicketType")
			.constant("vip-ticket")
			.constant("123 basic")
			.constant("standard");

		final var source = source(model);

		assertThat(source).contains("VIP_TICKET(\"vip-ticket\")");
		assertThat(source).contains("_123_BASIC(\"123 basic\")");
		assertThat(source).contains("STANDARD(\"standard\")");
		assertThat(source).contains("private final String value;");
		assertThat(source).contains("TicketType(String value)");
		assertThat(source).contains("this.value = value;");
		assertThat(source).contains("public String value()");
		assertThat(source).contains("return value;");
		assertThat(source).contains("@Override");
		assertThat(source).contains("public String toString()");
	}

	@Test
	public void parseEnumNameAndValue() throws Exception {
		final var type = compileEnum();

		final var parse = type.getMethod("parse", String.class);
		final var byValue = (Optional<?>)parse.invoke(null, "vip-ticket");
		final var byName = (Optional<?>)parse.invoke(null, "VIP_TICKET");
		final var unknown = (Optional<?>)parse.invoke(null, "unknown");

		assertThat(byValue).isPresent();
		assertThat(byValue.get()).isEqualTo(byName.get());
		assertThat(((Enum<?>)byValue.get()).name()).isEqualTo("VIP_TICKET");
		assertThat(unknown).isEmpty();
	}

	private static String source(final JCodeModel model) throws IOException {
		final var out = new ByteArrayOutputStream();
		final var writer = new JCMWriter(model);
		writer.build(
			new OutputStreamCodeWriter(out, StandardCharsets.UTF_8)
		);
		return out.toString(StandardCharsets.UTF_8);
	}

	private static Class<?> compileEnum() throws Exception {
		final var dir = Files.createTempDirectory("enum-generator-test");
		try {
			final var model = new JCodeModel();
			new EnumBuilder(model, "io.jenetics.incubator.test.TicketType")
				.constant("vip-ticket");

			final var writer = new JCMWriter(model);
			writer.setCharset(Charset.defaultCharset());
			writer.build(dir.toFile());

			final var source = dir.resolve(
				Path.of("io/jenetics/incubator/test/TicketType.java")
			);
			final var compiler = ToolProvider.getSystemJavaCompiler();
			final var result = compiler.run(
				null,
				null,
				null,
				"-d",
				dir.toString(),
				source.toString()
			);

			assertThat(result).isZero();

			final var loader = URLClassLoader.newInstance(
				new java.net.URL[]{ dir.toUri().toURL() }
			);
			return loader.loadClass("io.jenetics.incubator.test.TicketType");
		} finally {
			delete(dir);
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
