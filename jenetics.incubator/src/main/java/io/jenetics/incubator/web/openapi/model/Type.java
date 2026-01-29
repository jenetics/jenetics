package io.jenetics.incubator.web.openapi.model;

import java.util.Map;

public sealed interface Type {

	String name();

	record Str() implements Type {
		public String name() {
			return "java.lang.String";
		}
	}

	record Num() implements Type {
		public String name() {
			return "java.math.BigDecimal";
		}
	}

	record Bool() implements Type {
		public String name() {
			return "java.lang.Boolean";
		}
	}

	record Array(Type elemetType) implements Type {
		public String name() {
			return "java.util.List<%s>".formatted(elemetType.name());
		}
	}

	record Obj(String name, Map<String, ? extends Type> properties) implements Type {

		public Obj {
			properties = Map.copyOf(properties);
		}

	}

}
