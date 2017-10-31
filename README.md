# Jenetics

[![Build Status](https://travis-ci.org/jenetics/jenetics.svg?branch=master)](https://travis-ci.org/jenetics/jenetics)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.jenetics/jenetics/badge.svg)](http://search.maven.org/#search|ga|1|a%3A%22jenetics%22)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/io.jenetics/jenetics/badge.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics)

**Jenetics** is an **Genetic Algorithm**, **Evolutionary Algorithm** and **Genetic Programming** library, respectively, written in Java. It is designed with a clear separation of the several concepts of the algorithm, e.g. `Gene`, `Chromosome`, `Genotype`, `Phenotype`, `Population` and fitness `Function`. **Jenetics** allows you to minimize and maximize the given fitness function without tweaking it. In contrast to other GA implementations, the library uses the concept of an evolution stream (`EvolutionStream`) for executing the evolution steps. Since the `EvolutionStream` implements the Java Stream interface, it works smoothly with the rest of the Java Stream API.

**Other languages**

* [**Jenetics.Net**](https://github.com/rmeindl/jenetics.net): Experimental .NET Core port in C# of the base library. 

## Documentation

The library is fully documented ([javadoc](http://jenetics.io/javadoc/jenetics/4.0/index.html)) and comes with an user manual ([pdf](http://jenetics.io/manual/manual-4.0.0.pdf)).


## Requirements

### Runtime
*  **JRE 8**: Java runtime version 8 is needed for using the library, respectively for running the examples.

### Build time
*  **JDK 8**: The Java [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) must be installed.
*  **Gradle 4.x**: [Gradle](http://www.gradle.org/) is used for building the library. (Gradle is download automatically, if you are using the Gradle Wrapper script `./gradlew`, located in the base directory, for building the library.)

### Test compile/execution
*  **TestNG 6.11**: Jenetics uses [TestNG](http://testng.org/doc/index.html) framework for unit tests.
*  **Apache Commons Math 3.6**: [Library](http://commons.apache.org/proper/commons-math/) is used for testing statistical collectors.

## Download
* **Github**: <https://github.com/jenetics/jenetics/releases/download/v4.0.0/jenetics-4.0.0.zip>
*  **Sourceforge**:  <https://sourceforge.net/projects/jenetics/files/latest/download>
*  **Maven**: `io.jenetics:jenetics:4.0.0` on [Maven Central](http://search.maven.org/#search|ga|1|a%3A%22jenetics%22)

## Build Jenetics

For building the Jenetics library from source, download the most recent, stable package version from [Github](https://github.com/jenetics/jenetics/releases/download/v4.0.0/jenetics-4.0.0.zip) (or [Sourceforge](https://sourceforge.net/projects/jenetics/files/latest/download)) and extract it to some build directory.

    $ unzip jenetics-<version>.zip -d <builddir>

`<version>` denotes the actual Jenetics version and `<builddir>` the actual build directory. Alternatively you can check out the master branch from Github.

    $ git clone https://github.com/jenetics/jenetics.git <builddir>

Jenetics uses [Gradle](http://www.gradle.org/downloads) as build system and organizes the source into *sub*-projects (modules). Each sub-project is located in it’s own sub-directory:

**Published projects**

The following projects/modules are also published to Maven.

* **[jenetics](jenetics)**: This project contains the source code and tests for the Jenetics core-module.
* **[jenetics.ext](jenetics.ext)**: This module contains additional _non_-standard GA operations and data types.
* **[jenetics.prog](jenetics.prog)**: The modules contains classes which allows to do genetic programming (GP). It seamlessly works with the existing `EvolutionStream` and evolution `Engine`.
* **[jenetics.xml](jenteics,xml)**: XML marshalling module for the _Jenetics_ base data structures.

**Non-published projects**

* **jenetics.example**: This project contains example code for the *core*-module.
* **jenetics.doc**: Contains the code of the web-site and the manual.
* **jenetics.tool**: This module contains classes used for doing integration testing and algorithmic performance testing. It is also used for creating GA performance measures and creating diagrams from the performance measures.

For building the library change into the `<builddir>` directory (or one of the module directory) and call one of the available tasks:

* **compileJava**: Compiles the Jenetics sources and copies the class files to the `<builddir>/<module-dir>/build/classes/main` directory.
* **jar**: Compiles the sources and creates the JAR files. The artifacts are copied to the `<builddir>/<module-dir>/build/libs` directory.
* **javadoc**: Generates the API documentation. The Javadoc is stored in the `<builddir>/<module-dir>/build/docs` directory
* **test**: Compiles and executes the unit tests. The test results are printed onto the console and a test-report, created by TestNG, is written to `<builddir>/<module-dir>` directory.
* **clean**: Deletes the `<builddir>/build/*` directories and removes all generated artifacts.

For building the library jar from the source call

    $ cd <build-dir>
    $ ./gradlew jar


## Example

### Hello World (Ones counting)

The minimum evolution Engine setup needs a genotype factory, `Factory<Genotype<?>>`, and a fitness `Function`. The `Genotype` implements the `Factory` interface and can therefore be used as prototype for creating the initial `Population` and for creating new random `Genotypes`.

```java
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;

public class HelloWorld {
    // 2.) Definition of the fitness function.
    private static Integer eval(Genotype<BitGene> gt) {
        return gt.getChromosome()
            .as(BitChromosome.class)
            .bitCount();
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
```

In contrast to other GA implementations, the library uses the concept of an evolution stream (`EvolutionStream`) for executing the evolution steps. Since the `EvolutionStream` implements the Java Stream interface, it works smoothly with the rest of the Java streaming API. Now let's have a closer look at listing above and discuss this simple program step by step:

1. The probably most challenging part, when setting up a new evolution `Engine`, is to transform the problem domain into a appropriate `Genotype` (factory) representation. In our example we want to count the number of ones of a `BitChromosome`. Since we are counting only the ones of one chromosome, we are adding only one `BitChromosome` to our `Genotype`. In general, the `Genotype` can be created with 1 to n chromosomes.

1. Once this is done, the fitness function which should be maximized, can be defined. Utilizing the new language features introduced in Java 8, we simply write a private static method, which takes the genotype we defined and calculate it's fitness value. If we want to use the optimized bit-counting method, `bitCount()`, we have to cast the `Chromosome<BitGene>` class to the actual used `BitChromosome` class. Since we know for sure that we created the Genotype with a `BitChromosome`, this can be done safely. A reference to the eval method is then used as fitness function and passed to the `Engine.build` method.

1. In the third step we are creating the evolution `Engine`, which is responsible for changing, respectively evolving, a given population. The `Engine` is highly configurable and takes parameters for controlling the evolutionary and the computational environment. For changing the evolutionary behavior, you can set different alterers and selectors. By changing the used `Executor` service, you control the number of threads, the Engine is allowed to use. An new `Engine` instance can only be created via its builder, which is created by calling the `Engine.builder` method.

1. In the last step, we can create a new `EvolutionStream` from our `Engine`. The `EvolutionStream` is the model or view of the evolutionary process. It serves as a »process handle« and also allows you, among other things, to control the termination of the evolution. In our example, we simply truncate the stream after 100 generations. If you don't limit the stream, the `EvolutionStream` will not terminate and run forever. Since the `EvolutionStream` extends the `java.util.stream.Stream` interface, it integrates smoothly with the rest of the Java Stream API. The final result, the best `Genotype` in our example, is then collected with one of the predefined collectors of the `EvolutionResult` class.


### Evolving images

This example tries to approximate a given image by semitransparent polygons.  It comes with an Swing UI, where you can immediately start your own experiments. After compiling the sources with

    $ ./gradlew compileTestJava

you can start the example by calling

    $ ./jrun io.jenetics.example.image.EvolvingImages

![Evolving images](https://raw.githubusercontent.com/jenetics/jenetics/master/jenetics.doc/src/main/resources/graphic/EvolvingImagesExampleScreenShot.png)

The previous image shows the GUI after evolving the default image for about 4,000 generations. With the »Open« button it is possible to load other images for polygonization. The »Save« button allows to store polygonized images in PNG format to disk. At the button of the UI, you can change some of the GA parameters of the example.


## Projects using Jenetics

* <a href="http://chronetic.io/"><b>Chronetic</b>:</a> Chronetic is an open-source time pattern analysis library built to describe time-series data.
* <a href="http://www.eclipse.org/app4mc/"><b>APP4MC</b>:</a> Eclipse APP4MC is a platform for engineering embedded multi- and many-core software systems.

## Citations

* Michael Trotter, Guyue Liu, Timothy Wood. <a href="http://ieeexplore.ieee.org/abstract/document/8064120/">Into the Storm: Descrying Optimal Configurations Using Genetic Algorithms and Bayesian Optimization. </a> <em>2017 IEEE 2nd International Workshops on Foundations and Applications of Self* Systems (FAS*W).</em> Sep. 2017.
* Cuadra P., Krawczyk L., Höttger R., Heisig P., Wolff C. <a href="https://link.springer.com/chapter/10.1007/978-3-319-67642-5_30">Automated Scheduling for Tightly-Coupled Embedded Multi-core Systems Using Hybrid Genetic Algorithms. </a> <em>Information and Software Technologies: 23rd International Conference, ICIST 2017, Druskininkai, Lithuania.</em> Communications in Computer and Information Science, vol 756. Springer, Cham, Sep. 2017
* Abraão G. Nazário, Fábio R. A. Silva, Raimundo Teive, Leonardo Villa, Antônio Flávio, João Zico, Eire Fragoso, Ederson F. Souza. <a href="http://siaiap32.univali.br/seer/index.php/acotb/article/view/10579/5933">Automação Domótica Simulada Utilizando Algoritmo Genético Especializado na Redução do Consumo de Energia. </a> <em>Computer on the Beach 2017</em> pp. 180-189, March 2017.
* Bandaru, S. and Deb, K. <a href="http://dx.doi.org/10.1201/9781315183176-12">Metaheuristic Techniques.</a> <em>Decision Sciences.</em> CRC Press, pp. 693-750, Nov. 2016.
* Lyazid Toumi, Abdelouahab Moussaoui, and Ahmet Ugur. <a href="http://dx.doi.org/10.1145/2816839.2816876">EMeD-Part: An Efficient Methodology for Horizontal Partitioning in Data Warehouses.</a> <em>Proceedings of the International Conference on Intelligent Information Processing, Security and Advanced Communication.</em> Djallel Eddine Boubiche, Faouzi Hidoussi, and Homero Toral Cruz (Eds.). ACM, New York, NY, USA, Article 43, 7 pages, 2015.
* Andreas Holzinger (Editor), Igo Jurisica (Editor). <a href="http://www.springer.com/computer/database+management+%26+information+retrieval/book/978-3-662-43967-8">Interactive Knowledge Discovery and Data Mining in Biomedical Informatics.</a> <em>Lecture Notes in Computer Science, Vol. 8401.</em> <a href="http://www.springer.com">Springer</a>, 2014.
* Lyazid Toumi, Abdelouahab Moussaoui, Ahmet Ugur. <a href="http://link.springer.com/article/10.1007%2Fs11227-013-1058-9">Particle swarm optimization for bitmap join indexes selection problem in data warehouses.</a> <em><a href="http://link.springer.com/journal/11227">The Journal of Supercomputing</a>, Volume 68, <a href="http://link.springer.com/journal/11227/68/2/page/1">Issue 2</a>, pp 672-708, May 2014.</em>
* TANG Yi (Guangzhou Power Supply Bureau Limited, Guangzhou 511400, China) <a href="http://en.cnki.com.cn/Article_en/CJFDTOTAL-JXKF201210017.htm"> <em>Study on Object-Oriented Reactive Compensation Allocation Optimization Algorithm for Distribution Networks</em></a>, Oct. 2012.
* John M. Linebarger, Richard J. Detry, Robert J. Glass, Walter E. Beyeler, Arlo L. Ames, Patrick D. Finley, S. Louise Maffitt. <a href="http://prod.sandia.gov/techlib/access-control.cgi/2012/121117.pdf"> <em>Complex Adaptive Systems of Systems Engineering Environment Version 1.0.  </em></a> <a href="http://www.sandia.gov/CasosEngineering/">SAND REPORT</a>, Feb. 2012.

## Blogs

* <a href="http://www.baeldung.com/jenetics">Introduction to Jenetics Library</a>, by <em>baeldung</em>, April 11. 2017
* <a href="http://blog.takipi.com/how-to-solve-tough-problems-using-genetic-algorithms/">How to Solve Tough Problems Using Genetic Algorithms</a>, by <em>Tzofia Shiftan</em>, April 6. 2017
* <a href="http://fxapps.blogspot.co.at/2017/01/genetic-algorithms-with-java.html">Genetic algorithms with Java</a>, by <em>William Antônio</em>, January 10. 2017
* <a href="http://jdm.kr/blog/135">Jenetics 설치 및 예제</a>, by <em>JDM</em>, May 8. 2015
* <a href="http://jdm.kr/blog/104">유전 알고리즘 (Genetic Algorithms)</a>, by <em>JDM</em>, April 2. 2015

## License

The library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

	Copyright 2007-2017 Franz Wilhelmstötter

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

### [4.0.0](https://github.com/jenetics/jenetics/releases/tag/v4.0.0)

#### Improvements

* [#28](https://github.com/jenetics/jenetics/issues/28): Immutable population class. The original `Population` class has been replaced by `Seq<Phenotype<G, C>>`. This points to a more _functional_ implementation of the library.
* [#119](https://github.com/jenetics/jenetics/issues/119): `Chromosome` implementations are now fully immutable. This is an internal change only.
* [#121](https://github.com/jenetics/jenetics/issues/121): `Mutator` class is easier now to extend. It has been extended with additional `mutate` methods which serves as extension points for onw `Mutator` implementations.
* [#123](https://github.com/jenetics/jenetics/issues/123): `Chromosome` with variable number of genes: Most chromosomes can now be created with a variable number of genes. `DoubleChromosome.of(0.0, 1.0, IntRange.of(5, 16))`.
* [#172](https://github.com/jenetics/jenetics/issues/172): [`io.jenetics.prngine`](https://github.com/jenetics/prngine) library replaces the existing PRNG implementations in the `io.jenetics.base` module.
* [#175](https://github.com/jenetics/jenetics/issues/175): Align random int range generation with `io.jenetics.prngine` library. This is an internal change only. 
* [#180](https://github.com/jenetics/jenetics/issues/180): Change library namespace from `org.jenetics` to `io.jenetics`. This is the **most** invasive change of this release. Users have to adopt the imports in all their code.
* [#183](https://github.com/jenetics/jenetics/issues/183): Change copyright email address to ...@gmail.com
* [#200](https://github.com/jenetics/jenetics/issues/200): Implementation of gene convergence termination: _A termination method that stops the evolution when a user-specified percentage of the genes that make up a `Genotype` are deemed as converged. A gene is deemed as converged when the average value of that gene across all of the genotypes in the current population is less than a user-specified percentage away from the maximum gene value across the genotypes._
* [#253](https://github.com/jenetics/jenetics/issues/253): Removal of deprecated code and classes: mainly `JAXB` marshalling and the `LCG64ShiftRandom` class.
* [#260](https://github.com/jenetics/jenetics/issues/260): Clean room implementation of internal `subset` function. This method was a port from the [C++ source](https://people.scs.fsu.edu/~burkardt/c_src/subset/subset.html) written by John Burkardt. The original source has been published under the LGPL licence, which is not compatible to tha Apache 2 licence. To avoid legal issues, the affected method has been reimplemented using the [Clean Room](http://wiki.c2.com/?CleanRoomImplementation) method, based on the original book, [Combinatorial Algorithms for Computers and Calculators](https://www.math.upenn.edu/%7Ewilf/website/CombinatorialAlgorithms.pdf), by Albert Nijenhuis and Herbert Wilf. The `io.jenetics.internal.math.comb.subset` method is now fully compatible with the Apache 2 licence.
* [#262](https://github.com/jenetics/jenetics/issues/262): Filter for duplicate individuals: It is now possible to intercept the stream of `EvolutionResult`s of the evolution `Engine`:
```java
final Engine<DoubleGene, Integer> engine = Engine.builder(problem)
	.mapping(EvolutionResult.toUniquePopulation())
	.build();
```
* [#264](https://github.com/jenetics/jenetics/issues/264): Upgrade Gradle to version 4.3.
* [#266](https://github.com/jenetics/jenetics/issues/266): The `Seq` serialization should be more robust in the case of implementation changes.
* [#268](https://github.com/jenetics/jenetics/issues/269): Implementation of an [`EliteSelector`](https://en.wikipedia.org/wiki/Selection_(genetic_algorithm)).
* [#269](https://github.com/jenetics/jenetics/issues/269): Cleanup of internal, mathematical helper functions.
* [#272](https://github.com/jenetics/jenetics/issues/272): Obey Java naming convention. Two helper classes have been renamed to obey the Java naming conventions. `codecs` -> `Codecs` and `limits` -> `Limits`.
* [#279](https://github.com/jenetics/jenetics/issues/279): Additional `MSeq.swap` method.

#### Bugs

* [#247](https://github.com/jenetics/jenetics/issues/247): Fix the classpath of the `jrun` helper script.
* [#256](https://github.com/jenetics/jenetics/issues/256): Buggy type signature of `AnyChromosome.of` method.


### [3.9.0](https://github.com/jenetics/jenetics/releases/tag/v3.9.0)

#### Improvements

* [#26](https://github.com/jenetics/jenetics/issues/26): Extend Gradle scripts for multi-module releases.
* [#27](https://github.com/jenetics/jenetics/issues/27): Parallel `EvolutionStream`.
* [#64](https://github.com/jenetics/jenetics/issues/64): Implementation of `TreeGene`/`Chromosome`.
* [#181](https://github.com/jenetics/jenetics/issues/181): XML marshaling module: `org.jenetics.xml`.
* [#199](https://github.com/jenetics/jenetics/issues/199): Termination: Population convergence.
* [#201](https://github.com/jenetics/jenetics/issues/201): Simplify Gradle build scripts.
* [#204](https://github.com/jenetics/jenetics/issues/204): Remove internal `Stack` container class.
* [#207](https://github.com/jenetics/jenetics/issues/207): Add missing `BitChromosome` factory methods.
* [#216](https://github.com/jenetics/jenetics/issues/216): Restructuring of User's Manual.
* [#218](https://github.com/jenetics/jenetics/issues/218): Mark `LCG64ShiftRandom` class as deprecated.
* [#219](https://github.com/jenetics/jenetics/issues/219): Mark `JAXB` marshaling as deprecated.
* [#227](https://github.com/jenetics/jenetics/issues/227): Genetic Programming module: `org.jenetics.prog`.
* [#228](https://github.com/jenetics/jenetics/issues/228): Upgrade Gradle to 4.0.2.
* [#229](https://github.com/jenetics/jenetics/issues/229): Define stable module names.
* [#236](https://github.com/jenetics/jenetics/issues/236): Rename module `jenetix` to `org.jenetics.ext`
* [#238](https://github.com/jenetics/jenetics/issues/238): Align project directories with maven artifact names.

#### Bugs

* [#212](https://github.com/jenetics/jenetics/issues/212): Fix `Seq.toArray(Object[])` method.
* [#226](https://github.com/jenetics/jenetics/pull/226): Incorrect `MinMax.toString()` output.
* [#233](https://github.com/jenetics/jenetics/pull/233): `Engine.java` Comment Grammar Fix.
* [#234](https://github.com/jenetics/jenetics/issues/234): `Population.empty()` isn't empty


_[All Release Notes](RELEASE_NOTES.md)_

## Used software

<a href="https://www.jetbrains.com/idea/">![IntelliJ](http://jenetics.io/img/icon_IntelliJIDEA.png)</a>

