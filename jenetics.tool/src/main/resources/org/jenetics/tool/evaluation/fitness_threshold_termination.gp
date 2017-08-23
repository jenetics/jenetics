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
set logscale y
set yrange[1:]
set format y "   10^{%L}"
set key right bottom
set style fill empty

set multiplot
set size 1, 0.60
set origin 0, 0.40
set bmargin 0.1
set format x ""
set ylabel "{/:Bold Total generation}"
set xrange[7000:11000]

set lmargin 10
set rmargin 2

plot data using 1:7:9:10:8 with candlesticks lt rgb "red" notitle axes x1y1 whiskerbars, \
     data using 1:6:6:6:6 with candlesticks lt rgb "red" lw 2 notitle axes x1y1, \
     data using 1:2 with lines notitle lt rgb "red" axes x1y1

################################################################################
# Sub graph
################################################################################

unset title
unset logscale y

set autoscale y
set ylabel "{/:Bold Fitness}"
set xlabel "{/:Bold Fitness threshold}"
set bmargin
set size 1.0, 0.40
set origin 0.0, 0.0
set bmargin
set tmargin 0.5
set format y " %2.1f"
set format x "%2.1f"
set xrange[7:11]
set ytics 1

plot data using (0.001*$1):(0.001*$11) with lines lt rgb "blue" notitle axes x1y1

unset multiplot
