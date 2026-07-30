package io.jenetics.incubator.structural;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		final var structure = structureType(type);

		final var instance = Proxy.newProxyInstance(
			type.getClassLoader(),
			new Class<?>[]{ type },
			new StructureComponentHandler(store, type, structure)
		);

		return type.cast(instance);
	}

	private static Class<?> structureType(final Class<?> type) {
		if (isStructure(type)) {
			return type;
		}

		Structures.Builders.check(type);
		return Stream.of(type.getInterfaces())
			.filter(Structures::isStructure)
			.findFirst()
			.orElseThrow();
	}

	private record StructureComponentHandler(
		Map<String, Object> store,
		Class<?> type,
		Class<?> structure
	)
		implements InvocationHandler
	{
		private StructureComponentHandler {
			requireNonNull(store);
			requireNonNull(type);
			requireNonNull(structure);
		}

		@Override
		public Object invoke(
			final Object proxy,
			final Method method,
			final Object[] args
		) {
			return method.getDeclaringClass() == Object.class
				? invokeObject(method, args)
				: invokeStructural(method, args, proxy);
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

		private Object invokeStructural(
			final Method method,
			final Object[] args,
			final Object proxy
		) {
			return method.getParameterCount() == 0
				? invokeComponent(method)
				: invokeBuilder(method, args, proxy);
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

		private Object invokeBuilder(
			final Method method,
			final Object[] args,
			final Object proxy
		) {
			if (isNestedBuilderMethod(method)) {
				invokeNestedBuilder(method, args[0]);
			} else {
				store.put(method.getName(), args[0]);
			}

			return proxy;
		}

		private boolean isNestedBuilderMethod(final Method method) {
			final var component = componentType(method);
			return method.getParameterTypes()[0].equals(Consumer.class) &&
				isStructure(component);
		}

		private void invokeNestedBuilder(final Method method, final Object value) {
			@SuppressWarnings("unchecked")
			final var consumer = (Consumer<Object>)value;

			final var component = componentType(method);
			final var nested = nestedStore(method.getName());
			consumer.accept(of(nested, builderType(component)));
		}

		@SuppressWarnings("unchecked")
		private Map<String, Object> nestedStore(final String name) {
			return switch (store.get(name)) {
				case null -> {
					final var nested = new LinkedHashMap<String, Object>();
					store.put(name, nested);
					yield nested;
				}
				case Map<?, ?> nested -> (Map<String, Object>)nested;
				default -> throw new IllegalStateException(
					"Existing component '%s' is not a nested structure."
						.formatted(name)
				);
			};
		}

		private Class<?> componentType(final Method method) {
			return Structures.components(structure).stream()
				.filter(component -> component.getName().equals(method.getName()))
				.map(Method::getReturnType)
				.findFirst()
				.orElseThrow();
		}

		private static Class<?> builderType(final Class<?> type) {
			return Stream.of(type.getDeclaredClasses())
				.filter(cls -> cls.getSimpleName().equals("Builder"))
				.findFirst()
				.orElseThrow();
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Proxy proxy &&
				getInvocationHandler(proxy) instanceof StructureComponentHandler(
					var s,
					var t,
					var ignored
				) &&
				type.equals(t) &&
				store.equals(s);
		}

		@Override
		public int hashCode() {
			return Objects.hash(type, store);
		}

		@Override
		public String toString() {
			return format(structure, store);
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
