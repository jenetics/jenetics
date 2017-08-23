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
#include <sys/types.h>
#include <sys/stat.h>
#include <trng/lcg64_shift.hpp>
#include <trng/mt19937.hpp>
#include <trng/mt19937_64.hpp>
#include <trng/mrg2.hpp>
#include <trng/mrg3.hpp>
#include <trng/yarn2.hpp>

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0
 */

template<class Random, typename seed_type, typename signed_seed_type>
void write(const std::string& dir, Random& random, seed_type seed, std::size_t numbers) {
	::mkdir(dir.c_str(), S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);

	std::stringstream file;
	file << dir << "/" << seed;
	std::fstream out(file.str().c_str(), std::fstream::out);

	random.seed(seed);
	for (std::size_t i = 0; i < numbers; ++i) {
		out << static_cast<signed_seed_type>(random()) << std::endl;
	}

	out.close();
}

template<class Random, typename seed_type, typename signed_seed_type>
void generate_numbers(const std::string& name, Random& random) {
	::mkdir("output", S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);

	for (seed_type i = 0; i < 100; ++i) {
		seed_type seed = i*32344;

		write<Random, seed_type, signed_seed_type>(
			"output/" + name, random, seed, 1000
		);
	}
}

int main(void) {
	trng::mt19937 random1;
	generate_numbers<trng::mt19937, unsigned long, int>("MT19937_32Random.dat", random1);

	trng::yarn2 random2;
	generate_numbers<trng::yarn2, unsigned long, int>("MRG2Random.dat", random2);

	trng::mt19937_64 random3;
	generate_numbers<trng::mt19937_64, unsigned long long, long>("MT19937_64Random.dat", random3);

	return 0;
}
