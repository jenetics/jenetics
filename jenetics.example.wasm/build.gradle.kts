import com.sun.net.httpserver.SimpleFileServer
import java.net.InetSocketAddress
import java.util.concurrent.CountDownLatch

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

plugins {
	`java-library`
	id("org.graalvm.buildtools.native") version "1.1.11"
}

dependencies {
	implementation(project(":jenetics"))
	implementation("org.graalvm.sdk:webimage-preview:25.0.4.1")
	compileOnly(files("${System.getenv("GRAALVM_HOME")}/lib/svm/tools/svm-wasm/builder/svm-wasm.jar"))

	testImplementation(libs.assertj.core)
	testImplementation(libs.testng)
}

tasks.withType<JavaCompile> {
	options.compilerArgs.add("-parameters")
}

graalvmNative {
	toolchainDetection.set(false)
	binaries.named("main") {
		imageName.set("evolving-images")
		mainClass.set("io.jenetics.example.wasm.EvolvingImagesWasm")
		buildArgs.add("--tool:svm-wasm")
		if (providers.gradleProperty("wasm.noBinaryen").isPresent) {
			buildArgs.add("-H:-UseBinaryen")
		}
	}
}

val wasmSite by tasks.registering(Sync::class) {
	group = "wasm"
	dependsOn("nativeCompile")
	from(layout.buildDirectory.dir("native/nativeCompile")) {
		include("evolving-images.js", "evolving-images.js.wasm")
	}
	from("src/main/web")
	into(layout.buildDirectory.dir("wasm-site"))
	doLast {
		// Make the generated launcher invoke the exported application startup and expose its runtime promise.
		val launcher = layout.buildDirectory.file("wasm-site/evolving-images.js").get().asFile
		val source = launcher.readText().replace(System.lineSeparator(), Char(10).toString())
		val oldEntry = """getExport("main")(toJavaStringArray(args));"""
		val newEntry = oldEntry + Char(10) + """    getExport("evolvingImagesMain")();"""
		val patchedSource = source.replace(oldEntry, newEntry)
		val original = """
const config = new GraalVM.Config();
GraalVM.run(load_cmd_args(),config).catch(e => { console.error(e); throw e; });
""".trimIndent()
		val replacement = """
const config = new GraalVM.Config();
const __evolvingImagesArgs = load_cmd_args();
const __evolvingImagesRun = GraalVM.run(__evolvingImagesArgs, config);
if (typeof globalThis !== "undefined") {
    globalThis.__evolvingImagesRun = __evolvingImagesRun;
    __evolvingImagesRun.then(vm => { globalThis.__evolvingImagesVM = vm; });
}
__evolvingImagesRun.catch(e => { console.error(e); throw e; });
""".trimIndent()
		check(patchedSource.contains(original)) { "Unexpected GraalVM launcher format." }
		launcher.writeText(patchedSource.replace(original, replacement))
	}
}

val wasmServe by tasks.registering {
	group = "wasm"
	description = "Build and serve the Evolving Images WASM site on localhost."
	dependsOn(wasmSite)
	doLast {
		val port = providers.gradleProperty("wasm.port").orElse("8080").get().toInt()
		val site = layout.buildDirectory.dir("wasm-site").get().asFile.toPath()
		val server = SimpleFileServer.createFileServer(
			InetSocketAddress("localhost", port),
			site,
			SimpleFileServer.OutputLevel.INFO
		)
		server.start()
		println("Serving $site at http://localhost:$port/")
		try {
			CountDownLatch(1).await()
		} finally {
			server.stop(0)
		}
	}
}
