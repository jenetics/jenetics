package io.jenetics.incubator.restfulclient;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public final class DefaultClient {

	private final URI host;
	private final HttpClient client;
	private final ObjectMapper mapper;

	public DefaultClient(
		final URI host,
		final HttpClient client,
		final ObjectMapper mapper
	) {
		this.host = host.normalize();
		this.client = requireNonNull(client);
		this.mapper = requireNonNull(mapper);
	}

	public DefaultClient(String host) {
		this(URI.create(host), HttpClient.newHttpClient(), new ObjectMapper());
	}

	public <T> Response<T> call(Resource<? extends T> resource) {
		try {
			final HttpResponse<?> result = client.send(
				toRequest(resource),
				BodyHandlers.jackson(mapper, resource.type())
			);

			if (result.body() instanceof ErrorInfo error) {
				return new Response.Failure<>(
					result.statusCode(),
					result.headers(),
					resource,
					error
				);
			} else {
				return new Response.Success<>(
					result.statusCode(),
					result.headers(),
					resource,
					resource.type().cast(result.body())
				);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new CancellationException(e.getMessage());
		}
	}

	private <T> HttpRequest toRequest(final Resource<? extends T> resource) {
		final var builder = HttpRequest.newBuilder()
			.uri(host.resolve(resource.resolvedPath().substring(1)));

		resource.parameters().stream()
			.filter(p -> p instanceof Parameter.Header)
			.forEach(header -> builder.header(header.key(), header.value()));

		switch (resource.method()) {
			case GET -> builder.GET();
			case POST -> builder.POST(BodyPublishers.jackson(resource.body().orElse(null), mapper));
			case PUT -> builder.PUT(BodyPublishers.jackson(resource.body().orElse(null), mapper));
			case DELETE -> builder.DELETE();
		}

		return builder.build();
	}

	public <T> CompletableFuture<Response<T>> callAsync(Resource<? extends T> resource) {
		final CompletableFuture<? extends HttpResponse<?>> res =
			client.sendAsync(
				toRequest(resource),
				BodyHandlers.jackson(mapper, resource.type())
			);

		return res.thenApply(response -> {
			if (response.body() instanceof ErrorInfo error) {
				return new Response.Failure<>(
					response.statusCode(),
					response.headers(),
					resource,
					error
				);
			} else {
				return new Response.Success<>(
					response.statusCode(),
					response.headers(),
					resource,
					resource.type().cast(response.body())
				);
			}
		});
	}

	public <T> Mono<T> callReactive(Resource<? extends T> resource) {
		return Mono.create(sink -> {
			final var subscriber = new Flow.Subscriber<List<ByteBuffer>>() {
				Flow.Subscription subscription;
				@Override
				public void onSubscribe(Flow.Subscription subscription) {
					this.subscription = subscription;
					subscription.request(1);
				}
				@Override
				public void onNext(List<ByteBuffer> item) {
					subscription.request(1);
					//sink.success(item);
				}
				@Override
				public void onError(Throwable throwable) {
					subscription.cancel();
					sink.error(throwable);
				}
				@Override
				public void onComplete() {
					subscription.cancel();
				}
			};

			client.sendAsync(
				toRequest(resource),
				HttpResponse.BodyHandlers.fromSubscriber(subscriber)
			);
		});
	}

}
