package io.jenetics.incubator.beans.description;

public sealed interface IndexedAccessor {

	Size size();

	IndexedGetter getter();

	record Readonly(Size size, IndexedGetter getter) implements IndexedAccessor {}

	record Writable(Size size, IndexedGetter getter, IndexedSetter setter) implements IndexedAccessor {}

}
