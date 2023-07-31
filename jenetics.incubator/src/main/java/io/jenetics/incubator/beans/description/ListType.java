package io.jenetics.incubator.beans.description;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public record ListType(Class<?> type, Class<?> component) {

	public static Object of(final Type type) {
		if (type instanceof ParameterizedType parameterizedType &&
			parameterizedType.getRawType() instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			final var typeArguments = parameterizedType.getActualTypeArguments();
			if (typeArguments.length == 1 &&
				typeArguments[0] instanceof Class<?> componentType)
			{
				return new ListType(listType, componentType);
			}
		}

		if (type instanceof Class<?> listType &&
			List.class.isAssignableFrom(listType))
		{
			return new ListType(listType, Object.class);
		}

		return null;
	}

}
