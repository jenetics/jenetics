#!/bin/bash

# Java Genetic Algorithm Library (@__identifier__@).
# Copyright (c) @__year__@ Franz Wilhelmstötter
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Author:
#    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
#

##
# Run the examples for profiling, using the JGrind library:
#     https://bitbucket.org/fwilhelm/jgrind
#
# The created reports can be viewed with the KCachgrind application:
#    http://kcachegrind.sourceforge.net/html/Home.html
##

SCRIPT_DIR=`readlink -f $0`
SCRIPT_DIR=`dirname ${SCRIPT_DIR}`
REPORT_DIR=`readlink -f ${SCRIPT_DIR}/../reports/performance`

VERSION="@__version__@"
CLS_PATH=`readlink -f ${SCRIPT_DIR}/../lib/org.jenetics-all-${VERSION}.jar`
CLS_PATH=${CLS_PATH}:`readlink -f ${SCRIPT_DIR}/../lib/org.jenetics.example-${VERSION}.jar`:.

agent_param() {
	output_file=$1
	agent=`readlink -f ${SCRIPT_DIR}/../source/project/lib/libjgrind-x86_64.so`
	agent="${agent}=output=${REPORT_DIR}/${output_file}:include=org.jenetics"

	echo ${agent}
}

main() {
	mkdir "${REPORT_DIR}"

	java -agentpath:`agent_param Knapsack.jgrind` -cp $CLS_PATH org.jenetics.example.Knapsack
	java -agentpath:`agent_param OnesCounting.jgrind` -cp $CLS_PATH org.jenetics.example.OnesCounting
	java -agentpath:`agent_param RealFunction.jgrind` -cp $CLS_PATH org.jenetics.example.RealFunction
	java -agentpath:`agent_param StringGenerator.jgrind` -cp $CLS_PATH org.jenetics.example.StringGenerator
	java -agentpath:`agent_param Transformation.jgrind` -cp $CLS_PATH org.jenetics.example.Transformation
	java -agentpath:`agent_param TravelingSalesman.jgrind` -cp $CLS_PATH org.jenetics.example.TravelingSalesman
}

main $*




