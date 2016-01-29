#!/usr/bin/env bash

function read_link() {
    local path=$1
    if [ -d $path ] ; then
        local abspath=$(cd $path; pwd)
    else
        local prefix=$(cd $(dirname -- $path); pwd)
        local suffix=$(basename $path)
        local abspath="$prefix/$suffix"
    fi
    echo $abspath
}

TESTS=(
	"KnapsackExecutionTime:knapsack_execution_time-perf.xml"
	"KnapsackFitnessThreshold:knapsack_fitness_threshold-perf.xml"
	"KnapsackFixedGeneration:knapsack_fixed_generation-perf.xml"
	"KnapsackSteadyFitness:knapsack_steady_fitness-perf.xml"
)

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
RESULT_BASE_PATH=`read_link "${SCRIPT_DIR}/../resources/org/jenetics/trail"`
JRUN=`read_link "${SCRIPT_DIR}/../../../../jrun"`

while true; do
    for test in ${TESTS[@]}
    do
        CLASS="org.jenetics.evaluation.${test%%:*}"
        RESULT="${RESULT_BASE_PATH}/${test#*:}"

        ${JRUN} ${CLASS} --result-file ${RESULT} --sample-count 1
    done
done
