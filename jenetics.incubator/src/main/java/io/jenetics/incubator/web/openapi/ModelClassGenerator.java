package io.jenetics.incubator.web.openapi;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

final class ModelClassGenerator extends Generator {

	private final JDefinedClass clazz;

	ModelClassGenerator(
		final OpenAPI api,
		final JCodeModel model,
		final JDefinedClass clazz
	) {
		super(api, model);
		this.clazz = requireNonNull(clazz);
	}

	ModelClassGenerator property(final Consumer<PropertyGenerator> property) {
		final var generator = new PropertyGenerator(api, model);
		property.accept(generator);
		generator.generate(clazz);
		return this;
	}

	ModelClassGenerator property(String name, String type) {
		return property(p -> p.name(name).type(model.parseType(type)));
	}

	ModelClassGenerator property(String name, Schema<?> schema) {
		property(name, "String");
		return this;
	}

	ModelClassGenerator equalsAndHashCode() {
		generateHashCode();
		generateEquals();
		return this;
	}

	private ModelClassGenerator generateHashCode() {
		final var hashCode = clazz.method(JMod.PUBLIC, int.class, "hasCode");
		hashCode.annotate(Override.class);

		JInvocation hash = model.ref(Objects.class).staticInvoke("hash");
		clazz.fields().values().forEach(field -> {
			if (field.type().isArray()) {
				var h = model.ref(Arrays.class).staticInvoke("hashCode");
				h.arg(JExpr._this().ref(field));
				hash.arg(h);
			} else {
				hash.arg(JExpr._this().ref(field));
			}
		});

		hashCode.body()._return(hash);
		return this;
	}

	private ModelClassGenerator generateEquals() {
		final var equalsMethod = clazz.method(JMod.PUBLIC, boolean.class, "equals");
		equalsMethod.annotate(Override.class);

		final var param = equalsMethod.param(Object.class, "obj");
		param.mods().setFinal(true);

		JBlock body = equalsMethod.body();
		body._if(param.eq(JExpr._null()))._then()._return(JExpr.lit(false));
		body._if(
				JExpr.invoke(param, "getClass")
					.ne(JExpr.invoke(JExpr._this(), "getClass"))
			)
			._then()._return(JExpr.lit(false));

		JVar other = body.decl(clazz, "other", JExpr.cast(clazz, param));
		other.mods().setFinal(true);

		clazz.fields().values().forEach(field -> {
			if (field.type().isArray()) {
				var eq = model.ref(Arrays.class).staticInvoke("equals");
				eq.arg(JExpr._this().ref(field));
				eq.arg(other.ref(field));
				body._if(eq.not())._then()._return(JExpr.lit(false));
			} else if (field.type().isReference()) {
				var eq = model.ref(Objects.class).staticInvoke("equals");
				eq.arg(JExpr._this().ref(field));
				eq.arg(other.ref(field));
				body._if(eq.not())._then()._return(JExpr.lit(false));
			} else if (field.type().isPrimitive()) {
				switch (field.type().name()) {
					case "boolean", "byte", "short", "int", "long" ->
						body._if(JExpr._this().ref(field).ne(other.ref(field)))
							._then()._return(JExpr.lit(false));
					case "float" -> {
						var eq = model.ref(Float.class).staticInvoke("compare");
						eq.arg(JExpr._this().ref(field));
						eq.arg(other.ref(field));
						body._if(eq.ne(JExpr.lit(0)))
							._then()._return(JExpr.lit(false));
					}
					case "double" -> {
						var eq = model.ref(Double.class).staticInvoke("compare");
						eq.arg(JExpr._this().ref(field));
						eq.arg(other.ref(field));
						body._if(eq.ne(JExpr.lit(0)))
							._then()._return(JExpr.lit(false));
					}
				}
			}
		});

		body._return(JExpr.lit(true));
		return this;
	}

}
