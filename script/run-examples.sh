#!/bin/bash

SCRIPT_DIR=`readlink -f $0`
SCRIPT_DIR=`dirname ${SCRIPT_DIR}`

VERSION=`cat ${SCRIPT_DIR}/../VERSION`
CLS_PATH=`readlink -f ${SCRIPT_DIR}/../build/main/jenetics-all-${VERSION}.jar`
CLS_PATH=${CLS_PATH}:`readlink -f ${SCRIPT_DIR}/../build/main/jenetics-examples-${VERSION}.jar`:.

java -cp $CLS_PATH org.jenetics.examples.Knapsack
java -cp $CLS_PATH org.jenetics.examples.OnesCounting
java -cp $CLS_PATH org.jenetics.examples.RealFunction
java -cp $CLS_PATH org.jenetics.examples.StringGenerator
java -cp $CLS_PATH org.jenetics.examples.Transformation
java -cp $CLS_PATH org.jenetics.examples.TravelingSalesman


