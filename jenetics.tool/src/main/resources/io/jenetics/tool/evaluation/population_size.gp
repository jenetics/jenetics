################################################################################
# Parameters
# * data - the data file
# * output - the output file
################################################################################

################################################################################
# Output definition
################################################################################
set terminal svg size 700, 500 enhanced fname "Times Roman" fsize 11
set output output

################################################################################
# Main graph
################################################################################

set grid
set logscale x
set autoscale y
set key right bottom
set style fill empty

set multiplot
set size 1, 0.67
set origin 0, 0.33
set bmargin 0.1
set format x ""
set ylabel "Fitness" font ",12"

set lmargin 12
set rmargin 4

plot data using 1:(0.001*$12) with lines lt rgb "blue" notitle axes x1y1


################################################################################
# Sub graph
################################################################################

unset title
unset logscale y
#unset logscale x

set logscale y
set ylabel "Generation"
set xlabel "Population size" font ",12"
set bmargin
set format x "10^{%L}"
set size 1.0, 0.33
set origin 0.0, 0.0
set tmargin 1
#set format y " %2.1f"
set format y "  10^{%L}"

plot data using 1:2 with lines notitle lt rgb "red" axes x1y1

unset multiplot
