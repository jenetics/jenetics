package io.jenetics.incubator.restfulclient;

import java.util.concurrent.CompletableFuture;

public class Main {

	public enum Account implements Parameter.Header {
		FOO("x-account", "foo"),
		BAR("x-account", "bar");

		private final String name;
		private final String value;

		Account(final String name, final String value) {
			this.name = name;
			this.value = value;
		}

		public String key() {
			return name;
		}

		public String value() {
			return value;
		}
	}

	static final Resource<String> DOCUMENT = Resource
		.of("/documents/{id}", String.class)
		.params(ContentType.JSON);

	static final Resource<String> BOOK = Resource
		.of("/books/{id}", String.class)
		.params(ContentType.JSON);

	static final Resource<String> PERSON = Resource
		.of("/persons/{id}", String.class)
		.params(ContentType.JSON);

	static final Parameter.Value ID = Parameter.Path.key("id");

	public static void main(String[] args) throws Exception {
		final DefaultClient client = new DefaultClient("https://jsonplaceholder.typicode.com/");

		final var TODOS = Resource
			.of("/todos/{id}/", Todo.class);

		final var result = TODOS
			.params(ID.value("1"))
			.GET(client::callReactive);

		System.out.println(result.block());

		/*
		final Response<String> person = PERSON
			.params(ID.value("2323"))
			.PUT("body-content", client::call);

		final CompletableFuture<Response<String>> person2 = PERSON
			.params(ID.value("32"))
			.PUT("body-content", client::callAsync);

		final Resource<String> document = DOCUMENT
			.params(Account.BAR, ID.value("23"));

		final Resource<String> book = BOOK
			.params(Account.BAR)
			.params(ID.value("23"));

		final Response<Integer> count = person
			.flatMap(p -> DOCUMENT.PUT(p, client::call))
			.flatMap(d -> BOOK.PUT(d, client::call))
			.map(Integer::parseInt);
		 */
	}

	/**
	 * <pre>{@code
	 * {
	 *   "userId": 1,
	 *   "id": 1,
	 *   "title": "delectus aut autem",
	 *   "completed": false
	 * }
	 * }</pre>
	 */
	record Todo(int userId, int id, String title, boolean completed) {
	}

}
