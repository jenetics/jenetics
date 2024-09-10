# Optimizing with Jenetics

Jenetics is a library which allows to solve optimization problems using metaheuristic methods.

> In computer science and mathematical optimization, a metaheuristic is a higher-level procedure or heuristic designed to find, generate, tune, or select a heuristic (partial search algorithm) that may provide a sufficiently good solution to an optimization problem or a machine learning problem, especially with incomplete or imperfect information or limited computation capacity.
> 
> [_Wikipedia_: Metaheuristic](https://en.wikipedia.org/wiki/Metaheuristic)

Trying to solve a non-trivial optimization problem, one might be overwhelmed by the problem itself **and** the _correct_ usage of the Jenetics library. This might be the case when one is not familiar with Metaheuristics in general, or the field of Evolutionary algorithms in particular. The following will give a simple _methodology_ when developing optimization algorithms with Jenetics.

Before describing one best practise when implementing an optimization algorithms with Jenetics, we should recap what an optimization problem actually is.

> In mathematics, engineering, computer science and economics, an optimization problem is the problem of finding the best solution from all feasible solutions.
> 
> [_Wikipedia_: Optimization problem](https://en.wikipedia.org/wiki/Optimization_problem)

A graphical representation of an optimization problem is shown in the diagram above.

![SVG Image](jenetics.doc/src/main/resources/graphic/OptimizationProblem.svg)

 From a given solution space, _S_, we want to find the solution with the _optimal_ fitness value, which fulfills a given constraint, _C_. With this definition, we can start and suggest some implementation strategies using Jenetics.

## General methodology

Jenetics uses the `Genotpye` class as unified view onto the solution space of the problem. For trivial "Hello World"-problems, it is totally fine to describe the solution space in terms of this class. E.g. for the Bit-count _text book_ example, where the fitness function directly uses the `Genotype` as input parameter.

```java
static int count(final Genotype<BitGene> gt) {
    return gt.chromosome()
        .as(BitChromosome.class)
        .bitCount();
}
```

For non-trivial optimization problems, using the encoding classes  `Genotype`, directly is not the best way using the Jenetics library. 

It is recommended to first find a domain model, `T`, which best represents your solution space. Ideally, the domain model doesn't allow the modelling of invalid solutions. The fitness function calculates the fitness value with the model class `T`, instead of the `Genotype`, which usually simplifies the implementation. 

> **Hint:** Choose a _native_ solution model, `T`, which is able to represent only valid solution candidates, or as view as possible invalid solution candidates.

To connect the _native_ domain model and the fitness function with the Jenetics `Engine`, an appropriate `Codec` must be implemented. The `Codec` is the connector between the _native_ model and the `Engine`. With this approach, one can separate the problem of implementing the fitness function from the _mapping_ between `Genotype` and _native_ model `T`.

> **Hint:** The `Codec` should map every `Genotype` to only valid solution candidates in `T`, or as view as possible invalid solution candidates. Every `Genotype` should also be mapped to only one solution candidate in `T`.

**Implementation steps**
1) Find domain model, `T`, for the solution space.
2) Implement the fitness function in terms of `T`.
3) Find a `Codec` which maps a `Genotype` to `T`. 
4) Setup `Engine` with `Codec` and fitness function, _f(T)_.

The following code snippet shows how to use a `Codec` for the Ones-count problem, introduced above. It shows the steps 2 to 4 from the general solution template. Step 1, finding the domain model representing the solution space, is trivial in this example: `T := int`.

```java
// 2. Fitness function working on native solution space.
static int count(final int count) {
    return count;
}

// 3. Codec for Genotype <--> int mapping.
final Codec<Integer, BitGene> codec = Codec.of(
    Genotype.of(BitChromosome.of(100)),
    gt -> gt.chromosome()
        .as(BitChromosome.class)
        .bitCount()
);

// 4. Engine creation.
final static Engine<BitGene, Integer> engine = Engine
    .builder(this::count, codec)
    .build();
```

Although the given guideline seems quite simplistic, it allows to break down the implementation process into several step, which can be solved one after another. And this can help to reduce the overall complexity of the final implementation.
