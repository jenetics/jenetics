package io.jenetics.incubator.web.openapi.structural;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class TypedValueGeneratorTest {

	@Test
	public void typedValueHasValueComponent() throws IOException {
		final var model = new JCodeModel();

		new TypedValueGenerator(
			model,
			"io.jenetics.incubator.test.Price",
			"java.math.BigDecimal"
		);

		assertThat(source(model))
			.contains("import java.math.BigDecimal;")
			.contains("record Price(BigDecimal value)")
			.contains("requireNonNull(value);");
	}

	@Test
	public void primitiveTypedValueHasNoNullCheck() throws IOException {
		final var model = new JCodeModel();

		new TypedValueGenerator(
			model,
			"io.jenetics.incubator.test.Count",
			"int"
		);

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
