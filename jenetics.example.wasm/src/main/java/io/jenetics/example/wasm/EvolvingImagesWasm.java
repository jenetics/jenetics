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
package io.jenetics.example.wasm;

import java.util.function.Function;

import com.oracle.svm.webimage.wasmgc.annotation.WasmExport;
import org.graalvm.webimage.api.JS;
import org.graalvm.webimage.api.JSNumber;
import org.graalvm.webimage.api.JSObject;
import org.graalvm.webimage.api.JSString;

/**
 * WebAssembly entry point and JavaScript bridge for the Evolving Images
 * example.
 * <p>
 * The browser invokes {@link #start()} to install a request-based bridge on
 * {@code globalThis.evolvingImagesCall}. The bridge keeps the Java evolution
 * state in WebAssembly while the HTML canvas remains controlled by JavaScript.
 */
public final class EvolvingImagesWasm {

	@JS(args = {"handler"}, value = "globalThis.evolvingImagesCall = handler;")
	private static native void exportHandler(Function<JSObject, JSObject> handler);

	@JS.Coerce
	@org.graalvm.webimage.api.JS("console.log(message);")
	private static native void consoleLog(String message);

	private EvolvingImagesWasm() {
	}

		private static int intArg(final JSObject request, final String key) {
		return ((JSNumber) request.get(key)).asInt();
	}

	private static double doubleArg(final JSObject request, final String key) {
		return ((JSNumber) request.get(key)).asDouble();
	}

	private static int[] intArray(final JSObject value) {
	final int length = ((JSNumber) value.get("length")).asInt();
	final int[] result = new int[length];
	for (int i = 0; i < length; ++i) {
		result[i] = ((JSNumber) value.get(i)).asInt();
	}
	return result;
}

@JS.Coerce
	@JS("return value;")
	private static native double[] doubleArray(double[] value);

	@JS.Coerce
	@JS("return value;")
	private static native int[] jsIntArray(int[] value);

	private static JSObject result() {
		return JSObject.create();
	}

	private static JSObject result(final double value) {
		final var result = result();
		result.set("value", JSNumber.of(value));
		return result;
	}

	private static JSObject result(final int value) {
		final var result = result();
		result.set("value", JSNumber.of(value));
		return result;
	}

	private static JSObject dispatch(final EvolvingImagesApp app, final JSObject request) {
		final var op = ((JSString) request.get("op")).asString();
		return switch (op) {
			case "init" -> {
				app.init(
					intArg(request, "populationSize"),
					intArg(request, "tournamentSize"),
					doubleArg(request, "mutationRate"),
					doubleArg(request, "mutationMultitude"),
					intArg(request, "polygonLength"),
					intArg(request, "polygonCount"),
					intArg(request, "referenceWidth"),
					intArg(request, "referenceHeight"),
					intArray((JSObject) request.get("refRgba"))
				);
				yield result();
			}
			case "evolve" -> result(app.evolve(intArg(request, "generations")));
			case "generation" -> result(app.generation());
			case "bestGeneration" -> result(app.bestGeneration());
			case "currentFitness" -> result(app.currentFitness());
			case "bestFitness" -> result(app.bestFitness());
			case "currentPopulationAge" -> result(app.currentPopulationAge());
			case "bestPopulationAge" -> result(app.bestPopulationAge());
			case "currentEvaluationMillis" -> result(app.currentEvaluationMillis());
			case "bestEvaluationMillis" -> result(app.bestEvaluationMillis());
			case "polygonCount" -> result(app.polygonCount());
			case "polygonLength" -> result(app.polygonLength());
			case "referenceWidth" -> result(app.referenceWidth());
			case "referenceHeight" -> result(app.referenceHeight());
			case "status" -> {
				final var result = result();
				result.set("value", JSString.of(app.status()));
				yield result;
			}
			case "bestPolygons" -> {
				final var result = result();
				result.set("value", doubleArray(app.bestPolygons()));
				yield result;
			}
			case "renderBest" -> {
				final var result = result();
				result.set("value", jsIntArray(app.renderBest(intArg(request, "width"), intArg(request, "height"))));
				yield result;
			}
			default -> throw new IllegalArgumentException("Unknown EvolvingImages operation: " + op);
		};
	}

	/**
	 * Starts the WebAssembly application and installs the JavaScript bridge.
	 */
	@WasmExport(value = "evolvingImagesMain", comment = "Start the Evolving Images application.")
	public static void start() {
	main(new String[0]);
}

	/**
	 * Java entry point used by the generated WebAssembly launcher.
	 *
	 * @param args command-line arguments supplied by the launcher
	 */
	public static void main(final String[] args) {
		final var app = new EvolvingImagesApp();
		exportHandler(request -> dispatch(app, request));

		consoleLog("EvolvingImages WASM initialized.");
		System.out.flush();
	}
}
