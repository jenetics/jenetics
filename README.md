# Jenetics

![Build Status](https://github.com/jenetics/jenetics/actions/workflows/gradle.yml/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.jenetics/jenetics/badge.svg)](http://search.maven.org/#search|ga|1|a%3A%22jenetics%22)
[![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics)

**Jenetics** is a **Genetic Algorithm**, **Evolutionary Algorithm**, **Grammatical Evolution**, **Genetic Programming**, and **Multi-objective Optimization** library, written in modern day Java. It is designed with a clear separation of the several concepts of the algorithm, e.g. `Gene`, `Chromosome`, `Genotype`, `Phenotype`, `Population` and fitness `Function`. **Jenetics** allows you to minimize and maximize the given fitness function without tweaking it. In contrast to other GA implementations, the library uses the concept of an evolution stream (`EvolutionStream`) for executing the evolution steps. Since the `EvolutionStream` implements the Java Stream interface, it works smoothly with the rest of the Java Stream API.

**Other languages**

* [**Jenetics.Net**](https://github.com/rmeindl/jenetics.net): Experimental .NET Core port in C# of the base library. 
* [**Helisa**](https://github.com/softwaremill/helisa/): Scala wrapper around the Jenetics library.

## Documentation

The library is fully documented ([javadoc](https://jenetics.io/javadoc/combined/8.0/index.html)) and comes with a user manual ([pdf](http://jenetics.io/manual/manual-8.0.0.pdf)).

## Build Jenetics

**Jenetics** requires at least **Java 21** to compile and run.

Check out the master branch from GitHub.

    $ git clone https://github.com/jenetics/jenetics.git <builddir>

Jenetics uses [Gradle](http://www.gradle.org/downloads) as build system and organizes the source into *sub*-projects (modules). Each subproject is located in its own subdirectory:

**Published projects**

The following projects/modules are also published to Maven.

* **[jenetics](jenetics)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics): This project contains the source code and tests for the Jenetics core-module.
* **[jenetics.ext](jenetics.ext)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics.ext): This module contains additional _non_-standard GA operations and data types. It also contains classes for solving multi-objective problems (MOEA) and doing Grammatical Evolution (GE). 
* **[jenetics.prog](jenetics.prog)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics.prog): The modules contains classes which allows to do genetic programming (GP). It seamlessly works with the existing `EvolutionStream` and evolution `Engine`.
* **[jenetics.xml](jenetics.xml)** [![Javadoc](https://www.javadoc.io/badge/io.jenetics/jenetics.svg)](http://www.javadoc.io/doc/io.jenetics/jenetics.xml): XML marshalling module for the _Jenetics_ base data structures.

**Non-published projects**

* **jenetics.example**: This project contains example code for the *core*-module.
* **jenetics.doc**: Contains the code of the web-site and the manual.
* **jenetics.tool**: This module contains classes used for doing integration testing and algorithmic performance testing. It is also used for creating GA performance measures and creating diagrams from the performance measures.

For building the library change into the `<builddir>` directory (or one of the module directories) and call one of the available tasks:

* **compileJava**: Compiles the Jenetics sources and copies the class files to the `<builddir>/<module-dir>/build/classes/main` directory.
* **jar**: Compiles the sources and creates the JAR files. The artifacts are copied to the `<builddir>/<module-dir>/build/libs` directory.
* **javadoc**: Generates the API documentation. The Javadoc is stored in the `<builddir>/<module-dir>/build/docs` directory
* **test**: Compiles and executes the unit tests. The test results are printed onto the console, and a test-report, created by TestNG, is written to `<builddir>/<module-dir>` directory.
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

In contrast to other GA implementations, the library uses the concept of an evolution stream (`EvolutionStream`) for executing the evolution steps. Since the `EvolutionStream` implements the Java Stream interface, it works smoothly with the rest of the Java streaming API. Now let's have a closer look at the listing above and discuss this simple program step by step:

1. The probably most challenging part, when setting up a new evolution `Engine`, is to transform the problem domain into a appropriate `Genotype` (factory) representation. In our example we want to count the number of ones of a `BitChromosome`. Since we are counting only the ones of one chromosome, we are adding only one `BitChromosome` to our `Genotype`. In general, the `Genotype` can be created with 1 to n chromosomes.

2. Once this is done, the fitness function, which should be maximized, can be defined. Utilizing the new language features introduced in Java 8, we simply write a private static method, which takes the genotype we defined and calculates its fitness value. If we want to use the optimized bit-counting method, `bitCount()`, we have to cast the `Chromosome<BitGene>` class to the actual used `BitChromosome` class. Since we know for sure that we created the Genotype with a `BitChromosome`, this can be done safely. A reference to the eval method is then used as fitness function and passed to the `Engine.build` method.

3. In the third step we are creating the evolution `Engine`, which is responsible for changing, respectively evolving, a given population. The `Engine` is highly configurable and takes parameters for controlling the evolutionary and the computational environment. For changing the evolutionary behavior, you can set different alterers and selectors. By changing the used `Executor` service, you control the number of threads; the Engine is allowed to use. A new `Engine` instance can only be created via its builder, which is created by calling the `Engine.builder` method.

4. In the last step, we can create a new `EvolutionStream` from our `Engine`. The `EvolutionStream` is the model or view of the evolutionary process. It serves as a »process handle« and also allows you, among other things, to control the termination of the evolution. In our example, we simply truncate the stream after 100 generations. If you don't limit the stream, the `EvolutionStream` will not terminate and run forever. Since the `EvolutionStream` extends the `java.util.stream.Stream` interface, it integrates smoothly with the rest of the Java Stream API. The final result, the best `Genotype` in our example, is then collected with one of the predefined collectors of the `EvolutionResult` class.


### Evolving images

This example tries to approximate a given image by semitransparent polygons.  It comes with a Swing UI, where you can immediately start your own experiments. After compiling the sources with

    $ ./gradlew compileTestJava

you can start the example by calling

    $ ./jrun io.jenetics.example.image.EvolvingImages

![Evolving images](https://raw.githubusercontent.com/jenetics/jenetics/master/jenetics.doc/src/main/resources/graphic/EvolvingImagesExampleScreenShot.png)

The previous image shows the GUI after evolving the default image for about 4,000 generations. With the »Open« button, it is possible to load other images for polygonization. The »Save« button allows storing polygonized images in PNG format to disk. At the button of the UI, you can change some GA parameters of the example.


## Projects using Jenetics

* <a href="https://spear-project.eu/"><b>SPEAR</b>:</a> SPEAR (Smart Prognosis of Energy with Allocation of Resources) created an extendable platform for energy and efficiency optimizations of production systems.
* <a href="https://renaissance.dev/"><b>Renaissance Suite</b>:</a> Renaissance is a modern, open, and diversified benchmark suite for the JVM, aimed at testing JIT compilers, garbage collectors, profilers, analyzers and other tools.
* <a href="http://www.eclipse.org/app4mc/"><b>APP4MC</b>:</a> Eclipse APP4MC is a platform for engineering embedded multi- and many-core software systems.

## Blogs and articles

* <a href="https://dieschwalbe.de/schwalbeaktuell.htm">Schachprobleme komponieren mit evolutionären Algorithmen</a>, by <em>Jakob Leck</em>, Dec 2023, Die Schwalbe 324-2, pp. 373-380. Composition and solving chess problems with a greater number of peaces than usual. Instead of a brute force approach, a GA is used solving the problems (German).
* <a href="https://craftcodecrew.com/solving-the-knapsack-problem-with-the-jenetics-library/">Solving the Knapsack Problem with the Jenetics Library</a>, by <em>Craftcode Crew</em>, May 13, 2021
* <a href="http://www.fx361.com/page/2018/1126/4534731.shtml">一种基于Jenetics的遗传算法程序设计</a>, <em>电脑知识与技术 2018年22期 by 王康</em>, Nov. 26. 2018
* <a href="http://www.baeldung.com/jenetics">Introduction to Jenetics Library</a>, by <em>baeldung</em>, April 11. 2017
* <a href="http://blog.takipi.com/how-to-solve-tough-problems-using-genetic-algorithms/">How to Solve Tough Problems Using Genetic Algorithms</a>, by <em>Tzofia Shiftan</em>, April 6. 2017
* <a href="http://fxapps.blogspot.co.at/2017/01/genetic-algorithms-with-java.html">Genetic algorithms with Java</a>, by <em>William Antônio</em>, January 10. 2017
* <a href="http://jdm.kr/blog/135">Jenetics 설치 및 예제</a>, by <em>JDM</em>, May 8. 2015
* <a href="http://jdm.kr/blog/104">유전 알고리즘 (Genetic Algorithms)</a>, by <em>JDM</em>, April 2. 2015

## Citations

<details>
<summary>
Bernhard J. Berger; Christina Plump; Lauren Paul; Rolf Drechsler. <a href="https://agra.informatik.uni-bremen.de/doc/konf/gecco2024_cp.pdf"> EvoAl — Codeless Domain-Optimisation.</a> <em> Genetic and Evolutionary Computation Conference (GECCO-2024).</em> July 2024.

...
</summary>

1) Bernhard J. Berger; Christina Plump; Lauren Paul; Rolf Drechsler. <a href="https://agra.informatik.uni-bremen.de/doc/konf/gecco2024_cp.pdf"> EvoAl — Codeless Domain-Optimisation.</a> <em> Genetic and Evolutionary Computation Conference (GECCO-2024).</em> July 2024.
2) Christina Plump, Daniel C. Hoinkiss, Jörn Huber, Bernhard J. Berger, Matthias Günther, Christoph Lüth, Rolf Drechsler. <a href="https://agra.informatik.uni-bremen.de/doc/konf/cec2024_cp.pdf"> Finding the perfect MRI sequence for your patient --- Towards an optimisation workflow for MRI-sequences.</a> <em>  IEEE WCCI 2024.</em> June 2024.
3) Milan Čugurović, Milena Vujošević Janičić, Vojin Jovanović, Thomas Würthinger. <a href="https://doi.org/10.1016/j.jss.2024.112058"> GraalSP: Polyglot, efficient, and robust machine learning-based static profiler.</a> <em>  Journal of Systems and Software, Volume 213, 2024, 112058, ISSN 0164-1212.</em> July. 2024.
4) Wenwen Feng, Xiaohui Lei, Yunzhong Jiang, Chao Wang, Weihong Liao, Hao Wang, Gong Xinghui, Yu Feng. <a href="https://doi.org/10.1016/j.jhydrol.2024.131228"> Coupling model predictive control and rules-based control for real-time control of urban river systems.</a> <em>  Journal of Hydrology, 2024, 131228, ISSN 0022-1694.</em> April 2024.
5) S. Sint, A. Mazak-Huemer, M. Eisenberg, D. Waghubinger and M. Wimmer. <a href="https://ieeexplore.ieee.org/document/10499231"> Automatic Optimization of Tolerance Ranges for Model-Driven Runtime State Identification.</a> <em>   IEEE Transactions on Automation Science and Engineering.</em> April. 2024.
6) Cicirello, Vincent A. <a href="https://doi.org/10.3390/app14062542"> Evolutionary Computation: Theories, Techniques, and Applications.</a> <em>   Applied Sciences 14, no. 6: 2542.</em> Mar. 2024.
7) Koitz-Hristov R, Sterner T, Stracke L, Wotawa F. <a href="https://doi.org/10.1002/smr.2656"> On the suitability of checked coverage and genetic parameter tuning in test suite reduction.</a> <em>   J Softw Evol Proc. 2024;e2656.</em> Feb. 2024.
8) Jordão, Rodolfo; Becker, Matthias; Sander, Ingo. <a href="https://doi.org/10.1145/3647640">IDeSyDe: Systematic Design Space Exploration via Design Space Identification.</a> <em>  ACM Transactions on Design Automation of Electronic Systems.</em> Feb. 2024.
9) Squillero, G., Tonda, A. <a href="https://link.springer.com/article/10.1007/s10710-023-09472-0">Veni, Vidi, Evolvi commentary on W. B. Langdon’s “Jaws 30”.</a> <em>  Genet Program Evolvable Mach 24, 24 (2023)</em> Nov. 2023.
10) Eneko Osaba, Gorka Benguria, Jesus L. Lobo, Josu Diaz-de-Arcaya, Juncal Alonso, Iñaki Etxaniz. <a href="https://doi.org/10.48550/arXiv.2311.10767">Optimizing IaC Configurations: a Case Study Using Nature-inspired Computing.</a> <em>  CIIS 2023. </em> Nov. 2023.
11) Sapra, D., Pimentel, A.D. <a href="https://doi.org/10.1007/978-3-031-46077-7_6">Exploring Multi-core Systems with Lifetime Reliability and Power Consumption Trade-offs.</a> <em>  Embedded Computer Systems: Architectures, Modeling, and Simulation. SAMOS 2023. Lecture Notes in Computer Science, vol 14385. Springer, Cham.</em> Nov. 2023.
12) Syed Juned Ali, Jan Michael Laranjo, Dominik Bork. <a href="https://model-engineering.info/publications/papers/EDOC23-GGMF-web.pdf">A Generic and Customizable Genetic Algorithms-based Conceptual Model Modularization Framework.</a> <em> 27th International EDOC Conference (EDOC 2023) - Enterprise Design, Operations and Computing.</em> Sep. 2023.
13) A. Elyasaf, E. Farchi, O. Margalit, G. Weiss and Y. Weiss. <a href="https://doi.ieeecomputersociety.org/10.1109/TSE.2023.3279570">Generalized Coverage Criteria for Combinatorial Sequence Testing.</a> <em> IEEE Transactions on Software Engineering, vol. 49, no. 08, pp. 4023-4034.</em> Aug. 2023.
14) Julien Amblard, Robert Filman, Gabriel Kopito. <a href="https://doi.org/10.1145/3583133.3596369">GPStar4: A flexible framework for experimenting with genetic programming.</a> <em> OGECCO '23 Companion: Proceedings of the Companion Conference on Genetic and Evolutionary Computation. </em> July 2023.
15) Garmendia, A., Bork, D., Eisenberg, M., Ferreira, T., Kessentini, M., Wimmer, M. <a href="https://doi.org/10.1007/978-981-19-9948-2_4">Leveraging Artificial Intelligence for Model-based Software Analysis and Design.</a> <em> Optimising the Software Development Process with Artificial Intelligence. Natural Computing Series. Springer, Singapore. </em> July 2023.
16) Sikora, M., Smołka, M. <a href="https://doi.org/10.1007/978-3-031-35995-8_16">An Application of Evolutionary Algorithms and Machine Learning in Four-Part Harmonization.</a> <em> Computational Science – ICCS 2023. ICCS 2023. Lecture Notes in Computer Science, vol 14073. Springer </em> June 2023.
17) Dolly Sapra and Andy D. Pimentel. <a href="http://admorph.eu/wp-content/uploads/2023/05/SAMOS_Dolly-2.pdf">Exploring Multi-core Systems with Lifetime Reliability and Power Consumption Trade-offs.</a> <em> SAMOS '23. </em> May 2023.
18) Vipin Shukla, Mainak Bandyopadhyay. <a href="https://doi.org/10.1016/j.iswa.2023.200200">Optimization of input parameters of ANN–driven plasma source through nature-inspired evolutionary algorithms.</a> <em> Intelligent Systems with Applications, Volume 18, 2023, 200200, ISSN 2667-3053. </em> May 2023.
19) P. Feichtenschlager, K. Schuetz, S. Jaburek, C. Schuetz, E. Gringinger. <a href="http://www.dke.jku.at/rest/dke_web_res/publications/papers/Schu23c/Schu23c_copy.pdf">Privacy-Preserving Implementation of an Auction Mechanism for ATFM Slot Swapping.</a> <em> Proceedings of the 23rd Integrated Communications, Navigation and Surveillance Conference (ICNS 2023), Washington D.C., U.S.A., April 18-20, 2023, IEEE Press, 12 pages. </em> April 2023.
20) Christoph Laaber, Tao Yue, Shaukat Ali. <a href="https://arxiv.org/abs/2211.13525">Multi-Objective Search-Based Software Microbenchmark Prioritization.</a> <em> ArXiv/Computer Science/Software Engineering. </em> Nov. 2022.
21) Ricardo Ferreira Vilela, João Choma Neto, Victor Hugo Santiago Costa Pinto, Paulo Sérgio Lopes de Souza, Simone do Rocio Senger de Souza. <a href="https://doi.org/10.1002/cpe.7489">Bio-inspired optimization to support the test data generation of concurrent software.</a> <em> Concurrency and Computation: Practice and Experience. </em> Nov. 2022.
22) G. Mateeva, D. Parvanov, I. Dimitrov, I. Iliev and T. Balabanov. <a href="https://doi.org/10.1109/ICAI55857.2022.9960128">An Efficiency of Third Party Genetic Algorithms Software Libraries in Mobile Distributed Computing for Financial Time Series Forecasting.</a> <em> 2022 International Conference Automatics and Informatics (ICAI). </em> Oct. 2022.
23) Guilherme Espada, Leon Ingelse, Paulo Canelas, Pedro Barbosa, Alcides Fonseca. <a href="https://arxiv.org/abs/2210.04826">Data types as a more ergonomic frontend for Grammar-Guided Genetic Programming.</a> <em> arXiv. </em> Oct. 2022.
24) Christoph G. Schuetz, Thomas Lorünser, Samuel Jaburek, Kevin Schuetz, Florian Wohner, Roman Karl & Eduard Gringinger. <a href="https://doi.org/10.1007/978-3-031-17834-4_10">A Distributed Architecture for Privacy-Preserving Optimization Using Genetic Algorithms and Multi-party Computation.</a> <em> CoopIS 2022: Cooperative Information Systems pp 168–185. </em> Sep. 2022.
25) Christina Plump, Bernhard J. Berger, Rolf Drechsler. <a href="https://www.informatik.uni-bremen.de/agra/doc/konf/cec2022_cp.pdf">Using density of training data to improve evolutionary algorithms with approximative fitness functions.</a> <em> WCCI2022 IEEE WORLD CONGRESS ON COMPUTATIONAL INTELLIGENCE. </em> July 2022.
26) Christina Plump, Bernhard J. Berger, Rolf Drechsler. <a href="https://doi.org/10.1145/3520304.3529066">Adapting mutation and recombination operators to range-aware relations in real-world application data.</a> <em> GECCO '22: Proceedings of the Genetic and Evolutionary Computation Conference Companion. Pages 755–758. </em> July 2022.
27) Eric Medvet, Giorgia Nadizar, Luca Manzoni. <a href="https://doi.org/10.1145/3520304.3533960">JGEA: a modular java framework for experimenting with evolutionary computation.</a> <em> GECCO '22: Proceedings of the Genetic and Evolutionary Computation Conference Companion. Pages 2009–2018. </em> July 2022.
28) Moshe Sipper, Tomer Halperin, Itai Tzruia, Achiya Elyasaf. <a href="https://arxiv.org/pdf/2207.10367.pdf">EC-KitY: Evolutionary Computation Tool Kit in Python with Seamless Machine Learning Integration.</a> <em>arXiv:2207.10367v1 [cs.NE]. </em> July 2022. 
29) A. Billedeaux and B. DeVries. <a href="https://doi.org/10.1109/eIT53891.2022.9813795">Using Metamorphic Relationships and Genetic Algorithms to Test Open-Source Software.</a> <em> 2022 IEEE International Conference on Electro Information Technology (eIT), 2022, pp. 342-345. </em> July 2022.
30) R. Koitz-Hristov, L. Stracke and F. Wotawa. <a href="https://ieeexplore.ieee.org/abstract/document/9796394">Checked Coverage for Test Suite Reduction – Is It Worth the Effort?</a> <em>  2022 IEEE/ACM International Conference on Automation of Software Test (AST), pp. 6-16. </em> June 2022.
31) Abdessamed Ouessai, Mohammed Salem, Antonio M. Mora. <a href="https://doi.org/10.1016/j.entcom.2022.100493">Evolving action pre-selection parameters for MCTS in real-time strategy games.</a> <em>  Entertainment Computing, Volume 42. </em> April 2022.
32) Musatafa Abbas Abbood Albadr, Sabrina Tiun, Masri Ayob, Fahad Taha AL-Dhief, Khairuddin Omar & Mhd Khaled Maen. <a href="https://doi.org/10.1007/s11042-022-12747-w">Speech emotion recognition using optimized genetic algorithm-extreme learning machine.</a> <em>  Multimedia Tools and Applications, </em> March 2022.
33) Christina Plump, Bernhard Berger, Rolf Drechsler. <a href="https://www.informatik.uni-bremen.de/agra/doc/konf/LDIC2022Plump.pdf">Choosing the right technique for the right restriction - a domain-specific approach for enforcing search-space restrictions in evolutionary algorithms.</a> <em>  LDIC-2022, International Conference on Dynamics in Logistics, </em> Feb. 2022.
34) Quoc Nhat Han Tran, Nhan Quy Nguyen, Hicham Chehade, Lionel Amodeo, Farouk Yalaoui. <a href="https://doi.org/10.3390/app12020659">Outpatient Appointment Optimization: A Case Study of a Chemotherapy Service.</a> <em>  Applied Sciences/Computing and Artificial Intelligence. </em> Jan. 2022.
35) Achiya Elyasaf, Eitan Farchi, Oded Margalit, Gera Weiss,  Yeshayahu Weiss. <a href="https://arxiv.org/pdf/2201.00522.pdf">Combinatorial Sequence Testing Using Behavioral Programming and Generalized Coverage Criteria.</a> <em>  Journal of Systems and Software. </em> Jan. 2022.
36) Frequentis Group. <a href="https://www.frequentis.com/sites/default/files/support/2021-12/D4.1%20Report%20on%20State-of-the-Art%20of%20Relevant%20Concepts.pdf">D4.1 Report on State-ofthe-Art of Relevant Concepts.</a> <em> <a href="https://www.frequentis.com/en/research/projects/slotmachine/results-and-deliverables">SLOTMACHINE - RESULTS & PUBLIC DELIVERABLES, Frequentis
    </a> </em> Dec. 2021.
37) Huang Wanjie, Wang Haotian, Xue Yibo. <a href="https://www.webofproceedings.org/proceedings_series/ESSP/ICITED%202021/Y0197.pdf">Research on Optimization of in-warehouse picking Model based on genetic algorithm.</a> <em> 2021 International Conference on Information Technology, Education and Development (ICITED 2021). </em> Dec. 2021.
38) Aalam Z., Kaur S., Vats P., Kaur A., Saxena R. <a href="https://link.springer.com/chapter/10.1007/978-981-16-6369-7_34">A Comprehensive Analysis of Testing Efforts Using the Avisar Testing Tool for Object Oriented Softwares.</a> <em> Intelligent Sustainable Systems. Lecture Notes in Networks and Systems, vol 334. Springer, Singapore. </em> Dec. 2021. 
39) Anh Vu Vo, Debra F. Laefer, Jonathan Byrne. <a href="https://doi.org/10.3390/rs13214437">Optimizing Urban LiDAR Flight Path Planning Using a Genetic Algorithm and a Dual Parallel Computing Framework.</a> <em>Remote Sensing, Volume 13, Issue 21. </em> Nov. 2021.
40) Pozas N., Durán F. <a href="https://link.springer.com/chapter/10.1007/978-3-030-91431-8_28">On the Scalability of Compositions of Service-Oriented Applications.</a> <em> ICSOC 2021: Service-Oriented Computing pp 449-463 </em> Nov. 2021.
41) Küster, T., Rayling, P., Wiersig, R. et al. <a href="https://doi.org/10.1007/s11081-021-09691-3">Multi-objective optimization of energy-efficient production schedules using genetic algorithms.</a> <em> Optimization and Engineering (2021). </em> Oct. 2021.
42) B. DeVries and C. Trefftz. <a href="https://doi.org/10.1109/SBST52555.2021.00008">A Novelty Search and Metamorphic Testing Approach to Automatic Test Generation.</a> <em>2021 IEEE/ACM 14th International Workshop on Search-Based Software Testing (SBST), 2021, pp. 8-11. </em> May 2021.
43) W. Geithner, Z. Andelkovic, O. Geithner, F. Herfurth, V. Rapp, A. Németh, F. Wilhelmstötter, A. H. Van Benschoten. <a href="https://accelconf.web.cern.ch/ipac2021/papers/tupab300.pdf">ION SOURCE OPTIMIZATION USING BI-OBJECTIVE GENETIC AND MATRIX-PROFILE ALGORITHM.</a> <em>IPAC2021 - 12th International Particle Accelerator Conference. </em> May 2021.
44) C. Plump, B. J. Berger and R. Drechsler. <a href="https://doi.org/10.1109/CEC45853.2021.9504931">Domain-driven Correlation-aware Recombination and Mutation Operators for Complex Real-world Applications.</a> <em> 2021 IEEE Congress on Evolutionary Computation (CEC), pp. 540-548. </em> July 2021.
45) Sapra, D., Pimentel, A.D. <a href="https://doi.org/10.1007/s10489-021-02679-7">Designing convolutional neural networks with constrained evolutionary piecemeal training.</a> <em> Appl Intell (2021). </em> July 2021.
46) Michela Lorandi, Leonardo Lucio Custode, Giovanni Iacca. <a href="https://doi.org/10.1145/3449726.3462716">Genetic improvement of routing in delay tolerant networks.</a> <em>GECCO '21: Proceedings of the Genetic and Evolutionary Computation Conference Companion. </em> July 2021, Pages 35–36.
47) Plump, Christina and Berger, Bernhard J. and Drechsler, Rolf. <a href="http://www.informatik.uni-bremen.de/agra/doc/konf/CEC_approximative.pdf">Improving evolutionary algorithms by enhancing an approximative fitness function through prediction intervals.</a> <em>IEEE Congress on Evolutionary Computation (IEEE CEC-2021). </em> June 2021.
48) Faltaous, Sarah, Abdulmaksoud, Aya, Kempe, Markus, Alt, Florian and Schneegass, Stefan. <a href="https://doi.org/10.1515/itit-2020-0035">GeniePutt: Augmenting human motor skills through electrical muscle stimulation.</a> <em>it - Information Technology, vol. , no. , 2021. </em> May 2021.
49) Yiming Tang, Raffi Khatchadourian, Mehdi Bagherzadeh, Rhia Singh, Ajani Stewart, and Anita Raja. <a href="https://academicworks.cuny.edu/hc_pubs/671/">An Empirical Study of Refactorings and Technical Debt in Machine Learning Systems.</a> <em>In International Conference on Software Engineering, ICSE ’21. </em> May 2021.
50) Arifin H.H., Robert Ong H.K., Dai J., Daphne W., Chimplee N. <a href="https://doi.org/10.1007/978-3-030-73539-5_23"> Model-Based Product Line Engineering with Genetic Algorithms for Automated Component Selection.</a> <em>In: Krob D., Li L., Yao J., Zhang H., Zhang X. (eds) Complex Systems Design & Management. Springer, Cham. </em> April 2021.
51) MICHELA LORANDI, LEONARDO LUCIO CUSTODE, and GIOVANNI IACCA. <a href="https://arxiv.org/pdf/2103.07428.pdf">Genetic Improvement of Routing Protocols for DelayTolerant Networks. </a> <em>arXiv:2103.07428v1 </em> March 2021.
52) Amine Aziz-Alaoui, Carola Doerr, Johann Dreo. <a href="https://arxiv.org/abs/2102.06435">Towards Large Scale Automated Algorithm Designby Integrating Modular Benchmarking Frameworks. </a> <em>E arXiv:2102.06435 </em> Feb. 2021.
53) Dominik Bork and Antonio Garmendia and Manuel Wimmer. <a href="https://model-engineering.info/publications/papers/ER2020_Forum_ModulER-CR.pdf">Towards a Multi-Objective Modularization Approach for Entity-Relationship Models.</a> <em>ER 2020, 39th International Conference on Conceptual Modeling. </em></a> Nov. 2020.
54) Sarfarazi, S.; Deissenroth-Uhrig, M.; Bertsch, V. <a href="https://doi.org/10.3390/en13195154 ">Aggregation of Households in Community Energy Systems: An Analysis from Actors’ and Market Perspectives. </a> <em>Energies 2020, 13, 5154. </em> Oct. 2020.  
55) M. Šipek, D. Muharemagić, B. Mihaljević and A. Radovan. <a href="https://ieeexplore.ieee.org/abstract/document/9245290">Enhancing Performance of Cloud-based Software Applications with GraalVM and Quarkus.</a> <em>2020 43rd International Convention on Information, Communication and Electronic Technology (MIPRO), Opatija, Croatia, 2020, pp. 1746-1751. </em> Oct. 2020.
56) Vats P., Mandot M. <a href="https://link.springer.com/chapter/10.1007/978-981-15-7106-0_60">A Comprehensive Analysis for Validation of AVISAR Object-Oriented Testing Tool.</a> <em>Joshi A., Khosravy M., Gupta N. (eds) Machine Learning for Predictive Analysis. Lecture Notes in Networks and Systems, vol 141. Springer, Singapore. </em></a> Oct. 2020.
57) Thakur, K., Kumar, G. <a href="https://doi.org/10.1007/s11831-020-09481-7">Nature Inspired Techniques and Applications in Intrusion Detection Systems: Recent Progress and Updated Perspective.</a> <em>Archives of Computational Methods in Engineering (2020). </em></a> Aug. 2020.
58) Nur Hidayah Mat Yasin, Abdul Sahli Fakhrudin, Abdul Wafie Afnan Abdul Hadi, Muhammad Harith Mohd Khairuddin, Noor Raihana Abu Sepian, Farhan Mohd Said, Norazwina Zainol. <a href="http://modern-journals.com/index.php/ijma/article/view/378">Comparison of Response Surface Methodology and Artificial Neural Network for the Solvent Extraction of Fatty Acid Methyl Ester from Fish Waste. </a> <em>International Journal of Modern Agriculture, Volume 9, No.3, 2020, ISSN: 2305-7246. </em> Sep. 2020.
59) Cicirello, V. A. <a href="https://doi.org/10.21105/joss.02448">Chips-n-Salsa: A Java Library of Customizable, Hybridizable, Iterative, Parallel, Stochastic, and Self-Adaptive Local Search Algorithms.</a> <em>Journal of Open Source Software, 5(52), 2448. </em></a> Aug. 2020.
60) Li, Yuanyuan; Carabelli, Stefano;Fadda, Edoardo; Manerba, Daniele; Tadei, Roberto; Terzo, Olivier. <a href="https://iris.polito.it/retrieve/handle/11583/2842141/388548/I40for_Advanced_Manufacturing_Technology.pdf">Machine Learning and Optimization for Production Rescheduling in Industry 4.0.</a> <em>THE INTERNATIONAL JOURNAL OF ADVANCED MANUFACTURING TECHNOLOGY. - ISSN 1433-3015. </em></a> Aug. 2020.
61) Dolly Sapra and Andy D. Pimentel. <a href="https://staff.fnwi.uva.nl/a.d.pimentel/artemis/GECCO2020.pdf">An Evolutionary Optimization Algorithm for GraduallySaturating Objective Functions.</a> <em>GECCO ’20, Cancún, Mexico. </em></a> July. 2020.
62) Dolly Sapra and Andy D. Pimentel. <a href="https://staff.fnwi.uva.nl/a.d.pimentel/artemis/EvoML2020.pdf">Constrained Evolutionary Piecemeal Training to Design Convolutional Neural Networks.</a> <em>IEA/AIE 2020 – Kitakyushu, Japan. </em></a> July. 2020.
63) Femi Emmanuel Ayo, Sakinat Oluwabukonla Folorunso, Adebayo A. Abayomi-Alli, Adebola Olayinka Adekunle, Joseph Bamidele   Awotunde. <a href="https://doi.org/10.1080/19393555.2020.1767240">Network intrusion detection based on deep learning model optimized with rule-based hybrid feature selection.</a> <em>Information Security Journal: A Global Perspective. </em></a> May 2020.
64) Zainol N., Fakharudin A.S., Zulaidi N.I.S. <a href="https://link.springer.com/chapter/10.1007%2F978-981-15-4821-5_11">Model Optimization Using Artificial Intelligence Algorithms for Biological Food Waste Degradation.</a> <em>Yaser A. (eds) Advances in Waste Processing Technology. Springer, Singapore. </em></a> May 2020.
65) Sonya Voneva, Manar Mazkatli, Johannes Grohmann and Anne Koziolek. <a href="https://sdqweb.ipd.kit.edu/publications/pdfs/voneva2020a.pdf">Optimizing Parametric Dependencies forIncremental Performance Model Extraction.</a> <em>Karlsruhe Institute of Technology, Karlsruhe, Germany. </em></a> April. 2020.
66) Raúl Lara-Cabrera, Ángel González-Prieto, Fernando Ortega and Jesús Bobadilla. <a href="https://www.mdpi.com/2076-3417/10/2/675">Evolving Matrix-Factorization-Based Collaborative Filtering Using Genetic Programming.</a> <em>MDPI, Applied Sciences. </em></a> Feb. 2020.
67) Humm B.G., Hutter M. <a href="https://link.springer.com/chapter/10.1007/978-3-030-41913-4_12">Learning Patterns for Complex Event Detection in Robot Sensor Data.</a> <em>Optimization and Learning. OLA 2020. Communications in Computer and Information Science, vol 1173. Springer </em></a> Feb. 2020.
68) Erich C. Teppan, Giacomo Da Col. <a href="https://link.springer.com/chapter/10.1007/978-981-15-1918-5_7">Genetic Algorithms for Creating Large Job Shop Dispatching Rules. </a> <em>Advances in Integrations of Intelligent Methods. Smart Innovation, Systems and Technologies, vol 170. Springer, Singapore. </em></a> Jan. 2020.
69) Ricardo Pérez-Castillo, Francisco Ruiz, Mario Piattini. <a href="https://www.sciencedirect.com/science/article/pii/S016792362030004X">A decision-making support system for Enterprise Architecture Modelling. </a> <em>Decision Support Systems. </em></a> Jan. 2020.
70) Sabrina Appel, Wolfgang Geithner, Stephan Reimann, Mariusz Sapinski, Rahul Singh and Dominik Vilsmeier. <a href="https://www.worldscientific.com/doi/abs/10.1142/S0217751X19420193">Application of nature-inspired optimization algorithms and machine learning for heavy-ion synchrotrons. </a> <em>International Journal of Modern Physics A. </em></a> Dec. 2019.
71) O. M. Elzeki, M. F. Alrahmawy, Samir Elmougy. <a href="http://www.mecs-press.org/ijisa/ijisa-v11-n12/IJISA-V11-N12-3.pdf">A New Hybrid Genetic and Information Gain Algorithm for Imputing Missing Values in Cancer Genes Datasets. </a> <em>PInternational Journal of Intelligent Systems and Applications (IJISA), Vol.11, No.12, pp.20-33, DOI: 10.5815/ijisa.2019.12.03. </em></a> Dec. 2019.
72) Oliver Strauß, Ahmad Almheidat and Holger Kett. <a href="https://pdfs.semanticscholar.org/0a91/c4e03a2acd8c295af398167edf7350ad0662.pdf">Applying Heuristic and Machine Learning Strategies to ProductResolution. </a> <em>Proceedings of the 15th International Conference on Web Information Systems and Technologies (WEBIST 2019), pages 242-249. </em></a> Nov. 2019.
73) Yuanyuan Li, Stefano Carabelli, Edoardo Fadda, Daniele Manerba, Roberto Tadei1 and Olivier Terzo. <a href="http://www.orgroup.polito.it/material/DAUIN-ORO-2019-06.pdf">Integration of Machine Learning and OptimizationTechniques for Flexible Job-Shop Rescheduling inIndustry 4.0. </a> <em>Politecnico di Torino, Operations Research and Optimization Group. </em></a> Oct. 2019.
74) Höttger R., Igel B., Spinczyk O. <a href="https://link.springer.com/chapter/10.1007/978-3-030-30275-7_44">Constrained Software Distribution for Automotive Systems. </a> <em>Communications in Computer and Information Science, vol 1078. </em></a> Oct. 2019.
75) Jin-wooLee, Gwangseon Jang, Hohyun Jung, Jae-Gil Lee, Uichin Lee. <a href="https://doi.org/10.1016/j.pmcj.2019.101082">Maximizing MapReduce job speed and reliability in the mobile cloud by optimizing task allocation. </a> <em>Pervasive and Mobile Computing. </em></a> Oct. 2019.
76) Krawczyk, Lukas, Mahmoud Bazzal, Ram Prasath Govindarajan and Carsten Wolff. <a href="https://ieeexplore.ieee.org/document/8904877">Model-Based Timing Analysis and Deployment Optimization for Heterogeneous Multi-core Systems using Eclipse APP4MC. </a> <em>2019 ACM/IEEE 22nd International Conference on Model Driven Engineering Languages and Systems Companion: 44-53. </em></a> Sep. 2019.
77) Junio Cezar Ribeiro da Silva, Lorena Leão, Vinicius Petrucci, Abdoulaye Gamatié, Fernando MagnoQuintao Pereira. <a href="https://hal-lirmm.ccsd.cnrs.fr/lirmm-02281112/document">Scheduling in Heterogeneous Architectures via Multivariate Linear Regression on Function Inputs. </a> <em>lirmm-02281112. </em></a> Sep. 2019.
78) Eric O. Scott, Sean Luke. <a href="https://dl.acm.org/doi/10.1145/3319619.3326865">ECJ at 20: toward a general metaheuristics toolkit. </a> <em>GECCO '19: Proceedings of the Genetic and Evolutionary Computation Conference Companion, Pages 1391–1398. </em></a> July 2019.
79) Francisco G. Montoya and Raúl Baños Navarro (Eds.). <a href="https://www.mdpi.com/books/pdfview/book/1450">Optimization Methods Applied to Power Systems, Volume 2. </a> <em>MDPI Books, ISBN 978-3-03921-156-2. </em></a> July 2019.
80) Höttger, Robert & Ki, Junhyung & Bui, Bao & Igel, Burkhard & Spinczyk, Olaf. <a href="https://www.researchgate.net/publication/335137686_CPU-GPU_Response_Time_and_Mapping_Analysis_for_High-Performance_Automotive_Systems">CPU-GPU Response Time and Mapping Analysis for High-Performance Automotive Systems. </a> <em>10th International Workshop on Analysis Tools and Methodologies for Embedded and Real-time Systems (WATERS) co-located with the 31st Euromicro Conference on Real-Time Systems (ECRTS'19). </em></a> July 2019.
81) Maxime Cordy, Steve Muller, Mike Papadakis, and Yves Le Traon. <a href="http://delivery.acm.org/10.1145/3340000/3330580/issta19main-p399-p.pdf?ip=84.114.111.7&id=3330580&acc=OPEN&key=4D4702B0C3E38B35%2E4D4702B0C3E38B35%2E4D4702B0C3E38B35%2E6D218144511F3437&__acm__=1563299816_46b771752984b933c8c119b7f7d81805">Search-based test and improvement of machine-learning-based anomaly detection systems. </a> <em>Proceedings of the 28th ACM SIGSOFT International Symposium on Software Testing and Analysis (ISSTA 2019). ACM, New York, NY, USA, 158-168. </em></a> July 2019.
82) Michael Vistein, Jan Faber, Clemens Schmidt-Eisenlohr, Daniel Reiter. <a href="https://doi.org/10.1016/j.promfg.2020.01.220">Automated Handling of Auxiliary Materials using a Multi-Kinematic Gripping System. </a> <em>Procedia Manufacturing Volume 38, 2019, Pages 1276-1283. </em></a> June 2019.
83) Nikolaos Nikolakis, Ioannis Stathakis, Sotirios Makris. <a href="https://doi.org/10.1016/j.procir.2019.03.153">On an evolutionary information system for personalized support to plant operators. </a> <em>52nd CIRP Conference on Manufacturing Systems (CMS), Ljubljana, Slovenia. </em></a> June 2019.
84) Michael Trotter, Timothy Wood and Jinho Hwang. <a href="http://faculty.cs.gwu.edu/timwood/papers/19-ICAC-storm.pdf">Forecasting a Storm: Divining Optimal Configurations using Genetic Algorithms and Supervised Learning. </a> <em>13th IEEE International Conference on Self-Adaptive and Self-Organizing Systems (SASO 2019). </em></a> June 2019.
85) Krawczyk, Lukas & Bazzal, Mahmoud & Prasath Govindarajan, Ram & Wolff, Carsten. <a href="https://www.researchgate.net/publication/334084554_An_analytical_approach_for_calculating_end-to-end_response_times_in_autonomous_driving_applications">An analytical approach for calculating end-to-end response times in autonomous driving applications. </a> <em>10th International Workshop on Analysis Tools and Methodologies for Embedded and Real-time Systems (WATERS 2019). </em></a> June 2019.
86) Rodolfo Ayala Lopes, Thiago Macedo Gomes, and Alan Robert Resende de Freitas. <a href="http://delivery.acm.org/10.1145/3330000/3326828/p1366-lopes.pdf?ip=84.114.111.7&id=3326828&acc=OPEN&key=4D4702B0C3E38B35%2E4D4702B0C3E38B35%2E4D4702B0C3E38B35%2E6D218144511F3437&__acm__=1563021092_5e8cda0c5ddddb14d4f5e9e3bd610a44">A symbolic evolutionary algorithm software platform. </a> <em>Proceedings of the Genetic and Evolutionary Computation Conference Companion (GECCO '19). </em></a> July 2019.
87) Aleksandar Prokopec, Andrea Rosà, David Leopoldseder, Gilles Duboscq, Petr Tůma, Martin Studener, Lubomír Bulej, Yudi Zheng, Alex Villazón, Doug Simon, Thomas Würthinger, Walter Binder. <a href="https://renaissance.dev/resources/docs/renaissance-suite.pdf">Renaissance: Benchmarking Suite for Parallel Applications on the JVM. </a> <em>PLDI ’19, Phoenix, AZ, USA. </em></a> June 2019.
88) Robert Höttger, Lukas Krawczyk, Burkhard Igel, Olaf Spinczyk. <a href="http://2019.rtas.org/wp-content/uploads/2019/04/RTAS19_BP_proceedings.pdf#page=23">Memory Mapping Analysis for Automotive Systems. </a> <em>Brief Presentations Proceedings (RTAS 2019). </em></a> Apr. 2019.
89) Al Akkad, M. A., & Gazimzyanov, F. F. <a href="http://izdat.istu.ru/index.php/ISM/article/view/4317">AUTOMATED SYSTEM FOR EVALUATING 2D-IMAGE COMPOSITIONAL CHARACTERISTICS: CONFIGURING THE MATHEMATICAL MODEL.</a> <em>Intellekt. Sist. Proizv., 17(1), 26-33. doi: 10.22213/2410-9304-2019-1-26-33. </em></a> Apr. 2019.
90) Alcayde, A.; Baños, R.; Arrabal-Campos, F.M.; Montoya, F.G. <a href="https://www.mdpi.com/1996-1073/12/7/1270">Optimization of the Contracted Electric Power by Means of Genetic Algorithms.</a> <em>Energies, Volume 12, Issue 7, </em></a> Apr. 2019.
91) Abdul Sahli Fakharudin, Norazwina Zainol, Zulsyazwan Ahmad Khushairi. <a href="https://dl.acm.org/doi/10.1145/3323716.3323737">Modelling and Optimisation of Oil Palm Trunk Core Biodelignification using Neural Network and Genetic Algorithm.</a> <em>IEEA '19: Proceedings of the 8th International Conference on Informatics, Environment, Energy and Applications; Pages 155–158, </em></a> Mar. 2019.
92) Aleksandar Prokopec, Andrea Rosà, David Leopoldseder, Gilles Duboscq, Petr Tůma, Martin Studener, Lubomír Bulej, Yudi Zheng, Alex Villazón, Doug Simon, Thomas Wuerthinger, Walter Binder. <a href="https://arxiv.org/pdf/1903.10267.pdf">On Evaluating the Renaissance Benchmarking Suite: Variety, Performance, and Complexity.</a> <em>Cornell University: Programming Languages, </em></a> Mar. 2019.
93) S. Appel, W. Geithner, S. Reimann, M Sapinski, R. Singh, D. M. Vilsmeier <a href="https://www.researchgate.net/profile/Sabrina_Appel/publication/330934110_OPTIMIZATION_OF_HEAVY-ION_SYNCHROTRONS_USING_NATURE-INSPIRED_ALGORITHMS_AND_MACHINE_LEARNING/links/5c5c425b299bf1d14cb33546/OPTIMIZATION-OF-HEAVY-ION-SYNCHROTRONS-USING-NATURE-INSPIRED-ALGORITHMS-AND-MACHINE-LEARNING.pdf">OPTIMIZATION OF HEAVY-ION SYNCHROTRONS USINGNATURE-INSPIRED ALGORITHMS AND MACHINE LEARNING.</a><em><a href="https://bt.pa.msu.edu/ICAP18/index.html">13th Int. Computational Accelerator Physics Conf.</a>, </em></a> Feb. 2019.
94) Saad, Christian, Bernhard Bauer, Ulrich R Mansmann, and Jian Li. <a href="https://journals.sagepub.com/doi/10.1177/1177932218818458">AutoAnalyze in Systems Biology.</a> <em>Bioinformatics and Biology Insights, </em></a> Jan. 2019.
95) Gandeva Bayu Satrya, Soo Young Shin. <a href="https://arxiv.org/pdf/1812.01201.pdf">Evolutionary Computing Approach to Optimize Superframe Scheduling on Industrial Wireless Sensor Networks.</a> <em>Cornell University, </em></a> Dec. 2018.
96) H.R. Maier, S. Razavi, Z. Kapelan, L.S. Matott, J. Kasprzyk, B.A. Tolson. <a href="https://www.sciencedirect.com/science/article/pii/S1364815218305905">Introductory overview: Optimization using evolutionary algorithms and other metaheuristics.</a> <em>Environmental Modelling & Software, </em></a> Dec. 2018.
97) Erich C. Teppan and Giacomo Da Col. <a href="http://ceur-ws.org/Vol-2252/paper4.pdf">Automatic Generation of Dispatching Rules for Large Job Shops by Means of Genetic Algorithms.</a> <em>CIMA 2018, International Workshop on Combinations of Intelligent Methods and Applications, </em></a> Nov. 2018.
98) Pasquale Salzaa, Filomena Ferrucci. <a href="https://www.sciencedirect.com/science/article/pii/S0167739X17324147">Speed up genetic algorithms in the cloud using software containers.</a> <em>Future Generation Computer Systems, </em></a> Oct. 2018.
99) Ghulam Mubashar Hassan and Mark Reynolds. <a href="https://easychair.org/publications/open/GRLP">Genetic Algorithms for Scheduling and Optimization of Ore Train Networks.</a> <em>GCAI-2018. 4th Global Conference on Artificial Intelligence, </em></a> Sep. 2018.
100) Drezewski, Rafal & Kruk, Sylwia & Makowka, Maciej. [The Evolutionary Optimization of a Company’s Return on Equity Factor: Towards the Agent-Based Bio-Inspired System Supporting Corporate Finance Decisions.](https://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=8466578) <em>IEEE Access. 6. 10.1109/ACCESS.2018.2870201, </em></a> Sep. 2018.
101) Arifin, H. H., Chimplee, N. , Kit Robert Ong, H. , Daengdej, J. and Sortrakul, T. <a href="https://onlinelibrary.wiley.com/doi/abs/10.1002/j.2334-5837.2018.00549.x">Automated Component‐Selection of Design Synthesis for Physical Architecture with Model‐Based Systems Engineering using Evolutionary Trade‐off.</a> <em><a href="https://onlinelibrary.wiley.com/doi/abs/10.1002/j.2334-5837.2018.00549.x">INCOSE International Symposium, 28: 1296-1310</a>, </em></a> Aug. 2018.
102) Ong, Robert & Sortrakul, Thotsapon. <a href="https://www.researchgate.net/publication/327096423_Comparison_of_Selection_Methods_of_Genetic_Algorithms_for_Automated_Component-Selection_of_Design_Synthesis_with_Model-Based_Systems_Engineering">Comparison of Selection Methods of Genetic Algorithms for Automated Component-Selection of Design Synthesis with Model-Based Systems Engineering.</a> <em>Conference: I-SEEC 2018, </em> May 2018.
103) Stephan Pirnbaum. [Die Evolution im Algorithmus - Teil 2: Multikriterielle Optimierung und Architekturerkennung.](http://www.buschmais.de/wp-content/uploads/2018/06/Die-Evolution-im-Algorithmus_Teil2_JS_03_18.pdf) <a href="https://www.sigs-datacom.de/digital/javaspektrum/"><em>JavaSPEKTRUM 03/2018, pp 66–69, </em></a> May 2018.
104) W. Geithner, Z. Andelkovic, S. Appel, O. Geithner, F. Herfurth, S. Reimann, G. Vorobjev, F. Wilhelmstötter. [Genetic Algorithms for Machine Optimization in the Fair Control System Environment.](http://accelconf.web.cern.ch/AccelConf/ipac2018/papers/thpml028.pdf)<em> [The 9th International Particle Accelerator Conference (IPAC'18)](https://ipac18.org/welcome/), </em></a> May 2018.
105) Stephan Pirnbaum. [Die Evolution im Algorithmus - Teil 1: Grundlagen.](http://www.buschmais.de/wp-content/uploads/2018/02/Die-Evolution-im-Algorithmus_JS_01_18.pdf) <a href="https://www.sigs-datacom.de/digital/javaspektrum/"><em>JavaSPEKTRUM 01/2018, pp 64–68, </em></a> Jan. 2018.
106) Alexander Felfernig, Rouven Walter, José A. Galindo, David Benavides, Seda Polat Erdeniz, Müslüm Atas, Stefan Reiterer. <a href="https://link.springer.com/article/10.1007/s10844-017-0492-1">Anytime diagnosis for reconfiguration. </a> <em>Journal of Intelligent Information Systems, pp 1–22, </em> Jan. 2018.
107) Bruce A. Johnson. <a href="https://link.springer.com/protocol/10.1007/978-1-4939-7386-6_13">From Raw Data to Protein Backbone Chemical Shifts Using NMRFx Processing and NMRViewJ Analysis. </a> <em>Protein NMR: Methods and Protocols, pp. 257--310, Springer New York, </em> Nov. 2017.
108) Cuadra P., Krawczyk L., Höttger R., Heisig P., Wolff C. <a href="https://link.springer.com/chapter/10.1007/978-3-319-67642-5_30">Automated Scheduling for Tightly-Coupled Embedded Multi-core Systems Using Hybrid Genetic Algorithms. </a> <em>Information and Software Technologies: 23rd International Conference, ICIST 2017, Druskininkai, Lithuania.</em> Communications in Computer and Information Science, vol 756. Springer, Cham, Sep. 2017.
109) Michael Trotter, Guyue Liu, Timothy Wood. <a href="http://ieeexplore.ieee.org/abstract/document/8064120/">Into the Storm: Descrying Optimal Configurations Using Genetic Algorithms and Bayesian Optimization. </a> <em><a href="http://ieeexplore.ieee.org/xpl/mostRecentIssue.jsp?punumber=8063634">Foundations and Applications of Self* Systems (FAS*W), 2017 IEEE 2nd International Workshops</a></em> Sep. 2017.
110) Emna Hachicha, Karn Yongsiriwit, Mohamed Sellami. <a href="https://doi.org/10.1109/ICWS.2017.101">Genetic-Based Configurable Cloud Resource Allocation in QoS-Aware Business Process Development. </a> <em>Information and Software Technologies: 23rd International Conference, ICIST 2017, Druskininkai, Lithuania.</em> Web Services (ICWS), 2017 IEEE International Conference, Jun. 2017.
111) Abraão G. Nazário, Fábio R. A. Silva, Raimundo Teive, Leonardo Villa, Antônio Flávio, João Zico, Eire Fragoso, Ederson F. Souza. <a href="http://siaiap32.univali.br/seer/index.php/acotb/article/view/10579/5933">Automação Domótica Simulada Utilizando Algoritmo Genético Especializado na Redução do Consumo de Energia. </a> <em>Computer on the Beach 2017</em> pp. 180-189, March 2017.
112) Bandaru, S. and Deb, K. <a href="http://dx.doi.org/10.1201/9781315183176-12">Metaheuristic Techniques.</a> <em>Decision Sciences.</em> CRC Press, pp. 693-750, Nov. 2016.
113) Lyazid Toumi, Abdelouahab Moussaoui, and Ahmet Ugur. <a href="http://dx.doi.org/10.1145/2816839.2816876">EMeD-Part: An Efficient Methodology for Horizontal Partitioning in Data Warehouses.</a> <em>Proceedings of the International Conference on Intelligent Information Processing, Security and Advanced Communication.</em> Djallel Eddine Boubiche, Faouzi Hidoussi, and Homero Toral Cruz (Eds.). ACM, New York, NY, USA, Article 43, 7 pages, 2015.
114) Andreas Holzinger (Editor), Igo Jurisica (Editor). <a href="http://www.springer.com/computer/database+management+%26+information+retrieval/book/978-3-662-43967-8">Interactive Knowledge Discovery and Data Mining in Biomedical Informatics.</a> <em>Lecture Notes in Computer Science, Vol. 8401.</em> <a href="http://www.springer.com">Springer</a>, 2014.
115) Lyazid Toumi, Abdelouahab Moussaoui, Ahmet Ugur. <a href="http://link.springer.com/article/10.1007%2Fs11227-013-1058-9">Particle swarm optimization for bitmap join indexes selection problem in data warehouses.</a> <em><a href="http://link.springer.com/journal/11227">The Journal of Supercomputing</a>, Volume 68, <a href="http://link.springer.com/journal/11227/68/2/page/1">Issue 2</a>, pp 672-708, May 2014.</em>
116) TANG Yi (Guangzhou Power Supply Bureau Limited, Guangzhou 511400, China) <a href="http://en.cnki.com.cn/Article_en/CJFDTOTAL-JXKF201210017.htm"> <em>Study on Object-Oriented Reactive Compensation Allocation Optimization Algorithm for Distribution Networks</em></a>, Oct. 2012.
117) John M. Linebarger, Richard J. Detry, Robert J. Glass, Walter E. Beyeler, Arlo L. Ames, Patrick D. Finley, S. Louise Maffitt. <a href="http://prod.sandia.gov/techlib/access-control.cgi/2012/121117.pdf"> <em>Complex Adaptive Systems of Systems Engineering Environment Version 1.0.  </em></a> <a href="http://www.sandia.gov/CasosEngineering/">SAND REPORT</a>, Feb. 2012.
</details>

## Release notes

### [8.0.0](https://github.com/jenetics/jenetics/releases/tag/v8.0.0)

#### Improvements

* Java 21 is used for building and using the library.
* [#878](https://github.com/jenetics/jenetics/issues/878): Allow Virtual-Threads evaluating the fitness function. Must be enabled when creating an `Engine` (see code snippet below), the previous behaviour has been preserverd.
```java
final Engine<DoubleGene, Double> engine = Engine.builder(ff)
	.fitnessExecutor(BatchExecutor.ofVirtualThreads())
	.build();
```
* [#880](https://github.com/jenetics/jenetics/issues/880): Replace code examples in Javadoc with [JEP 413](https://openjdk.org/jeps/413).
* [#886](https://github.com/jenetics/jenetics/issues/886): Improve `CharStore` sort.
* [#894](https://github.com/jenetics/jenetics/issues/894): New genetic operators: `ShiftMutator`, `ShuffleMutator` and `UniformOrderBasedCrossover`.
* [#895](https://github.com/jenetics/jenetics/issues/895): Improve default `RandomGenerator` selection. The used `RandomGenerator` is selected in the following order:
	1) Check if the `io.jenetics.util.defaultRandomGenerator` start parameter is set. If so, take this generator.
	2) Check if the `L64X256MixRandom` generator is available. If so, take this generator.
	3) Find the _best_ available random generator according to the `RandomGeneratorFactory.stateBits()` value.
	4) Use the `Random` generator if no _best_ generator can be found. This generator is guaranteed to be available on every platform.


_[All Release Notes](RELEASE_NOTES.md)_

## License

The library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

	Copyright 2007-2024 Franz Wilhelmstötter

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


<a href="https://www.jetbrains.com/idea/"><img src="http://jenetics.io/img/icon_IntelliJIDEA.png" alt="IntelliJ" height="100"/></a>

<a href="https://www.syntevo.com/smartgit/"><img src="https://www.syntevo.com/assets/images/logos/smartgit-8c1aa1e2.svg" alt="SmartGit" height="100"/></a>
