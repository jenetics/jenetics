## Release notes

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
