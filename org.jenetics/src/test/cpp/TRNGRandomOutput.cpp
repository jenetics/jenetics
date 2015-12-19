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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
template<class Random>
class TRNGRandomOutput {
public:

	TRNGRandomOutput(
		unsigned long long seed,
		unsigned int splitp,
		unsigned int splits,
		unsigned long long jump,
		unsigned int jump2
	) {
		_random.seed(seed);
		_random.split(splitp, splits);
		_random.jump(jump);
		_random.jump2(jump2);

		std::stringstream name;
		name << seed << "-";
		name << splitp << "-" << splits << "-";
		name << jump << "-";
		name << jump2;
		_fileName = name.str();
	}

	~TRNGRandomOutput() {
	}

	std::string next() {
		std::stringstream out;
		out << static_cast<long long>(_random());
		return out.str();
	}

	std::string fileName() {
		return _fileName;
	}

private:
	Random _random;
	std::string _fileName;
};

template<class Random>
void write(const std::string& dir, TRNGRandomOutput<Random>& random, std::size_t numbers) {
	std::string file = dir + "/" + random.fileName();
	std::fstream out(file.c_str(), std::fstream::out);

	for (std::size_t i = 0; i < numbers; ++i) {
		out << random.next() << std::endl;
	}

	out.close();
}



int main(void) {

	int count = 0;

	for (unsigned long long seed = 0; seed < 2; ++seed) {
		for (unsigned int splitp = 5; splitp < 10; splitp += 3) {
			for (unsigned int splits = 0; splits < splitp; splits += 2) {
				for (unsigned long long jump = 0; jump < 2; ++jump) {
					for (unsigned int jump2 = 0; jump2 < 64; jump2 += 23) {

						std::cout <<
							"{new Long(" << static_cast<long long>(seed*74236788222246L) << "L), " <<
							"new Integer(" << static_cast<long long>(splitp) << "), " <<
							"new Integer(" << static_cast<long long>(splits) << "), " <<
							"new Long(" << static_cast<long long>(jump*948392782247324L) << "L), " <<
							"new Integer(" << static_cast<long long>(jump2) << ")}," << std::endl;


						TRNGRandomOutput<trng::lcg64_shift> random(
							seed*74236788222246L,
							splitp, splits,
							jump*948392782247324L, jump2
						);
						write<trng::lcg64_shift>("output", random, 150);
					}
				}
			}
		}
	}

	return 0;
}
