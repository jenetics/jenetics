package io.jenetics.incubator.http;

import java.util.Map;
import java.util.Optional;

public sealed interface Request<T> {

	/**
	 * Return the request's response type.
	 *
	 * @return the request's response type
	 */
	Class<T> type();

	/**
	 * Return the request headers.
	 *
	 * @return the request headers
	 */
	Headers headers();

	default <C> C execute(final Endpoint<? super T, ? extends C> endpoint) {
		return endpoint.call(this);
	}

	non-sealed interface GET<T> extends Request<T> {
		static <T> GET<T> of(Class<T> type, Headers headers) {
			record GETRecord<T>(Class<T> type, Headers headers)
				implements GET<T> { }

			return new GETRecord<>(type, headers);
		}

		static <T> GET<T> of(Class<T> type) {
			return of(type, new Headers(Map.of()));
		}
	}

	non-sealed interface PUT<T> extends Request<T> {
		Optional<Object> body();
	}

	non-sealed interface POST<T> extends Request<T> {
		Optional<Object> body();
	}

	non-sealed interface DELETE<T> extends Request<T> {
		static <T> DELETE<T> of(Class<T> type, Headers headers) {
			record DELETERecord<T>(Class<T> type, Headers headers)
				implements DELETE<T> { }

			return new DELETERecord<>(type, headers);
		}

		static <T> DELETE<T> of(Class<T> type) {
			return of(type, new Headers(Map.of()));
		}
	}

}
