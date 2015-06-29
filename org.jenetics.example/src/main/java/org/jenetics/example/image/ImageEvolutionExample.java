/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jenetics.example.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;

/**
 * This example shows a more advanced use of a genetic algorithm: approximate a raster image with
 * ~100 semi-transparent polygons of length 6.
 * <p>
 * The fitness function is quite simple yet expensive to compute:
 * <ul>
 * <li>draw the polygons of a chromosome to an image
 * <li>compare each pixel with the corresponding reference image
 * </ul>
 * <p>
 * To improve the speed of the calculation, we calculate the fitness not on the original image size, but rather on a
 * scaled down version, which is sufficient to demonstrate the power of such a genetic algorithm.
 *
 * @see <a href="http://www.nihilogic.dk/labs/evolving-images/">Evolving Images with JavaScript and canvas (Nihilogic)</a>
 */
@SuppressWarnings("serial")
public class ImageEvolutionExample {

    public static final int POPULATION_SIZE = 40;
    public static final int TOURNAMENT_ARITY = 3;
    public static final float MUTATION_RATE = 0.02f;
    public static final float MUTATION_CHANGE = 0.1f;

    public static final int POLYGON_LENGTH = 6;
    public static final int POLYGON_COUNT = 100;

    private static BufferedImage refImage;
    private static int[] refImagePixels;

    // a ThreadLocal to cache the images used during the fitness calculation
    private static ThreadLocal<BufferedImage> imageCache = new ThreadLocal<BufferedImage>() {
        @Override
        protected BufferedImage initialValue() {
            return new BufferedImage(refImage.getWidth(), refImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
    };

    /**
     * Calculate the fitness function for a Polygon chromosome.
     * <p>
     * For this purpose, we first draw the polygons on the test buffer, and then compare the resulting image pixel by
     * pixel with the reference image.
     */
    public static double fitness(final Genotype<PolygonGene> gt) {
        PolygonChromosome chromosome = (PolygonChromosome) gt.getChromosome();

        BufferedImage img = imageCache.get();
        Graphics2D g2 = img.createGraphics();

        int width = img.getWidth();
        int height = img.getHeight();

        chromosome.draw(g2, width, height);
        g2.dispose();

        int[] refPixels = refImagePixels;
        int[] testPixels = img.getData().getPixels(0, 0, width, height, (int[]) null);

        int diff = 0;
        int p = width * height * 4 - 1; // 4 channels: rgba
        int idx = 0;

        do {
            if (idx++ % 4 != 0) { // ignore the alpha channel for fitness
                int dp = testPixels[p] - refPixels[p];
                diff += (dp < 0) ? -dp : dp;
            }
        } while (--p > 0);

        return (1.0 - diff / (width * height * 3.0 * 256));
    }

    private static class Display extends JFrame {

        private Engine<PolygonGene, Double> engine;
        private PolygonChromosome bestFit;

        private Thread internalThread;
        private volatile boolean stopRequested;

        private BufferedImage origImage;
        private ImagePainter painter;
        private JLabel bestFitLabel;

        public Display() throws Exception {
            setTitle("Image Evolution Example");
            setSize(300, 200);

            setLayout(new BorderLayout());

            Box bar = Box.createHorizontalBox();

            InputStream is = this.getClass().getClassLoader().getResourceAsStream("monalisa.png");
            origImage = ImageIO.read(is);

            refImage = resizeImage(origImage, 50, 50, BufferedImage.TYPE_INT_ARGB);
            refImagePixels = refImage.getData().getPixels(0, 0, refImage.getWidth(), refImage.getHeight(), (int[]) null);

            JLabel picLabel = new JLabel(new ImageIcon(origImage));
            bar.add(picLabel);

            painter = new ImagePainter(origImage.getWidth(), origImage.getHeight());
            bar.add(painter);

            add(bar, BorderLayout.CENTER);

            JButton startButton = new JButton("Start");
            startButton.setActionCommand("start");
            add(startButton, BorderLayout.EAST);

            bestFitLabel = new JLabel("Click on the start button...");
            add(bestFitLabel, BorderLayout.SOUTH);

            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isAlive()) {
                        stopRequest();
                        startButton.setText("Start");
                    } else {
                        startButton.setText("Stop");
                        startEvolution();
                    }
                }
            });

            // initialize a new genetic algorithm
            final Factory<Genotype<PolygonGene>> gtf = Genotype.of(new PolygonChromosome(POLYGON_COUNT, POLYGON_LENGTH));

            engine = Engine
                    .builder(ImageEvolutionExample::fitness, gtf)
                    .populationSize(POPULATION_SIZE)
                    .optimize(Optimize.MAXIMUM)
                    .survivorsSelector(new TruncationSelector<>())
                    .offspringSelector(new TournamentSelector<>(TOURNAMENT_ARITY))
                    .alterers(
                              new PolygonMutator<Double>(MUTATION_RATE, MUTATION_CHANGE),
                              new UniformCrossover<>(0.5)
                             )
                    .build();
        }

        public boolean isAlive() {
            return internalThread != null && internalThread.isAlive();
        }

        public void stopRequest() {
            stopRequested = true;
            internalThread.interrupt();
            internalThread = null;
        }

        public void startEvolution() {
            stopRequested = false;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Iterator<EvolutionResult<PolygonGene, Double>> evolutionIterator = engine.iterator();
                    while (!stopRequested && evolutionIterator.hasNext()) {
                        EvolutionResult<PolygonGene, Double> nextEvolution = evolutionIterator.next();
                        bestFit = (PolygonChromosome) nextEvolution.getBestPhenotype().getGenotype().getChromosome();
                        bestFitLabel.setText(String.format("generation: %d - bestFit: %.4f",
                                                           nextEvolution.getGeneration(), nextEvolution.getBestPhenotype().getFitness()));
                        painter.repaint();
                    }
                }
            };

            internalThread = new Thread(r);
            internalThread.start();
        }

        private class ImagePainter extends Component {

            private int width;
            private int height;

            public ImagePainter(int width, int height) {
                this.width = width;
                this.height = height;
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(width, height);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            public void paint(Graphics g) {
                PolygonChromosome chromosome = bestFit;
                if (chromosome != null) {
                    chromosome.draw((Graphics2D) g, width, height);
                } else {
                    g.setColor(Color.white);
                    g.clearRect(0, 0, width, height);
                }
            }
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type)
        throws IOException {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public static void main(String[] args)
        throws Exception {
        final JFrame display = new Display();
        final Runnable r = new Runnable() {

            @Override
            public void run() {
                JMenuItem exit = new JMenuItem("Exit");
                exit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });

                JMenu menu = new JMenu("File");
                menu.add(exit);
                JMenuBar mb = new JMenuBar();
                mb.add(menu);
                display.setJMenuBar(mb);

                display.setLocationRelativeTo(null);
                display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                display.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }

}
