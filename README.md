# Jenetics (_1.6.0_)


Jenetics is an Genetic Algorithm, respectively an Evolutionary Algorithm, library written in Java. It is designed with a clear separation of the several  algorithm concepts, e. g. `Gene`, `Chromosome`, `Genotype`, `Phenotype`, `Population` and  fitness `Function`. Jenetics allows you to minimize or maximize the given fitness  function without tweaking it.


## Requirements

### Build time
*  **JDK 7**: The `JAVA_HOME` variable must be set to your java installation directory.
*  **Gradle 1.10**: [Gradle](http://www.gradle.org/) is used for building the library. (Gradle is download automatically, if you are using the Gradle Wrapper script `gradlew`, located in the base directory, for building the library.)

### Test compile/execution
*  **TestNG 8.8**: Jenetics uses [TestNG](http://testng.org/doc/index.html) framework for unit tests. 
*  **Apache Commons Math 3.2**: [Library](http://commons.apache.org/proper/commons-math/) is used for testing statistical accumulators.

### Runtime
*  **JRE 7**: Java runtime version 7 is needed for using the library, respectively for running the examples.
*  **JScience** library, <http://jscience.org>: This library is  included and lies in the `buildSrc/lib` directory.

## Download
*  **Sourceforge**:  <https://sourceforge.net/projects/jenetics/files/latest/download>
*  **Bitbucket**:  <https://bitbucket.org/fwilhelm/jenetics/downloads>

## Build Jenetics


For building the Jenetics library from source, download the most recent, stable package version from [Sourceforge](https://sourceforge.net/projects/jenetics/files/latest/download) or [Bitbucket](https://bitbucket.org/fwilhelm/jenetics/downloads) and extract it to some build directory.

    $ unzip jenetics-<version>.zip -d <builddir>

`<version>` denotes the actual Jenetics version and `<builddir>` the actual build directory. Alternatively you can check out the latest-unstable-version from the Mercurial default branch.

    $ hg clone https://bitbucket.org/fwilhelm/jenetics <builddir>
    # or
    $ hg clone http://hg.code.sf.net/p/jenetics/main <builddir>
    # or
    $ git clone https://github.com/jenetics/jenetics.git <builddir>

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

## Examples
### Ones Counting

Ones counting is one of the simplest model-problem and consists of a binary chromosome. The fitness of a `Genotype` is proportional to the number of ones. The fitness `Function` looks like this:

	import org.jenetics.BitChromosome;
	import org.jenetics.BitGene;
	import org.jenetics.GeneticAlgorithm;
	import org.jenetics.Genotype;
	import org.jenetics.Mutator;
	import org.jenetics.NumberStatistics;
	import org.jenetics.Optimize;
	import org.jenetics.RouletteWheelSelector;
	import org.jenetics.SinglePointCrossover;
	import org.jenetics.util.Factory;
	import org.jenetics.util.Function;

	final class OneCounter
		implements Function<Genotype<BitGene>, Integer>
	{
		@Override
		public Integer apply(final Genotype<BitGene> genotype) {
			return ((BitChromosome)genotype.getChromosome()).bitCount();
		}
	}

	public class OnesCounting {
		public static void main(String[] args) {
			Factory<Genotype<BitGene>> gtf = Genotype.of(
				BitChromosome.of(20, 0.15)
			);
			Function<Genotype<BitGene>, Integer> ff = new OneCounter();
			GeneticAlgorithm<BitGene, Integer> ga =
			new GeneticAlgorithm<>(
				gtf, ff, Optimize.MAXIMUM
			);

			ga.setStatisticsCalculator(
				new NumberStatistics.Calculator<BitGene, Integer>()
			);
			ga.setPopulationSize(500);
			ga.setSelectors(
				new RouletteWheelSelector<BitGene, Integer>()
			);
			ga.setAlterers(
				new Mutator<BitGene>(0.55),
				new SinglePointCrossover<BitGene>(0.06)
			);

			ga.setup();
			ga.evolve(100);
			System.out.println(ga.getBestStatistics());
			System.out.println(ga.getBestPhenotype());
		}
	}


The genotype in this example consists of one `BitChromosome` with a ones probability of 0.15. The altering of the offspring population is performed by mutation, with mutation probability of 0.55, and then by a single-point crossover, with crossover probability of 0.06. After creating the initial population, with the `ga.setup()` call, 100 generations are evolved. The tournament selector is used for both, the offspring- and the survivor selection-this is the default selector.

	+---------------------------------------------------------+
	|  Population Statistics                                  |
	+---------------------------------------------------------+
	|                     Age mean: 1.14800000000             |
	|                 Age variance: 2.88386372745             |
	|                      Samples: 500                       |
	|                 Best fitness: 19                        |
	|                Worst fitness: 4                         |
	+---------------------------------------------------------+
	+---------------------------------------------------------+
	|  Fitness Statistics                                     |
	+---------------------------------------------------------+
	|                 Fitness mean: 11.00400000000            |
	|             Fitness variance: 6.28856112224             |
	|        Fitness error of mean: 0.49211384049             |
	+---------------------------------------------------------+
	[00001111|11111111|11111011] --> 19


The given example will print the overall timing statistics onto the console.

### 0/1 Knapsack Problem

In the [knapsack problem](http://en.wikipedia.org/wiki/Knapsack_problem) a set of items, together with their size and value, is given. The task is to select a disjoint subset so that the total size does not exeed the knapsacks size. For the 0/1 knapsack problem we define a `BitChromosome`, one bit for each item. If the ith `BitGene` is set to one the ith item is selected.

	import static org.jenetics.util.math.random.nextDouble;

	import java.util.Random;

	import org.jenetics.BitChromosome;
	import org.jenetics.BitGene;
	import org.jenetics.Chromosome;
	import org.jenetics.GeneticAlgorithm;
	import org.jenetics.Genotype;
	import org.jenetics.Mutator;
	import org.jenetics.NumberStatistics;
	import org.jenetics.RouletteWheelSelector;
	import org.jenetics.SinglePointCrossover;
	import org.jenetics.TournamentSelector;
	import org.jenetics.util.Factory;
	import org.jenetics.util.Function;
	import org.jenetics.util.RandomRegistry;

	final class Item {
		public final double size;
		public final double value;

		Item(final double size, final double value) {
			this.size = size;
			this.value = value;
		}
	}

	final class KnapsackFunction
		implements Function<Genotype<BitGene>, Double>
	{
		private final Item[] items;
		private final double size;

		public KnapsackFunction(final Item[] items, double size) {
			this.items = items;
			this.size = size;
		}

		@Override
		public Double apply(final Genotype<BitGene> genotype) {
			final Chromosome<BitGene> ch = genotype.getChromosome();

			double size = 0;
			double value = 0;
			for (int i = 0, n = ch.length(); i < n; ++i) {
				if (ch.getGene(i).getBit()) {
					size += items[i].size;
					value += items[i].value;
				}
			}

			return size <= this.size ? value : 0;
		}
	}

	public class Knapsack {

		private static KnapsackFunction FF(final int n, final double size) {
			final Random random = RandomRegistry.getRandom();
			final Item[] items = new Item[n];
			for (int i = 0; i < items.length; ++i) {
				items[i] = new Item(
					nextDouble(random, 1, 10),
					nextDouble(random, 1, 15)
				);
			}

			return new KnapsackFunction(items, size);
		}

		public static void main(String[] args) throws Exception {
			final KnapsackFunction ff = FF(15, 100);
			final Factory<Genotype<BitGene>> genotype = Genotype.of(
				BitChromosome.of(15, 0.5)
			);

			final GeneticAlgorithm<BitGene, Double> ga = new GeneticAlgorithm<>(
				genotype, ff
			);
			ga.setPopulationSize(500);
			ga.setStatisticsCalculator(
				new NumberStatistics.Calculator<BitGene, Double>()
			);
			ga.setSurvivorSelector(
				new TournamentSelector<BitGene, Double>(5)
			);
			ga.setOffspringSelector(
				new RouletteWheelSelector<BitGene, Double>()
			);
			ga.setAlterers(
				new Mutator<BitGene>(0.115),
				new SinglePointCrossover<BitGene>(0.16)
			);

			ga.setup();
			ga.evolve(100);
			System.out.println(ga.getBestStatistics());
			System.out.println(ga.getBestPhenotype());
		}
	}


The console out put for the Knapsack GA will look like the listing beneath.

	+---------------------------------------------------------+
	|  Population Statistics                                  |
	+---------------------------------------------------------+
	|                     Age mean: 2.15800000000             |
	|                 Age variance: 4.71446492986             |
	|                      Samples: 500                       |
	|                 Best fitness: 99.95345446956883         |
	|                Worst fitness: 25.50205183297677         |
	+---------------------------------------------------------+
	+---------------------------------------------------------+
	|  Fitness Statistics                                     |
	+---------------------------------------------------------+
	|                 Fitness mean: 88.61369640889            |
	|             Fitness variance: 129.23019686091           |
	|        Fitness error of mean: 3.96292497816             |
	+---------------------------------------------------------+
	[01111111|11111111] --> 99.95345446956883


## Traveling Salesman Problem (TSP)

The Traveling Salesman problem is a very good example which shows you how to solve combinatorial problems with an GA. Jenetics contains several classes which will work very well with this kind of problems. Wrapping the base type into an `EnumGene` is the first thing to do. In our example, every city has an unique number, that means we are wrapping an Integer into an `EnumGene`. Creating a genotype for integer values is very easy with the factory method of the `PermutationChromosome`. For other data types you have to use one of the constructors of the permutation chromosome. As alterers, we are using a swap-mutator and a partially-matched crossover. These alterers guarantees that no invalid solutions are created—every city exists exactly once in the altered chromosomes.

	import static java.lang.Math.PI;
	import static java.lang.Math.abs;
	import static java.lang.Math.sin;

	import java.io.Serializable;

	import org.jenetics.Chromosome;
	import org.jenetics.EnumGene;
	import org.jenetics.GeneticAlgorithm;
	import org.jenetics.Genotype;
	import org.jenetics.NumberStatistics.Calculator;
	import org.jenetics.Optimize;
	import org.jenetics.PartiallyMatchedCrossover;
	import org.jenetics.PermutationChromosome;
	import org.jenetics.SwapMutator;
	import org.jenetics.util.Factory;
	import org.jenetics.util.Function;

	public class TravelingSalesman {

		private static class FF
			implements Function<Genotype<EnumGene<Integer>>, Double>,
						Serializable
		{
			private static final long serialVersionUID = 1L;

			private final double[][] adjacence;

			public FF(final double[][] adjacence) {
				this.adjacence = adjacence;
			}

			@Override
			public Double apply(final Genotype<EnumGene<Integer>> genotype) {
				final Chromosome<EnumGene<Integer>> path = genotype.getChromosome();

				double length = 0.0;
				for (int i = 0, n = path.length(); i < n; ++i) {
					final int from = path.getGene(i).getAllele();
					final int to = path.getGene((i + 1)%n).getAllele();
					length += adjacence[from][to];
				}
				return length;
			}

			@Override
			public String toString() {
				return "Point distance";
			}
		}

		public static void main(String[] args) {
			final int stops = 20;

			final Function<Genotype<EnumGene<Integer>>, Double> ff = new FF(adjacencyMatrix(stops));
			final Factory<Genotype<EnumGene<Integer>>> gtf = Genotype.of(
				PermutationChromosome.ofInteger(stops)
			);
			final GeneticAlgorithm<EnumGene<Integer>, Double>
				ga = new GeneticAlgorithm<>(gtf, ff, Optimize.MINIMUM);
			ga.setStatisticsCalculator(
					new Calculator<EnumGene<Integer>, Double>()
				);
			ga.setPopulationSize(500);
			ga.setAlterers(
				new SwapMutator<EnumGene<Integer>>(0.2),
				new PartiallyMatchedCrossover<Integer>(0.3)
			);

			ga.setup();
			ga.evolve(100);
			System.out.println(ga.getBestStatistics());
			System.out.println(ga.getBestPhenotype());
		}

		/**
		* All points in the created adjacency matrix lie on a circle. So it is easy
		* to check the quality of the solution found by the GA.
		*/
		private static double[][] adjacencyMatrix(int stops) {
			double[][] matrix = new double[stops][stops];
			for (int i = 0; i < stops; ++i) {
				for (int j = 0; j < stops; ++j) {
					matrix[i][j] = chord(stops, abs(i - j), RADIUS);
				}
			}
			return matrix;
		}
		private static double chord(int stops, int i, double r) {
			return 2.0*r*abs(sin((PI*i)/stops));
		}
		private static double RADIUS = 10.0;
	}


The listing above shows the output generated by our example. The last line represents the phenotype of the best solution found by the GA, which represents the traveling path. As you can see, the GA has found the shortest path, in reverse order.

	+---------------------------------------------------------+
	|  Population Statistics                                  |
	+---------------------------------------------------------+
	|                     Age mean: 1.67600000000             |
	|                 Age variance: 4.17537474950             |
	|                      Samples: 500                       |
	|                 Best fitness: 104.39898868995472        |
	|                Worst fitness: 310.43291286365866        |
	+---------------------------------------------------------+
	+---------------------------------------------------------+
	|  Fitness Statistics                                     |
	+---------------------------------------------------------+
	|                 Fitness mean: 132.54050652459           |
	|             Fitness variance: 3230.15643373580          |
	|        Fitness error of mean: 5.92739164722             |
	+---------------------------------------------------------+
	[1|0|19|16|15|5|6|7|8|9|10|11|12|13|14|17|18|4|3|2] --> 104.39898868995472


## Coding standards

Beside the Java coding standards as given in <http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html> the following extensions are used.

- All non-constant variables members start with underscore.
- Variable name for arrays or collections are plural.
- All helper classes which only contains static methods are lower-case. This  indicates that the given class can not be used as type, because no instance can be created.

## Release notes

### 1.6.0

* Preparation work for removing the dependency to the JScience library.
    * Add Double/Long Gene/Chromosome as a replacement for Float64/Integer64 Gene/Chromosome.
    * Add JAXB XML serialization as a replacement of the Javolution XML marshalling.
* Streamlining of the existing API: Marking inconsistent methods/classes as deprecated.

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
