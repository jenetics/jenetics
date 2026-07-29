package io.jenetics.incubator.structural;

import java.lang.reflect.Proxy;
import java.util.Map;

import static io.jenetics.incubator.structural.Structures.isStructure;

public class StructureView {
	private StructureView() {
	}

	public static <T> T of(Map<String, Object> store, Class<T> type) {
		Structures.check(type);

		final var instance = Proxy.newProxyInstance(
			type.getClassLoader(),
			new Class<?>[]{ type },
			(proxy, method, args) -> {
				final var value = store.get(method.getName());
				return switch (value) {
					case Map<?, ?> map when isStructure(method.getReturnType()) -> {
						@SuppressWarnings("unchecked")
						final var str = (Map<String, Object>)map;
						yield of(str, method.getReturnType());
					}
					default -> value;
				};
			}
		);

		return type.cast(instance);
	}

}
