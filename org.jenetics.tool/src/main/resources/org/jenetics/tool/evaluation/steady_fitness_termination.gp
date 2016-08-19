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
set logscale y
set yrange[1:]
set format y "   10^{%L}"
set key right bottom
set style fill empty

set multiplot
set size 1, 0.67
set origin 0, 0.33
set bmargin 0.1
set format x ""
set ylabel "Total generation" font ",12"

set lmargin 12
set rmargin 4

plot data using 1:7:9:10:8 with candlesticks lt rgb "red" notitle axes x1y1 whiskerbars, \
     data using 1:6:6:6:6 with candlesticks lt rgb "red" lw 2 notitle axes x1y1, \
     data using 1:2 with lines notitle lt rgb "red" axes x1y1

################################################################################
# Sub graph
################################################################################

unset title
unset logscale y
set autoscale y
set ylabel "Fitness"
set xlabel "Steady generation" font ",12"
set bmargin
set format x "10^{%L}"
set size 1.0, 0.33
set origin 0.0, 0.0
set tmargin 1
set format y " %2.1f"

plot data using 1:(0.001*$11) with lines lt rgb "blue" notitle axes x1y1

unset multiplot
