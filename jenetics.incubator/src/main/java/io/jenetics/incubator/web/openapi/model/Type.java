package io.jenetics.incubator.web.openapi.model;

import java.util.Set;

public sealed interface Type {

	String name();

	record Str(String format, Set<String> enumeration) implements Type {
		public String name() {
			return "string";
		}
	}

	record Num(String format) implements Type {
		public String name() {
			return "number";
		}
	}

	record Bool() implements Type {
		public String name() {
			return "boolean";
		}
	}

	record Array(Type elemetType) implements Type {
		public String name() {
			return "array";
		}
	}

	record Obj() implements Type {
		public String name() {
			return "object";
		}
	}

	record Ref(Schema.Primitive schema) implements Type {
		public String name() {
			return "ref";
		}
	}

}
