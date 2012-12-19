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

SCRIPT_DIR=`readlink -f $0`
SCRIPT_DIR=`dirname ${SCRIPT_DIR}`

VERSION="@__version__@"
CLS_PATH=`readlink -f ${SCRIPT_DIR}/../lib/org.jenetics-all-${VERSION}.jar`
CLS_PATH=${CLS_PATH}:`readlink -f ${SCRIPT_DIR}/../lib/org.jenetics.example-${VERSION}.jar`:.

java -cp $CLS_PATH org.jenetics.example.Knapsack
java -cp $CLS_PATH org.jenetics.example.OnesCounting
java -cp $CLS_PATH org.jenetics.example.RealFunction
java -cp $CLS_PATH org.jenetics.example.StringGenerator
java -cp $CLS_PATH org.jenetics.example.Transformation
java -cp $CLS_PATH org.jenetics.example.TravelingSalesman


