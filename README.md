# Jenetics (_3.0.0_)

**Jenetics** is an **Genetic Algorithm**, respectively an **Evolutionary
Algorithm**, library written in Java. It is designed with a
clear separation of the several concepts of the algorithm,
e.g. `Gene`, `Chromosome`, `Genotype`, `Phenotype`, `Population` and
fitness `Function`. **Jenetics** allows you to minimize
and maximize the given fitness function without tweaking it. In contrast
to other GA implementations, the library uses the concept of an
evolution stream (`EvolutionStream`) for executing the evolution steps.
Since the `EvolutionStream` implements the Java Stream interface,
it works smoothly with the rest of the Java Stream API.

## Requirements

### Runtime
*  **JRE 8**: Java runtime version 8 is needed for using the library, respectively for running the examples.

### Build time
*  **JDK 8**: The `JAVA_HOME` variable must be set to your java installation directory.
*  **Gradle 2.0**: [Gradle](http://www.gradle.org/) is used for building the library. (Gradle is download automatically, if you are using the Gradle Wrapper script `gradlew`, located in the base directory, for building the library.)

### Test compile/execution
*  **TestNG 8.8**: Jenetics uses [TestNG](http://testng.org/doc/index.html) framework for unit tests.
*  **Apache Commons Math 3.3**: [Library](http://commons.apache.org/proper/commons-math/) is used for testing statistical accumulators.

## Download
*  **Sourceforge**:  <https://sourceforge.net/projects/jenetics/files/latest/download>
*  **Bitbucket**:  <https://bitbucket.org/fwilhelm/jenetics/downloads>
*  **Maven**: `org.bitbucket.fwilhelm:org.jenetics:3.0.0` on [Maven Central](http://search.maven.org/#search|ga|1|a%3A%22org.jenetics%22)

## Build Jenetics


For building the Jenetics library from source, download the most recent, stable package version from [Sourceforge](https://sourceforge.net/projects/jenetics/files/latest/download) or [Bitbucket](https://bitbucket.org/fwilhelm/jenetics/downloads) and extract it to some build directory.

    $ unzip jenetics-<version>.zip -d <builddir>

`<version>` denotes the actual Jenetics version and `<builddir>` the actual build directory. Alternatively you can check out the latest-unstable-version from the Mercurial default branch.

    $ git clone https://github.com/jenetics/jenetics.git <builddir>
    # or
    $ hg clone https://bitbucket.org/fwilhelm/jenetics <builddir>
    # or
    $ hg clone http://hg.code.sf.net/p/jenetics/main <builddir>

Jenetics uses [Gradle](http://www.gradle.org/downloads) as build system and organizes the source into *sub*-projects (modules). Each sub-project is located in it’s own sub-directory:

* **org.jenetics**: This project contains the source code and tests for the Jenetics core-module.
* **org.jenetics.example**: This project contains example code for the *core*-module.
* **org.jenetics.doc**: Contains the code of the web-site and the manual.

For building the library change into the `<builddir>` directory (or one of the module directory) and call one of the available tasks:

* **compileJava**: Compiles the Jenetics sources and copies the class files to the `<builddir>/<module-dir>/build/classes/main` directory.
* **test**: Compiles and executes the unit tests. The test results are printed onto the console and a test-report, created by TestNG, is written to `<builddir>/<module-dir>` directory.
* **javadoc**: Generates the API documentation. The Javadoc is stored in the `<builddir>/<module-dir>/build/docs` directory
* **jar**: Compiles the sources and creates the JAR files. The artifacts are copied to the `<builddir>/<module-dir>/build/libs` directory.
* **packaging**: Compiles the sources of all modules, creates the JAR files and the Javadoc and creates a complete library package--the same which you can download from the home page. The build artifacts are copied into the `<builddir>/build/package/jenetics-<version>` directory.
* **clean**: Deletes the `<builddir>/build/*` directories and removes all generated artifacts.

For packaging (building)  the source call

    $ cd <build-dir>
    $ ./gradlew packaging


**IDE Integration**

Gradle has tasks which creates the project file for Eclipse and IntelliJ IDEA. Call

    $ ./gradlew [eclipse|idea]

for creating the project files for Eclipse or IntelliJ, respectively.

## Example

The minimum evolution Engine setup needs a genotype factory,
`Factory<Genotype<?>>`, and a fitness `Function`. The `Genotype` implements the
`Factory` interface and can therefore be used
as prototype for creating the initial `Population` and for creating
new random `Genotypes`.

	import org.jenetics.BitChromosome;
	import org.jenetics.BitGene;
	import org.jenetics.Genotype;
	import org.jenetics.engine.Engine;
	import org.jenetics.engine.EvolutionResult;

	public class HelloWorld {
		// 2.) Definition of the fitness function.
		private static Integer eval(Genotype<BitGene> gt) {
			return ((BitChromosome)gt.getChromosome()).bitCount();
		}

		public static void main(String[] args) {
			// 1.) Define the genotype (factory) suitable
			//     for the problem.
			Factory<Genotype<BitGene>> gtf =
				Genotype.of(BitChromosome.of(10, 0.5));

			// 3.) Create the execution environment.
			Engine<BitGene, Integer> engine = Engine
				.builder(HelloWorld::eval, gtf)
				.build();

			// 4.) Start the execution (evolution) and
			//     collect the result.
			Genotype<BitGene> result = engine.stream()
				.limit(100)
				.collect(EvolutionResult.toBestGenotype());

			System.out.println("Hello World:\n" + result);
		}
	}

In contrast to other GA implementations, the library uses
the concept of an evolution stream (`EvolutionStream`) for
executing the evolution steps. Since the `EvolutionStream`
implements the Java Stream interface, it works smoothly with
the rest of the Java streaming API. Now let's have a closer
look at listing above and discuss this simple program step by step:

1. The probably most challenging part, when setting up a new evolution `Engine`, is to transform the problem domain into a appropriate `Genotype` (factory) representation. In our example we want to count the number of ones of a `BitChromosome`. Since we are counting only the ones of one chromosome, we are adding only one `BitChromosome` to our `Genotype`. In general, the `Genotype` can be created with 1 to n chromosomes.

1. Once this is done, the fitness function which should be maximized, can be defined. Utilizing the new language features introduced in Java 8, we simply write a private static method, which takes the genotype we defined and calculate it's fitness value. If we want to use the optimized bit-counting method, `bitCount()`, we have to cast the `Chromosome<BitGene>` class to the actual used `BitChromosome` class. Since we know for sure that we created the Genotype with a `BitChromosome`, this can be done safely. A reference to the eval method is then used as fitness function and passed to the `Engine.build` method.

1. In the third step we are creating the evolution `Engine`, which is responsible for changing, respectively evolving, a given population. The `Engine` is highly configurable and takes parameters for controlling the evolutionary and the computational environment. For changing the evolutionary behavior, you can set different alterers and selectors. By changing the used `Executor` service, you control the number of threads, the Engine is allowed to use. An new `Engine` instance can only be created via its builder, which is created by calling the `Engine.builder` method.

1. In the last step, we can create a new `EvolutionStream` from our `Engine`. The `EvolutionStream` is the model or view of the evolutionary process. It serves as a »process handle« and also allows you, among other things, to control the termination of the evolution. In our example, we simply truncate the stream after 100 generations. If you don't limit the stream, the `EvolutionStream` will not terminate and run forever. Since the `EvolutionStream` extends the `java.util.stream.Stream` interface, it integrates smoothly with the rest of the Java Stream API. The final result, the best `Genotype` in our example, is then collected with one of the predefined collectors of the `EvolutionResult` class.

## License

The library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

	Copyright 2007-2014 Franz Wilhelmstötter

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.


## Release notes

### 3.0.0

* Rewrite of *engine* classes to make use of Java 8 Stream API.

