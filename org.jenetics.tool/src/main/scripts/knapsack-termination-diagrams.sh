#!/usr/bin/env bash

read_link() {
	local path=$1
	if [ -d ${path} ] ; then
		local abspath=$(cd ${path}; pwd)
	else
		local prefix=$(cd $(dirname -- ${path}); pwd)
		local suffix=$(basename ${path})
		local abspath="$prefix/$suffix"
	fi
	echo ${abspath}
}

RESULTS=(
	"Knapsack-execution_time_termination.xml:Generation,Fitness"
	"Knapsack-fitness_convergence_termination-10@30.xml:Generation,Fitness"
	"Knapsack-fitness_convergence_termination-50@150.xml:Generation,Fitness"
	"Knapsack-fitness_convergence_termination-150@450.xml:Generation,Fitness"
	"Knapsack-fitness_threshold_termination.xml:Generation,Fitness"
	"Knapsack-fixed_generation_termination.xml:Generation,Fitness"
	##"Knapsack-generation_population_size.xml:Fitness,Runtime"
	"Knapsack-selector_comparison.xml:Fitness1,Fitness2"
	"Knapsack-steady_fitness_termination.xml:Generation,Fitness"
)

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
RESULT_BASE_PATH=`read_link "${SCRIPT_DIR}/../results/org/jenetics/tool/evaluation"`
JRUN=`read_link "${SCRIPT_DIR}/../../../../jrun"`

for item in ${RESULTS[@]}
do
	echo "Processing '${item%%:*}'"
	RESULT="${RESULT_BASE_PATH}/${item%%:*}"
	SAMPLES="${item#*:}"
	${JRUN} org.jenetics.tool.evaluation.Diagram --input ${RESULT} --samples ${SAMPLES}

	cp -a "${RESULT%.*}.svg" "${SCRIPT_DIR}/../../../../org.jenetics.doc/src/main/resources/diagram"
done
