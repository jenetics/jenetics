package io.jenetics.incubator.metamodel.property;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.access.Accessor;
import io.jenetics.incubator.metamodel.access.Writer;

abstract class AbstractProperty {

	final PropParam param;

	AbstractProperty(final PropParam param) {
		this.param = requireNonNull(param);
	}

	public Path path() {
		return param.path();
	}

	public Object enclosure() {
		return param.enclosure();
	}

	public Object value() {
		return param.value();
	}

	public Class<?> type() {
		return param.type();
	}

	public Stream<Annotation> annotations() {
		return param.annotations().stream();
	}

	public Object read() {
		return param.accessor().getter().get();
	}

	public Optional<Writer> writer() {
		return param.accessor() instanceof Accessor.Writable
			? Optional.of(this::write)
			: Optional.empty();
	}

	/**
	 * Writes a new value to the property.
	 *
	 * @param value the new property value
	 * @return {@code true} if the new value has been written, {@code false}
	 * otherwise
	 */
	private boolean write(final Object value) {
		try {
			if (param.accessor() instanceof Accessor.Writable(var __, var setter)) {
				setter.set(value);
				return true;
			} else {
				return false;
			}
		} catch (VirtualMachineError | LinkageError e) {
			throw e;
		} catch (Throwable e) {
			return false;
		}
	}

}
