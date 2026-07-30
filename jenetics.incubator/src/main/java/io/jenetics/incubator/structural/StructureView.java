/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.structural;

import static java.lang.reflect.Proxy.getInvocationHandler;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static io.jenetics.incubator.structural.Structures.isStructure;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StructureView {
	private StructureView() {
	}

	public static <T> T of(Map<String, Object> store, Class<T> type) {
		return of(store, type, new Class<?>[0]);
	}

	public static <T> T of(
		Map<String, Object> store,
		Class<T> type,
		Class<?>... additionalTypes
	) {
		requireNonNull(store);
		requireNonNull(type);
		requireNonNull(additionalTypes);

		final var types = types(type, additionalTypes);
		final List<Class<?>> structures = types.stream()
			.map(StructureView::structureType)
			.collect(Collectors.toUnmodifiableList());
		final var components = components(structures);

		final var instance = Proxy.newProxyInstance(
			type.getClassLoader(),
			types.toArray(Class<?>[]::new),
			new StructureComponentHandler(store, types, structures.getFirst(), components)
		);

		return type.cast(instance);
	}

	private static List<Class<?>> types(
		final Class<?> type,
		final Class<?>[] additionalTypes
	) {
		return Stream.concat(
				Stream.of(type),
				Arrays.stream(additionalTypes).map(Objects::requireNonNull)
			)
			.toList();
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

	private static Map<String, Class<?>> components(final List<Class<?>> structures) {
		final var components = new LinkedHashMap<String, Class<?>>();

		for (var structure : structures) {
			for (var component : Structures.components(structure)) {
				final var existing = components.putIfAbsent(
					component.getName(),
					component.getReturnType()
				);

				if (existing != null && !existing.equals(component.getReturnType())) {
					throw new IllegalArgumentException(
						"Conflicting component type for '%s'."
							.formatted(component.getName())
					);
				}
			}
		}

		return components;
	}

	private record StructureComponentHandler(
		Map<String, Object> store,
		List<Class<?>> types,
		Class<?> primaryStructure,
		Map<String, Class<?>> components
	)
		implements InvocationHandler
	{
		private StructureComponentHandler {
			requireNonNull(store);
			requireNonNull(types);
			requireNonNull(primaryStructure);
			requireNonNull(components);
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
			return components.get(method.getName());
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
					var ts,
					var ignored,
					var ignoredComponents
				) &&
				types.equals(ts) &&
				store.equals(s);
		}

		@Override
		public int hashCode() {
			return Objects.hash(types, store);
		}

		@Override
		public String toString() {
			return format(primaryStructure, store);
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
