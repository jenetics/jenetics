package io.jenetics.incubator.web.openapi.codegenerator;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Objects;

public final class Context {

	private static final ScopedValue<Context> INSTANCE = ScopedValue.newInstance();
	private final OpenAPI api;
	private final SchemaQname qname;

	public Context(OpenAPI api, SchemaQname qname) {
		this.api = api;
		this.qname = qname;
	}

	public Context(OpenAPI api, String namespace) {
		this(api, schema -> new Qname(namespace, schema.getName()));
	}

	@FunctionalInterface
	public interface SchemaQname {
		Qname of(Schema<?> schema);
	}

	public void run(Runnable op) {
		ScopedValue.where(INSTANCE, this).run(op);
	}

	public static Qname qnameOf(Schema<?> schema) {
		return get().qname.of(schema);
	}

	public static Context get() {
		return INSTANCE.get();
	}

	public static OpenAPI api() {
		return get().api;
	}

}
