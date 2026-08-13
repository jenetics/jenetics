package io.jenetics.incubator.web.openapi.codegenerator.marshalling;

import static io.jenetics.incubator.web.openapi.codegenerator.ApiContext.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.helger.jcodemodel.JCodeModel;
import com.museum.model.Email;

import java.io.IOException;

public class JacksonModuleBuilder {

	private JacksonModuleBuilder() {
	}

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

	public static void build(final JCodeModel model) {
		api().getComponents().getSchemas().values()
			.forEach(schema -> {});
	}

	static void main() throws IOException {

	}

}
