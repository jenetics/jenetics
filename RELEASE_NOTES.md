## Release notes

### [8.0.0](https://github.com/jenetics/jenetics/releases/tag/v8.0.0)

#### Improvements

* Java 21 is used for building and using the library.
* [#878](https://github.com/jenetics/jenetics/issues/878): Allow Virtual-Threads evaluating the fitness function.
```java
final Engine<DoubleGene, Double> engine = Engine.builder(ff)
    .fitnessExecutor(BatchExecutor.ofVirtualThreads())
    .build();
```
* [#880](https://github.com/jenetics/jenetics/issues/880): Replace code examples in Javadoc with [JEP 413](https://openjdk.org/jeps/413).
* [#886](https://github.com/jenetics/jenetics/issues/886): Improve `CharStore` sort.
* [#894](https://github.com/jenetics/jenetics/issues/894): New genetic operators: `ShiftMutator`, `ShuffleMutator` and `UniformOrderBasedCrossover`.
* [#895](https://github.com/jenetics/jenetics/issues/895): Improve default RandomGenerator selection. The used `RandomGenerator` is selected in the following order:
	1) Check if the `io.jenetics.util.defaultRandomGenerator` start parameter ist set. If so, take this generator.
	2) Check if the `L64X256MixRandom` generator is available. If so, take this generator.
	3) Find the _best_ available random generator according to the `RandomGeneratorFactory.stateBits()` value.
	4) Use the `Random` generator if no _best_ generator can be found. This generator is guaranteed to be available on every platform.

### [7.2.0](https://github.com/jenetics/jenetics/releases/tag/v7.2.0)

#### Improvements

* [#862](https://github.com/jenetics/jenetics/issues/862): Add a method, which allows to create a sliced (chromosome) view onto a given Genotype.
* [#866](https://github.com/jenetics/jenetics/issues/866): Allow specifying the default `RandomGenerator` used by the library.
```
java -Dio.jenetics.util.defaultRandomGenerator=L64X1024MixRandom\
     -cp jenetics-@__version__@.jar:app.jar\
         com.foo.bar.MyJeneticsAppjava 
```

* [#872](https://github.com/jenetics/jenetics/issues/872): Improve generic type parameters for some argument types in `io.jenetics.prog` module.
* [#876](https://github.com/jenetics/jenetics/issues/876): Fix compiler warnings with Java 21-

#### Bugs

* [#865](https://github.com/jenetics/jenetics/issues/865), [#867](https://github.com/jenetics/jenetics/issues/867): Fixing typos in documentation.
* [#868](https://github.com/jenetics/jenetics/issues/868): Fix execution script `./jrun.cmd`


### [7.1.3](https://github.com/jenetics/jenetics/releases/tag/v7.1.3)

#### Improvements

* [#857](https://github.com/jenetics/jenetics/issues/857): Make library compile with Java 20.

### [7.1.2](https://github.com/jenetics/jenetics/releases/tag/v7.1.2)

#### Improvements

* [#853](https://github.com/jenetics/jenetics/issues/853): Improve error message for `Codecs::ofSubSet::encode` method.

### [7.1.1](https://github.com/jenetics/jenetics/releases/tag/v7.1.1)

#### Bugs

* [#842](https://github.com/jenetics/jenetics/issues/842): `BitChromosone::bitCount` returns wrong results for chromosome lengths <= 8.

### [7.1.0](https://github.com/jenetics/jenetics/releases/tag/v7.1.0)

#### Improvements

* [#813](https://github.com/jenetics/jenetics/issues/813): Re-implementation of `MathExpr` class. Replace ad-hoc parsing implementation.
* [#815](https://github.com/jenetics/jenetics/issues/815): Implement Grammatical-Evolution.
* [#820](https://github.com/jenetics/jenetics/issues/820): Additional `BitChromosome` methods: `and`, `or`, `xor`, `not`, `shiftRight`, `shiftLeft`.
* [#833](https://github.com/jenetics/jenetics/issues/833): Implement `Tree::reduce` function. Allows to write code as follows:
```java
final Tree<String, ?> formula = TreeNode.parse(
    "add(sub(6, div(230, 10)), mul(5, 6))",
    String::trim
);
final double result = formula.reduce(new Double[0], (op, args) ->
    switch (op) {
        case "add" -> args[0] + args[1];
        case "sub" -> args[0] - args[1];
        case "mul" -> args[0] * args[1];
        case "div" -> args[0] / args[1];
        default -> Double.parseDouble(op);
    }
);
```

#### Bugs

* [#831](https://github.com/jenetics/jenetics/issues/831): Error while parsing parentheses trees.
* [#836](https://github.com/jenetics/jenetics/issues/836): Fix `BitChromosome`(`Test`).

### [7.0.0](https://github.com/jenetics/jenetics/releases/tag/v7.0.0)

#### Improvements

* [#632](https://github.com/jenetics/jenetics/issues/632): Convert data classes to `records`.
* [#696](https://github.com/jenetics/jenetics/issues/693): Convert libraries to JPMS modules.
* [#715](https://github.com/jenetics/jenetics/issues/715): Improve `BitChromosome`.
* [#762](https://github.com/jenetics/jenetics/issues/762): Apply new Java17 construct where useful.
* [#767](https://github.com/jenetics/jenetics/issues/767): **Incubator** - Grammar-based evolution.
* [#773](https://github.com/jenetics/jenetics/issues/773): **Incubator** - Simplify and unify parsing code for `MathExpr` class.
* [#785](https://github.com/jenetics/jenetics/issues/785): Using `RandomGenerator` instead of `Random` class.
* [#787](https://github.com/jenetics/jenetics/issues/787): **Breaking change** - Change upper limit of `Integer`/`LongeGenes` from _inclusively_ to _exclusively_.
* [#789](https://github.com/jenetics/jenetics/issues/789): Make `AbstractChromosome` non-`Serializable`.
* [#796](https://github.com/jenetics/jenetics/issues/796): Use `InstantSource` instead of `Clock` for measuring evolution durations.
* [#798](https://github.com/jenetics/jenetics/issues/798): Performance improve of _subset_ creation method.
* [#801](https://github.com/jenetics/jenetics/issues/801): Introduce `Self` interface.
* [#816](https://github.com/jenetics/jenetics/issues/816): Add Sudoku example (by [alex-cornejo](https://github.com/alex-cornejo)).

#### Bugs

* [#791](https://github.com/jenetics/jenetics/issues/791): Fix possible overflow in Integer/LongGene mean method.
* [#794](https://github.com/jenetics/jenetics/issues/794): Fix possible underflow in DoubleGene mean method.
* [#803](https://github.com/jenetics/jenetics/issues/803): Bug checking Sample arity in class SampleList.

### [6.3.0](https://github.com/jenetics/jenetics/releases/tag/v6.3.0)

#### Improvements

* [#763](https://github.com/jenetics/jenetics/issues/763): `ProxySorter` is now able to sort array slices.
* [#768](https://github.com/jenetics/jenetics/issues/768): Implement `Ordered` class. Currently, it is required that the return value of the fitness function to be `Comparable`. But sometimes you might want to change the order of a given type or add some order to a type. The `Ordered` class makes this possible.

### [6.2.0](https://github.com/jenetics/jenetics/releases/tag/v6.2.0)

#### Improvements

* [#754](https://github.com/jenetics/jenetics/issues/754): Make `Optimize.best` method `null` friendly

#### Bugs

* [#742](https://github.com/jenetics/jenetics/issues/742): Fix compile error with Java 15.
* [#746](https://github.com/jenetics/jenetics/issues/746): `Const<Double>` equals doesn't conform with `Double.compare`.
* [#748](https://github.com/jenetics/jenetics/issues/748): Fix broken formulas in Javadoc.
* [#752](https://github.com/jenetics/jenetics/issues/752): `StreamPublisher` doesn't close underlying `Stream` on close.

### [6.1.0](https://github.com/jenetics/jenetics/releases/tag/v6.1.0)

#### Improvements

* [#323](https://github.com/jenetics/jenetics/issues/323): Fix leaky abstraction of `CompositeCodec`. 
* [#434](https://github.com/jenetics/jenetics/issues/434): Rewrite build scripts using Kotlin.
* [#695](https://github.com/jenetics/jenetics/issues/695): Simplify MOEA for continious optimization.
* [#704](https://github.com/jenetics/jenetics/issues/704): Add `FlatTreeNode.ofTree` factory method, for cleaner `Tree` API.
* [#706](https://github.com/jenetics/jenetics/issues/706): The `Constraint` is now part of the `Problem` interface. If defined, it will automatically be part of the created `Engine`.
```java
default Optional<Constraint<G, C>> constraint() {
    return Optional.empty();
}
```
* [#708](https://github.com/jenetics/jenetics/issues/708): Additional `Chromosome.map(Function)` methods. This allows a more efficient mapping of chromosomes.
* [#731](https://github.com/jenetics/jenetics/issues/731): Improve creation of _constrained_ individuals, as defined in the `Constraint` interface.
* [#739](https://github.com/jenetics/jenetics/issues/736): Add `jenetics.incubator` module. This module will contain classes which might be part of one of the main module.

### [6.0.1](https://github.com/jenetics/jenetics/releases/tag/v6.0.0)

#### Bugs

* [#701](https://github.com/jenetics/jenetics/issues/701): Invalid `DoubleGene.isValid` method.
* [#713](https://github.com/jenetics/jenetics/issues/713): Fix numeric instability of `RouletteWheleSelector`class.
* [#718](https://github.com/jenetics/jenetics/issues/718): `IntermediateCrossover` is not terminating for invalid genes.

### [6.0.0](https://github.com/jenetics/jenetics/releases/tag/v6.0.0)

#### Improvements

* [#403](https://github.com/jenetics/jenetics/issues/403): Converting library to Java 11.
* [#581](https://github.com/jenetics/jenetics/issues/581): Minimize the required _evaluation_ calls per generation.
* [#587](https://github.com/jenetics/jenetics/issues/587): Fix Javadoc for Java 11.
* [#590](https://github.com/jenetics/jenetics/issues/590): Improve serialization of `Seq` implementations.
* [#591](https://github.com/jenetics/jenetics/issues/591): Remove deprecated classes and methods.
* [#606](https://github.com/jenetics/jenetics/issues/606): Improve serialization of `*Range` classes.
* [#630](https://github.com/jenetics/jenetics/issues/630): Fix inconsistency in `Codec.of` factory methods.
* [#659](https://github.com/jenetics/jenetics/issues/659): Additional factory methods for `VecFactory` interface in the `moea` package.
* [#661](https://github.com/jenetics/jenetics/issues/661): Allow the re-evaluation of the population fitness value
* [#665](https://github.com/jenetics/jenetics/issues/665): Implement `CombineAlterer`, which is a generalization of th `MeanAlterer` class.
* [#669](https://github.com/jenetics/jenetics/issues/669): Regression analysis with dynamically chaning sample points.
```java
final var scheduler = Executors.newScheduledThreadPool(1);
final var nullifier = new FitnessNullifier<ProgramGene<Double>, Double>();
final var sampling = new SampleBuffer<Double>(100);
scheduler.scheduleWithFixedDelay(
    () -> {
        // Adding a new sample point every second to the ring buffer.
        sampling.add(nextSamplePoint());
        // Force re-evaluation of populations fitness values.
        nullifier.nullifyFitness();
    },
    1, 1, TimeUnit.SECONDS
);

final Codec<Tree<Op<Double>, ?>, ProgramGene<Double>> codec =
    Regression.codecOf(OPS, TMS, 5, t -> t.gene().size() < 30);

final Regression<Double> regression = Regression.of(
    codec,
    Error.of(LossFunction::mse),
    sampling
);

final Engine<ProgramGene<Double>, Double> engine = Engine
    .builder(regression)
    .interceptor(nullifier)
    .build();

engine.stream()
    .flatMap(Streams.toIntervalMax(Duration.ofSeconds(30)))
    .map(program -> program.bestPhenotype()
        .genotype().gene()
        .toParenthesesString())
    // Printing the best program found so far every 30 seconds.
    .forEach(System.out::println);
```
* [#671](https://github.com/jenetics/jenetics/issues/671): Adding helper methods in `Streams` class, which allows to emit the best evolution result of every _n_ generation.
```java
final ISeq<Integer> values = IntStream.range(0, streamSize).boxed()
    .flatMap(Streams.toIntervalMax(sliceSize))
    .collect(ISeq.toISeq());
``` 
* [#672](https://github.com/jenetics/jenetics/issues/672): Introduce the `StreamPublisher` class, which allows to use a _normal_ Java Stream in a _reactive_ way.
```java
final var publisher = new StreamPublisher<EvolutionResult<IntegerGene, Integer>>();
try (publisher) {
    final var stream= engine.stream();
    publisher.subscribe(new Subscriber<>() { ... });
    publisher.attach(stream);
    ...
}
```
* [#679](https://github.com/jenetics/jenetics/issues/679): Additional constructor for the `TournamentSelector`, which allows to define own `Phenotype` comparator.
* [#685](https://github.com/jenetics/jenetics/issues/685): Add `Engine.Setup` interface, which allows combining different dependent engine configurations.
* [#687](https://github.com/jenetics/jenetics/issues/687): Add engien setup for (μ,λ)- and (μ+λ)-Evolution Strategy.

#### Bugs

* [#663](https://github.com/jenetics/jenetics/issues/663): `PartialAlterer` uses fitness of unaltered phenotype.
* [#667](https://github.com/jenetics/jenetics/issues/667): Fix `Concurrency.close()` method.


### [5.2.0](https://github.com/jenetics/jenetics/releases/tag/v5.2.0)

#### Improvements

* [#542](https://github.com/jenetics/jenetics/issues/542): Introduce `InvertibleCodec` interface. This interface extends the the current `Codec` interface.
```java
public interface InvertibleCodec<T, G extends Gene<?, G>> extends Codec<T, G> {
    Function<T, Genotype<G>> encoder();
    default Genotype<G> encode(final T value) {
        return encoder().apply(value); 
    }
}
```
* [#543](https://github.com/jenetics/jenetics/issues/543): Simplified `Constraint` factory methods.
* [#566](https://github.com/jenetics/jenetics/issues/566): New parameter class, `EvolutionParams`, contains all `Engine` parameters which influence the evolution performance.
* [#607](https://github.com/jenetics/jenetics/issues/607): More flexible MOEA optimization. It is now possible to do minimization/maximization on every dimension independently.
* [#614](https://github.com/jenetics/jenetics/issues/614): Generalize the `ConstExprRewriter` class. It can no be used with every type, not only with `Double` values.
* [#635](https://github.com/jenetics/jenetics/issues/635): Mark the `Chromosome.toSeq()` and `Genotype.toSeq()` methods as deprecated. This methods are no longer needed, because the `Chromosome` and `Genotype` itself will implement the new `BaseSeq` interfaces and are now _sequences_ itself. 
* [#645](https://github.com/jenetics/jenetics/issues/645): Mark all bean-like _getter_ methods as deprecated. This methods will be replaced by simple _accessor_-methods, and is a preparation step for using the new Java _records_.


#### Bugs

* [#621](https://github.com/jenetics/jenetics/issues/621): `ìo.jenetics.prog.op.Program.arity()` returns the wrong value.



### [5.1.0](https://github.com/jenetics/jenetics/releases/tag/v5.1.0)

#### Improvements

* [#522](https://github.com/jenetics/jenetics/issues/522): Replace `io.jenetics.ext.engine.AdaptiveEngine` with `io.jenetics.ext.engine.UpdatableEngine`. The `AdaptiveEngine` has been marked as deprecated.
* [#557](https://github.com/jenetics/jenetics/issues/557): Implementation `io.jenetics.util.ProxySorter` class, which sorts a proxy array instead of an sequence itself.
* [#563](https://github.com/jenetics/jenetics/issues/563): Introduction of `Evolution` interface, which makes the _concept_ of an _evolution_ function more explicit.
* [#579](https://github.com/jenetics/jenetics/issues/579): Improve internal `RingBuffer` implementation.
* [#585](https://github.com/jenetics/jenetics/issues/585): Improve `EphemeralConst` serialization.
* [#592](https://github.com/jenetics/jenetics/issues/592): Add `Tree.path()` and `Tree.pathElements()` methods.

#### Bugs

* [#539](https://github.com/jenetics/jenetics/issues/539): Fix JHM tests.
* [#599](https://github.com/jenetics/jenetics/issues/599): `Recombinator` performs `recombine` on an individual with itself.
* [#600](https://github.com/jenetics/jenetics/issues/600): Duplicates in Pareto set owing to the `equals` method in `Phenotype` class.

### [5.0.1](https://github.com/jenetics/jenetics/releases/tag/v5.0.1)

#### Bugs

* [#550](https://github.com/jenetics/jenetics/issues/550): Erroneous index check for `Sample.argAt(int)` method in `io.jenetics.prog.regression` package. 
* [#554](https://github.com/jenetics/jenetics/issues/550): `ClassCastException` in `io.jenetics.prog.regression.Regression` class. 

### [5.0.0](https://github.com/jenetics/jenetics/releases/tag/v5.0.0)

#### Improvements

* [#534](https://github.com/jenetics/jenetics/issues/534): Generify `Regression` classes so it can be used for regression analysis of arbitrary types.
* [#529](https://github.com/jenetics/jenetics/issues/529): Implementation of Hybridizing PSM and RSM mutation operator (HPRM)
* [#518](https://github.com/jenetics/jenetics/issues/518): Implementation of Symbolic Regression classes. This makes it easier to solve such optimization problems.
* [#515](https://github.com/jenetics/jenetics/issues/515): Rename `Tree.getIndex(Tree)` to `Tree.indexOf(Tree)`.
* [#509](https://github.com/jenetics/jenetics/issues/509): Allow to collect the nth best optimization results.
```java
final ISeq<EvolutionResult<DoubleGene, Double>> best = engine.stream()
    .limit(Limits.bySteadyFitness(50))
    .flatMap(MinMax.toStrictlyIncreasing())
    .collect(ISeq.toISeq(10));
```
* [#504](https://github.com/jenetics/jenetics/issues/504): Rename `Tree.getChild(int)` to `Tree.childAt(int)`.
* [#500](https://github.com/jenetics/jenetics/issues/500): Implementation of Reverse Sequence mutation operator (RSM).
* [#497](https://github.com/jenetics/jenetics/issues/497): Implement Boolean operators for GP.
* [#496](https://github.com/jenetics/jenetics/issues/496): Implement `GT` operator for GP.
* [#493](https://github.com/jenetics/jenetics/issues/493): Add dotty tree formatter
* [#488](https://github.com/jenetics/jenetics/issues/488): Implement new tree formatter `TreeFormatter.LISP`. This allows to create a Lisp string representation from a given `Tree`.
* [#487](https://github.com/jenetics/jenetics/issues/487): Re-implementation of 'MathTreePruneAlterer'. The new implementation uses the newly introduced Tree Rewriting API, implemented in #442.
* [#486](https://github.com/jenetics/jenetics/issues/486): Implement `TreeRewriteAlterer`, based on the new Tree Rewriting API.
* [#485](https://github.com/jenetics/jenetics/issues/485): Cleanup of `MathExpr` class.
* [#484](https://github.com/jenetics/jenetics/issues/484): The `Tree.toString()` now returns a parentheses string.
* [#481](https://github.com/jenetics/jenetics/issues/481): The parentheses tree representation now only escapes "protected" characters.
* [#469](https://github.com/jenetics/jenetics/issues/469): Implementation of additional `Evaluator` factory methods.
* [#465](https://github.com/jenetics/jenetics/issues/465): Remove fitness scaler classes. The fitness scaler doesn't carry its weight.
* [#455](https://github.com/jenetics/jenetics/issues/455): Implementation of `CompletableFutureEvaluator`.
* [#450](https://github.com/jenetics/jenetics/issues/450): Improvement of `FutureEvaluator` class.
* [#449](https://github.com/jenetics/jenetics/issues/449): The `Engine.Builder` constructor is now public and is the most generic way for creating engine builder instances. All other builder factory methods are calling this _primary_ constructor.
* [#447](https://github.com/jenetics/jenetics/issues/447): Remove evolution iterators. The whole evolution is no performed via streams.
* [#442](https://github.com/jenetics/jenetics/issues/442): Introduce Tree Rewriting API, which allows to define own rewrite rules/system. This is very helpful when solving GP related problems.
```java
final TRS<String> trs = TRS.parse(
    "add(0,$x) -> $x",
    "add(S($x),$y) -> S(add($x,$y))",
    "mul(0,$x) -> 0",
    "mul(S($x),$y) -> add(mul($x,$y),$y)"
);

// Converting the input tree into its normal form.
final TreeNode<String> tree = TreeNode.parse("add(S(0),S(mul(S(0),S(S(0)))))");
trs.rewrite(tree);
assert tree.equals(TreeNode.parse("S(S(S(S(0))))"));
```
* [#372](https://github.com/jenetics/jenetics/issues/372): Allow to define the chromosome index an `Alterer` is allowed to change. This allows to define alterers for specific chromosomes in a genotype.
```java
// The genotype prototype, consisting of 4 chromosomes
final Genotype<DoubleGene> gtf = Genotype.of(
    DoubleChromosome.of(0, 1),
    DoubleChromosome.of(1, 2),
    DoubleChromosome.of(2, 3),
    DoubleChromosome.of(3, 4)
);

// Define the GA engine.
final Engine<DoubleGene, Double> engine = Engine
    .builder(gt -> gt.getGene().doubleValue(), gtf)
    .selector(new RouletteWheelSelector<>())
    .alterers(
        // The `Mutator` is used on chromosome with index 0 and 2.
        SectionAlterer.of(new Mutator<DoubleGene, Double>(), 0, 2),
        // The `MeanAlterer` is used on chromosome 3.
        SectionAlterer.of(new MeanAlterer<DoubleGene, Double>(), 3),
        // The `GaussianMutator` is used on all chromosomes.
        new GaussianMutator<>()
    )
    .build();
```
* [#368](https://github.com/jenetics/jenetics/issues/368): Remove deprecated code.
* [#364](https://github.com/jenetics/jenetics/issues/364): Clean implementation of async fitness functions.
* [#342](https://github.com/jenetics/jenetics/issues/342): The `Tree` accessor names are no longer in a Java Bean style: `getChild(int)` -> `childAt(int)`. This corresponds to the `childAtPath(path)` methods.
* [#331](https://github.com/jenetics/jenetics/issues/331): Remove `hashCode` and `equals` method from `Selector` and `Alterer`.
* [#314](https://github.com/jenetics/jenetics/issues/314): Add factory method for `AdaptiveEngine`, which simplifies its creation.
* [#308](https://github.com/jenetics/jenetics/issues/308): General improvement of object serialization.
* [#50](https://github.com/jenetics/jenetics/issues/50): Improve Genotype validation. The new `Constraint` interface, and its implementation `RetryConstraint`, now allows a finer control of the validation and recreation of individuals.


#### Bugs

* [#520](https://github.com/jenetics/jenetics/issues/520): Fix tree-rewriting for `Const` values. This leads to non-matching nodes when trying to simplify the GP tree.
* [#475](https://github.com/jenetics/jenetics/issues/475): Level function returns different results depending on whether the iterator is iterating through a `ProgramGene` or `TreeNode`.
* [#473](https://github.com/jenetics/jenetics/issues/473): `DynamicGenotype` example causes `IllegalArgumentException`.

### [4.4.0](https://github.com/jenetics/jenetics/releases/tag/v4.4.0)

#### Improvements

* [#316](https://github.com/jenetics/jenetics/issues/316): Improve implementation of tree rewriting. This is a preparations tep for [#442](https://github.com/jenetics/jenetics/issues/442).
* [#414](https://github.com/jenetics/jenetics/issues/414): Use Gradle 'implementation' instead of 'compile' dependency
* [#426](https://github.com/jenetics/jenetics/issues/426): Relax `final` restriction on some `Alterer` implementations. All alterers can now be sub-classed. 
* [#430](https://github.com/jenetics/jenetics/issues/430): Codec for numeric 2d matrices.
* [#433](https://github.com/jenetics/jenetics/issues/433): Upgrade Gradle to 5.x.
* [#443](https://github.com/jenetics/jenetics/issues/443): Precondition check for `XXXChromosome.of(Gene...)` factory methods.
* [#445](https://github.com/jenetics/jenetics/issues/445): Mark `Phenotype.newInstance` methods as deprecated. 
* [#457](https://github.com/jenetics/jenetics/issues/457): Add `<A> A[] Seq.toArray(IntFunction<A[]> generator)` method.


#### Bugs

* [#425](https://github.com/jenetics/jenetics/issues/425): Manual fixes.


### [4.3.0](https://github.com/jenetics/jenetics/releases/tag/v4.3.0)

#### Improvements

* [#347](https://github.com/jenetics/jenetics/issues/347): Improve `hashCode` and `equals` methods.
* [#349](https://github.com/jenetics/jenetics/issues/349): Cleanup of chromosome constructors. Make the constructors more regular.
* [#355](https://github.com/jenetics/jenetics/issues/355): Simplify implementation of numeric genes.
* [#361](https://github.com/jenetics/jenetics/issues/361): Add `NumericChromosome.primitiveStream()` methods.
* [#366](https://github.com/jenetics/jenetics/issues/366): Deprecate reference to fitness function property from `Phenotype`. Preparation step for generalizing the fitness evaluation.
* [#377](https://github.com/jenetics/jenetics/issues/377): Add `Tree.childAt` method. Lets you fetch deeply nested child nodes.
* [#378](https://github.com/jenetics/jenetics/issues/378): Convert tree to parentheses tree string.
* [#379](https://github.com/jenetics/jenetics/issues/379): Parse parentheses tree string to tree object.
* [#380](https://github.com/jenetics/jenetics/issues/380): Add `TreeNode.map` method.
* [#400](https://github.com/jenetics/jenetics/issues/400): Codec for mapping source- and target objects.
* [#406](https://github.com/jenetics/jenetics/issues/406): Make the library compilable under Java 11.
* [#411](https://github.com/jenetics/jenetics/issues/411): Improve the behaviour of the `MathExpr.format` method.

#### Bugs

* [#357](https://github.com/jenetics/jenetics/issues/357): Invalid length of selected population in `MonteCarloSelector`.
* [#420](https://github.com/jenetics/jenetics/issues/420): `Limits.byFitnessThreshold` termination strategy is missing _best_ generation.

### [4.2.1](https://github.com/jenetics/jenetics/releases/tag/v4.2.1)

#### Bugs

* [416](https://github.com/jenetics/jenetics/issues/416): Method internal `comb.subset` doesn't create all possible combinations. The `PermutationChromosome` is therefore not able to create solutions for the whole search space.

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
