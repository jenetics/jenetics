package io.jenetics.incubator.restfulclient;

import java.util.List;

/**
 * RFC 9457
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
 */
public record ErrorInfo(
	String type,
	String title,
	int status,
	String detail,
	String instance,
	List<Error> errors
) {
	public record Error(String name, String reason) { }
}
