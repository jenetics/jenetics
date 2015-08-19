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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.min;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SpinnerModel;

import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.stat.MinMax;

public final class ImageEvolution extends javax.swing.JFrame {

	private static final int MIN_POPULATION_SIZE = 5;
	private static final int MAX_POPULATION_SIZE = 250;
	private static final int DEFAULT_POPULATION_SIZE = 30;

	public static final int POPULATION_SIZE = 40;
	public static final int TOURNAMENT_ARITY = 3;
	public static final float MUTATION_RATE = 0.02f;
	public static final float MUTATION_CHANGE = 0.1f;
	public static final int POLYGON_LENGTH = 6;
	public static final int POLYGON_COUNT = 100;

	private static final Codec<PolygonChromosome, PolygonGene> CODEC = Codec.of(
		Genotype.of(new PolygonChromosome(POLYGON_COUNT, POLYGON_LENGTH)),
		gt -> (PolygonChromosome) gt.getChromosome()
	);

	private BufferedImage _image;
	private Engine<PolygonGene, Double> _engine;
	private BufferedImage _refImage;
	private int[] _refImagePixels;
	private ThreadLocal<BufferedImage> _workingImage;
	private Thread _thread;
	private final NumberFormat _fitnessFormat = NumberFormat.getNumberInstance();

	private final ImagePanel _origImagePanel;
	private final PolygonPanel _painter;

	/**
	 * Creates new form ImageEvolution
	 */
	public ImageEvolution() {
		_origImagePanel = new ImagePanel();
		_painter = new PolygonPanel();

		initComponents();
		init();
	}

	private void init() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/monalisa.png")));
		
		origImagePanel.add(_origImagePanel);
		polygonImagePanel.add(_painter);

		_fitnessFormat.setMaximumIntegerDigits(1);
		_fitnessFormat.setMinimumIntegerDigits(1);
		_fitnessFormat.setMinimumFractionDigits(5);
		_fitnessFormat.setMaximumFractionDigits(5);

		populationSizeSlider.setMinimum(MIN_POPULATION_SIZE);
		populationSizeSlider.setMaximum(MAX_POPULATION_SIZE);
		populationSizeSlider.setValue(populationSize());

		imageSplitPane.setDividerLocation(0.5);

		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		try (InputStream in = getClass()
			.getClassLoader()
			.getResourceAsStream("monalisa.png"))
		{
			setImage(ImageIO.read(in));
		} catch (IOException e) {
			throw new AssertionError(e);
		}


		initEngine();
	}

	private void initEngine() {
		_engine = Engine.builder(this::fitness, CODEC)
			.populationSize(populationSizeSlider.getValue())
			.optimize(Optimize.MAXIMUM)
			.survivorsSelector(new TruncationSelector<>())
			.offspringSelector(new TournamentSelector<>(TOURNAMENT_ARITY))
			.alterers(
				new PolygonMutator<>(MUTATION_RATE, MUTATION_CHANGE),
				new UniformCrossover<>(0.5))
			.build();
	}

	private void setImage(final BufferedImage image) {
		_image = requireNonNull(image);
		_refImage = resizeImage(_image, 50, 50, BufferedImage.TYPE_INT_ARGB);

		_workingImage = ThreadLocal.withInitial(() -> new BufferedImage(
			_refImage.getWidth(),
			_refImage.getHeight(),
			BufferedImage.TYPE_INT_ARGB
		));

		_refImagePixels = _refImage.getData().getPixels(
			0, 0, _refImage.getWidth(), _refImage.getHeight(), (int[]) null
		);

		_origImagePanel.setImage(_image);
		_painter.setDimension(_image.getWidth(), _image.getHeight());
	}

	private static BufferedImage resizeImage(
		final BufferedImage image,
		final int width,
		final int height,
		final int type
	) {
		final BufferedImage resizedImage = new BufferedImage(width, height, type);
		final Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

	/**
	 * Calculate the fitness function for a Polygon chromosome.
	 * <p>
	 * For this purpose, we first draw the polygons on the test buffer,  and
	 * then compare the resulting image pixel by pixel with the  reference image.
	 */
	double fitness(final PolygonChromosome chromosome) {
		final BufferedImage img = _workingImage.get();
		final Graphics2D g2 = img.createGraphics();
		final int width = img.getWidth();
		final int height = img.getHeight();

		chromosome.draw(g2, width, height);
		g2.dispose();

		final int[] refPixels = _refImagePixels;
		final int[] testPixels = img.getData()
			.getPixels(0, 0, width, height, (int[])null);

		int diff = 0;
		int p = width*height*4 - 1; // 4 channels: rgba
		int idx = 0;
		do {
			if (idx++%4 != 0) { // ignore the alpha channel for fitness
				int dp = testPixels[p] - refPixels[p];
				diff += (dp < 0) ? -dp : dp;
			}
		} while (--p > 0);

		return 1.0 - diff/(width*height*3.0*256);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imagePanel = new javax.swing.JPanel();
        imageSplitPane = new javax.swing.JSplitPane();
        origImagePanel = new javax.swing.JPanel();
        polygonImagePanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        generationLabel = new javax.swing.JLabel();
        generationTextField = new javax.swing.JTextField();
        bestFitnessLabel = new javax.swing.JLabel();
        bestFitnessTextField = new javax.swing.JTextField();
        populationSizeLabel = new javax.swing.JLabel();
        populationSizeSlider = new javax.swing.JSlider();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Image Evolution Example");

        imagePanel.setBackground(new java.awt.Color(153, 153, 153));
        imagePanel.setLayout(new java.awt.GridLayout(1, 1));

        imageSplitPane.setDividerLocation(300);

        origImagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Source image"));
        origImagePanel.setName(""); // NOI18N
        origImagePanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                origImagePanelComponentResized(evt);
            }
        });
        origImagePanel.setLayout(new java.awt.BorderLayout());
        imageSplitPane.setLeftComponent(origImagePanel);

        polygonImagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Polygon image"));
        polygonImagePanel.setLayout(new java.awt.GridLayout(1, 1));
        imageSplitPane.setRightComponent(polygonImagePanel);

        imagePanel.add(imageSplitPane);

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        openButton.setText("Open");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(openButton, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(startButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stopButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 285, Short.MAX_VALUE)
                .addComponent(openButton)
                .addContainerGap())
        );

        generationLabel.setText("Generation:");

        generationTextField.setEditable(false);

        bestFitnessLabel.setText("Best fitness:");

        bestFitnessTextField.setEditable(false);

        populationSizeLabel.setText("Population size:");

        populationSizeSlider.setMajorTickSpacing(50);
        populationSizeSlider.setPaintLabels(true);
        populationSizeSlider.setPaintTicks(true);

        jTextField1.setText("jTextField1");

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bestFitnessLabel)
                    .addComponent(generationLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(generationTextField)
                    .addComponent(bestFitnessTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
                .addGap(86, 86, 86)
                .addComponent(populationSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(populationSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, statusPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(populationSizeLabel)
                                .addComponent(generationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(generationLabel))
                            .addComponent(populationSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bestFitnessLabel)
                    .addComponent(bestFitnessTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 45, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
		_thread = new Thread(() -> {
			final MinMax<EvolutionResult<PolygonGene, Double>> best = MinMax.of();

			_engine.stream()
				.limit(result -> !Thread.currentThread().isInterrupted())
				.peek(best)
				.forEach(result -> {
					final Genotype<PolygonGene> gt = best.getMax()
						.getBestPhenotype()
						.getGenotype();

					invokeLater(() -> onNewResult(result, best.getMax()));
				});
		});
		_thread.start();

		// Enable/Disable UI controls.
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		openButton.setEnabled(false);
		populationSizeSlider.setEnabled(false);
	}//GEN-LAST:event_startButtonActionPerformed

	private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
		_thread.interrupt();
		stopButton.setEnabled(false);
		startButton.setEnabled(true);
		openButton.setEnabled(true);
		populationSizeSlider.setEnabled(true);
	}//GEN-LAST:event_stopButtonActionPerformed

	private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
		final File dir = lastOpenDirectory();
		final JFileChooser chooser = dir != null
			? new JFileChooser(dir)
			: new JFileChooser();
		chooser.setFont(new Font("Dialog", 0, 12));
		chooser.setDialogTitle("Choose Image");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		final int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File imageFile = chooser.getSelectedFile();
			try {
				setImage(ImageIO.read(imageFile));
				if (imageFile.getParentFile() != null) {
					lastOpenDirectory(imageFile.getParentFile());
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(
					rootPane,
					format("Error while loading image '%s'.", imageFile),
					e.toString(),
					JOptionPane.ERROR_MESSAGE
				);
			}
		}
	}//GEN-LAST:event_openButtonActionPerformed

    private void origImagePanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_origImagePanelComponentResized
        origImagePanel.repaint();
		polygonImagePanel.repaint();
    }//GEN-LAST:event_origImagePanelComponentResized

	private void onNewResult(
		final EvolutionResult<PolygonGene, Double> current,
		final EvolutionResult<PolygonGene, Double> best
	) {
		final Genotype<PolygonGene> gt = best
			.getBestPhenotype()
			.getGenotype();
		_painter.setChromosome(CODEC.decoder().apply(gt));

		_painter.repaint();
		generationTextField.setText(Long.toString(current.getGeneration()));
		bestFitnessTextField.setText(_fitnessFormat.format(best.getBestFitness()));
	}

	/* *************************************************************************
	 * Application preferences.
	 **************************************************************************/

	private static final String POPULATION_SIZE_PREF = "population_size";
	private static final String LAST_OPEN_DIRECTORY_PREF = "last_open_directory";

	private int populationSize() {
		return appPref().getInt(POPULATION_SIZE_PREF, DEFAULT_POPULATION_SIZE);
	}

	private void populationSize(final int populationSize) {
		appPref().putInt(POPULATION_SIZE_PREF, populationSize);
	}

	private File lastOpenDirectory() {
		final String dirName = appPref().get(LAST_OPEN_DIRECTORY_PREF, null);
		return dirName != null ? new File(dirName) : null;
	}

	private void lastOpenDirectory(final File dir) {
		appPref().put(LAST_OPEN_DIRECTORY_PREF, dir.getAbsolutePath());
	}

	private void savePrefs() {
		populationSize(populationSizeSlider.getValue());
		
		prefFlush();
	}
	
	private static Preferences appPref() {
		return Preferences.userRoot().node("org/jenetics/example/image");
	}

	private static void prefFlush() {
		try {
			appPref().flush();
		} catch (BackingStoreException ex) {
			Logger.getLogger(ImageEvolution.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(ImageEvolution.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(ImageEvolution.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(ImageEvolution.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(ImageEvolution.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

        /* Create and display the form */
		java.awt.EventQueue.invokeLater(() -> {
			new ImageEvolution().setVisible(true);
		});
		
		prefFlush();
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bestFitnessLabel;
    private javax.swing.JTextField bestFitnessTextField;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel generationLabel;
    private javax.swing.JTextField generationTextField;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JSplitPane imageSplitPane;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton openButton;
    private javax.swing.JPanel origImagePanel;
    private javax.swing.JPanel polygonImagePanel;
    private javax.swing.JLabel populationSizeLabel;
    private javax.swing.JSlider populationSizeSlider;
    private javax.swing.JButton startButton;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}
