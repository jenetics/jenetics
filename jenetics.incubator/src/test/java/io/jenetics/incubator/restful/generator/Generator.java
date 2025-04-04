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
package io.jenetics.incubator.restful.generator;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import javax.lang.model.type.ReferenceType;
import java.io.IOException;
import java.util.Map;

public class Generator {

	public static void main(String[] args) throws IOException {
		var resource = "/io/jenetics/incubator/restful/museum-api.yaml";

		var parser = new OpenAPIV3Parser();
		SwaggerParseResult result;
		try (var in = Generator.class.getResourceAsStream(resource)) {
			 result = parser.readContents(new String(in.readAllBytes()));
		}

		ReferenceType t;

		OpenAPI api = result.getOpenAPI();
		api.getComponents().getSchemas().entrySet().stream()
			.filter(entry -> isPrimitive((Map.Entry<String, Schema<?>>)(Object)entry))
			.forEach(entry -> System.out.println(entry.getKey()));
	}

 	static boolean isPrimitive(Map.Entry<String, Schema<?>> schema) {
		var types = schema.getValue().getTypes();
		String type = types != null && types.size() == 1
			? schema.getValue().getTypes().iterator().next()
			: null;

		return "string".equals(type);
	}

}
