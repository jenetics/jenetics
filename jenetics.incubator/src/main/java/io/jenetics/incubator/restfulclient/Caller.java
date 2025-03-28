package io.jenetics.incubator.restfulclient;

@FunctionalInterface
public interface Caller<T, C> {
	C call(Resource<? extends T> resource);
}
