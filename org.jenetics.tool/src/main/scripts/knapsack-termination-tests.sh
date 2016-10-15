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

TESTS=(
	#"KnapsackFitnessThreshold:Knapsack-fitness_threshold_termination.xml"
	"KnapsackFitnessConvergence:Knapsack-fitness_convergence_termination.xml:50,150"
	"KnapsackFixedGeneration:Knapsack-fixed_generation_termination.xml"
	#"KnapsackSteadyFitness:Knapsack-steady_fitness_termination.xml"
	#"KnapsackExecutionTime:Knapsack-execution_time_termination.xml"
)

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
RESULT_BASE_PATH=`read_link "${SCRIPT_DIR}/../results/org/jenetics/tool/evaluation"`
JRUN=`read_link "${SCRIPT_DIR}/../../../../jrun"`

for test in ${TESTS[@]}
do
	IFS=':' read -r -a parts <<< "$test"
	CLASS="org.jenetics.tool.evaluation.${parts[0]}"

	if [[ "${#parts[@]}" == 3 ]]; then
		PARAMS="--params ${parts[2]}"
		RESULT="${RESULT_BASE_PATH}/${parts[1]}"
	else
		PARAMS=""
	fi

	${JRUN} ${CLASS} ${PARAMS} --result-file ${RESULT} --sample-count 1000
done
