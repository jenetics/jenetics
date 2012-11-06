#!/bin/bash

# Java Genetic Algorithm Library (@__identifier__@).
# Copyright (c) @__year__@ Franz Wilhelmstötter
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
#
# Author:
#   Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)

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

	java -agentpath:`agent_param Knapsack.jgrind` -cp $CLS_PATH org.jenetics.examples.Knapsack
	java -agentpath:`agent_param OnesCounting.jgrind` -cp $CLS_PATH org.jenetics.examples.OnesCounting
	java -agentpath:`agent_param RealFunction.jgrind` -cp $CLS_PATH org.jenetics.examples.RealFunction
	java -agentpath:`agent_param StringGenerator.jgrind` -cp $CLS_PATH org.jenetics.examples.StringGenerator
	java -agentpath:`agent_param Transformation.jgrind` -cp $CLS_PATH org.jenetics.examples.Transformation
	java -agentpath:`agent_param TravelingSalesman.jgrind` -cp $CLS_PATH org.jenetics.examples.TravelingSalesman
}

main $*




