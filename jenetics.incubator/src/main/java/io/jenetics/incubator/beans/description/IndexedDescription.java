package io.jenetics.incubator.beans.description;

import java.lang.reflect.Type;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.reflect.IndexedType;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public record IndexedDescription(
	Path path,
	Class<?> enclosure,
	Type type,
	IndexedAccessor accessor
)
	implements Description
{

	static IndexedDescription of(final Path path, final IndexedType type) {
		return new IndexedDescription(
			path.append(new Path.Index(0)),
			type.type(),
			type.componentType(),
			type.isMutable()
				? new IndexedAccessor.Writable(type::size, type::get, type::set)
				: new IndexedAccessor.Readonly(type::size, type::get)
		);
	}

}
