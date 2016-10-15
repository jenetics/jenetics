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

################################################################################
# Main graph
################################################################################

set grid
set logscale x
set key right bottom
set style fill empty

set label 1 "{N_S = ${PARAM_0}}" at graph 0.06,0.9 left
set label 2 "{N_L = ${PARAM_1}}" at graph 0.06,0.8 left

set multiplot
set size 1, 0.60
set origin 0, 0.40
set bmargin 0.1
set format x ""
set format y "%5.0f"
set ylabel "{/:Bold Total generation}"
set xrange [10E-2:10E-11] reverse

set lmargin 10
set rmargin 2

plot data using 1:7:9:10:8 with candlesticks lt rgb "red" notitle axes x1y1 whiskerbars, \
     data using 1:6:6:6:6 with candlesticks lt rgb "red" lw 2 notitle axes x1y1, \
     data using 1:2 with lines notitle lt rgb "red" axes x1y1

################################################################################
# Sub graph
################################################################################

unset title
unset label 1
unset label 2
unset logscale y
set logscale x

set autoscale y
set ylabel "{/:Bold Fitness}"
set xlabel "{/:Bold Epsilon}"
set size 1.0, 0.40
set origin 0.0, 0.0
set bmargin
set tmargin 0.5
set format y "% 5.1f"
set format x "10^{%L}"
set xrange [10E-2:10E-11] reverse
set ytics 0.2

plot data using 1:(0.001*$11) with lines lt rgb "blue" notitle axes x1y1

unset multiplot
