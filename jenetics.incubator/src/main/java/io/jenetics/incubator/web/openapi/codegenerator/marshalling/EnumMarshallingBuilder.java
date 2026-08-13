package io.jenetics.incubator.web.openapi.codegenerator.marshalling;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.builder.ModelBuilder.read;
import static io.jenetics.incubator.web.openapi.codegenerator.internal.JCodeModels.class_;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Stream;

import io.jenetics.incubator.web.openapi.codegenerator.ApiContext;
import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.model.EnumSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.TypedSchema;

public final class EnumMarshallingBuilder {

	private final EnumSchema schema;
	private final String namespace;

	private EnumMarshallingBuilder(
		final EnumSchema schema,
		final String namespace
	) {
		this.schema = requireNonNull(schema);
		this.namespace = requireNonNull(namespace);
	}

	public void build(final JCodeModel model) {
		final var serializer = serializer(model);
		final var deserializer = deserializer(model);
	}

	private JDefinedClass serializer(final JCodeModel model) {
		final var enumType = model.ref(schema.name().toString());
		final var genType = model.ref(JsonGenerator.class);
		final var serType = model.ref(SerializerProvider.class);
		final var jsonSerType = model.ref(JsonSerializer.class).narrow(enumType);

		final var serializer = class_(
			model,
			new Qname(
				namespace,
				schema.name().name() + "Serializer"
			)
		);
		serializer._extends(jsonSerType);


		final var serialize = serializer
			.method(JMod.PUBLIC, model.VOID, "serialize");
		serialize.annotate(Override.class);
		serialize._throws(IOException.class);

		final var enumValue = serialize.param(enumType, "value");
		final var genValue = serialize.param(genType, "gen");
		final var serValue = serialize.param(serType, "ser");

		// gen.writeObject(Email.unbox(value));
		serialize.body()
			.add(
				genValue
					.invoke("writeObject")
					.arg(
						enumType
							.staticInvoke("unbox")
							.arg(enumValue)
					)
			);

		return serializer;
	}

	private JDefinedClass deserializer(final JCodeModel model) {
		final var enumType = model.ref(schema.name().toString());
		final var jsonDeserType = model.ref(JsonDeserializer.class).narrow(enumType);

		final var deserializer = class_(
			model,
			new Qname(
				namespace,
				schema.name().name() + "Deserializer"
			)
		);
		deserializer._extends(jsonDeserType);


		final var deserialize = deserializer
			.method(JMod.PUBLIC, enumType, "deserialize");
		deserialize.annotate(Override.class);
		deserialize._throws(IOException.class);

		final var parser = deserialize.param(JsonParser.class, "p");
		final var context = deserialize.param(DeserializationContext.class, "ctx");

		// return Email.box(p.getValueAsString());
		deserialize.body()._return(
			enumType.staticInvoke("box")
				.arg(parser.invoke("getValueAsString"))
			);

		return deserializer;
	}

	static void main() throws IOException {
		final var api = read("/museum-api.yaml");
		api.getComponents().getSchemas()
			.forEach((name, schema) -> schema.setName(name));

		final var model = new JCodeModel();
		new ApiContext(api, "com.museum.model").run(() -> {
			final var enumSchema = api.getComponents().getSchemas().values().stream()
				.flatMap(schema -> switch (TypedSchema.of(schema)) {
					case EnumSchema s -> Stream.of(s);
					default -> Stream.empty();
				})
				.findAny()
				.orElseThrow();
;
			final var builder = new EnumMarshallingBuilder(enumSchema, "com.museum.serializer");
			builder.build(model);
		});

		var writer = new JCMWriter(model);
		writer.setCharset(Charset.defaultCharset());
		writer.build(new OutputStreamCodeWriter(System.out, Charset.defaultCharset()));
	}
}
