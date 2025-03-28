package io.jenetics.incubator.restfulclient;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public final class Client {

	private final HttpClient client;
	private final ObjectMapper mapper;

	public Client(final HttpClient client, final ObjectMapper mapper) {
		this.client = requireNonNull(client);
		this.mapper = requireNonNull(mapper);
	}

	public Client() {
		this(HttpClient.newHttpClient(), new ObjectMapper());
	}

	public <T> Response<T> call(Resource<? extends T> resource) {
		final var builder = HttpRequest.newBuilder()
			.uri(URI.create(resource.path()))
			.timeout(Duration.ofMinutes(2))
			.header("Content-Type", "application/json");

		switch (resource.method()) {
			case GET -> builder.GET();
			case POST -> builder.POST(HttpRequest.BodyPublishers.ofString("UTF-8"));
			case PUT -> builder.PUT(HttpRequest.BodyPublishers.ofString("UTF-8"));
			case DELETE -> builder.DELETE();
		}

		final HttpRequest request = builder.build();

		try {
			final HttpResponse<?> result = client.send(
				request,
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

	public <T> CompletableFuture<Response<T>> callAsync(Resource<? extends T> resource) {
		return null;
	}

}
