rem Java Genetic Algorithm Library (@__identifier__@).
rem Copyright (c) @__year__@ Franz Wilhelmstötter
rem
rem Licensed under the Apache License, Version 2.0 (the "License");
rem you may not use this file except in compliance with the License.
rem You may obtain a copy of the License at
rem
rem      http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.
rem
rem Author:
rem   Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)

set VERSION="@__version__@"
set CLS_PATH=../lib/jenetics-%VERSION%.jar
set CLS_PATH=%CLS_PATH%;../lib/jenetics.example-%VERSION%.jar;.

java -cp %CLS_PATH% org.jenetics.example.Knapsack
java -cp %CLS_PATH% org.jenetics.example.OnesCounting
java -cp %CLS_PATH% org.jenetics.example.RealFunction
java -cp %CLS_PATH% org.jenetics.example.StringGenerator
java -cp %CLS_PATH% org.jenetics.example.TravelingSalesman
