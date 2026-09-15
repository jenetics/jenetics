# Jenetics Evolving Images (WebAssembly)

Port of the Jenetics Evolving Images Swing example compiled to WebAssembly via GraalVM Native Image (`--tool:svm-wasm`).

The genetic algorithm runs in Java inside WebAssembly, driving evolution step-by-step with a custom software scanline rasterizer and fitness evaluation, while the user interface runs in HTML5 Canvas and JavaScript with live statistics.

## Features
- **Interactive Evolution**: Start, Pause/Resume, and Stop controls driving a single-threaded evolution loop in WebAssembly.
- **Dual Visual Display**: Side-by-side view showing the source reference image and real-time anti-aliased Canvas 2D polygon rendering.
- **Live Telemetry & Statistics**: Current and best generation, fitness, population age, and evaluation duration, plus overall generations/second speed.
- **Configurable Engine Parameters**: Tune population size, tournament size, mutation rate & multitude, polygon length & count, and reference downscaling resolution with input validation and `localStorage` persistence.
- **Custom Image Upload**: Load any local image file to evolve custom images.
- **High-Resolution PNG Export**: Download the best polygon approximation as a PNG image.

## Prerequisites
- **GraalVM for JDK 25+** with the WebAssembly tool (`svm-wasm`) installed.
- `GRAALVM_HOME` environment variable pointing to your GraalVM installation.
- (Optional) Binaryen `wasm-opt` on your `PATH`, or build with `-Pwasm.noBinaryen`.
- Modern web browser with WasmGC support (Chrome ≥ 119, Firefox ≥ 120, Safari ≥ 18.2).

## Building & Testing

### JVM Unit Tests
Run the unit tests on the JVM (GraalVM is still required to compile the WASM bridge):
```bash
./gradlew :jenetics.example.wasm:test
```

### Assemble WebAssembly Static Site
Compile the WebAssembly binary and assemble the complete static web application:
```bash
./gradlew :jenetics.example.wasm:wasmSite
```
The output is assembled into `jenetics.example.wasm/build/wasm-site/` (`index.html`, `app.js`, `style.css`, `monalisa.png`, `evolving-images.js`, and `evolving-images.js.wasm`).

## Running
Build and serve the generated directory with the JDK’s built-in HTTP server:
```bash
./gradlew :jenetics.example.wasm:wasmServe
```
Then open the displayed URL (by default `http://localhost:8080`) in a compatible web browser. Use `-Pwasm.port=8081` to select another port.
