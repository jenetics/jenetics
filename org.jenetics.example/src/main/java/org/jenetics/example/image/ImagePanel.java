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

import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Draws an given {@code BufferedImage}.
 */
final class ImagePanel extends JPanel {

	private BufferedImage _image;

	ImagePanel() {
	}

	public void setImage(final BufferedImage image) {
		_image = requireNonNull(image);
		repaint();
	}

	public BufferedImage getImage() {
		return _image;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (_image != null) {
			g.drawImage(_image, 0, 0, width(), height(), null);
		}
	}

	private double scaleFactor() {
		final double sw = getWidth()/(double)_image.getWidth();
		final double sh = getHeight()/(double)_image.getHeight();
		return min(sw, sh);
	}

	private int width() {
		return (int)(_image.getWidth()*scaleFactor());
	}

	private int height() {
		return (int)(_image.getHeight()*scaleFactor());
	}

}
