################################################################################
# Parameters
# * data - the data file
# * output - the output file
################################################################################

################################################################################
# Output definition
################################################################################
set terminal svg size 700, 400 enhanced fname "Times Roman" fsize 11
set output output

set grid

set logscale x
set logscale y
set autoscale z

set xlabel "Generation" font ",12"
set ylabel "Population size" font ",12"
set zlabel "Fitness" rotate left

set bmargin
set origin 0.0, 0.0
set tmargin 1
set pm3d

set format x "10^{%L}"
set format y "10^{%L}"
set format z " %2.1f"

set key bottom
set hidden3d
set dgrid3d 30,30

#plot data using 1:(0.001*$2) with lines lt rgb "blue"
splot data using 1:2:(0.001*$12) with lines notitle
