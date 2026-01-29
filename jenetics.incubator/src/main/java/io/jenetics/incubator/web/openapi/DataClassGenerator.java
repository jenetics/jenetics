package io.jenetics.incubator.web.openapi;

import static java.util.Objects.requireNonNull;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

final class DataClassGenerator {

	private final JCodeModel model;
	private final JDefinedClass clazz;

	DataClassGenerator(final JCodeModel model, final JDefinedClass clazz) {
		this.model = requireNonNull(model);
		this.clazz = requireNonNull(clazz);
	}

	DataClassGenerator property(final Consumer<PropertyGenerator> property) {
		final var generator = new PropertyGenerator();
		property.accept(generator);
		generator.generate(clazz);
		return this;
	}

	DataClassGenerator equalsAndHashCode() {
		generateHashCode();
		generateEquals();
		return this;
	}

	private DataClassGenerator generateHashCode() {
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

	private DataClassGenerator generateEquals() {
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
