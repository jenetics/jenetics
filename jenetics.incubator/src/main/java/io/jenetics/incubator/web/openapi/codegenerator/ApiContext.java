package io.jenetics.incubator.web.openapi.codegenerator;

import static java.util.Objects.requireNonNull;

import io.swagger.v3.oas.models.OpenAPI;

import java.util.function.Supplier;

/**
 * Holds the OpenAPI model context, needed for the code generation.
 */
public final class ApiContext {

	private static final ScopedValue<ApiContext> INSTANCE = ScopedValue.newInstance();

	private final OpenAPI api;
	private final String namespace;

	public ApiContext(OpenAPI api, String namespace) {
		this.api = requireNonNull(api);
		this.namespace = requireNonNull(namespace);
	}

	public void run(Runnable op) {
		ScopedValue.where(INSTANCE, this).run(op);
	}

	public <T> T call(Supplier<? extends T> fn) {
		return ScopedValue.where(INSTANCE,this).call(fn::get);
	}

	public static ApiContext get() {
		return INSTANCE.get();
	}

	public static OpenAPI api() {
		return get().api;
	}

	public static String namespace() {
		return get().namespace;
	}

}
