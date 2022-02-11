package io.jenetics.incubator.bean;

public interface Property {

	String path();

	Class<?> type();

	String name();

	Object value();

}
