#!/bin/bash

VERSION="0.8.1.0"
CLS_PATH="./build/main/jenetics-${VERSION}-all.jar:./build/main/jenetics-${VERSION}-examples.jar:."

java -cp $CLS_PATH org.jenetics.examples.Knapsack
java -cp $CLS_PATH org.jenetics.examples.OnesCounting
java -cp $CLS_PATH org.jenetics.examples.RealFunction
java -cp $CLS_PATH org.jenetics.examples.StringGenerator
java -cp $CLS_PATH org.jenetics.examples.Transformation
java -cp $CLS_PATH org.jenetics.examples.TravelingSalesman


