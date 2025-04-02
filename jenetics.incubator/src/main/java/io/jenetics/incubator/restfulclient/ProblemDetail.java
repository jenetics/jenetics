/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.restfulclient;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * The canonical model for problem details. When serialized in a JSON document,
 * that format is identified with the {@code application/problem+json} media type.
 *
 * <pre>{@code
 * {
 *   "type": "https://example.net/validation-error",
 *   "title": "Form validation failed",
 *   "status": 400,
 *   "detail": "One or more fields have validation errors. Please check and try again.",
 *   "instance": "/log/registration/12345",
 *   "errors": [
 *     {
 *       "name": "username",
 *       "reason": "Username is already taken."
 *     },
 *     {
 *       "name": "email",
 *       "reason": "Email format is invalid."
 *     }
 *   ]
 * }
 * }</pre>
 *
 * @see <a href="RFC 9457">https://datatracker.ietf.org/doc/html/rfc9457</a>
 *
 * @param type A URI reference that identifies the problem type. The type field
 *        is used to identify the error type and provide a link to the
 *        documentation that describes the error in more detail, so you can put
 *        a link to your API documentation here.
 * @param title an optional short, human-readable summary of the problem type
 * @param status the HTTP status code generated by the origin server for this
 *        occurrence of the problem
 * @param detail an optional human-readable explanation specific to this
 *        occurrence of the problem.
 * @param instance the called endpoint, optional
 * @param extensions extension for more detailed information; optional
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProblemDetail(
	URI type,
	String title,
	int status,
	String detail,
	String instance,

	// Map for extension properties.
	@JsonAnyGetter
	@JsonAnySetter
	Map<String, Object> extensions
) {
	public static final URI BLANK = URI.create("about:blank");

	public ProblemDetail {
		requireNonNull(type);
	}

	public ProblemDetail(
		String title,
		int status,
		String detail,
		String instance,
		Map<String, Object> properties
	) {
		this(BLANK, title, status, detail, instance, properties);
	}

	public ProblemDetail(
		String title,
		String detail,
		String instance,
		Map<String, Object> properties
	) {
		this(BLANK, title, 500, detail, instance, properties);
	}

}
