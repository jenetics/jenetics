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

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.stat.MinMax;

/**
 * This example shows a more advanced use of a genetic algorithm: approximate a
 * raster image with ~100 semi-transparent polygons of length 6.
 * <p>
 * The fitness function is quite simple yet expensive to compute:
 * <ul>
 * <li>draw the polygons of a chromosome to an image
 * <li>compare each pixel with the corresponding reference image
 * </ul>
 * <p>
 * To improve the speed of the calculation, we calculate the fitness not on the
 * original image size, but rather on a scaled down version, which is sufficient
 * to demonstrate the power of such a genetic algorithm.
 *
 * @see <a href="http://www.nihilogic.dk/labs/evolving-images/">
 *      Evolving Images with JavaScript and canvas (Nihilogic)</a>
 */
public final class ImageEvolutionExample extends JFrame {

	// Additional Swing components.
	private final NumberFormat _fitnessFormat = NumberFormat.getNumberInstance();
	private final ImagePanel _origImagePanel;
	private final PolygonPanel _painter;

	private BufferedImage _image;
	private BufferedImage _refImage;
	private int[] _refImagePixels;
	private ThreadLocal<BufferedImage> _workingImage;
	private Thread _thread;

	// The GA engine classes.
	private Codec<PolygonChromosome, PolygonGene> _codec;
	private Engine<PolygonGene, Double> _engine;

	/**
	 * Creates new form ImageEvolution
	 */
	public ImageEvolutionExample() {
		_origImagePanel = new ImagePanel();
		_painter = new PolygonPanel();

		initComponents();
		init();
	}

	private void init() {
		setIconImage(
			Toolkit.getDefaultToolkit()
				.getImage(getClass().getResource("/monalisa.png"))
		);

		origImagePanel.add(_origImagePanel);
		polygonImagePanel.add(_painter);
		engineParamPanel.setEngineParam(engineParam());

		_fitnessFormat.setMaximumIntegerDigits(1);
		_fitnessFormat.setMinimumIntegerDigits(1);
		_fitnessFormat.setMinimumFractionDigits(5);
		_fitnessFormat.setMaximumFractionDigits(5);

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
		final EngineParam param = engineParamPanel.getEngineParam();
		engineParam(param);

		_refImage = resizeImage(
			_image,
			param.getReferenceImageSize().width,
			param.getReferenceImageSize().height,
			BufferedImage.TYPE_INT_ARGB
		);

		_workingImage = ThreadLocal.withInitial(() -> new BufferedImage(
			_refImage.getWidth(),
			_refImage.getHeight(),
			BufferedImage.TYPE_INT_ARGB
		));

		_refImagePixels = _refImage.getData().getPixels(
			0, 0, _refImage.getWidth(), _refImage.getHeight(), (int[])null
		);

		_codec = Codec.of(
			Genotype.of(new PolygonChromosome(
				param.getPolygonCount(), param.getPolygonLength()
			)),
			gt -> (PolygonChromosome) gt.getChromosome()
		);

		_engine = Engine.builder(this::fitness, _codec)
			.populationSize(param.getPopulationSize())
			.optimize(Optimize.MAXIMUM)
			.survivorsSelector(new TruncationSelector<>())
			.offspringSelector(new TournamentSelector<>(param.getTournamentSize()))
			.alterers(
				new PolygonMutator<>(
					param.getMutationRate(), param.getMutationChange()
				),
				new UniformCrossover<>(0.5))
			.build();
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

	private void setImage(final BufferedImage image) {
		_image = requireNonNull(image);

		_origImagePanel.setImage(_image);
		_painter.setDimension(_image.getWidth(), _image.getHeight());
	}

	/**
	 * Calculate the fitness function for a Polygon chromosome.
	 * <p>
	 * For this purpose, we first draw the polygons on the test buffer,  and
	 * then compare the resulting image pixel by pixel with the  reference image.
	 */
	final double fitness(final PolygonChromosome chromosome) {
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
        java.awt.GridBagConstraints gridBagConstraints;

        imagePanel = new javax.swing.JPanel();
        imageSplitPane = new javax.swing.JSplitPane();
        origImagePanel = new javax.swing.JPanel();
        polygonImagePanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        resultPanel = new javax.swing.JPanel();
        bestEvolutionResultPanel = new org.jenetics.example.image.EvolutionResultPanel();
        currentevolutionResultPanel = new org.jenetics.example.image.EvolutionResultPanel();
        engineParamPanel = new org.jenetics.example.image.EngineParamPanel();

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 258, Short.MAX_VALUE)
                .addComponent(openButton)
                .addContainerGap())
        );

        resultPanel.setLayout(new java.awt.GridBagLayout());

        bestEvolutionResultPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Best"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        resultPanel.add(bestEvolutionResultPanel, gridBagConstraints);

        currentevolutionResultPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Current"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        resultPanel.add(currentevolutionResultPanel, gridBagConstraints);

        engineParamPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Engine parameter"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        resultPanel.add(engineParamPanel, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
		initEngine();

		_thread = new Thread(() -> {
			final MinMax<EvolutionResult<PolygonGene, Double>> best = MinMax.of();

			_engine.stream()
				.limit(result -> !Thread.currentThread().isInterrupted())
				.peek(best)
				.forEach(r -> invokeLater(() -> onNewResult(r, best.getMax())));
		});
		_thread.start();

		// Enable/Disable UI controls.
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		openButton.setEnabled(false);
		engineParamPanel.setEnabled(false);
	}//GEN-LAST:event_startButtonActionPerformed

	private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
		_thread.interrupt();

		stopButton.setEnabled(false);
		startButton.setEnabled(true);
		openButton.setEnabled(true);
		engineParamPanel.setEnabled(true);
	}//GEN-LAST:event_stopButtonActionPerformed

	private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
		final File dir = lastOpenDirectory();
		final JFileChooser chooser = dir != null
			? new JFileChooser(dir)
			: new JFileChooser();
		chooser.setDialogTitle("Choose Image");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.addChoosableFileFilter(new FileNameExtensionFilter(
			format(
				"Images (%s)",
				Stream.of(ImageIO.getReaderFileSuffixes())
					.map(s -> format(" *.%s", s))
					.collect(Collectors.joining(","))
			),
			ImageIO.getReaderFileSuffixes()
		));

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

		bestEvolutionResultPanel.update(best);
		currentevolutionResultPanel.update(current);
		_painter.setChromosome(_codec.decoder().apply(gt));
		_painter.repaint();
	}

	/* *************************************************************************
	 * Application preferences.
	 **************************************************************************/

	private static final String ENGINE_PARAM_NODE = "engine_param";
	private static final String LAST_OPEN_DIRECTORY_PREF = "last_open_directory";

	private EngineParam engineParam() {
		return EngineParam.load(appPref().node(ENGINE_PARAM_NODE));
	}

	private void engineParam(final EngineParam param) {
		param.store(appPref().node(ENGINE_PARAM_NODE));
	}

	private File lastOpenDirectory() {
		final String dirName = appPref().get(LAST_OPEN_DIRECTORY_PREF, null);
		return dirName != null ? new File(dirName) : null;
	}

	private void lastOpenDirectory(final File dir) {
		appPref().put(LAST_OPEN_DIRECTORY_PREF, dir.getAbsolutePath());
	}

	private static Preferences appPref() {
		return Preferences.userRoot().node("org/jenetics/example/image");
	}

	private static void prefFlush() {
		try {
			appPref().flush();
		} catch (BackingStoreException ex) {
			Logger.getLogger(ImageEvolutionExample.class.getName())
				.log(Level.SEVERE, null, ex);
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
			java.util.logging.Logger.getLogger(ImageEvolutionExample.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(ImageEvolutionExample.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(ImageEvolutionExample.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(ImageEvolutionExample.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>
		//</editor-fold>

        /* Create and display the form */
		java.awt.EventQueue.invokeLater(() -> {
			new ImageEvolutionExample().setVisible(true);
		});

		prefFlush();
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jenetics.example.image.EvolutionResultPanel bestEvolutionResultPanel;
    private javax.swing.JPanel buttonPanel;
    private org.jenetics.example.image.EvolutionResultPanel currentevolutionResultPanel;
    private org.jenetics.example.image.EngineParamPanel engineParamPanel;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JSplitPane imageSplitPane;
    private javax.swing.JButton openButton;
    private javax.swing.JPanel origImagePanel;
    private javax.swing.JPanel polygonImagePanel;
    private javax.swing.JPanel resultPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}
