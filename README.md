# Jenetics


Jenetics is an Genetic Algorithm, respectively an Evolutionary Algorithm, library written in Java. It is designed with a clear separation of the several  algorithm concepts, e. g. `Gene`, `Chromosome`, `Genotype`, `Phenotype`, `Population` and  fitness `Function`. Jenetics allows you to minimize or maximize the given fitness  function without tweaking it.


## Requirements

### Build time
*  **JDK 1.7**: The `JAVA_HOME` variable must be set to your java installation directory.
### Run time
*  **JScience** library, <http://jscience.org>: This library is included and lies in the `project/lib` directory.

## Build Jenetics


For building the Jenetics library from source, download the most recent, stable package version from <https://sourceforge.net/projects/jenetics/files/latest/download> and extract it to some build directory.

    $ unzip jenetics-<version>.zip -d <builddir>

<version> denotes the actual Jenetics version and `<builddir>` the actual build directory. Alternatively you can check out the latest-unstable-version from the Mercurial default branch.

    $ hg clone http://hg.code.sf.net/p/jenetics/main <builddir>

Jenetics uses Gradle13 as build system and organizes the source into *sub*-projects (modules). Each sub-project is located in it’s own sub-directory:


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

For packaging (building) the source call

    $ cd <build-dir>
    $ gradle pack



**IDE Integration**

Gradle has tasks which creates the project file for Eclipse and IntelliJ IDEA. Call

    $ gradle [eclipse|idea]

for creating the project files for Eclipse or IntelliJ, respectively.

## Example

### Ones Counting

Ones counting is one of the simplest model-problem and consists of a binary chromosome. The fitness of a Genotype is proportional to the number of ones. The FitnessFunction looks like this:

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


The genotype in this example consists of one BitChromosome with a ones probability of 0.15. The altering of the offspring population is performed by mutation, with mutation probability of 0.55, and then by a single-point crossover, with crossover probability of 0.06. After creating the initial population, with the ga.setup() call, 100 generations are evolved. The tournament selector is used for both, the offspring- and the survivor selection---this is the default selector.

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

## Coding standards

Beside the Java coding standards as given in <http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html> the following extensions are used.

- All non-constant variables members start with underscore.
- Variable name for arrays or collections are plural.
- All helper classes which only contains static methods are lower-case. This  indicates that the given class can not be used as type, because no instance can be created.

## Licence

The library is licensed under the [GNU Lesser General Public License](https://www.gnu.org/licenses/lgpl-2.1.html)  as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
