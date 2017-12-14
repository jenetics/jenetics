################################################################################
# Output definition
################################################################################
set terminal svg size 510, 500 fname "Serif" fsize 16
set output '/home/fwilhelm/Workspace/Development/Projects/Jenetics/jenetics.tool/src/main/results/io/jenetics/tool/moea/circle_points.svg'

set title "Circle points"

# color definitions
set style line 1 pt 7 ps 0.3 lt 1 lw 2 # --- blue

unset key

# Axes
set style line 11 lt 1

# Grid
set style line 12 lt 0 lw 1
set grid back ls 12

plot data using 1:2 w p ls 1


# gnuplot -e "data='/home/fwilhelm/Workspace/Development/Projects/Jenetics/jenetics.tool/src/main/results/io/jenetics/tool/moea/circle_points.dat'" ./circle_points.gp
