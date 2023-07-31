package io.jenetics.incubator.beans.description;

import java.lang.reflect.Type;

public record ArrayType(Class<?> type, Class<?> component) {

	public static Object of(final Type type) {
		if (type instanceof Class<?> arrayType &&
			arrayType.isArray() &&
			!arrayType.getComponentType().isPrimitive())
		{
			return new ArrayType(arrayType, arrayType.getComponentType());
		} else {
			return null;
		}
	}

}
