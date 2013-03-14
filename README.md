# Jenetics


Jenetics is an Genetic Algorithm, respectively an Evolutionary Algorithm, library written in Java. It is designed with a clear separation of the several  algorithm concepts, e. g. `Gene`, `Chromosome`, `Genotype`, `Phenotype`, `Population` and  fitness `Function`. Jenetics allows you to minimize or maximize the given fitness  function without tweaking it.


## Requirements

### Build time
*  **JDK 1.7**: The `JAVA_HOME` variable must be set to your java installation directory.
*  **Gradle 1.3**: [Gradle](http://www.gradle.org/) is used for building the library.

### Run time
*  **JScience** library, <http://jscience.org>: This library is  included and lies in the `project/lib` directory.

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

Jenetics uses [Gradle 1.3](http://www.gradle.org/downloads) as build system and organizes the source into *sub*-projects (modules). Each sub-project is located in it’s own sub-directory:

* **org.jenetics**: This project contains the source code and tests for the Jenetics core-module.
* **org.jenetics.example**: This project contains example code for the *core*-module.
* **org.jenetics.doc**: Contains the code of the web-site and the manual.

For building the library change into the `<builddir>` directory (or one of the module directory) and call one of the available tasks:

* **pack**: Compiles the sources of all modules, creates the JAR files and the Javadoc. The build artifacts are copied into the `<builddir>/build/package` directory. This task is only available in the `<builddir>` directory.
* **compileJava**: Compiles the Jenetics sources and copies the class files to the `<builddir>/<module-dir>/build/classes/main` directory.
* **test**: Compiles and executes the unit tests. The test results are printed onto the console and a test-report, created by TestNG, is written to `<builddir>/<module-dir>` directory.
* **javadoc**: Generates the API documentation. The Javadoc is stored in the `<builddir>/<module-dir>/build/docs` directory
* **jar**: Compiles the sources and creates the JAR files. The artifacts are copied to the `<builddir>/<module-dir>/build/libs` directory.
* **clean**: Deletes the `<builddir>/build/*` directories and removes all generated artifacts.

For packaging (building)  the source call

    $ cd <build-dir>
    $ gradle pack



**IDE Integration**

Gradle has tasks which creates the project file for Eclipse and IntelliJ IDEA. Call

    $ gradle [eclipse|idea]

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
		public Integer apply(Genotype<BitGene> genotype) {
			int count = 0;
			for (BitGene gene : genotype.getChromosome()) {
				if (gene.getBit()) {
					++count;
				}
			}
			return count;
		}
	}

	public class OnesCounting {
		public static void main(String[] args) {
			Factory<Genotype<BitGene>> gtf = Genotype.valueOf(
				new BitChromosome(20, 0.15)
			);
			Function<Genotype<BitGene>, Integer> ff = new OneCounter();
			GeneticAlgorithm<BitGene, Integer> ga =
				new GeneticAlgorithm<>(gtf, ff, Optimize.MAXIMUM);

			ga.setStatisticsCalculator(
				new NumberStatistics.Calculator<BitGene, Integer>()
			);
			ga.setPopulationSize(50);
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
		}
	}


The genotype in this example consists of one `BitChromosome` with a ones probability of 0.15. The altering of the offspring population is performed by mutation, with mutation probability of 0.55, and then by a single-point crossover, with crossover probability of 0.06. After creating the initial population, with the `ga.setup()` call, 100 generations are evolved. The tournament selector is used for both, the offspring- and the survivor selection-this is the default selector.

	+---------------------------------------------------------+
	|  Population Statistics                                  |
	+---------------------------------------------------------+
	|                     Age mean: 1.36000000000             |
	|                 Age variance: 3.74530612245             |
	|                      Samples: 50                        |
	|                 Best fitness: 18                        |
	|                Worst fitness: 5                         |
	+---------------------------------------------------------+
	+---------------------------------------------------------+
	|  Fitness Statistics                                     |
	+---------------------------------------------------------+
	|                 Fitness mean: 12.30000000000            |
	|             Fitness variance: 8.25510204082             |
	|        Fitness error of mean: 1.73948268172             |
	+---------------------------------------------------------+


The given example will print the overall timing statistics onto the console.

### 0/1 Knapsack Problem

In the [knapsack problem](http://en.wikipedia.org/wiki/Knapsack_problem) a set of items, together with their size and value, is given. The task is to select a disjoint subset so that the total size does not exeed the knapsacks size. For the 0/1 knapsack problem we define a `BitChromosome`, one bit for each item. If the ith `BitGene` is set to one the ith item is selected.

	import org.jscience.mathematics.number.Float64;

	import org.jenetics.BitChromosome;
	import org.jenetics.BitGene;
	import org.jenetics.Chromosome;
	import org.jenetics.GeneticAlgorithm;
	import org.jenetics.Genotype;
	import org.jenetics.Mutator;
	import org.jenetics.NumberStatistics;
	import org.jenetics.RouletteWheelSelector;
	import org.jenetics.SinglePointCrossover;
	import org.jenetics.util.Factory;
	import org.jenetics.util.Function;

	final class Item {
		public double size;
		public double value;
	}

	final class KnappsackFunction
		implements Function<Genotype<BitGene>, Float64>
	{
		private final Item[] _items;
		private final double _size;

		public KnappsackFunction(final Item[] items, double size) {
			_items = items;
			_size = size;
		}

		public Item[] getItems() {
			return _items;
		}

		@Override
		public Float64 apply(final Genotype<BitGene> genotype) {
			final Chromosome<BitGene> ch = genotype.getChromosome();

			double size = 0;
			double value = 0;
			for (int i = 0, n = ch.length(); i < n; ++i) {
				if (ch.getGene(i).getBit()) {
					size += _items[i].size;
					value += _items[i].value;
				}
			}

			if (size > _size) {
				return Float64.ZERO;
			} else {
				return Float64.valueOf(value);
			}
		}
	}

	public class Knapsack {

		private static KnappsackFunction FF(int n, double size) {
			Item[] items = new Item[n];
			for (int i = 0; i < items.length; ++i) {
				items[i] = new Item();
				items[i].size = (Math.random() + 1)*10;
				items[i].value = (Math.random() + 1)*15;
			}

			return new KnappsackFunction(items, size);
		}

		public static void main(String[] argv) throws Exception {
			KnappsackFunction ff = FF(15, 100);
			Factory<Genotype<BitGene>> genotype = Genotype.valueOf(
				new BitChromosome(15, 0.5)
			);

			GeneticAlgorithm<BitGene, Float64> ga =
				new GeneticAlgorithm<>(genotype, ff);

			ga.setMaximalPhenotypeAge(30);
			ga.setPopulationSize(100);
			ga.setStatisticsCalculator(
				new NumberStatistics.Calculator<BitGene, Float64>()
			);
			ga.setSelectors(
				new RouletteWheelSelector<BitGene, Float64>()
			);
			ga.setAlterers(
				new Mutator<BitGene>(0.115),
				new SinglePointCrossover<BitGene>(0.16)
			);

			ga.setup();
			ga.evolve(100);
			System.out.println(ga.getBestStatistics());
		}
	}


The console out put for the Knapsack GA will look like the listing beneath.

	+---------------------------------------------------------+
	|  Population Statistics                                  |
	+---------------------------------------------------------+
	|                     Age mean: 1.55000000000             |
	|                 Age variance: 2.69444444444             |
	|                      Samples: 100                       |
	|                 Best fitness: 188.57227213871303        |
	|                Worst fitness: 0.0                       |
	+---------------------------------------------------------+
	+---------------------------------------------------------+
	|  Fitness Statistics                                     |
	+---------------------------------------------------------+
	|                 Fitness mean: 157.60654768894           |
	|             Fitness variance: 1486.23455609328          |
	|        Fitness error of mean: 15.76065476889            |
	+---------------------------------------------------------+



## Traveling Salesman Problem (TSP)

The Traveling Salesman problem is a very good example which shows you how to solve combinatorial problems with an GA. Jenetics contains several classes which will work very well with this kind of problems. Wrapping the base type into an `EnumGene` is the first thing to do. In our example, every city has an unique number, that means we are wrapping an Integer into an `EnumGene`. Creating a genotype for integer values is very easy with the factory method of the `PermutationChromosome`. For other data types you have to use one of the constructors of the permutation chromosome. As alterers, we are using a swap-mutator and a partially-matched crossover. These alterers guarantees that no invalid solutions are created—every city exists exactly once in the altered chromosomes.

	import static java.lang.Math.PI;
	import static java.lang.Math.abs;
	import static java.lang.Math.sin;

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

	class FF
		implements Function<Genotype<EnumGene<Integer>>, Float64>
	{
		private final double[][] _adjacence;
		public FF(final double[][] adjacence) {
			_adjacence = adjacence;
		}
		@Override
		public Float64 apply(Genotype<EnumGene<Integer>> genotype) {
			Chromosome<EnumGene<Integer>> path =
				genotype.getChromosome();

			double length = 0.0;
			for (int i = 0, n = path.length(); i < n; ++i) {
				final int from = path.getGene(i).getAllele();
				final int to = path.getGene((i + 1)%n).getAllele();
				length += _adjacence[from][to];
			}
			return Float64.valueOf(length);
		}
	}

	public class TravelingSalesman {

		public static void main(String[] args) {
			final int stops = 20;

			Function<Genotype<EnumGene<Integer>>, Float64> ff =
				new FF(adjacencyMatrix(stops));
			Factory<Genotype<EnumGene<Integer>>> gt = Genotype.valueOf(
				PermutationChromosome.ofInteger(stops)
			);
			final GeneticAlgorithm<EnumGene<Integer>, Float64>
				ga = new GeneticAlgorithm<>(gt, ff, Optimize.MINIMUM);
			ga.setStatisticsCalculator(
				new Calculator<EnumGene<Integer>, Float64>()
			);
			ga.setPopulationSize(300);
			ga.setAlterers(
				new SwapMutator<EnumGene<Integer>>(0.2),
				new PartiallyMatchedCrossover<Integer>(0.3)
			);

			ga.setup();
			ga.evolve(700);
			System.out.println(ga.getBestStatistics());
			System.out.println(ga.getBestPhenotype());
		}

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
	|                     Age mean: 1.48333333333             |
	|                 Age variance: 3.72212931996             |
	|                      Samples: 300                       |
	|                 Best fitness: 62.573786016092335        |
	|                Worst fitness: 315.49784819824816        |
	+---------------------------------------------------------+
	+---------------------------------------------------------+
	|  Fitness Statistics                                     |
	+---------------------------------------------------------+
	|                 Fitness mean: 118.87334461782           |
	|             Fitness variance: 6464.82405084876          |
	|        Fitness error of mean: 6.86315575146             |
	+---------------------------------------------------------+
	[19|18|17|16|15|14|13|12|11|10|9|8|7|6|5|4|3|2|1|0] --> 62.573786016092335


## Coding standards

Beside the Java coding standards as given in <http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html> the following extensions are used.

- All non-constant variables members start with underscore.
- Variable name for arrays or collections are plural.
- All helper classes which only contains static methods are lower-case. This  indicates that the given class can not be used as type, because no instance can be created.

## Licence

The library is licensed under the [GNU Lesser General Public License](https://www.gnu.org/licenses/lgpl-2.1.html)  as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
