package io.jenetics.incubator.restfulclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.testng.annotations.Test;

public class ProblemDetailTest {

	@Test
	public void unmarshalling() throws JsonProcessingException {
		var mapper = new ObjectMapper();

		var detail = mapper.readValue("""
			{
				"type": "https://example.net/validation-error",
				"title": "Form validation failed",
				"status": 400,
				"detail": "One or more fields have validation errors. Please check and try again.",
				"instance": "/log/registration/12345",
				"errors": [
					{
						"name": "username",
						"reason": "Username is already taken."
					},
					{
						"name": "email",
						"reason": "Email format is invalid."
					}
				]
			}
			""",
			ProblemDetail.class
		);

		System.out.println(detail);
	}

}
