## Release notes

### [4.2.1](https://github.com/jenetics/jenetics/releases/tag/v4.2.1)

#### Bugs

* [3416](https://github.com/jenetics/jenetics/issues/416): Method 'comb.subset' doesn't create all possible combinations

### [4.2.0](https://github.com/jenetics/jenetics/releases/tag/v4.2.0)

#### Improvements

* [#325](https://github.com/jenetics/jenetics/issues/325): **Allow customization of fitness evaluation execution for bundling calculations**
* [#327](https://github.com/jenetics/jenetics/issues/327): Improve CPU utilization during fitness evaluation.
* [#335](https://github.com/jenetics/jenetics/issues/335): Seq view wrapper for List<T> and T[] types.

#### Bugs

* [#317](https://github.com/jenetics/jenetics/issues/317): Fix links of Javadoc images.
* [#318](https://github.com/jenetics/jenetics/issues/318): NULL result from engine.stream() after upgrade from 4.0.0 to 4.1.0.
* [#336](https://github.com/jenetics/jenetics/issues/336): Errornous default implementation of 'Seq.indexWhere'. 
* [#341](https://github.com/jenetics/jenetics/issues/341): Error in internal 'bit.increment' method.
* [#345](https://github.com/jenetics/jenetics/issues/345): Assumption for 'Genotype.newInstance(ISeq)' no longer holds.

### [4.1.0](https://github.com/jenetics/jenetics/releases/tag/v4.1.0)

#### Improvements

* [#223](https://github.com/jenetics/jenetics/issues/223): **Implementation of Multi-Objective Optimization.**
* [#259](https://github.com/jenetics/jenetics/issues/259): Pruning GP program tree.
* [#285](https://github.com/jenetics/jenetics/issues/285): Population exchange between different Engines.
* [#294](https://github.com/jenetics/jenetics/issues/294): Cleanup of Jenetics examples.
* [#295](https://github.com/jenetics/jenetics/issues/295): Upgrade Gradle version 4.5.
* [#297](https://github.com/jenetics/jenetics/issues/297): Compile JMH test on test execution.
* [#306](https://github.com/jenetics/jenetics/issues/306): Improve Javadoc on how to extend chromosomes.
* [#307](https://github.com/jenetics/jenetics/issues/307): Enable @apiNote, @implSpec and @implNote Javadoc tag.

#### Bugs

* [#290](https://github.com/jenetics/jenetics/issues/290): User's manual fixes.
* [#298](https://github.com/jenetics/jenetics/issues/298): Fix GP load/save of generated tree.

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
* [#204](https://github.com/jenetics/jenetics/issues/204): Remove internal 'Stack' container class.
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

### [3.8.0](https://github.com/jenetics/jenetics/releases/tag/v3.8.0)

#### Improvements

* [#157](https://github.com/jenetics/jenetics/issues/157): Add `LineCrossover` class.
* [#158](https://github.com/jenetics/jenetics/issues/158): Add `IntermediateCrossover` class.
* [#168](https://github.com/jenetics/jenetics/issues/168): Remove dependency to `java.desktop` module.
* [#169](https://github.com/jenetics/jenetics/issues/169): Describe how to configure (μ, λ) and (μ + λ) Evolution Strategies in manual.
* [#177](https://github.com/jenetics/jenetics/issues/177): Additional 'Seq' conversion functions:
    * [Seq.asISeq()](http://jenetics.io/javadoc/org.jenetics/3.8/org/jenetics/util/Seq.html#asISeq--)
    * [Seq.asMSeq()](http://jenetics.io/javadoc/org.jenetics/3.8/org/jenetics/util/Seq.html#asMSeq--)    
* [#182](https://github.com/jenetics/jenetics/issues/182): Rename build script to default names. All build scripts are now named `build.gradle`.
* [#188](https://github.com/jenetics/jenetics/issues/188): Additional `Engine.Builder` methods
    * [Engine.Builder.survivorsSize(int)](http://jenetics.io/javadoc/org.jenetics/3.8/org/jenetics/engine/Engine.Builder.html#survivorsSize-int-)
    * [Engine.Builder.offspringSize(int)](http://jenetics.io/javadoc/org.jenetics/3.8/org/jenetics/engine/Engine.Builder.html#offspringSize-int-)   
    * [Engine.Builder.survivorsFraction(double)](http://jenetics.io/javadoc/org.jenetics/3.8/org/jenetics/engine/Engine.Builder.html#survivorsFraction-double-)      
* [#189](https://github.com/jenetics/jenetics/issues/189): `TruncationSelector` is now able to globally limit best selected individuals. This is used for (μ, λ) and (μ + λ) Evolution Strategies.
* [#197](https://github.com/jenetics/jenetics/issues/197): Improve CPU utilization for long running fitness functions. The original concurrent fitness function evaluation where assumed to be quite *fast*.

### [3.7.0](https://github.com/jenetics/jenetics/releases/tag/v3.7.0)

#### Improvements

* [#127](https://github.com/jenetics/jenetics/issues/127): Change the maven group and artifact ID from `org.bitbucket:org.jenetics` to `io.jenetics:jenetics`.
* [#142](https://github.com/jenetics/jenetics/issues/142): Jenetics now compiles without warnings with Java 9 EA.
* [#145](https://github.com/jenetics/jenetics/issues/145): Add additional `Engine.stream(...)` and `Engine.iterator(...)` methods:
    * [stream(Iterable<Genotype<G>> genotypes)](http://jenetics.io/javadoc/org.jenetics/3.7/org/jenetics/engine/Engine.html#stream-java.lang.Iterable-)
    * [stream(Iterable<Genotype<G>> genotypes, long generation)](http://jenetics.io/javadoc/org.jenetics/3.7/org/jenetics/engine/Engine.html#stream-java.lang.Iterable-long-)
    * [stream(EvolutionResult<G, C> result)](http://jenetics.io/javadoc/org.jenetics/3.7/org/jenetics/engine/Engine.html#stream-org.jenetics.engine.EvolutionResult-)
    * [iterator(Iterable<Genotype<G>> genotypes, long generation)](http://jenetics.io/javadoc/org.jenetics/3.7/org/jenetics/engine/Engine.html#iterator-java.lang.Iterable-)
    * [iterator(Population<G, C> population)](http://jenetics.io/javadoc/org.jenetics/3.7/org/jenetics/engine/Engine.html#iterator-org.jenetics.Population-)
    * [iterator(EvolutionResult<G, C> result)](http://jenetics.io/javadoc/org.jenetics/3.7/org/jenetics/engine/Engine.html#iterator-org.jenetics.engine.EvolutionResult-)
* [#150](https://github.com/jenetics/jenetics/issues/150): Implement _fitness convergence_ termination strategy.    
* [#152](https://github.com/jenetics/jenetics/issues/152): Remove `hashCode` and replace `equals` method with `sameState(T)` for mutable objects.
* [#156](https://github.com/jenetics/jenetics/issues/156): Implementation of an [UniformCrossover](https://en.wikipedia.org/wiki/Crossover_(genetic_algorithm)#Uniform_crossover_and_half_uniform_crossover).
* [#162](https://github.com/jenetics/jenetics/issues/162): Update and improve _User's Manual_.

#### Bug fixes

* [#143](https://github.com/jenetics/jenetics/issues/143): Fix serialization of `EvolutionResult` class.
* [#146](https://github.com/jenetics/jenetics/issues/146): NPE in `EvolutionResult.toBestEvolutionResult()` when collecting empty `EvolutionStream`s.
* [#159](https://github.com/jenetics/jenetics/issues/159): The _User's Manual_ build fails for [Lyx](http://www.lyx.org/) version 2.2.2. 

### [3.6.0](https://github.com/jenetics/jenetics/releases/tag/v3.6.0)

#### Improvements

* [#103](https://github.com/jenetics/jenetics/issues/103): Add `MSeq.sort` method.
* [#114](https://github.com/jenetics/jenetics/issues/114): `Alterer` implementations are now able to handle `Chromosome`s of different length.
* [#135](https://github.com/jenetics/jenetics/issues/135): Add `Codec.decode(Genotype)` default method.
* [#140](https://github.com/jenetics/jenetics/issues/140): Additional `EvolutionResult.toBestResult` collectors.

#### Bug fixes

* [#129](https://github.com/jenetics/jenetics/issues/129): Fix Javadoc encoding.

#### Updates

* [#134](https://github.com/jenetics/jenetics/issues/134): Update Gradle to 3.1.
* [#138](https://github.com/jenetics/jenetics/issues/138): Update TestNG to 6.9.13.

### [3.5.1](https://github.com/jenetics/jenetics/releases/tag/v3.5.1)

#### Bug fixes

* [#111](https://github.com/jenetics/jenetics/issues/111): Dead lock for single-threaded executors.

### [3.5.0](https://github.com/jenetics/jenetics/releases/tag/v3.5.0)

#### Improvement

* [#81](https://github.com/jenetics/jenetics/issues/81): It is now easier to register user-defined JAXB marshallings -- `org.jenetics.util.IO.JAXB.register`
* [#90](https://github.com/jenetics/jenetics/issues/90), [#91](https://github.com/jenetics/jenetics/issues/91): The manual contains now a section where the performance of the `MonteCarloSelector` and an evolutionary `Selector` is compared (fig. 6.8, page 52).
* [#96](https://github.com/jenetics/jenetics/issues/96): Merge branch with incubation module `org.jenetix`, which contains experimental classes.
* [#101](https://github.com/jenetics/jenetics/issues/101): Add manual example for solving the *Rastrigin* function.

#### Bug fixes

* [#92](https://github.com/jenetics/jenetics/issues/92): Fix example code in user manual.
* [#94](https://github.com/jenetics/jenetics/issues/94): Inconsistent pre-condition check of `Engine.Builder.build` method.
* [#99](https://github.com/jenetics/jenetics/issues/99): `EvolutionResult` was not completely immutable.

### [3.4.0](https://github.com/jenetics/jenetics/releases/tag/v3.4.0)

#### Improvement

* [#68](https://github.com/jenetics/jenetics/issues/68): Improve implementations of `Seq` interfaces. *Note*: The changes of this issue changes the Java serialization of the `Genes` and `Chromosomes`. `Gene`/`Chromosomes` which has been serialized with version 3.3 can't be loaded with version 3.4. As a workaround, it is still possible to write the `Genes`/`Chromosomes` in XML format and load it with version 3.4.
* [#73](https://github.com/jenetics/jenetics/issues/73): Add additional methods to `Seq` interface: `Seq.append` and `Seq.prepend`.
* [#79](https://github.com/jenetics/jenetics/issues/79): Improve evolution performance measuring. Code resides now in (experimental) `org.jenetics.tool` module. 
* [#85](https://github.com/jenetics/jenetics/issues/85): Add support for fixed-sized subsets in `PermutationChromosome` class. See also [codecs.ofSubSet(ISeq, int)](http://jenetics.io/javadoc/org.jenetics/3.4/org/jenetics/engine/codecs.html#ofSubSet-org.jenetics.util.ISeq-int-).

### [3.3.0](https://github.com/jenetics/jenetics/releases/tag/v3.3.0)

#### Improvement

* [#43](https://github.com/jenetics/jenetics/issues/43): Add _Evolving images_ example.
* [#62](https://github.com/jenetics/jenetics/issues/62): Two or more `Codec` interfaces can be combined into a single one. 
* [#66](https://github.com/jenetics/jenetics/issues/66): Add `AnyGene` and `AnyChromosome` for arbitrary allele types.

#### Bug fixes

* [#52](https://github.com/jenetics/jenetics/issues/52): Immutability of ISeq violated.
* [#55](https://github.com/jenetics/jenetics/issues/55): Fixing example-run script for Mac.


### [3.2.0](https://github.com/jenetics/jenetics/releases/tag/v3.2.0)

#### Improvements

* [#24](https://github.com/jenetics/jenetics/issues/24): Stabilize statistical selector tests.
* [#25](https://github.com/jenetics/jenetics/issues/25): Remove `testng.xml` file. The test classes are now determined automatically.
* [#40](https://github.com/jenetics/jenetics/issues/40): Introduce `Codec` interface for defining problem encodings.
* Add _Internal_ section in manual, which describes implementation details.

#### Bug fixes

* [#33](https://github.com/jenetics/jenetics/issues/33): Selectors must not change the input population. This occasionally caused `ConcurrentModificationException`.  Such selectors are now creating a defensive copy of the input population.
* [#34](https://github.com/jenetics/jenetics/issues/34): `IndexOutOfBoundsException` when selecting populations which are too short.
* [#35](https://github.com/jenetics/jenetics/issues/35): `IndexOutOfBoundsException` when altering populations which are too short.
* [#39](https://github.com/jenetics/jenetics/issues/39): Numerical instabilities of `ProbabilitySelector`.
* [#47](https://github.com/jenetics/jenetics/issues/47): `Engine` deadlock for long running fitness functions.

### [3.1.0](https://github.com/jenetics/jenetics/releases/tag/v3.1.0)

#### Improvements

* Additional termination strategies in `org.jenetics.engine.limit` class.
* Add `EvolutionStream.of` factory method. This allows to use other _evolution_ functions than the `Engine` class.
* `org.jenetics.stat.Quantile` has now a `combine` method which lets them use in a parallel stream.
* [#12](https://github.com/jenetics/jenetics/issues/12): Fix typos in user manual.
* [#13](https://github.com/jenetics/jenetics/issues/13): Add link to Javadoc and manual to README file.
* [#14](https://github.com/jenetics/jenetics/issues/14): Remove `Serializable` interface from `Gene` and `Chromosome`.
* [#16](https://github.com/jenetics/jenetics/issues/16): Make code examples in Javadoc standard conform.
* [#17](https://github.com/jenetics/jenetics/issues/17): Improve recombination section in manual.
* [#20](https://github.com/jenetics/jenetics/issues/20): Advance `Genotype` validity checks.
    

### [3.0.1](https://github.com/jenetics/jenetics/releases/tag/v3.0.1)

* Fixes: [#2](https://github.com/jenetics/jenetics/issues/2), [#7](https://github.com/jenetics/jenetics/issues/7)

### [3.0.0](https://github.com/jenetics/jenetics/releases/tag/v3.0.0)

* Rewrite of *engine* classes to make use of Java 8 Stream API.
