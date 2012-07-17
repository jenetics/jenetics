#!/bin/bash

agent_param() {
	output_file=$1
	agent=`pwd`/../lib/build/libjgrind-x86_64.so
	agent=`readlink -f ${agent}`
	agent="${agent}=output=${output_file}:include=org.jenetics"
	
	echo ${agent}
}

VERSION=`cat ../VERSION`
CLS_PATH="../build/main/jenetics-all-${VERSION}.jar:../build/main/jenetics-examples-${VERSION}.jar:."

java -agentpath:`agent_param Knapsack.jgrind` -cp $CLS_PATH org.jenetics.examples.Knapsack
java -agentpath:`agent_param OnesCounting.jgrind` -cp $CLS_PATH org.jenetics.examples.OnesCounting
java -agentpath:`agent_param RealFunction.jgrind` -cp $CLS_PATH org.jenetics.examples.RealFunction
java -agentpath:`agent_param StringGenerator.jgrind` -cp $CLS_PATH org.jenetics.examples.StringGenerator
java -agentpath:`agent_param Transformation.jgrind` -cp $CLS_PATH org.jenetics.examples.Transformation
java -agentpath:`agent_param TravelingSalesman.jgrind` -cp $CLS_PATH org.jenetics.examples.TravelingSalesman


