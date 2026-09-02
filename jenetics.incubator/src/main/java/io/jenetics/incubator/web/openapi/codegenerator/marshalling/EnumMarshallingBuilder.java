package io.jenetics.incubator.web.openapi.codegenerator.marshalling;

import static com.helger.jcodemodel.JMod.PUBLIC;
import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.internal.JCodeModels.class_;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Stream;

import io.jenetics.incubator.web.openapi.codegenerator.API;
import io.jenetics.incubator.web.openapi.codegenerator.ApiContext;
import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.model.EnumSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.TypedSchema;

final class EnumMarshallingBuilder {

	private final EnumSchema schema;
	private final String namespace;

	private JDefinedClass serializer;
	private JDefinedClass deserializer;

	EnumMarshallingBuilder(final EnumSchema schema, final String namespace) {
		this.schema = requireNonNull(schema);
		this.namespace = requireNonNull(namespace);
	}

	JDefinedClass serializer() {
		return serializer;
	}

	JDefinedClass deserializer() {
		return deserializer;
	}

	public void build(final JCodeModel model) {
		serializer = serializer(model);
		deserializer = deserializer(model);
	}

	private JDefinedClass serializer(final JCodeModel model) {
		final var enumType = model.ref(schema.name().toString());

		// public class EmailSerializer extends JsonSerializer<Email>
		final var serializer = class_(
				model,
				new Qname(namespace, schema.name().name() + "Serializer")
			)
			._extends(model.ref(JsonSerializer.class).narrow(enumType));

		// @Override
		// public void serialize(Email value, JsonGenerator gen, SerializerProvider ser)
		//     throws IOException;
		final var serialize = serializer.method(PUBLIC, model.VOID, "serialize");
		serialize.annotate(Override.class);
		serialize._throws(IOException.class);
		final var value = serialize.param(enumType, "value");
		final var gen = serialize.param(JsonGenerator.class, "gen");
		serialize.param(SerializerProvider.class, "ser");

		// gen.writeObject(Email.unbox(value));
		serialize.body()
			.add(
				gen.invoke("writeObject").arg(
					enumType.staticInvoke("unbox").arg(value)
				)
			);

		return serializer;
	}

	private JDefinedClass deserializer(final JCodeModel model) {
		final var enumType = model.ref(schema.name().toString());

		// public class EmailDeserializer extends JsonDeserializer<Email>
		final var deserializer = class_(
				model,
				new Qname(namespace, schema.name().name() + "Deserializer")
			)
			._extends(model.ref(JsonDeserializer.class).narrow(enumType));

		// @Override
		// public Email deserialize(JsonParser p, DeserializationContext ctx)
		//     throws IOException
		final var deserialize = deserializer.method(PUBLIC, enumType, "deserialize");
		deserialize.annotate(Override.class);
		deserialize._throws(IOException.class);
		final var p = deserialize.param(JsonParser.class, "p");
		deserialize.param(DeserializationContext.class, "ctx");

		// return Email.box(p.getValueAsString());
		deserialize.body()._return(
			enumType.staticInvoke("box")
				.arg(p.invoke("getValueAsString"))
			);

		return deserializer;
	}


	/* *************************************************************************
	 * Create the marshaling classes for the example museum API.
	 * ************************************************************************/

	static void main() throws IOException {
		final var api =  API.readResource("/museum-api.yaml");

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
