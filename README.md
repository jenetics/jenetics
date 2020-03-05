# Jenetics

[![Build Status](https://travis-ci.org/jenetics/jenetics.svg?branch=master)](https://travis-ci.org/jenetics/jenetics)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.jenetics/jenetics/badge.svg)](http://search.maven.org/#search|ga|1|a%3A%22jenetics%22)
[![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics)
[![Code Quality: Java](https://img.shields.io/lgtm/grade/java/g/jenetics/jenetics.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/jenetics/jenetics/context:java)
[![Total Alerts](https://img.shields.io/lgtm/alerts/g/jenetics/jenetics.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/jenetics/jenetics/alerts)

**Jenetics** is an **Genetic Algorithm**, **Evolutionary Algorithm**, **Genetic Programming**, and **Multi-objective Optimization** library, written in modern day Java. It is designed with a clear separation of the several concepts of the algorithm, e.g. `Gene`, `Chromosome`, `Genotype`, `Phenotype`, `Population` and fitness `Function`. **Jenetics** allows you to minimize and maximize the given fitness function without tweaking it. In contrast to other GA implementations, the library uses the concept of an evolution stream (`EvolutionStream`) for executing the evolution steps. Since the `EvolutionStream` implements the Java Stream interface, it works smoothly with the rest of the Java Stream API.

**Other languages**

* [**Jenetics.Net**](https://github.com/rmeindl/jenetics.net): Experimental .NET Core port in C# of the base library. 
* [**Helisa**](https://github.com/softwaremill/helisa/): Scala wrapper around the Jenetics library.

## Documentation

The library is fully documented ([javadoc](http://jenetics.io/javadoc/jenetics/5.2/index.html)) and comes with an user manual ([pdf](http://jenetics.io/manual/manual-5.2.0.pdf)).


## Requirements

### Runtime
*  **JRE 8**: Java runtime version 8 is needed for using the library, respectively for running the examples.

### Build time
*  **JDK 8**: The Java [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) must be installed.
*  **Gradle 5.x**: [Gradle](http://www.gradle.org/) is used for building the library. (Gradle is download automatically, if you are using the Gradle Wrapper script `./gradlew`, located in the base directory, for building the library.)

### Test compile/execution
*  **TestNG 7.x**: Jenetics uses [TestNG](http://testng.org/doc/index.html) framework for unit tests.
*  **Apache Commons Math 3.6**: [Library](http://commons.apache.org/proper/commons-math/) is used for testing statistical collectors.

## Build Jenetics

Check out the master branch from Github.

    $ git clone https://github.com/jenetics/jenetics.git <builddir>

Jenetics uses [Gradle](http://www.gradle.org/downloads) as build system and organizes the source into *sub*-projects (modules). Each sub-project is located in it’s own sub-directory:

**Published projects**

The following projects/modules are also published to Maven.

* **[jenetics](jenetics)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics): This project contains the source code and tests for the Jenetics core-module.
* **[jenetics.ext](jenetics.ext)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics.ext): This module contains additional _non_-standard GA operations and data types. It also contains classes for solving multi-objective problems (MOEA). 
* **[jenetics.prog](jenetics.prog)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics.prog): The modules contains classes which allows to do genetic programming (GP). It seamlessly works with the existing `EvolutionStream` and evolution `Engine`.
* **[jenetics.xml](jenteics,xml)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics.xml): XML marshalling module for the _Jenetics_ base data structures.

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
        return gt.chromosome()
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

* <a href="https://renaissance.dev/"><b>Renaissance Suite</b>:</a> Renaissance is a modern, open, and diversified benchmark suite for the JVM, aimed at testing JIT compilers, garbage collectors, profilers, analyzers and other tools.
* <a href="https://www.chartsy.one/"><b>Chartsy|One</b>:</a> Chartsy|One is a Netbeans based tool for stock market investors and traders.
* <a href="http://chronetic.io/"><b>Chronetic</b>:</a> Chronetic is an open-source time pattern analysis library built to describe time-series data.
* <a href="http://www.eclipse.org/app4mc/"><b>APP4MC</b>:</a> Eclipse APP4MC is a platform for engineering embedded multi- and many-core software systems.

## Blogs

* <a href="http://www.baeldung.com/jenetics">Introduction to Jenetics Library</a>, by <em>baeldung</em>, April 11. 2017
* <a href="http://blog.takipi.com/how-to-solve-tough-problems-using-genetic-algorithms/">How to Solve Tough Problems Using Genetic Algorithms</a>, by <em>Tzofia Shiftan</em>, April 6. 2017
* <a href="http://fxapps.blogspot.co.at/2017/01/genetic-algorithms-with-java.html">Genetic algorithms with Java</a>, by <em>William Antônio</em>, January 10. 2017
* <a href="http://jdm.kr/blog/135">Jenetics 설치 및 예제</a>, by <em>JDM</em>, May 8. 2015
* <a href="http://jdm.kr/blog/104">유전 알고리즘 (Genetic Algorithms)</a>, by <em>JDM</em>, April 2. 2015

## Citations

* Raúl Lara-Cabrera, Ángel González-Prieto, Fernando Ortega and Jesús Bobadilla. <a href="https://www.mdpi.com/2076-3417/10/2/675">Evolving Matrix-Factorization-Based Collaborative Filtering Using Genetic Programming.</a> <em>MDPI, Applied Sciences. </em></a> Feb. 2020.
* Humm B.G., Hutter M. <a href="https://link.springer.com/chapter/10.1007/978-3-030-41913-4_12">Learning Patterns for Complex Event Detection in Robot Sensor Data.</a> <em>Optimization and Learning. OLA 2020. Communications in Computer and Information Science, vol 1173. Springer </em></a> Feb. 2020.
* Erich C. Teppan, Giacomo Da Col. <a href="https://link.springer.com/chapter/10.1007/978-981-15-1918-5_7">Genetic Algorithms for Creating Large Job Shop Dispatching Rules. </a> <em>Advances in Integrations of Intelligent Methods. Smart Innovation, Systems and Technologies, vol 170. Springer, Singapore. </em></a> Jan. 2020.
* Ricardo Pérez-Castillo, Francisco Ruiz, Mario Piattini. <a href="https://www.sciencedirect.com/science/article/pii/S016792362030004X">A decision-making support system for Enterprise Architecture Modelling. </a> <em>Decision Support Systems. </em></a> Jan. 2020.
* Sabrina Appel, Wolfgang Geithner, Stephan Reimann, Mariusz Sapinski, Rahul Singh and Dominik Vilsmeier. <a href="https://www.worldscientific.com/doi/abs/10.1142/S0217751X19420193">Application of nature-inspired optimization algorithms and machine learning for heavy-ion synchrotrons. </a> <em>International Journal of Modern Physics A. </em></a> Dec. 2019.
* O. M. Elzeki, M. F. Alrahmawy, Samir Elmougy. <a href="http://www.mecs-press.org/ijisa/ijisa-v11-n12/IJISA-V11-N12-3.pdf">A New Hybrid Genetic and Information Gain Algorithm for Imputing Missing Values in Cancer Genes Datasets. </a> <em>PInternational Journal of Intelligent Systems and Applications (IJISA), Vol.11, No.12, pp.20-33, DOI: 10.5815/ijisa.2019.12.03. </em></a> Dec. 2019.
* Oliver Strauß, Ahmad Almheidat and Holger Kett. <a href="https://pdfs.semanticscholar.org/0a91/c4e03a2acd8c295af398167edf7350ad0662.pdf">Applying Heuristic and Machine Learning Strategies to ProductResolution. </a> <em>Proceedings of the 15th International Conference on Web Information Systems and Technologies (WEBIST 2019), pages 242-249. </em></a> Nov. 2019.
* Yuanyuan Li, Stefano Carabelli, Edoardo Fadda, Daniele Manerba, Roberto Tadei1 and Olivier Terzo. <a href="http://www.orgroup.polito.it/material/DAUIN-ORO-2019-06.pdf">Integration of Machine Learning and OptimizationTechniques for Flexible Job-Shop Rescheduling inIndustry 4.0. </a> <em>Politecnico di Torino, Operations Research and Optimization Group. </em></a> Oct. 2019.
* Höttger R., Igel B., Spinczyk O. <a href="https://link.springer.com/chapter/10.1007/978-3-030-30275-7_44">Constrained Software Distribution for Automotive Systems. </a> <em>Communications in Computer and Information Science, vol 1078. </em></a> Oct. 2019.
* Jin-wooLee, Gwangseon Jang, Hohyun Jung, Jae-Gil Lee, Uichin Lee. <a href="https://doi.org/10.1016/j.pmcj.2019.101082">Maximizing MapReduce job speed and reliability in the mobile cloud by optimizing task allocation. </a> <em>Pervasive and Mobile Computing. </em></a> Oct. 2019.
* Krawczyk, Lukas, Mahmoud Bazzal, Ram Prasath Govindarajan and Carsten Wolff. <a href="https://ieeexplore.ieee.org/document/8904877">Model-Based Timing Analysis and Deployment Optimization for Heterogeneous Multi-core Systems using Eclipse APP4MC. </a> <em>2019 ACM/IEEE 22nd International Conference on Model Driven Engineering Languages and Systems Companion: 44-53. </em></a> Sep. 2019.
* Junio Cezar Ribeiro da Silva, Lorena Leão, Vinicius Petrucci, Abdoulaye Gamatié, Fernando MagnoQuintao Pereira. <a href="https://hal-lirmm.ccsd.cnrs.fr/lirmm-02281112/document">Scheduling in Heterogeneous Architectures via Multivariate Linear Regression on Function Inputs. </a> <em>lirmm-02281112. </em></a> Sep. 2019.
* Francisco G. Montoya and Raúl Baños Navarro (Eds.). <a href="https://www.mdpi.com/books/pdfview/book/1450">Optimization Methods Applied to Power Systems, Volume 2. </a> <em>MDPI Books, ISBN 978-3-03921-156-2. </em></a> July 2019.
* Höttger, Robert & Ki, Junhyung & Bui, Bao & Igel, Burkhard & Spinczyk, Olaf. <a href="https://www.researchgate.net/publication/335137686_CPU-GPU_Response_Time_and_Mapping_Analysis_for_High-Performance_Automotive_Systems">CPU-GPU Response Time and Mapping Analysis for High-Performance Automotive Systems. </a> <em>10th International Workshop on Analysis Tools and Methodologies for Embedded and Real-time Systems (WATERS) co-located with the 31st Euromicro Conference on Real-Time Systems (ECRTS'19). </em></a> July 2019.
* Maxime Cordy, Steve Muller, Mike Papadakis, and Yves Le Traon. <a href="http://delivery.acm.org/10.1145/3340000/3330580/issta19main-p399-p.pdf?ip=84.114.111.7&id=3330580&acc=OPEN&key=4D4702B0C3E38B35%2E4D4702B0C3E38B35%2E4D4702B0C3E38B35%2E6D218144511F3437&__acm__=1563299816_46b771752984b933c8c119b7f7d81805">Search-based test and improvement of machine-learning-based anomaly detection systems. </a> <em>Proceedings of the 28th ACM SIGSOFT International Symposium on Software Testing and Analysis (ISSTA 2019). ACM, New York, NY, USA, 158-168. </em></a> July 2019.
* Nikolaos Nikolakis, Ioannis Stathakis, Sotirios Makris. <a href="https://doi.org/10.1016/j.procir.2019.03.153">On an evolutionary information system for personalized support to plant operators. </a> <em>52nd CIRP Conference on Manufacturing Systems (CMS), Ljubljana, Slovenia. </em></a> June 2019.
* Michael Trotter, Timothy Wood and Jinho Hwang. <a href="http://faculty.cs.gwu.edu/timwood/papers/19-ICAC-storm.pdf">Forecasting a Storm: Divining Optimal Configurations using Genetic Algorithms and Supervised Learning. </a> <em>13th IEEE International Conference on Self-Adaptive and Self-Organizing Systems (SASO 2019). </em></a> June 2019.
* Krawczyk, Lukas & Bazzal, Mahmoud & Prasath Govindarajan, Ram & Wolff, Carsten. <a href="https://www.researchgate.net/publication/334084554_An_analytical_approach_for_calculating_end-to-end_response_times_in_autonomous_driving_applications">An analytical approach for calculating end-to-end response times in autonomous driving applications. </a> <em>10th International Workshop on Analysis Tools and Methodologies for Embedded and Real-time Systems (WATERS 2019). </em></a> June 2019.
* Rodolfo Ayala Lopes, Thiago Macedo Gomes, and Alan Robert Resende de Freitas. <a href="http://delivery.acm.org/10.1145/3330000/3326828/p1366-lopes.pdf?ip=84.114.111.7&id=3326828&acc=OPEN&key=4D4702B0C3E38B35%2E4D4702B0C3E38B35%2E4D4702B0C3E38B35%2E6D218144511F3437&__acm__=1563021092_5e8cda0c5ddddb14d4f5e9e3bd610a44">A symbolic evolutionary algorithm software platform. </a> <em>Proceedings of the Genetic and Evolutionary Computation Conference Companion (GECCO '19). </em></a> July 2019.
* Aleksandar Prokopec, Andrea Rosà, David Leopoldseder, Gilles Duboscq, Petr Tůma, Martin Studener, Lubomír Bulej, Yudi Zheng, Alex Villazón, Doug Simon, Thomas Würthinger, Walter Binder. <a href="https://renaissance.dev/resources/docs/renaissance-suite.pdf">Renaissance: Benchmarking Suite for Parallel Applications on the JVM. </a> <em>PLDI ’19, Phoenix, AZ, USA. </em></a> June 2019.
* Robert Höttger, Lukas Krawczyk, Burkhard Igel, Olaf Spinczyk. <a href="http://2019.rtas.org/wp-content/uploads/2019/04/RTAS19_BP_proceedings.pdf#page=23">Memory Mapping Analysis for Automotive Systems. </a> <em>Brief Presentations Proceedings (RTAS 2019). </em></a> Apr. 2019.
* Al Akkad, M. A., & Gazimzyanov, F. F. <a href="http://izdat.istu.ru/index.php/ISM/article/view/4317">AUTOMATED SYSTEM FOR EVALUATING 2D-IMAGE COMPOSITIONAL CHARACTERISTICS: CONFIGURING THE MATHEMATICAL MODEL.</a> <em>Intellekt. Sist. Proizv., 17(1), 26-33. doi: 10.22213/2410-9304-2019-1-26-33. </em></a> Apr. 2019.
* Alcayde, A.; Baños, R.; Arrabal-Campos, F.M.; Montoya, F.G. <a href="https://www.mdpi.com/1996-1073/12/7/1270">Optimization of the Contracted Electric Power by Means of Genetic Algorithms.</a> <em>Energies, Volume 12, Issue 7, </em></a> Apr. 2019.
* Abdul Sahli Fakharudin, Norazwina Zainol, Zulsyazwan Ahmad Khushairi. <a href="https://dl.acm.org/doi/10.1145/3323716.3323737">Modelling and Optimisation of Oil Palm Trunk Core Biodelignification using Neural Network and Genetic Algorithm.</a> <em>IEEA '19: Proceedings of the 8th International Conference on Informatics, Environment, Energy and Applications; Pages 155–158, </em></a> Mar. 2019.
* Aleksandar Prokopec, Andrea Rosà, David Leopoldseder, Gilles Duboscq, Petr Tůma, Martin Studener, Lubomír Bulej, Yudi Zheng, Alex Villazón, Doug Simon, Thomas Wuerthinger, Walter Binder. <a href="https://arxiv.org/pdf/1903.10267.pdf">On Evaluating the Renaissance Benchmarking Suite: Variety, Performance, and Complexity.</a> <em>Cornell University: Programming Languages, </em></a> Mar. 2019.
* S. Appel, W. Geithner, S. Reimann, M Sapinski, R. Singh, D. M. Vilsmeier <a href="https://www.researchgate.net/profile/Sabrina_Appel/publication/330934110_OPTIMIZATION_OF_HEAVY-ION_SYNCHROTRONS_USING_NATURE-INSPIRED_ALGORITHMS_AND_MACHINE_LEARNING/links/5c5c425b299bf1d14cb33546/OPTIMIZATION-OF-HEAVY-ION-SYNCHROTRONS-USING-NATURE-INSPIRED-ALGORITHMS-AND-MACHINE-LEARNING.pdf">OPTIMIZATION OF HEAVY-ION SYNCHROTRONS USINGNATURE-INSPIRED ALGORITHMS AND MACHINE LEARNING.</a><em><a href="https://bt.pa.msu.edu/ICAP18/index.html">13th Int. Computational Accelerator Physics Conf.</a>, </em></a> Feb. 2019.
* Saad, Christian, Bernhard Bauer, Ulrich R Mansmann, and Jian Li. <a href="https://journals.sagepub.com/doi/10.1177/1177932218818458">AutoAnalyze in Systems Biology.</a> <em>Bioinformatics and Biology Insights, </em></a> Jan. 2019.
* Gandeva Bayu Satrya, Soo Young Shin. <a href="https://arxiv.org/pdf/1812.01201.pdf">Evolutionary Computing Approach to Optimize Superframe Scheduling on Industrial Wireless Sensor Networks.</a> <em>Cornell University, </em></a> Dec. 2018.
* H.R. Maier, S. Razavi, Z. Kapelan, L.S. Matott, J. Kasprzyk, B.A. Tolson. <a href="https://www.sciencedirect.com/science/article/pii/S1364815218305905">Introductory overview: Optimization using evolutionary algorithms and other metaheuristics.</a> <em>Environmental Modelling & Software, </em></a> Dec. 2018.
* Erich C. Teppan and Giacomo Da Col. <a href="http://ceur-ws.org/Vol-2252/paper4.pdf">Automatic Generation of Dispatching Rules for Large Job Shops by Means of Genetic Algorithms.</a> <em>CIMA 2018, International Workshop on Combinations of Intelligent Methods and Applications, </em></a> Nov. 2018.
* Pasquale Salzaa, Filomena Ferrucci. <a href="https://www.sciencedirect.com/science/article/pii/S0167739X17324147">Speed up genetic algorithms in the cloud using software containers.</a> <em>Future Generation Computer Systems, </em></a> Oct. 2018.
* Ghulam Mubashar Hassan and Mark Reynolds. <a href="https://easychair.org/publications/open/GRLP">Genetic Algorithms for Scheduling and Optimization of Ore Train Networks.</a> <em>GCAI-2018. 4th Global Conference on Artificial Intelligence, </em></a> Sep. 2018.
* Drezewski, Rafal & Kruk, Sylwia & Makowka, Maciej. [The Evolutionary Optimization of a Company’s Return on Equity Factor: Towards the Agent-Based Bio-Inspired System Supporting Corporate Finance Decisions.](https://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=8466578) <em>IEEE Access. 6. 10.1109/ACCESS.2018.2870201, </em></a> Sep. 2018.
* Arifin, H. H., Chimplee, N. , Kit Robert Ong, H. , Daengdej, J. and Sortrakul, T. <a href="https://onlinelibrary.wiley.com/doi/abs/10.1002/j.2334-5837.2018.00549.x">Automated Component‐Selection of Design Synthesis for Physical Architecture with Model‐Based Systems Engineering using Evolutionary Trade‐off.</a> <em><a href="https://onlinelibrary.wiley.com/doi/abs/10.1002/j.2334-5837.2018.00549.x">INCOSE International Symposium, 28: 1296-1310</a>, </em></a> Aug. 2018.
* Ong, Robert & Sortrakul, Thotsapon. <a href="https://www.researchgate.net/publication/327096423_Comparison_of_Selection_Methods_of_Genetic_Algorithms_for_Automated_Component-Selection_of_Design_Synthesis_with_Model-Based_Systems_Engineering">Comparison of Selection Methods of Genetic Algorithms for Automated Component-Selection of Design Synthesis with Model-Based Systems Engineering.</a> <em>Conference: I-SEEC 2018, </em> May 2018.
* Stephan Pirnbaum. [Die Evolution im Algorithmus - Teil 2: Multikriterielle Optimierung und Architekturerkennung.](http://www.buschmais.de/wp-content/uploads/2018/06/Die-Evolution-im-Algorithmus_Teil2_JS_03_18.pdf) <a href="https://www.sigs-datacom.de/digital/javaspektrum/"><em>JavaSPEKTRUM 03/2018, pp 66–69, </em></a> May 2018.
* W. Geithner, Z. Andelkovic, S. Appel, O. Geithner, F. Herfurth, S. Reimann, G. Vorobjev, F. Wilhelmstötter. [Genetic Algorithms for Machine Optimization in the Fair Control System Environment.](http://accelconf.web.cern.ch/AccelConf/ipac2018/papers/thpml028.pdf)<em> [The 9th International Particle Accelerator Conference (IPAC'18)](https://ipac18.org/welcome/), </em></a> May 2018.
* Stephan Pirnbaum. [Die Evolution im Algorithmus - Teil 1: Grundlagen.](http://www.buschmais.de/wp-content/uploads/2018/02/Die-Evolution-im-Algorithmus_JS_01_18.pdf) <a href="https://www.sigs-datacom.de/digital/javaspektrum/"><em>JavaSPEKTRUM 01/2018, pp 64–68, </em></a> Jan. 2018.
* Alexander Felfernig, Rouven Walter, José A. Galindo, David Benavides, Seda Polat Erdeniz, Müslüm Atas, Stefan Reiterer. <a href="https://link.springer.com/article/10.1007/s10844-017-0492-1">Anytime diagnosis for reconfiguration. </a> <em>Journal of Intelligent Information Systems, pp 1–22, </em> Jan. 2018.
* Bruce A. Johnson. <a href="https://link.springer.com/protocol/10.1007/978-1-4939-7386-6_13">From Raw Data to Protein Backbone Chemical Shifts Using NMRFx Processing and NMRViewJ Analysis. </a> <em>Protein NMR: Methods and Protocols, pp. 257--310, Springer New York, </em> Nov. 2017.
* Cuadra P., Krawczyk L., Höttger R., Heisig P., Wolff C. <a href="https://link.springer.com/chapter/10.1007/978-3-319-67642-5_30">Automated Scheduling for Tightly-Coupled Embedded Multi-core Systems Using Hybrid Genetic Algorithms. </a> <em>Information and Software Technologies: 23rd International Conference, ICIST 2017, Druskininkai, Lithuania.</em> Communications in Computer and Information Science, vol 756. Springer, Cham, Sep. 2017.
* Michael Trotter, Guyue Liu, Timothy Wood. <a href="http://ieeexplore.ieee.org/abstract/document/8064120/">Into the Storm: Descrying Optimal Configurations Using Genetic Algorithms and Bayesian Optimization. </a> <em><a href="http://ieeexplore.ieee.org/xpl/mostRecentIssue.jsp?punumber=8063634">Foundations and Applications of Self* Systems (FAS*W), 2017 IEEE 2nd International Workshops</a></em> Sep. 2017.
* Emna Hachicha, Karn Yongsiriwit, Mohamed Sellami. <a href="https://doi.org/10.1109/ICWS.2017.101">Genetic-Based Configurable Cloud Resource Allocation in QoS-Aware Business Process Development. </a> <em>Information and Software Technologies: 23rd International Conference, ICIST 2017, Druskininkai, Lithuania.</em> Web Services (ICWS), 2017 IEEE International Conference, Jun. 2017.
* Abraão G. Nazário, Fábio R. A. Silva, Raimundo Teive, Leonardo Villa, Antônio Flávio, João Zico, Eire Fragoso, Ederson F. Souza. <a href="http://siaiap32.univali.br/seer/index.php/acotb/article/view/10579/5933">Automação Domótica Simulada Utilizando Algoritmo Genético Especializado na Redução do Consumo de Energia. </a> <em>Computer on the Beach 2017</em> pp. 180-189, March 2017.
* Bandaru, S. and Deb, K. <a href="http://dx.doi.org/10.1201/9781315183176-12">Metaheuristic Techniques.</a> <em>Decision Sciences.</em> CRC Press, pp. 693-750, Nov. 2016.
* Lyazid Toumi, Abdelouahab Moussaoui, and Ahmet Ugur. <a href="http://dx.doi.org/10.1145/2816839.2816876">EMeD-Part: An Efficient Methodology for Horizontal Partitioning in Data Warehouses.</a> <em>Proceedings of the International Conference on Intelligent Information Processing, Security and Advanced Communication.</em> Djallel Eddine Boubiche, Faouzi Hidoussi, and Homero Toral Cruz (Eds.). ACM, New York, NY, USA, Article 43, 7 pages, 2015.
* Andreas Holzinger (Editor), Igo Jurisica (Editor). <a href="http://www.springer.com/computer/database+management+%26+information+retrieval/book/978-3-662-43967-8">Interactive Knowledge Discovery and Data Mining in Biomedical Informatics.</a> <em>Lecture Notes in Computer Science, Vol. 8401.</em> <a href="http://www.springer.com">Springer</a>, 2014.
* Lyazid Toumi, Abdelouahab Moussaoui, Ahmet Ugur. <a href="http://link.springer.com/article/10.1007%2Fs11227-013-1058-9">Particle swarm optimization for bitmap join indexes selection problem in data warehouses.</a> <em><a href="http://link.springer.com/journal/11227">The Journal of Supercomputing</a>, Volume 68, <a href="http://link.springer.com/journal/11227/68/2/page/1">Issue 2</a>, pp 672-708, May 2014.</em>
* TANG Yi (Guangzhou Power Supply Bureau Limited, Guangzhou 511400, China) <a href="http://en.cnki.com.cn/Article_en/CJFDTOTAL-JXKF201210017.htm"> <em>Study on Object-Oriented Reactive Compensation Allocation Optimization Algorithm for Distribution Networks</em></a>, Oct. 2012.
* John M. Linebarger, Richard J. Detry, Robert J. Glass, Walter E. Beyeler, Arlo L. Ames, Patrick D. Finley, S. Louise Maffitt. <a href="http://prod.sandia.gov/techlib/access-control.cgi/2012/121117.pdf"> <em>Complex Adaptive Systems of Systems Engineering Environment Version 1.0.  </em></a> <a href="http://www.sandia.gov/CasosEngineering/">SAND REPORT</a>, Feb. 2012.


## Release notes

### [5.2.0](https://github.com/jenetics/jenetics/releases/tag/v5.2.0)

#### Improvements

* [#542](https://github.com/jenetics/jenetics/issues/542): Introduce `InvertibleCodec` interface. This interface extends the the current `Codec` interface.
```java
public interface InvertibleCodec<T, G extends Gene<?, G>> extends Codec<T, G> {
    public Function<T, Genotype<G>> encoder();
    public default Genotype<G> encode(final T value) {
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



_[All Release Notes](RELEASE_NOTES.md)_

## License

The library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

	Copyright 2007-2020 Franz Wilhelmstötter

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.


## Used software

<a href="https://www.jetbrains.com/idea/">![IntelliJ](http://jenetics.io/img/icon_IntelliJIDEA.png)</a>

