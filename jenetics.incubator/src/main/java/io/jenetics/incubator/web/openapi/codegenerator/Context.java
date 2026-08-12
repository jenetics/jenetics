package io.jenetics.incubator.web.openapi.codegenerator;

import static java.util.Objects.requireNonNull;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * Holds the OpenAPI model context, needed for the code generation.
 */
public final class Context {

	private static final ScopedValue<Context> INSTANCE = ScopedValue.newInstance();

	private final OpenAPI api;
	private final String namespace;

	public Context(OpenAPI api, String namespace) {
		this.api = requireNonNull(api);
		this.namespace = requireNonNull(namespace);
	}

	public void run(Runnable op) {
		ScopedValue.where(INSTANCE, this).run(op);
	}

	public static Context get() {
		return INSTANCE.get();
	}

	public static OpenAPI api() {
		return get().api;
	}

	public static String namespace() {
		return get().namespace;
	}

}
