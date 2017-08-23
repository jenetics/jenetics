################################################################################
# Parameters
# * data - the data file
# * output - the output file
################################################################################

################################################################################
# Output definition
################################################################################
set terminal svg size 700, 500 fname "Serif" fsize 16
set output 'genotype_read_perf.svg'

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

set bmargin
set origin 0.0, 0.0
set tmargin 1
set format x "10^{%L}"
set ylabel "{/:Bold Marshalling time [µs]}"
set xlabel "{/:Bold Chromosome count}"

plot 'genotype_read_perf.dat' using 1:2 with linespoints ls 1 title 'JAXB', \
		'' using 1:4 with linespoints ls 2 title 'Java serialization', \
		'' using 1:6 with linespoints ls 3 title 'XML reader'
