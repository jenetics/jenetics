package io.jenetics.incubator.beans.description;

public sealed interface Accessor {

	Getter getter();

	record Readonly(Getter getter) implements Accessor {}

	record Writable(Getter getter, Setter setter) implements Accessor {}

}
