# Jenetics


Jenetics is an Genetic Algorithm, respectively an Evolutionary Algorithm, library written in Java. It is designed with a clear separation of the several  algorithm concepts, e. g. Gene, Chromosome, Genotype, Phenotype, Population and  fitness Function. Jenetics allows you to minimize or maximize the given fitness  function without tweaking it.


## Requirements


- **JDK 1.7**: The JAVA_HOME variable must be set to your java installation directory.
- **JScience** library, <http://jscience.org>: This library is included and lies in the lib directory.

## Build Jenetics


For building the Jenetics library from source, download the most recent, stable package version from <https://sourceforge.net/projects/jenetics/files/latest/download> and extract it to some build directory.

    $ unzip jenetics-<version>.zip -d <builddir>

<version> denotes the actual Jenetics version and ```<builddir>``` the actual build directory. Alternatively you can check out the latest-unstable-version from the Mercurial default branch.

    $ hg clone http://hg.code.sf.net/p/jenetics/main <builddir>

Compiling the sources and building the JAR files is done with the jar Ant target. Change to the <builddir> directory and call

    $ ./lib/build/ant/bin/ant jar

The JAR files, and all other build artifacts, are stored in the ```<builddir>/ build/main``` directory. The available Ant targets are:

- **compile**: Compiles the Jenetics sources and copies the class files to the ```<builddir>/build/main``` directory.

- **example-compile**: Compiles the examples and copies the class files to the ```<builddir>/build/main``` directory.

- **test-compile**: Compiles the tests and the class files to the ```<builddir>/ build/main``` directory.

- **test-run**: Compiles and executes the unit tests. The test results are printed onto the console and a test-report, created by TestNG, is written to ```<builddir>/test-report.html```. Since some of the unit-tests are sta- tistical 13 tests it is possible that the number of failed tests is greater than zero and the test run is still successful.

- **perftest-run**: Runs some performance tests and stores the result into ```<builddir>/pertest-report.txt```

- **javadoc**: Generates the API documentation.

- **jar**: Compiles the sources and creates the JAR files. The artifacts are copied to the ```<builddir>/build/main``` directory.

- **clean**: Deletes the ```<builddir>/build/main``` directory and removes all other generated artifacts.


## Coding standards

Beside the Java coding standards as given in <http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html> the following extensions are used.

- All non-constant variables members start with underscore.
- Variable name for arrays or collections are plural.
- All helper classes which only contains static methods are lower-case. This  indicates that the given class can not be used as type, because no instance can be created.



