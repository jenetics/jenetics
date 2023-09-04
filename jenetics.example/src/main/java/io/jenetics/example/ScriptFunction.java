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
package io.jenetics.example;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Helper class which executes a given scripted function. It executes the script
 * with the given {@link ScriptEngine}. This class is thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.1
 * @since 7.1
 */
public final class ScriptFunction {
	private final String _script;
	private final ScriptEngine _engine;

	private final CompiledScript _compiled;

	public ScriptFunction(final String script, final ScriptEngine engine) {
		_script = requireNonNull(script);
		_engine = requireNonNull(engine);

		if (_engine instanceof Compilable compiler) {
			try {
				_compiled = compiler.compile(_script);
			} catch (ScriptException e) {
				throw new ScriptFunctionException(e);
			}
		} else {
			_compiled = null;
		}
	}

	public Object apply(final Map<String, Object> bindings) {
		final var bnd = _engine.createBindings();
		bnd.putAll(bindings);

		try {
			if (_compiled != null) {
				return _compiled.eval(bnd);
			} else {
				return _engine.eval(_script, bnd);
			}
		} catch (ScriptException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return _script;
	}
}
