#!/bin/bash

VERSION=`cat ../VERSION`

CLS_PATH="../build/main/jenetics-all-${VERSION}.jar:../build/main/jenetics-examples-${VERSION}.jar:."

java -cp $CLS_PATH org.jenetics.examples.Knapsack
java -cp $CLS_PATH org.jenetics.examples.OnesCounting
java -cp $CLS_PATH org.jenetics.examples.RealFunction
java -cp $CLS_PATH org.jenetics.examples.StringGenerator
java -cp $CLS_PATH org.jenetics.examples.Transformation
java -cp $CLS_PATH org.jenetics.examples.TravelingSalesman


