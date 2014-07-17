/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */

#include <cstdlib>
#include <iostream>
#include <iomanip>
#include <sstream>
#include <fstream>
#include <vector>
#include <trng/lcg64_shift.hpp>
#include <trng/mt19937.hpp>

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-07-17 $</em>
 */

template<class Random>
void write(const std::string& dir, Random& random, unsigned long seed, std::size_t numbers) {
	std::stringstream file;
	file << dir << "/" << seed;
	std::cout << file.str() << std::endl;
	std::fstream out(file.str().c_str(), std::fstream::out);

	random.seed(seed);
	for (std::size_t i = 0; i < numbers; ++i) {
		out << static_cast<int>(random()) << std::endl;
	}

	out.close();
}

int main(void) {
	trng::mt19937 random;

	for (unsigned int i = 0; i < 100; ++i) {
		unsigned long seed = i*32344;

		write<trng::mt19937>("output", random, seed, 1000);
	}

	return 0;
}
