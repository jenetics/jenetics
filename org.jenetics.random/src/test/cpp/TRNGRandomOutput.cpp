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

#include <sys/types.h>
#include <sys/stat.h>

#include <cstdlib>
#include <iostream>
#include <iomanip>
#include <sstream>
#include <fstream>
#include <vector>
#include <trng/lcg64_shift.hpp>
#include <trng/mrg2.hpp>
#include <trng/mrg3.hpp>
#include <trng/mrg4.hpp>
#include <trng/mrg4.hpp>


/**
 * 
 */ 
template<
	typename Random, 
	typename SeedType,
	typename SplitType,
	typename JumpType,
	typename Jump2Type,
	typename ResultType
>
class TRNG {
public:

	TRNG(
		SeedType seed,
		SplitType splitp,
		SplitType splits,
		JumpType jump,
		Jump2Type jump2
	) {
		_random.seed(seed);
		_random.split(splitp, splits);
		_random.jump(jump);
		_random.jump2(jump2);

		std::stringstream name;
		name << "random[" << seed << ",";
		name << splitp << "," << splits << ",";
		name << jump << ",";
		name << jump2 << "].dat";
		_fileName = name.str();
	}

	~TRNG() {
	}

	std::string next() {
		std::stringstream out;
		out << static_cast<ResultType>(_random());
		return out.str();
	}

	std::string fileName() {
		return _fileName;
	}

private:
	Random _random;
	std::string _fileName;
};

template<
	typename Random, 
	typename SeedType,
	typename SplitType,
	typename JumpType,
	typename Jump2Type,
	typename ResultType
>
void write(
	const std::string& dir, 
	TRNG<Random, SeedType, SplitType, JumpType, Jump2Type, ResultType>& random, 
	std::size_t numbers
) {
	std::string file = dir + "/" + random.fileName();
	std::fstream out(file.c_str(), std::fstream::out);

	for (std::size_t i = 0; i < numbers; ++i) {
		out << random.next() << std::endl;
	}

	out.close();
}

void lcg64_shift(
	unsigned long seed, 
	unsigned long splitp, 
	unsigned long splits,
	unsigned long long jump,
	unsigned int jump2
) {
	mkdir("./LCG64ShiftRandom", S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
	
	TRNG<
		trng::lcg64_shift, 
		unsigned long, 
		unsigned long, 
		unsigned long long, 
		unsigned int, 
		long long
	> random(seed, splitp, splits, jump, jump2);
	write("./LCG64ShiftRandom", random, 150);
}

void mrg2(
	unsigned long seed, 
	unsigned long splitp, 
	unsigned long splits,
	unsigned long long jump,
	unsigned int jump2
) {
	mkdir("./MRG2Random", S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
	
	TRNG<
		trng::mrg2, 
		unsigned long, 
		unsigned long, 
		unsigned long long, 
		unsigned int, 
		long
	> random(seed, splitp, splits, jump, jump2);
	write("./MRG2Random", random, 150);
}

void mrg3(
	unsigned long seed, 
	unsigned long splitp, 
	unsigned long splits,
	unsigned long long jump,
	unsigned int jump2
) {
	mkdir("./MRG3Random", S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
	
	TRNG<
		trng::mrg2, 
		unsigned long, 
		unsigned long, 
		unsigned long long, 
		unsigned int, 
		int
	> random(seed, splitp, splits, jump, jump2);
	write("./MRG3Random", random, 150);
}


int main(void) {
	for (unsigned long long seed = 0; seed < 2; ++seed) {
		for (unsigned long splitp = 5; splitp < 10; splitp += 3) {
			for (unsigned long splits = 0; splits < splitp; splits += 2) {
				for (unsigned long long jump = 0; jump < 2; ++jump) {
					for (unsigned int jump2 = 0; jump2 < 64; jump2 += 23) {
						lcg64_shift(seed*742367882L, splitp, splits, jump*948392782L, jump2);
						mrg2(seed*742367882L, splitp, splits, jump*948392782L, jump2);
						mrg3(seed*742367882L, splitp, splits, jump*948392782L, jump2);
					}
				}
			}
		}
	}

	return 0;
}









