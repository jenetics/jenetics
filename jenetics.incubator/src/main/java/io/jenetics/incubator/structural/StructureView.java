package io.jenetics.incubator.structural;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.jenetics.incubator.structural.Structures.isStructure;
import static java.lang.reflect.Proxy.getInvocationHandler;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class StructureView {
	private StructureView() {
	}

	public static <T> T of(Map<String, Object> store, Class<T> type) {
		requireNonNull(store);
		requireNonNull(type);
		Structures.check(type);

		final var instance = Proxy.newProxyInstance(
			type.getClassLoader(),
			new Class<?>[]{ type },
			new StructureComponentHandler(store, type)
		);

		return type.cast(instance);
	}

	private record StructureComponentHandler(
		Map<String, Object> store,
		Class<?> type
	)
		implements InvocationHandler
	{
		private StructureComponentHandler {
			requireNonNull(store);
			requireNonNull(type);
		}

		@Override
		public Object invoke(
			final Object proxy,
			final Method method,
			final Object[] args
		) {
			return method.getDeclaringClass() == Object.class
				? invokeObject(method, args)
				: invokeComponent(method);
		}

		private Object invokeObject(final Method method, final Object[] args) {
			return switch (method.getName()) {
				case "toString" -> toString();
				case "hashCode" -> hashCode();
				case "equals" -> equals(args[0]);
				default -> throw new AssertionError(
					"Unknown Object method: " + method
				);
			};
		}

		private Object invokeComponent(final Method method) {
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

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Proxy proxy &&
				getInvocationHandler(proxy) instanceof StructureComponentHandler(var s, var t) &&
				type.equals(t) &&
				store.equals(s);
		}

		@Override
		public int hashCode() {
			return Objects.hash(type, store);
		}

		@Override
		public String toString() {
			return format(type, store);
		}
	}

	private static String format(
		final Class<?> type,
		final Map<String, Object> store
	) {
		final var components = Structures.components(type).stream()
			.collect(Collectors.toMap(Method::getName, Method::getReturnType));

		return store.entrySet().stream()
			.filter(entry -> components.containsKey(entry.getKey()))
			.map(entry ->
				"%s=%s".formatted(
					entry.getKey(),
					format(components.get(entry.getKey()), entry.getValue())
				)
			)
			.collect(joining(", ", type.getSimpleName() + "[", "]"));
	}

	private static Object format(final Class<?> type, final Object value) {
		return switch (value) {
			case Map<?, ?> map when isStructure(type) -> {
				@SuppressWarnings("unchecked")
				final var str = (Map<String, Object>)map;
				yield format(type, str);
			}
			default -> value;
		};
	}

}
