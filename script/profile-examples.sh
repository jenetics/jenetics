#!/bin/bash

##
# Run the examples for profiling, using the JGrind library:
#     https://bitbucket.org/fwilhelm/jgrind
# 
# The created reports can be viewed with the KCachgrind application:
#    http://kcachegrind.sourceforge.net/html/Home.html
##

SCRIPT_DIR=`readlink -f $0`
SCRIPT_DIR=`dirname ${SCRIPT_DIR}`

VERSION=`cat ${SCRIPT_DIR}/../VERSION`
CLS_PATH=`readlink -f ${SCRIPT_DIR}/../build/main/jenetics-all-${VERSION}.jar`
CLS_PATH=${CLS_PATH}:`readlink -f ${SCRIPT_DIR}/../build/main/jenetics-examples-${VERSION}.jar`:.

agent_param() {
	output_file=$1
	agent=`readlink -f ${SCRIPT_DIR}/../lib/build/libjgrind-x86_64.so`
	agent="${agent}=output=${SCRIPT_DIR}/${output_file}:include=org.jenetics"
	
	echo ${agent}
}

main() {
	java -agentpath:`agent_param Knapsack.jgrind` -cp $CLS_PATH org.jenetics.examples.Knapsack
	java -agentpath:`agent_param OnesCounting.jgrind` -cp $CLS_PATH org.jenetics.examples.OnesCounting
	java -agentpath:`agent_param RealFunction.jgrind` -cp $CLS_PATH org.jenetics.examples.RealFunction
	java -agentpath:`agent_param StringGenerator.jgrind` -cp $CLS_PATH org.jenetics.examples.StringGenerator
	java -agentpath:`agent_param Transformation.jgrind` -cp $CLS_PATH org.jenetics.examples.Transformation
	java -agentpath:`agent_param TravelingSalesman.jgrind` -cp $CLS_PATH org.jenetics.examples.TravelingSalesman
}

main $*




