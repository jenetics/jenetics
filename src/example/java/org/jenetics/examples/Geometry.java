/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 	 
 */
package org.jenetics.examples;

import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.Dictionary;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jscience.mathematics.number.Float64;

import org.jenetics.ExponentialScaler;
import org.jenetics.FitnessFunction;
import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.RandomRegistry;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Geometry extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;


	public Geometry() {
		initComponents();
	}	
	
	void setStartAction(final Action action) {
		_startButton.setAction(action);
	}
	
	void setStopAction(final Action action) {
		_stopButton.setAction(action);
	}
	
	void setInitAction(final Action action) {
		_initButton.setAction(action);
	}
	
	void setPauseAction(final Action action) {
		_pauseButton.setAction(action);
	}
	
	void setStepAction(final Action action) {
		_stepButton.setAction(action);
	}
	
	void setPopulationSpinnerModel(final SpinnerModel model) {
		_populationSizeSpinner.setModel(model);
	}
	
	void setMaximalPhenotypeAgeSpinnerModel(final SpinnerModel model) {
		_maxPTAgeSpinner.setModel(model);
	}
	
	void setOffspringFractionRangeModel(final LabeledBoundedRangeModel model) {
		_offspringFractionSlider.setModel(model);
		_offspringFractionSlider.setLabelTable(model.getLables());
	}
	
	void setMutationProbabilityRangeModel(final LabeledBoundedRangeModel model) {
		_mutationProbabilitySlider.setModel(model);
		_mutationProbabilitySlider.setLabelTable(model.getLables());
	}
	
	void setSourcePolygon(final Point2D[] polygon) {
		if (SwingUtilities.isEventDispatchThread()) {
			((DrawPanel)_drawPanel).setSourcePolygon(polygon);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					((DrawPanel)_drawPanel).setSourcePolygon(polygon);
				}
			});
		}
	}
	
	void setTargetPolygon(final Point2D[] polygon) {
		if (SwingUtilities.isEventDispatchThread()) {
			((DrawPanel)_drawPanel).setTargetPolygon(polygon);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					((DrawPanel)_drawPanel).setTargetPolygon(polygon);
				}
			});
		}
	}
	
	void setFitnessMean(final double mean) {
		if (SwingUtilities.isEventDispatchThread()) {
			_fitnessMeanTextField.setValue(format(mean));
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					_fitnessMeanTextField.setValue(format(mean));
				}
			});
		}
	}
	
	void setFitnessVariance(final double variance) {
		if (SwingUtilities.isEventDispatchThread()) {
			_fitnessVarianceTextField.setValue(format(variance));
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					_fitnessVarianceTextField.setValue(format(variance));
				}
			});
		}
	}
	
	void setGeneration(final int generation) {
		if (SwingUtilities.isEventDispatchThread()) {
			_generationTextField.setValue(generation);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					_generationTextField.setValue(generation);
				}
			});
		}
	}
	
	void setTargetTransform(final AffineTransform transform) {
		if (SwingUtilities.isEventDispatchThread()) {
			((TransformPanel)_targetTransformPanel).setAffineTransform(transform);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					((TransformPanel)_targetTransformPanel).setAffineTransform(transform);
				}
			});
		}
	}
	
	void setGABestTransform(final AffineTransform transform) {
		if (SwingUtilities.isEventDispatchThread()) {
			((DrawPanel)_drawPanel).setAlltimeBestTransform(transform);
			((TransformPanel)_gaBestTransformPanel).setAffineTransform(transform);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					((DrawPanel)_drawPanel).setAlltimeBestTransform(transform);
					((TransformPanel)_gaBestTransformPanel).setAffineTransform(transform);
				}
			});
		}
	}
	
	void setPopulationBestTransform(final AffineTransform transform) {
		if (SwingUtilities.isEventDispatchThread()) {
			((DrawPanel)_drawPanel).setPopulationBestTransform(transform);
			((TransformPanel)_populationBestTransformPanel).setAffineTransform(transform);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					((DrawPanel)_drawPanel).setPopulationBestTransform(transform);
					((TransformPanel)_populationBestTransformPanel).setAffineTransform(transform);
				}
			});
		}
	}
	
	private static String format(final double value) {
		final NumberFormat f = NumberFormat.getNumberInstance();
		f.setMaximumFractionDigits(2);
		f.setMinimumFractionDigits(2);
		
		return f.format(value);
	}
	
	@Override
	public void repaint() {
		if (SwingUtilities.isEventDispatchThread()) {
			super.repaint();
			_drawPanel.repaint();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					Geometry.super.repaint();
					_drawPanel.repaint();
				}
			});
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	 // <editor-fold defaultstate="collapsed" descending="Generated Code">//GEN-BEGIN:initComponents
	 private void initComponents() {
		  java.awt.GridBagConstraints gridBagConstraints;

		  javax.swing.JSplitPane drawToolSplitPane=new javax.swing.JSplitPane();
		  _toolBasePanel = new javax.swing.JPanel();
		  _toolPanel = new javax.swing.JPanel();
		  _startButton = new javax.swing.JButton();
		  _stopButton = new javax.swing.JButton();
		  _initButton = new javax.swing.JButton();
		  _pauseButton = new javax.swing.JButton();
		  _stepButton = new javax.swing.JButton();
		  _generationLabel = new javax.swing.JLabel();
		  _generationTextField = new javax.swing.JFormattedTextField();
		  _populationBestTransformPanel = new TransformPanel();
		  _gaBestTransformPanel = new TransformPanel();
		  _populationSizeLabel = new javax.swing.JLabel();
		  _populationSizeSpinner = new javax.swing.JSpinner();
		  _maxPTAgeLabel = new javax.swing.JLabel();
		  _maxPTAgeSpinner = new javax.swing.JSpinner();
		  _offspringFractionSlider = new javax.swing.JSlider();
		  _offspringFractionLabel = new javax.swing.JLabel();
		  _populationTransformBestLabel = new javax.swing.JLabel();
		  _gaBestTransformLabel = new javax.swing.JLabel();
		  _targetTransformPanel = new TransformPanel();
		  _targetTransformLabel = new javax.swing.JLabel();
		  _mutationProbabilityLabel = new javax.swing.JLabel();
		  _mutationProbabilitySlider = new javax.swing.JSlider();
		  _fitenssMeanLabel = new javax.swing.JLabel();
		  _fitnessMeanTextField = new javax.swing.JFormattedTextField();
		  _fitnessVarianceLabel = new javax.swing.JLabel();
		  _fitnessVarianceTextField = new javax.swing.JFormattedTextField();
		  _drawPanel = new DrawPanel();

		  setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		  drawToolSplitPane.setDividerLocation(500);
		  drawToolSplitPane.setResizeWeight(0.5);

		  _toolBasePanel.setMinimumSize(new java.awt.Dimension(200, 200));
		  _toolBasePanel.setLayout(new java.awt.GridBagLayout());

		  _toolPanel.setLayout(new java.awt.GridBagLayout());

		  _startButton.setText("Start");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 5;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 0);
		  _toolPanel.add(_startButton, gridBagConstraints);

		  _stopButton.setText("Stop");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 6;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 0);
		  _toolPanel.add(_stopButton, gridBagConstraints);

		  _initButton.setText("Init");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 4;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(20, 5, 3, 0);
		  _toolPanel.add(_initButton, gridBagConstraints);

		  _pauseButton.setText("Pause");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 8;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 0);
		  _toolPanel.add(_pauseButton, gridBagConstraints);

		  _stepButton.setText("Step");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 7;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 0);
		  _toolPanel.add(_stepButton, gridBagConstraints);

		  _generationLabel.setText("Generation:");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 11;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
		  _toolPanel.add(_generationLabel, gridBagConstraints);

		  _generationTextField.setEditable(false);
		  _generationTextField.setPreferredSize(new java.awt.Dimension(100, 24));
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 11;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 0);
		  _toolPanel.add(_generationTextField, gridBagConstraints);

		  _populationBestTransformPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 13;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.weighty = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 0);
		  _toolPanel.add(_populationBestTransformPanel, gridBagConstraints);

		  _gaBestTransformPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 14;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.weighty = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(2, 5, 3, 0);
		  _toolPanel.add(_gaBestTransformPanel, gridBagConstraints);

		  _populationSizeLabel.setText("Population size:");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 0;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		  _toolPanel.add(_populationSizeLabel, gridBagConstraints);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 0;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
		  _toolPanel.add(_populationSizeSpinner, gridBagConstraints);

		  _maxPTAgeLabel.setText("Maximal PT age:");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 1;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		  _toolPanel.add(_maxPTAgeLabel, gridBagConstraints);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 1;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
		  _toolPanel.add(_maxPTAgeSpinner, gridBagConstraints);

		  _offspringFractionSlider.setMajorTickSpacing(10);
		  _offspringFractionSlider.setMaximum(90);
		  _offspringFractionSlider.setMinimum(10);
		  _offspringFractionSlider.setMinorTickSpacing(5);
		  _offspringFractionSlider.setPaintLabels(true);
		  _offspringFractionSlider.setPaintTicks(true);
		  _offspringFractionSlider.setValue(30);
		  _offspringFractionSlider.setBorder(null);
		  _offspringFractionSlider.setName(""); // NOI18N
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 2;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
		  _toolPanel.add(_offspringFractionSlider, gridBagConstraints);

		  _offspringFractionLabel.setText("Offspring fraction:");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 2;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		  _toolPanel.add(_offspringFractionLabel, gridBagConstraints);

		  _populationTransformBestLabel.setText("Population best:");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 13;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		  _toolPanel.add(_populationTransformBestLabel, gridBagConstraints);

		  _gaBestTransformLabel.setText("GA best:");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 14;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		  _toolPanel.add(_gaBestTransformLabel, gridBagConstraints);

		  _targetTransformPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 12;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.weighty = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 0);
		  _toolPanel.add(_targetTransformPanel, gridBagConstraints);

		  _targetTransformLabel.setText("Target transform:");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 12;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
		  _toolPanel.add(_targetTransformLabel, gridBagConstraints);

		  _mutationProbabilityLabel.setText("Mutation probability:");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 3;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		  _toolPanel.add(_mutationProbabilityLabel, gridBagConstraints);

		  _mutationProbabilitySlider.setMajorTickSpacing(100);
		  _mutationProbabilitySlider.setMaximum(500);
		  _mutationProbabilitySlider.setMinorTickSpacing(50);
		  _mutationProbabilitySlider.setPaintLabels(true);
		  _mutationProbabilitySlider.setPaintTicks(true);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 3;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
		  _toolPanel.add(_mutationProbabilitySlider, gridBagConstraints);

		  _fitenssMeanLabel.setText("Fitness mean:");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 9;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(20, 3, 0, 3);
		  _toolPanel.add(_fitenssMeanLabel, gridBagConstraints);

		  _fitnessMeanTextField.setEditable(false);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 9;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(20, 5, 3, 0);
		  _toolPanel.add(_fitnessMeanTextField, gridBagConstraints);

		  _fitnessVarianceLabel.setText("Fitness variance:");
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 10;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
		  _toolPanel.add(_fitnessVarianceLabel, gridBagConstraints);

		  _fitnessVarianceTextField.setEditable(false);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 10;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 0);
		  _toolPanel.add(_fitnessVarianceTextField, gridBagConstraints);

		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 0;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		  gridBagConstraints.weightx = 1.0;
		  gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
		  _toolBasePanel.add(_toolPanel, gridBagConstraints);

		  drawToolSplitPane.setRightComponent(_toolBasePanel);

		  _drawPanel.setBackground(java.awt.Color.white);
		  _drawPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

		  javax.swing.GroupLayout _drawPanelLayout = new javax.swing.GroupLayout(_drawPanel);
		  _drawPanel.setLayout(_drawPanelLayout);
		  _drawPanelLayout.setHorizontalGroup(
				_drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 498, Short.MAX_VALUE)
		  );
		  _drawPanelLayout.setVerticalGroup(
				_drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 542, Short.MAX_VALUE)
		  );

		  drawToolSplitPane.setLeftComponent(_drawPanel);

		  getContentPane().add(drawToolSplitPane, java.awt.BorderLayout.CENTER);

		  pack();
	 }// </editor-fold>//GEN-END:initComponents

	
	/**
	 * @param args
	 * 			  the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override public void run() {
				final Geometry geometry = new Geometry();
				geometry.setVisible(true);
				new GeometryController(geometry);
			}
		});
	}

	 // Variables declaration - do not modify//GEN-BEGIN:variables
	 private javax.swing.JPanel _drawPanel;
	 private javax.swing.JLabel _fitenssMeanLabel;
	 private javax.swing.JFormattedTextField _fitnessMeanTextField;
	 private javax.swing.JLabel _fitnessVarianceLabel;
	 private javax.swing.JFormattedTextField _fitnessVarianceTextField;
	 private javax.swing.JLabel _gaBestTransformLabel;
	 private javax.swing.JPanel _gaBestTransformPanel;
	 private javax.swing.JLabel _generationLabel;
	 private javax.swing.JFormattedTextField _generationTextField;
	 private javax.swing.JButton _initButton;
	 private javax.swing.JLabel _maxPTAgeLabel;
	 private javax.swing.JSpinner _maxPTAgeSpinner;
	 private javax.swing.JLabel _mutationProbabilityLabel;
	 private javax.swing.JSlider _mutationProbabilitySlider;
	 private javax.swing.JLabel _offspringFractionLabel;
	 private javax.swing.JSlider _offspringFractionSlider;
	 private javax.swing.JButton _pauseButton;
	 private javax.swing.JPanel _populationBestTransformPanel;
	 private javax.swing.JLabel _populationSizeLabel;
	 private javax.swing.JSpinner _populationSizeSpinner;
	 private javax.swing.JLabel _populationTransformBestLabel;
	 private javax.swing.JButton _startButton;
	 private javax.swing.JButton _stepButton;
	 private javax.swing.JButton _stopButton;
	 private javax.swing.JLabel _targetTransformLabel;
	 private javax.swing.JPanel _targetTransformPanel;
	 private javax.swing.JPanel _toolBasePanel;
	 private javax.swing.JPanel _toolPanel;
	 // End of variables declaration//GEN-END:variables

}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class GeometryController implements StepListener {
	private final Geometry _geometry;
	
	private final InitAction _initAction = new InitAction(this);
	private final StartAction _startAction = new StartAction(this);
	private final StopAction _stopAction = new StopAction(this);
	private final StepAction _stepAction = new StepAction(this);
	private final PauseAction _pauseAction = new PauseAction(this);
	
	private final PopulationSpinnerModel 
		_populationSizeSpinnerModel = new PopulationSpinnerModel(this);
	private final MaximalPhenotypeAgeSpinnerModel
		_maximalPhenotypeAgeSpinnerModel = new MaximalPhenotypeAgeSpinnerModel(this);
	private final OffspringFractionRangeModel
		_offspringFractionRangeModel = new OffspringFractionRangeModel(this);
	private final MutationProbabilityRangeModel
		_mutationProbabilityRangeModel = new MutationProbabilityRangeModel(this);
	
	private GeneticAlgorithm<Float64Gene, Float64> _ga;
	private AffineTransform _transform;
	private GA.GAFF _function;
	private Point2D[] _source;
	private Point2D[] _target;
	private Stepable _stepable;
	private Thread _thread;
	private ExecutorService _threads;
	
	private static final long MIN_REPAINT_TIME = 50;
	private long _lastRepaintTime = 0;
	private Phenotype<Float64Gene, Float64> _populationBestPhenotype;
	private Phenotype<Float64Gene, Float64> _gaBestPhenotype;
	private int _generation = 0;
	
	GeometryController(final Geometry geometry) {
		_geometry = geometry;
		
		_geometry.setInitAction(_initAction);
		_geometry.setStartAction(_startAction);
		_geometry.setStopAction(_stopAction);
		_geometry.setStepAction(_stepAction);
		_geometry.setPauseAction(_pauseAction);
		
		_geometry.setPopulationSpinnerModel(_populationSizeSpinnerModel);
		_geometry.setMaximalPhenotypeAgeSpinnerModel(_maximalPhenotypeAgeSpinnerModel);
		_geometry.setOffspringFractionRangeModel(_offspringFractionRangeModel);
		_geometry.setMutationProbabilityRangeModel(_mutationProbabilityRangeModel);
		
		init();
	}
	
	void init() {
		_source = GA.getSourcePolygon();
		_transform = GA.getTargetTransform();
		_target = GA.getTargetPolygon(_transform);
		_function = new GA.GAFF(_source, _target);
		
		_ga = GA.getGA(_function);
		_ga.setPopulationSize(_populationSizeSpinnerModel.getNumber().intValue());
		
		_geometry.setSourcePolygon(_source);
		_geometry.setTargetPolygon(_target);
		
		if (_stepable != null) {
			_stepable.removeStepListener(this);
		}
		_stepable = new Stepable(new Runnable() {
			@Override public void run() {
				if (_ga.getGeneration() == 0) {
					_ga.setup();
				} else {
					_ga.evolve();
				}
			}
		});
		_stepable.addStepListener(this);
		
		if (_thread != null) {
			_thread.interrupt();
		}
		_thread = new Thread(_stepable);
		_thread.setPriority(Thread.MIN_PRIORITY);
		_thread.start();
		
		_threads = Executors.newFixedThreadPool(2);
		
		_geometry.setTargetTransform(_transform);
		_geometry.setPopulationBestTransform(new AffineTransform());
		_geometry.setGABestTransform(new AffineTransform());
		_geometry.setGeneration(0);
		_geometry.repaint();
		
		_startAction.setEnabled(true);
		_stopAction.setEnabled(false);
		_pauseAction.setEnabled(false);
		_stepAction.setEnabled(true);
		_initAction.setEnabled(false);
	}
	
	void start() {
		_stepable.start();
		
		_startAction.setEnabled(false);
		_stopAction.setEnabled(true);
		_pauseAction.setEnabled(true);
		_stepAction.setEnabled(false);
		_initAction.setEnabled(false);
	}
	
	void stop() {
		_stepable.stop();
		_ga.getLock().lock();
		try {
			_thread.interrupt();
		} finally {
			_ga.getLock().unlock();
		}
		
		_startAction.setEnabled(false);
		_stopAction.setEnabled(false);
		_pauseAction.setEnabled(false);
		_stepAction.setEnabled(false);
		_initAction.setEnabled(true);
	}
	
	void pause() {
		_stepable.stop();
		
		_startAction.setEnabled(true);
		_stopAction.setEnabled(true);
		_pauseAction.setEnabled(false);
		_stepAction.setEnabled(true);
		_initAction.setEnabled(false);
	}
	
	void step() {
		_stepable.step();
		
		_startAction.setEnabled(true);
		_stopAction.setEnabled(true);
		_pauseAction.setEnabled(false);
		_stepAction.setEnabled(true);
		_initAction.setEnabled(false);
	}
	
	void setPopulationSize(final int size) {
		if (_ga != null) {
			_threads.submit(new Runnable() {
				@Override
				public void run() {
					_ga.getLock().lock();
					try {
						_ga.setPopulationSize(size);
						System.out.println("Population size: " + size);
					} finally {
						_ga.getLock().unlock();
					}
				}
			});
		}
	}
	
	void setMaximalPhenotypeAge(final int age) {
		if (_ga != null) {
			_threads.submit(new Runnable() {
				@Override
				public void run() {
					_ga.getLock().lock();
					try {
						_ga.setMaximalPhenotypeAge(age);
						System.out.println("Phenotype age: " + age);
					} finally {
						_ga.getLock().unlock();
					}
				}
			});			
		}
	}
	
	void setOffspringFraction(final double fraction) {
		if (_ga != null) {
			_threads.submit(new Runnable() {
				@Override
				public void run() {
					_ga.getLock().lock();
					try {
						_ga.setOffspringFraction(fraction);
						System.out.println("Offspring fraction: " + fraction);
					} finally {
						_ga.getLock().unlock();
					}
				}
			});
		}
	}
	
	void setMutationProbability(final double probability) {
		if (_ga != null) {
			_threads.submit(new Runnable() {
				@Override
				public void run() {
					_ga.getLock().lock();
					try {
						_ga.setAlterer(new Mutator<Float64Gene>(probability));
						_ga.addAlterer(new MeanAlterer<Float64Gene>());
						System.out.println("Mutation probability: " + probability);
					} finally {
						_ga.getLock().unlock();
					}
				}
			});
		}
	}

	@Override
	public void stepped(EventObject event) {
		final NumberStatistics<Float64Gene, Float64> statistics = 
			(NumberStatistics<Float64Gene, Float64>)_ga.getStatistics();
		final Phenotype<Float64Gene, Float64> populationBest = statistics.getBestPhenotype();
		final Phenotype<Float64Gene, Float64> gaBest = _ga.getBestPhenotype();
		final int generation = _ga.getGeneration();
		
		
//		if (_populationBestPhenotype == null || 
//			_populationBestPhenotype.compareTo(populationBest) < 0) 
//		{
			_populationBestPhenotype = populationBest;
			_gaBestPhenotype = gaBest;
			_generation = generation;
//		}
		
		//Prevent from extensive repainting.
		final long time = System.currentTimeMillis();
		if (time - _lastRepaintTime > MIN_REPAINT_TIME) {
			_geometry.setPopulationBestTransform(
					_function.apply(_populationBestPhenotype.getGenotype())
				);
			_geometry.setGABestTransform(
					_function.apply(_gaBestPhenotype.getGenotype())
				);
			_geometry.repaint();
			_geometry.setGeneration(_generation);
			_geometry.setFitnessMean(statistics.getFitnessMean());
			_geometry.setFitnessVariance(statistics.getFitnessVariance());
			
			_lastRepaintTime = time;
			_populationBestPhenotype = null;
			_gaBestPhenotype = null;
			_generation = 0;
		}
	}

	@Override
	public void finished(EventObject event) {
		System.out.println("GA finished");
	}
	
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class InitAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private final GeometryController _controller;
	
	public InitAction(final GeometryController controller) {
		super("Init");
		_controller = controller;
	}
	
	@Override
	public void actionPerformed(final ActionEvent e) {
		_controller.init();
	}
	
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class StartAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private final GeometryController _controller;
	
	public StartAction(final GeometryController controller) {
		super("Start");
		_controller = controller;
	}
	
	@Override
	public void actionPerformed(final ActionEvent e) {
		_controller.start();
	}
	
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class StopAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private final GeometryController _controller;
	
	public StopAction(final GeometryController controller) {
		super("Stop");
		_controller = controller;
	}
	
	@Override
	public void actionPerformed(final ActionEvent e) {
		_controller.stop();
	}
	
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class PauseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private final GeometryController _controller;
	
	public PauseAction(final GeometryController controller) {
		super("Pause");
		_controller = controller;
	}
	
	@Override
	public void actionPerformed(final ActionEvent e) {
		_controller.pause();
	}
	
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class StepAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private final GeometryController _controller;
	
	public StepAction(final GeometryController controller) {
		super("Step");
		_controller = controller;
	}
	
	@Override
	public void actionPerformed(final ActionEvent e) {
		_controller.step();
	}
	
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class PopulationSpinnerModel extends SpinnerNumberModel implements ChangeListener {
	private static final long serialVersionUID = 1L;
	
	private final GeometryController _controller;
	
	public PopulationSpinnerModel(final GeometryController controller) {
		setMinimum(5);
		setMaximum(Integer.MAX_VALUE);
		setValue(20);
		_controller = controller;
		
		addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		_controller.setPopulationSize(getNumber().intValue());
	}
	
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class MaximalPhenotypeAgeSpinnerModel extends SpinnerNumberModel 
	implements ChangeListener 
{
	private static final long serialVersionUID = 1L;
	
	private final GeometryController _controller;
	
	public MaximalPhenotypeAgeSpinnerModel(final GeometryController controller) {
		setMinimum(1);
		setMaximum(Integer.MAX_VALUE);
		setValue(35);
		_controller = controller;
		
		addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		_controller.setMaximalPhenotypeAge(getNumber().intValue());
	}
	
}

interface LabeledBoundedRangeModel extends BoundedRangeModel {
	
	public Dictionary<Integer, JComponent> getLables();
	
}

class OffspringFractionRangeModel extends DefaultBoundedRangeModel 
	implements ChangeListener, LabeledBoundedRangeModel 
{

	private static final long serialVersionUID = 1L;
	
	private static final int MIN = 10;
	private static final int MAX = 90;
	private static final int VALUE = 20;

	private final GeometryController _controller;
	
	public OffspringFractionRangeModel(final GeometryController controller) {
		setMinimum(MIN);
		setMaximum(MAX);
		setValue(VALUE);
		_controller = controller;
		
		addChangeListener(this);
	}
	
	@Override
	public Dictionary<Integer, JComponent> getLables() {
		final Dictionary<Integer, JComponent> lables = new Hashtable<>();
		
		for (int i = MIN; i <= MAX; i += 10) {
			final JLabel label = new JLabel("." + i/10);
			lables.put(i, label);
		}
		
		return lables;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				_controller.setOffspringFraction(getValue()/100.0);
			}
		}).start();
	}
	
}

class MutationProbabilityRangeModel extends DefaultBoundedRangeModel 
	implements ChangeListener, LabeledBoundedRangeModel 
{
	
	private static final long serialVersionUID = 1L;
	
	private static final int MIN = 0;
	private static final int MAX = 800;
	private static final int VALUE = 50;
	
	private final GeometryController _controller;
	
	public MutationProbabilityRangeModel(final GeometryController controller) {
		setMinimum(MIN);
		setMaximum(MAX);
		setValue(VALUE);
		_controller = controller;
		
		addChangeListener(this);
	}
	
	@Override
	public Dictionary<Integer, JComponent> getLables() {
		final Dictionary<Integer, JComponent> lables = new Hashtable<>();
		
		for (int i = MIN; i <= MAX; i += 100) {
			final JLabel label = new JLabel("." + i/100);
			lables.put(i, label);
		}
		
		return lables;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		_controller.setMutationProbability(getValue()/1000.0);
	}

}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class TransformPanel extends javax.swing.JPanel {
	private static final long serialVersionUID = 1L;
	
	private AffineTransform _transform;
	
	 public TransformPanel() {
		  initComponents();
	 }

	 public void setAffineTransform(final AffineTransform transform) {
		if (!transform.equals(_transform)) {
			final double[] m = new double[6];
			transform.getMatrix(m);
			
			_m00.setValue(m[0]); _m01.setValue(m[2]); _m02.setValue(m[4]);
			_m10.setValue(m[1]); _m11.setValue(m[3]); _m12.setValue(m[5]);
			_m20.setValue(0); 	_m21.setValue(0); 	_m22.setValue(1);
			
			_transform = transform;
		}
	 }

	 private void initComponents() {
		  java.awt.GridBagConstraints gridBagConstraints;

		  _m00 = new javax.swing.JFormattedTextField();
		  _m01 = new javax.swing.JFormattedTextField();
		  _m02 = new javax.swing.JFormattedTextField();
		  _m10 = new javax.swing.JFormattedTextField();
		  _m11 = new javax.swing.JFormattedTextField();
		  _m12 = new javax.swing.JFormattedTextField();
		  _m20 = new javax.swing.JFormattedTextField();
		  _m21 = new javax.swing.JFormattedTextField();
		  _m22 = new javax.swing.JFormattedTextField();
		  
		  setLayout(new java.awt.GridBagLayout());
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  add(_m00, gridBagConstraints);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  add(_m01, gridBagConstraints);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  add(_m02, gridBagConstraints);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 1;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  add(_m10, gridBagConstraints);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 1;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  add(_m11, gridBagConstraints);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 2;
		  gridBagConstraints.gridy = 1;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  add(_m12, gridBagConstraints);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 0;
		  gridBagConstraints.gridy = 2;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  add(_m20, gridBagConstraints);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 1;
		  gridBagConstraints.gridy = 2;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  add(_m21, gridBagConstraints);
		  gridBagConstraints = new java.awt.GridBagConstraints();
		  gridBagConstraints.gridx = 2;
		  gridBagConstraints.gridy = 2;
		  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		  gridBagConstraints.weightx = 1.0;
		  add(_m22, gridBagConstraints);
		  
	 }


	 private javax.swing.JFormattedTextField _m00;
	 private javax.swing.JFormattedTextField _m01;
	 private javax.swing.JFormattedTextField _m02;
	 private javax.swing.JFormattedTextField _m10;
	 private javax.swing.JFormattedTextField _m11;
	 private javax.swing.JFormattedTextField _m12;
	 private javax.swing.JFormattedTextField _m20;
	 private javax.swing.JFormattedTextField _m21;
	 private javax.swing.JFormattedTextField _m22;

}

/**
 * The panel which draws the polygons.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class DrawPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Stroke THICK = new BasicStroke(
			3.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER
		);
//	private static final Stroke NORMAL = new BasicStroke(
//			1.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER
//		);	
	private static final Stroke THIN = new BasicStroke(
			1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER
		);	
	private static final Stroke DASHED = new BasicStroke(
		1.0f, CAP_BUTT, JOIN_MITER, 1f, new float[]{6, 3}, 0f
	);	
	
	private Point2D[] _sourcePolygon;
	private Point2D[] _targetPolygon;
	
	private final AtomicReference<AffineTransform> _populationBestTransform = 
		new AtomicReference<>();
	
	private final AtomicReference<AffineTransform> _alltimeBestTransform =
		new AtomicReference<>();
	
	public DrawPanel() {	
		addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent e) {
				DrawPanel.this.repaint();
			}
		});
	}
	
	@Override
	public void paintComponent(final Graphics graphics) {
		super.paintComponent(graphics);
		
		final Graphics2D g2d = (Graphics2D)graphics;
		g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		
		
		paintCoordinates(g2d);
		
		final AffineTransform transform = AffineTransform.getScaleInstance(1.0, 1.0);
		
		if (_sourcePolygon != null) {
			paint(g2d, _sourcePolygon, Color.LIGHT_GRAY, THICK, transform);
		}
		
		if (_targetPolygon != null) {
			paint(g2d, _targetPolygon, Color.GREEN, DASHED, transform);
			
			AffineTransform at = _alltimeBestTransform.get();
			if (at != null) {
				paint(g2d, _targetPolygon, Color.GREEN, THICK, at);
			} else {
				paint(g2d, _targetPolygon, Color.GREEN, THICK, transform);
			}
			
			at = _populationBestTransform.get();
			if (at != null) {
				paint(g2d, _targetPolygon, Color.BLUE, THIN,  at);
			} else {
				paint(g2d, _targetPolygon, Color.BLUE, THIN, transform);
			}
		}
	}
	
	private void paintCoordinates(final Graphics2D graphics) {
		final Dimension size = getSize();
		final int ox = size.width/2;
		final int oy = size.height/2;
		
		graphics.drawLine(ox, 0, ox, size.height);
		graphics.drawLine(0, oy, size.width, oy);
	}
	
	
	private void paint(
		final Graphics2D graphics, 
		final Point2D[] polygon,
		final Color color,
		final Stroke stroke,
		final AffineTransform transform
	) {
		final Color oldColor = graphics.getColor();
		final Stroke oldStroke = graphics.getStroke();
		
		graphics.setColor(color);
		graphics.setStroke(stroke);
		
		final Dimension size = getSize();
		final int ox = size.width/2;
		final int oy = size.height/2;
		
		for (int i = 0; i < polygon.length; ++i) {
			final Point2D p1 = transform.transform(polygon[i], null);
			final Point2D p2 = transform.transform(polygon[(i + 1)%polygon.length], null);
			
			graphics.drawLine(
				(int)p1.getX() + ox, -(int)p1.getY() + oy, 
				(int)p2.getX() + ox, -(int)p2.getY() + oy
			);
		}
		
		graphics.setColor(oldColor);
		graphics.setStroke(oldStroke);
	}
	
	public void setSourcePolygon(final Point2D[] polygon) {
		_sourcePolygon = polygon;
	}
	
	public void setTargetPolygon(final Point2D[] polygon) {
		_targetPolygon = polygon;
	}
	
	public void setPopulationBestTransform(final AffineTransform transform) {
		_populationBestTransform.set(transform);
	}
	
	public void setAlltimeBestTransform(final AffineTransform transform) {
		_alltimeBestTransform.set(transform);
	}
	
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class Stepable implements Runnable {
	private final Lock _lock = new ReentrantLock();
	private final Condition _run = _lock.newCondition();

	private final List<StepListener> _listeners = new CopyOnWriteArrayList<>();
	
	private volatile int _steps = 0;
	private final Runnable _stepTask;

	public Stepable(final Runnable stepTask) {
		_stepTask = stepTask;
	}

	public void start() {
		_lock.lock();
		try {
			_steps = Integer.MAX_VALUE;
			_run.signalAll();
		} finally {
			_lock.unlock();
		}
	}

	public void stop() {
		_lock.lock();
		try {
			_steps = 0;
			_run.signalAll();
		} finally {
			_lock.unlock();
		}
	}

	public void step(int steps) {
		_lock.lock();
		try {
			_steps += steps;
			_run.signalAll();
		} finally {
			_lock.unlock();
		}
	}

	public void step() {
		step(1);
	}

	private void waiting() throws InterruptedException {
		_lock.lock();
		try {
			while (_steps <= 0) {
				_run.await();
			}
		} finally {
			_lock.unlock();
		}
	}

	private boolean canExecute() {
		_lock.lock();
		try {
			return _steps > 0;
		} finally {
			_lock.unlock();
		}
	}
	
	private void execute() {
		_stepTask.run();
		
		_lock.lock();
		--_steps;
		_lock.unlock();
		
		final EventObject event = new EventObject(this);
		for (StepListener listener : _listeners) {
			listener.stepped(event);
		}
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				waiting();
				
				while (canExecute()) {
					execute();
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			final EventObject event = new EventObject(this);
			for (StepListener listener : _listeners) {
				listener.finished(event);
			}
		}
	}
	
	public void addStepListener(final StepListener listener) {
		_listeners.add(listener);
	}
	
	public void removeStepListener(final StepListener listener) {
		_listeners.remove(listener);
	}

}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
interface StepListener extends EventListener {
	
	public void stepped(EventObject event);
	
	public void finished(EventObject event);
	
}



/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class GA {
	
	static class GAFF 
		implements FitnessFunction<Float64Gene, Float64>, 
					Function<Genotype<Float64Gene>, AffineTransform> 
	{
		private static final long serialVersionUID = 1L;
	
		private final Point2D[] _source;
		private final Point2D[] _target;
	
		public GAFF() {
			this(null, null);
		}
		
		public GAFF(final Point2D[] source, final Point2D[] target) {
			_source = source != null ? source.clone() : source;
			_target = target != null ? target.clone() : target;
		}
	
		@Override
		public Float64 evaluate(final Genotype<Float64Gene> genotype) {
			return distance(genotype);
			//return area(genotype);
		}
		
		Float64 distance(final Genotype<Float64Gene> genotype) {
			final AffineTransform transform = apply(genotype);
	
			double error = 0;
			Point2D point = new Point2D.Double();
			for (int i = 0; i < _source.length; ++i) {
				point = transform.transform(_target[i], point);
	
				error += _source[i].distance(point);
			}
	
			return Float64.valueOf(error);
		}
		
		Float64 area(final Genotype<Float64Gene> genotype) {
			final AffineTransform transform = apply(genotype);
						
			final Point2D[] points = new Point2D.Double[_source.length];
			for (int i = 0; i < _source.length; ++i) {
				points[i]  = transform.transform(_target[i], null);
			}
			
			return Float64.valueOf(GeometryUtils.area(_source, points));
		}
	
		@Override
		public AffineTransform apply(final Genotype<Float64Gene> genotype) {
			System.out.println(genotype);
			final double theta = genotype.getChromosome(0).getGene().doubleValue();
			final double tx = genotype.getChromosome(1).getGene(0).doubleValue();
			final double ty = genotype.getChromosome(1).getGene(1).doubleValue();
			final double shx = genotype.getChromosome(2).getGene(0).doubleValue();
			final double shy = genotype.getChromosome(2).getGene(1).doubleValue();
	
			final AffineTransform rotate = AffineTransform.getRotateInstance(theta);
			final AffineTransform translate = AffineTransform.getTranslateInstance(tx, ty);
			final AffineTransform shear = AffineTransform.getShearInstance(shx,shy);
	
			final AffineTransform transform = new AffineTransform();
			transform.concatenate(shear);
			transform.concatenate(rotate);
			transform.concatenate(translate);
	
			return transform;
		}
	
	}
	
	
	private static final Point2D[] SOURCE_POLYGON = new Point2D[] {
			new Point2D.Double(-100, -100),
			new Point2D.Double(100, -100),
			new Point2D.Double(100, 100),
			new Point2D.Double(-100, 100)
		};
	
	private GA() {
	}
	
	public static Factory<Genotype<Float64Gene>> getGenotypeFactory() {
		return Genotype.valueOf(
			//Rotation
			new Float64Chromosome(-Math.PI, Math.PI),
			
			//Translation
			new Float64Chromosome(-300.0, 300.0, 2),
			
			//Shear
			new Float64Chromosome(-0.5, 0.5, 2)
		);
	}
	
	public static Point2D[] getSourcePolygon() {
		return SOURCE_POLYGON;
	}
	
	public static AffineTransform getTargetTransform() {
		final Random random = RandomRegistry.getRandom();
		final double theta = random.nextDouble()*2*Math.PI - Math.PI;
		final double tx = random.nextInt(600) - 300;
		final double ty = random.nextInt(600) - 300;
		final double shx = random.nextDouble() - 0.5;
		final double shy = random.nextDouble() - 0.5;
		
		final AffineTransform rotate = AffineTransform.getRotateInstance(theta);
		final AffineTransform translate = AffineTransform.getTranslateInstance(tx, ty);
		final AffineTransform shear = AffineTransform.getShearInstance(shx, shy);
		
		final AffineTransform transform = new AffineTransform();
		transform.concatenate(shear);
		transform.concatenate(rotate);
		transform.concatenate(translate);
		
		return transform;
	}
	
	public static Point2D[] getTargetPolygon(final AffineTransform transform) {	
		final Point2D[] target = new Point2D[SOURCE_POLYGON.length];
		try {
			for (int i = 0; i < SOURCE_POLYGON.length; ++i) {
				target[i]  = transform.inverseTransform(SOURCE_POLYGON[i], null);
			}
		} catch (Exception ignore) {
		}
		
		return target;
	}
	
	public static GeneticAlgorithm<Float64Gene, Float64> getGA(final GAFF function) {
		final GeneticAlgorithm<Float64Gene, Float64> ga = 
			new GeneticAlgorithm<>(
				GA.getGenotypeFactory(), function, new ExponentialScaler(2), Optimize.MINIMUM
			);
		ga.addAlterer(new Mutator<Float64Gene>(0.1));
		ga.setSelectors(new RouletteWheelSelector<Float64Gene, Float64>());
		ga.setPopulationSize(25);
		ga.setMaximalPhenotypeAge(30);
		ga.setOffspringFraction(0.3);
		ga.setStatisticsCalculator(new NumberStatistics.Calculator<Float64Gene, Float64>());
		
		return ga;
	}

}





