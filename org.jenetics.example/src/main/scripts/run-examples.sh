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

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)

VERSION="@__version__@"
CLS_PATH="${SCRIPT_DIR}/../lib/jenetics-${VERSION}.jar"
CLS_PATH=${CLS_PATH}:"${SCRIPT_DIR}/../lib/jenetics.example-${VERSION}.jar":.

java -cp $CLS_PATH org.jenetics.example.Knapsack
java -cp $CLS_PATH org.jenetics.example.OnesCounting
java -cp $CLS_PATH org.jenetics.example.RealFunction
java -cp $CLS_PATH org.jenetics.example.StringGenerator
java -cp $CLS_PATH org.jenetics.example.TravelingSalesman
