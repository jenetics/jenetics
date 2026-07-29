package io.jenetics.incubator.structural;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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

}
