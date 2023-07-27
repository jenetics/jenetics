package io.jenetics.incubator.property;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class Methods {
	private Methods() {
	}

	static Getter toGetter(final Method method) {
		return object -> {
			try {
				return method.invoke(object);
			} catch (IllegalAccessException | InvocationTargetException e) {
				return null;
			}
		};
	}

	static Setter toSetter(final Method method) {
		return (object, value) -> {
			try {
				if (method != null) {
					method.invoke(object, value);
					return true;
				}
			} catch (IllegalAccessException ignore) {
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof RuntimeException re) {
					throw re;
				} else {
					throw new IllegalStateException(e.getTargetException());
				}
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid argument: " + value, e);
			}

			return false;
		};
	}


}
