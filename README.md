REQUIREMENTS
---------------------------------------

- JDK 1.7
  The JAVA_HOME variable must be set to your
  java installation directory.
  
- Dependency to the JScience library (http:://jscience.org).
  This library is included and lies in the lib directory.

BUILD Jenetics
---------------------------------------

- cd into the directory containing build.xml
- execute ./lib/build/apacha-ant/bin/ant
  This compiles the sources creates two jar files and
  generates the javadoc. The jar Jenetics-*.jar contains
  only the "Jenetics" files. The jar Jenetics-all-*jar
  contains also the depending JScience classe.


TEST
---------------------------------------

For running the tests execute
    ant testng

Additional tests must be stored in the testng sub-directory.
You must also add the testng.xml file in the testng/org/jenetics
directory.

Run the examples:
Befor you can run the examples you must build the
library. Then you can run the given examples.

- Knapsack:
    java -cp jenetics-all-<version>.jar:Jenetics-examples-<version>.jar org.jenetics.examples.Knapsack
    
- OnesCounting:
    java -cp jenetics-all-<version>.jar:Jenetics-examples-<version>.jar org.jenetics.examples.OnesCounting
    
- RealFunction:
    java -cp jenetics-all-<version>.jar:Jenetics-examples-<version>.jar org.jenetics.examples.RealFunction
    
- StringGenerator:
    java -cp jenetics-all-<version>.jar:Jenetics-examples-<version>.jar org.jenetics.examples.StringGenerator
    
- TravelingSalesman:
    java -cp jenetics-all-<version>.jar:Jenetics-examples-<version>.jar org.jenetics.examples.TravelingSalesman

