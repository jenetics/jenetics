package io.jenetics.incubator.beans;

import java.lang.constant.Constable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.TemporalAccessor;
import java.util.List;

public final class Types {
	private Types() {
	}

	public record ArrayType(Class<?> arrayType, Class<?> componentType) {
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

	public record ListType(Class<?> listType, Class<?> componentType) {
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

	public record RecordType(Class<?> type) {
		public static Object of(final Type type) {
			if (type instanceof Class<?> cls && cls.isRecord()) {
				return new RecordType(cls);
			} else {
				return null;
			}
		}
	}

	public record BeanType(Class<?> type) {
		public static Object of(final Type type) {
			if (type instanceof ParameterizedType pt &&
				pt.getRawType() instanceof Class<?> rt
			) {
				return new BeanType(rt);
			} else if (type instanceof Class<?> cls) {
				return new BeanType(cls);
			} else {
				return null;
			}
		}
	}

	public static boolean isIdentityType(final Object object) {
		return
			object != null &&
				!(object instanceof Constable) &&
				!(object instanceof TemporalAccessor) &&
				!(object instanceof Number);
	}

}
