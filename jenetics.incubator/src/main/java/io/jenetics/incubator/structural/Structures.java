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

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper methods for checking and working with <em>structural</em> interfaces.
 */
public class Structures {
	private Structures() {
	}

	public static void check(Class<?> type) {
		final var msg = check0(type);
		if (msg != null) {
			throw new IllegalArgumentException(msg);
		}
	}

	private static String check0(Class<?> type) {
		// Type must be an interface.
		if (!type.isInterface()) {
			return "Type '%s' is not an interface."
				.formatted(type.getCanonicalName());
		}

		// Type must only contain accessor methods. These are methods with no
		// method arguments and a non-void return value.
		final var methods = new ArrayList<>(List.of(type.getMethods()));
		final var components = components(type);

		methods.removeAll(components);
		if (!methods.isEmpty()) {
			return "Found non-component methods: %s.".formatted(
				methods.stream()
					.map(Method::getName)
					.collect(Collectors.joining(", "))
			);
		}

		return null;
	}

	static List<Method> components(Class<?> type) {
		final var methods = type.getMethods();
		return Stream.of(methods)
			.filter(m -> m.getParameterCount() == 0)
			.toList();
	}

	public static boolean isStructure(Class<?> type) {
		return check0(type) == null;
	}

	public static final class Builders {
		private Builders() {
		}

		public static void check(Class<?> type) {
			final var msg = check0(type);
			if (msg != null) {
				throw new IllegalArgumentException(msg);
			}
		}

		private static String check0(final Class<?> type) {
			requireNonNull(type);

			if (!type.isInterface()) {
				return "Type '%s' is not an interface."
					.formatted(type.getCanonicalName());
			}

			final var structures = Stream.of(type.getInterfaces())
				.filter(Structures::isStructure)
				.toList();

			if (structures.size() != 1) {
				return "Builder '%s' must extend exactly one structural interface."
					.formatted(type.getCanonicalName());
			}

			final var structure = structures.getFirst();
			final var components = components(structure).stream()
				.collect(Collectors.toMap(Method::getName, identity()));

			for (var method : builderMethods(type, components)) {
				final var msg = checkMethod(type, components, method);
				if (msg != null) {
					return msg;
				}
			}

			for (var component : components.values()) {
				if (!hasComponentBuilderMethod(type, component)) {
					return "No builder method found for component '%s'."
						.formatted(component.getName());
				}
			}

			return null;
		}

		private static List<Method> builderMethods(
			final Class<?> type,
			final Map<String, Method> components
		) {
			return Stream.of(type.getMethods())
				.filter(method -> !isComponent(method, components))
				.toList();
		}

		private static boolean isComponent(
			final Method method,
			final Map<String, Method> components
		) {
			final var component = components.get(method.getName());
			return component != null &&
				method.getParameterCount() == 0 &&
				method.getReturnType().equals(component.getReturnType());
		}

		private static String checkMethod(
			final Class<?> type,
			final Map<String, Method> components,
			final Method method
		) {
			if (method.getParameterCount() != 1) {
				return "Builder method '%s' doesn't take exactly one argument."
					.formatted(method.getName());
			}

			if (!type.isAssignableFrom(method.getReturnType())) {
				return "Builder method '%s' doesn't return the builder type."
					.formatted(method.getName());
			}

			final var component = components.get(method.getName());
			if (component == null) {
				return "Builder method '%s' has no matching component."
					.formatted(method.getName());
			}

			if (!method.getParameterTypes()[0].equals(component.getReturnType()) &&
				!isNestedBuilderMethod(method, component.getReturnType()))
			{
				return "Builder method '%s' doesn't take the component type."
					.formatted(method.getName());
			}

			return null;
		}

		private static boolean hasComponentBuilderMethod(
			final Class<?> type,
			final Method component
		) {
			return Stream.of(type.getMethods())
				.anyMatch(method ->
					method.getName().equals(component.getName()) &&
					method.getParameterCount() == 1 &&
					method.getParameterTypes()[0].equals(component.getReturnType()) &&
					type.isAssignableFrom(method.getReturnType())
				);
		}

		private static boolean isNestedBuilderMethod(
			final Method method,
			final Class<?> componentType
		) {
			return isStructure(componentType) &&
				method.getParameterTypes()[0].equals(java.util.function.Consumer.class) &&
				builderType(componentType) instanceof Class<?> builder &&
				consumerType(method.getGenericParameterTypes()[0], builder);
		}

		private static Class<?> builderType(final Class<?> type) {
			return Stream.of(type.getDeclaredClasses())
				.filter(cls -> cls.getSimpleName().equals("Builder"))
				.findFirst()
				.orElse(null);
		}

		private static boolean consumerType(
			final Type type,
			final Class<?> builder
		) {
			if (type instanceof ParameterizedType parameterized &&
				parameterized.getRawType().equals(java.util.function.Consumer.class))
			{
				final var argument = parameterized.getActualTypeArguments()[0];
				return argument.equals(builder) ||
					argument instanceof WildcardType wildcard &&
					wildcard.getLowerBounds().length == 1 &&
					wildcard.getLowerBounds()[0].equals(builder);
			}

			return false;
		}
	}

}
