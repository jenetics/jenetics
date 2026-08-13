package io.jenetics.incubator.web.openapi.codegenerator.marshalling;

import static com.helger.jcodemodel.JMod.PUBLIC;
import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.web.openapi.codegenerator.internal.JCodeModels.class_;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;
import io.swagger.v3.oas.models.OpenAPI;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import io.jenetics.incubator.web.openapi.codegenerator.API;
import io.jenetics.incubator.web.openapi.codegenerator.Qname;
import io.jenetics.incubator.web.openapi.codegenerator.builder.CodeBuilder;
import io.jenetics.incubator.web.openapi.codegenerator.builder.ModelBuilder;
import io.jenetics.incubator.web.openapi.codegenerator.model.EnumSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.GenericSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.StructuralTypeSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.TypedSchema;
import io.jenetics.incubator.web.openapi.codegenerator.model.TypedValueSchema;

public final class JacksonModuleBuilder implements CodeBuilder {

	private final OpenAPI api;
	private final List<TypedSchema> schemas;
	private final String namespace;

	public JacksonModuleBuilder(
		final OpenAPI api,
		final List<TypedSchema> schemas,
		final String namespace
	) {
		this.api = requireNonNull(api);
		this.schemas = requireNonNull(schemas);
		this.namespace = requireNonNull(namespace);
	}

	public void build(final JCodeModel model) {
		final var module = class_(
				model,
				new Qname(namespace, "ApiJacksonModule")
			)
			._extends(model.ref(Module.class));

		final var getModuleName = module.method(PUBLIC, String.class, "getModuleName");
		getModuleName.annotate(Override.class);
		getModuleName.body()._return(JExpr.lit("MyModule"));

		final var setupModule = module.method(PUBLIC,  model.VOID, "setupModule");
		setupModule.annotate(Override.class);
		final var ctx = setupModule.param(Module.SetupContext.class, "ctx");

		final var body = setupModule.body();
		final var serializers = body.decl(
			model.ref(SimpleSerializers.class),
			"serializers",
			JExpr._new(model.ref(SimpleSerializers.class))
		);
		final var deserializers = body.decl(
			model.ref(SimpleDeserializers.class),
			"deserializers",
			JExpr._new(model.ref(SimpleDeserializers.class))
		);

		// Add the serializer/deserializer.
		schemas.forEach(schema -> {
			switch (schema) {
				case EnumSchema s -> {
					final var builder = new EnumMarshallingBuilder(s, namespace);
					builder.build(model);

					body.add(
						serializers.invoke("addSerializer")
							.arg(JExpr.dotClass(model.ref(s.name().toString())))
							.arg(JExpr._new(builder.serializer()))
					);
					body.add(
						serializers.invoke("addDeserializer")
							.arg(JExpr.dotClass(model.ref(s.name().toString())))
							.arg(JExpr._new(builder.deserializer()))
					);
				}
				case TypedValueSchema _ -> {}
				case StructuralTypeSchema _ -> {}
				case GenericSchema _ -> {}
			}
		});

		body.add(ctx.invoke("addSerializers").arg(serializers));
		body.add(ctx.invoke("addDeserializers").arg(deserializers));
	}


	/*
	static final class EmailSerializer extends JsonSerializer<Email> {
		@Override
		public void serialize(Email value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException
		{
			gen.writeObject(Email.unbox(value));
		}
	}

	static final class EmailDeserializer extends JsonDeserializer<Email> {
		@Override
		public Email deserialize(JsonParser p, DeserializationContext ctx)
			throws IOException
		{
			return Email.box(p.getValueAsString());
		}
	}

	static final class JacksonModule extends Module {
		@Override
		public String getModuleName() {
			return "MyModule";
		}

		@Override
		public void setupModule(Module.SetupContext context) {
			final var serializers = new SimpleSerializers();
			serializers.addSerializer(Email.class, new EmailSerializer());
			context.addSerializers(serializers);

			final var deserializers = new SimpleDeserializers();
			deserializers.addDeserializer(Email.class, new EmailDeserializer());
			context.addDeserializers(deserializers);
		}

		@Override
		public Version version() {
			return new Version(0,0,1,"SNAPSHOT", "group.id",  "artifactId");
		}
	}
	 */

	static void main() throws IOException {
		final var api =  API.readResource("/museum-api.yaml");
		final var schemas = new ModelBuilder(api, "com.museum.model").schemas();
		final var builder = new JacksonModuleBuilder(api, schemas, "com.museum.model.jackson");

		final var model = new JCodeModel();
		builder.build(model);

		var writer = new JCMWriter(model);
		writer.setCharset(Charset.defaultCharset());
		writer.build(new OutputStreamCodeWriter(System.out, Charset.defaultCharset()));
	}

}
