################################################################################
# Output definition
################################################################################
set terminal svg size 700, 700 font "Serif,16"
set output output

# color definitions
set style line 1 pt 7 ps 0.2 lt 1 lw 2 # --- blue

unset key

set grid
set xlabel "f_1"
set ylabel "f_2"
set zlabel "f_3"
set ticslevel 0
set view 60,75

set xrange [0:0.5]
set yrange [0:0.5]
set zrange [0:0.5]
# Axes
#set style line 11 lt 1

# Grid
#set style line 12 lt 0 lw 1
#set grid back ls 12

splot data using 1:2:3 w p ls 1

