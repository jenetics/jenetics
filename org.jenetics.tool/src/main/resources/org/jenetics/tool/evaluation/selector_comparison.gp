################################################################################
# Parameters
# * data - the data file
# * output - the output file
################################################################################

################################################################################
# Output definition
################################################################################
set terminal svg size 700, 500 fname "Serif" fsize 16
set output output

set grid

set logscale x
set autoscale y
set ylabel "{/:Bold Fitness}"
set xlabel "{/:Bold Generation}"
set bmargin
set origin 0.0, 0.0
set tmargin 1
set format x "10^{%L}"
set format y " %2.1f"

set key bottom

plot data using 1:(0.001*$2) with lines lt rgb "blue" title "MonteCarloSelector" axes x1y1, \
     data using 1:(0.001*$11) with lines lt rgb "red" title "{/:Italic Evolutionary-{/:Normal Selector}}" axes x1y1
