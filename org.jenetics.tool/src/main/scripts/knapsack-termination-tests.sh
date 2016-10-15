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
	#"KnapsackFitnessThreshold:Knapsack-fitness_threshold_termination"
	"KnapsackFitnessConvergence:Knapsack-fitness_convergence_termination:10@30"
	"KnapsackFitnessConvergence:Knapsack-fitness_convergence_termination:50@150"
	"KnapsackFitnessConvergence:Knapsack-fitness_convergence_termination:150@450"
	#"KnapsackFixedGeneration:Knapsack-fixed_generation_termination"
	#"KnapsackSteadyFitness:Knapsack-steady_fitness_termination"
	#"KnapsackExecutionTime:Knapsack-execution_time_termination"
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
		RESULT="${RESULT_BASE_PATH}/${parts[1]}-${parts[2]}.xml"
	else
		PARAMS=""
		RESULT="${RESULT_BASE_PATH}/${parts[1]}.xml"
	fi

	${JRUN} ${CLASS} ${PARAMS} --result-file ${RESULT} --sample-count 1000
done
