rem Java Genetic Algorithm Library (@__identifier__@).
rem Copyright (c) @__year__@ Franz Wilhelmstötter
rem
rem This library is free software; you can redistribute it and/or
rem modify it under the terms of the GNU Lesser General Public
rem License as published by the Free Software Foundation; either
rem version 2.1 of the License, or (at your option) any later version.
rem
rem This library is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
rem Lesser General Public License for more details.
rem
rem You should have received a copy of the GNU Lesser General Public
rem License along with this library; if not, write to the Free Software
rem Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
rem
rem Author:
rem   Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)

set VERSION="@__version__@"
set CLS_PATH=../lib/org.jenetics-all-%VERSION%.jar;../lib/org.jenetics.example-%VERSION%.jar;.

java -cp %CLS_PATH% org.jenetics.examples.Knapsack
java -cp %CLS_PATH% org.jenetics.examples.OnesCounting
java -cp %CLS_PATH% org.jenetics.examples.RealFunction
java -cp %CLS_PATH% org.jenetics.examples.StringGenerator
java -cp %CLS_PATH% org.jenetics.examples.Transformation
java -cp %CLS_PATH% org.jenetics.examples.TravelingSalesman
