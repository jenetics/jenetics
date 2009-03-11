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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
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
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutation;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.util.Converter;
import org.jenetics.util.Factory;
import org.jenetics.util.Probability;
import org.jscience.mathematics.number.Float64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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
	
	void setOffspringFractionRangeModel(final OffspringFractionRangeModel model) {
		_offspringFractionSlider.setModel(model);
	}
	
	void setSourcePolygon(final Point2D[] polygon) {
		((DrawPanel)_drawPanel).setSourcePolygon(polygon);
	}
	
	void setTargetPolygon(final Point2D[] polygon) {
		((DrawPanel)_drawPanel).setTargetPolygon(polygon);
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
	
	void setGABestTransform(final AffineTransform transform) {
		if (SwingUtilities.isEventDispatchThread()) {
			((DrawPanel)_drawPanel).setAlltimeBestTransform(transform);
			((TransformPanel)_gaBestPanel).setAffineTransform(transform);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					((DrawPanel)_drawPanel).setAlltimeBestTransform(transform);
					((TransformPanel)_gaBestPanel).setAffineTransform(transform);
				}
			});
		}
	}
	
	void setPopulationBestTransform(final AffineTransform transform) {
		if (SwingUtilities.isEventDispatchThread()) {
			((DrawPanel)_drawPanel).setPopulationBestTransform(transform);
			((TransformPanel)_populationBestPanel).setAffineTransform(transform);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					((DrawPanel)_drawPanel).setPopulationBestTransform(transform);
					((TransformPanel)_populationBestPanel).setAffineTransform(transform);
				}
			});
		}
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        _drawPanel = new DrawPanel();
        _startButton = new javax.swing.JButton();
        _stopButton = new javax.swing.JButton();
        _initButton = new javax.swing.JButton();
        _pauseButton = new javax.swing.JButton();
        _stepButton = new javax.swing.JButton();
        _generationLabel = new javax.swing.JLabel();
        _generationTextField = new javax.swing.JFormattedTextField();
        _populationBestPanel = new TransformPanel();
        _gaBestPanel = new TransformPanel();
        _populationSizeLabel = new javax.swing.JLabel();
        _populationSizeSpinner = new javax.swing.JSpinner();
        _maxPTAgeLabel = new javax.swing.JLabel();
        _maxPTAgeSpinner = new javax.swing.JSpinner();
        _offspringFractionSlider = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        _drawPanel.setBackground(java.awt.Color.white);
        _drawPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout _drawPanelLayout = new javax.swing.GroupLayout(_drawPanel);
        _drawPanel.setLayout(_drawPanelLayout);
        _drawPanelLayout.setHorizontalGroup(
            _drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 497, Short.MAX_VALUE)
        );
        _drawPanelLayout.setVerticalGroup(
            _drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 354, Short.MAX_VALUE)
        );

        _startButton.setText("Start");

        _stopButton.setText("Stop");

        _initButton.setText("Init");

        _pauseButton.setText("Pause");

        _stepButton.setText("Step");

        _generationLabel.setText("Generation:");

        _generationTextField.setPreferredSize(new java.awt.Dimension(100, 24));

        _populationBestPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Population best"));

        _gaBestPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("GA best"));

        _populationSizeLabel.setText("Population size:");

        _maxPTAgeLabel.setText("Maximal PT age:");

        _offspringFractionSlider.setMajorTickSpacing(10);
        _offspringFractionSlider.setMaximum(90);
        _offspringFractionSlider.setMinimum(10);
        _offspringFractionSlider.setMinorTickSpacing(5);
        _offspringFractionSlider.setPaintLabels(true);
        _offspringFractionSlider.setPaintTicks(true);
        _offspringFractionSlider.setValue(30);
        _offspringFractionSlider.setBorder(javax.swing.BorderFactory.createTitledBorder("Offspring fraction"));
        _offspringFractionSlider.setName(""); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(_generationLabel)
                        .addGap(22, 22, 22)
                        .addComponent(_generationTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
                    .addComponent(_gaBestPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                    .addComponent(_populationBestPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                    .addComponent(_drawPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(_populationSizeLabel)
                            .addComponent(_maxPTAgeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(_maxPTAgeSpinner)
                            .addComponent(_pauseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_stepButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_initButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(_populationSizeSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)))
                    .addComponent(_offspringFractionSlider, 0, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(_populationSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(_populationSizeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(_maxPTAgeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(_maxPTAgeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(_offspringFractionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(_drawPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(_populationBestPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(_initButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(_startButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(_stopButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(_stepButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(_pauseButton))
                    .addComponent(_gaBestPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_generationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_generationLabel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	/**
	 * @param args
	 *            the command line arguments
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
    private javax.swing.JPanel _gaBestPanel;
    private javax.swing.JLabel _generationLabel;
    private javax.swing.JFormattedTextField _generationTextField;
    private javax.swing.JButton _initButton;
    private javax.swing.JLabel _maxPTAgeLabel;
    private javax.swing.JSpinner _maxPTAgeSpinner;
    private javax.swing.JSlider _offspringFractionSlider;
    private javax.swing.JButton _pauseButton;
    private javax.swing.JPanel _populationBestPanel;
    private javax.swing.JLabel _populationSizeLabel;
    private javax.swing.JSpinner _populationSizeSpinner;
    private javax.swing.JButton _startButton;
    private javax.swing.JButton _stepButton;
    private javax.swing.JButton _stopButton;
    // End of variables declaration//GEN-END:variables

}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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
	
	private GeneticAlgorithm<DoubleGene, Float64> _ga;
	private GA.Function _function;
	private Point2D[] _source;
	private Point2D[] _target;
	private Stepable _stepable;
	private Thread _thread;
	
	private static final long MIN_REPAINT_TIME = 50;
	private long _lastRepaintTime = 0;
	private Phenotype<DoubleGene, Float64> _populationBestPhenotype;
	private Phenotype<DoubleGene, Float64> _gaBestPhenotype;
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
		
		init();
	}
	
	void init() {
		_source = GA.getSourcePolygon();
		_target = GA.getTargetPolygon();
		_function = new GA.Function(_source, _target);
		
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
		_thread.start();
		
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
		_thread.interrupt();
		
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
			_ga.getLock().lock();
			_ga.setPopulationSize(size);
			_ga.getLock().unlock();
		}
	}
	
	void setMaximalPhenotypeAge(final int age) {
		if (_ga != null) {
			_ga.getLock().lock();
			_ga.setMaximalPhenotypeAge(age);
			_ga.getLock().unlock();
		}
	}
	
	void setOffspringFraction(final Probability fraction) {
		if (_ga != null) {
			_ga.getLock().lock();
			_ga.setOffspringFraction(fraction);
			_ga.getLock().unlock();
		}
	}

	@Override
	public void stepped(EventObject event) {
		Phenotype<DoubleGene, Float64> populationBest = _ga.getStatistics().getBestPhenotype();
		Phenotype<DoubleGene, Float64> gaBest = _ga.getBestPhenotype();
		int generation = _ga.getGeneration();
		
		if (_populationBestPhenotype == null || 
			_populationBestPhenotype.compareTo(populationBest) < 0) 
		{
			_populationBestPhenotype = populationBest;
			_gaBestPhenotype = gaBest;
			_generation = generation;
		}
		
		//Prevent from extensive repainting.
		final long time = System.currentTimeMillis();
		if (time - _lastRepaintTime > MIN_REPAINT_TIME) {
			_geometry.setPopulationBestTransform(
					_function.convert(_populationBestPhenotype.getGenotype())
				);
			_geometry.setGABestTransform(
					_function.convert(_gaBestPhenotype.getGenotype())
				);
			_geometry.repaint();
			_geometry.setGeneration(_generation);
			
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
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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

class OffspringFractionRangeModel extends DefaultBoundedRangeModel 
	implements ChangeListener 
{

	private static final long serialVersionUID = 1L;

	private final GeometryController _controller;
	
	public OffspringFractionRangeModel(final GeometryController controller) {
		setMinimum(10);
		setMaximum(90);
		setValue(30);
		_controller = controller;
		
		addChangeListener(this);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		_controller.setOffspringFraction(Probability.valueOf(getValue()/100.0));
	}
	
}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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
    		_m20.setValue(0);    _m21.setValue(0);    _m22.setValue(1);
    		
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
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
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
		new AtomicReference<AffineTransform>();
	
	private final AtomicReference<AffineTransform> _alltimeBestTransform =
		new AtomicReference<AffineTransform>();
	
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
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
 */
class Stepable implements Runnable {
	private final Lock _lock = new ReentrantLock();
	private final Condition _run = _lock.newCondition();

	private final List<StepListener> _listeners = new CopyOnWriteArrayList<StepListener>();
	
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
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
 */
interface StepListener extends EventListener {
	
	public void stepped(EventObject event);
	
	public void finished(EventObject event);
	
}



/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Geometry.java,v 1.4 2009-03-11 23:03:40 fwilhelm Exp $
 */
class GA {
	
	static class Function 
		implements FitnessFunction<DoubleGene, Float64>, 
					Converter<Genotype<DoubleGene>, AffineTransform> 
	{
		private static final long serialVersionUID = 1L;
	
		private final Point2D[] _source;
		private final Point2D[] _target;
	
		public Function() {
			this(null, null);
		}
		
		public Function(final Point2D[] source, final Point2D[] target) {
			_source = source != null ? source.clone() : source;
			_target = target != null ? target.clone() : target;
		}
	
		@Override
		public Float64 evaluate(final Genotype<DoubleGene> genotype) {
			final AffineTransform transform = convert(genotype);
	
			double error = 0;
			Point2D point = new Point2D.Double();
			for (int i = 0; i < _source.length; ++i) {
				point = transform.transform(_target[i], point);
	
				error += _source[i].distance(point);
			}
	
			return Float64.valueOf(100000 - error);
		}
	
		@Override
		public AffineTransform convert(final Genotype<DoubleGene> genotype) {
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
	
	public static Factory<Genotype<DoubleGene>> getGenotypeFactory() {
		return Genotype.valueOf(
			//Rotation
			new DoubleChromosome(DoubleGene.valueOf(-Math.PI, Math.PI)),
			
			//Translation
			new DoubleChromosome(DoubleGene.valueOf(-300, 300), DoubleGene.valueOf(-300, 300)),
			
			//Shear
			new DoubleChromosome(DoubleGene.valueOf(-0.5, 0.5), DoubleGene.valueOf(-0.5, 0.5))
		);
	}
	
	public static Point2D[] getSourcePolygon() {
		return SOURCE_POLYGON;
	}
	
	public static Point2D[] getTargetPolygon() {
		final Random random = new Random(System.currentTimeMillis());
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
		
		final Point2D[] target = new Point2D[SOURCE_POLYGON.length];
		try {
			for (int i = 0; i < SOURCE_POLYGON.length; ++i) {
				target[i]  = transform.transform(SOURCE_POLYGON[i], null);
			}
		} catch (Exception ignore) {
		}
		
		return target;
	}
	
	public static GeneticAlgorithm<DoubleGene, Float64> getGA(final Function function) {
		final GeneticAlgorithm<DoubleGene, Float64> ga = 
			new GeneticAlgorithm<DoubleGene, Float64>(
				GA.getGenotypeFactory(), function
			);
		ga.addAlterer(new Mutation<DoubleGene>(Probability.valueOf(0.1)));
		ga.addAlterer(new MeanAlterer<DoubleGene>(Probability.valueOf(0.5)));
		ga.setSelectors(new RouletteWheelSelector<DoubleGene, Float64>());
		ga.setPopulationSize(25);
		ga.setMaximalPhenotypeAge(30);
		ga.setOffspringFraction(Probability.valueOf(0.3));
		
		return ga;
	}

}





