package io.jenetics.incubator.web.restful;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class OpenApi {

	static void main() throws Exception {
		OpenAPI openAPI = new OpenAPIV3Parser()
			.read("/home/fwilhelm/Workspace/Development/Projects/Jenetics" +
				"/jenetics.incubator/src/test/resources/io/jenetics/incubator/restful/" +
				"museum-api.yaml");
		System.out.println(openAPI);
	}

}
