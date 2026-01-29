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
package io.jenetics.incubator.web.restful;

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
