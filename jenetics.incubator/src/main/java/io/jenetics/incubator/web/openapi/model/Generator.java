package io.jenetics.incubator.web.openapi.model;

import java.util.Map;

public class Generator {


	public static void generate(Type type, String indent, StringBuilder out) {
		switch (type) {
			case Type.Obj o -> {
				out.append(indent).append(o.name()).append("{");
				for (var property : o.properties().entrySet()) {
					out.append(indent)
						.append("    ")
						.append(property.getKey()).append(":  ")
						.append(property.getValue().name())
						.append(";\n");
				}
				out.append(indent).append("}");
			}
			case Type t -> out.append(indent).append(t.name());
		}
	}

	static void main() {
		final var object = new Type.Obj(
			"Person",
			Map.of(
				"forename", new Type.Str(),
				"surname", new Type.Str()
			)
		);

		final var out = new StringBuilder();
		generate(object, "", out);
		System.out.println(out);
	}

}
