package io.jenetics.incubator.restfulclient;

public interface Rest<T> {

	<C> C GET(final Caller<? super T, ? extends C> caller);

	<C> C PUT(final Object body, final Caller<? super T, ? extends C> caller);

	<C> C POST(final Object body, final Caller<? super T, ? extends C> caller);

	<C> C DELETE(final Caller<? super T, ? extends C> caller);

}
