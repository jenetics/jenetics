################################################################################
# Output definition
################################################################################
set terminal svg size 400, 400 font "Serif,16"
set output output

# color definitions
set style line 1 pt 7 ps 0.3 lt 1 lw 2 # --- blue

#set ylabel "y"
#set xlabel "x"

unset key

# Axes
set style line 11 lt 1

# Grid
set style line 12 lt 0 lw 1
set grid back ls 12

plot data using 1:2 w p ls 1

