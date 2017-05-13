################################################################################
# Parameters
# * data - the data file
# * output - the output file
################################################################################

################################################################################
# Output definition
################################################################################
set terminal svg size 700, 500 fname "Serif" fsize 16
set output 'genotype_write_perf.svg'

################################################################################
# Main graph
################################################################################

set multiplot
set grid
set logscale x
set logscale y
set yrange[1:]
set format y "   10^{%L}"
set key right bottom
set style fill empty

set size 1, 0.60
set origin 0, 0.40
set bmargin 0.1
set format x "10^{%L}"
set ylabel "{/:Bold Marshaling time [µs]}"
set xlabel "{/:Bold Chromosomes count}"

set lmargin 10
set rmargin 2

plot 'genotype_write_perf.dat' using 1:4 with linespoints ls 1 title 'JAXB', \
		'' using 1:6 with linespoints ls 2 title 'Java serialization', \
		'' using 1:8 with linespoints ls 3 title 'XML writer'
