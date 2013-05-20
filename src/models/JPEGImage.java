/**
 * JPEG Image
 */

package models;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class JPEGImage extends ImageModel {
	private int originalHeight;
	private int originalWidth;

	private Map<xyAxis, Double> yValues = new TreeMap<xyAxis, Double>();
	private Map<xyAxis, Double> cbValues = new TreeMap<xyAxis, Double>();
	private Map<xyAxis, Double> crValues = new TreeMap<xyAxis, Double>();

	/**
	 * Default constructor
	 * 
	 * @return JPEGImage Class
	 */
	public JPEGImage() {

	}

	public JPEGImage(File file) {
		super(file);
	}

	public int getOriginalHeight() {
		return originalHeight;
	}
	 
	public void setOriginalHeight(int originalHeight) {
		this.originalHeight = originalHeight;
	}

	public int getOriginalWidth() {
		return originalWidth;
	}
	 
	public void setOriginalWidth(int originalWidth) {
		this.originalWidth = originalWidth;
	}


	public double getY(int x, int y) {
		return yValues.get(new xyAxis(x, y));
	}
	 
	public void setY(int x, int y, double yValue) {
		yValues.put(new xyAxis(x, y), yValue);
	}

	public double getCb(int x, int y) {
		return cbValues.get(new xyAxis(x, y));
	}
	 
	public void setCb(int x, int y, double cbValue) {
		cbValues.put(new xyAxis(x, y), cbValue);
	}

	public double getCr(int x, int y) {
		return crValues.get(new xyAxis(x, y));
	}
	 
	public void setCr(int x, int y, double crValue) {
		crValues.put(new xyAxis(x, y), crValue);
	}

	/**
	 * Homework 3 Step f-1: Resize the image and fill the extra pixels with black values({0, 0, 0})
	 */
	public void resize() {
		int[] black = new int[3];
		black[0] = 0;
		black[1] = 0;
		black[2] = 0;

		LinkedList<int[]> temp = new LinkedList<int[]>();

		for (int i = 0; i < super.width; i ++) {
			for (int j = 0; j < super.height; j ++) {
				int[] rgb = new int[3];

				super.getPixel(i, j, rgb);

				temp.add(rgb);
			}
		}

		if (super.height % 8 != 0) {
			originalHeight = super.height;
			super.height += 8 - super.height % 8;
		}
		if (super.width % 8 != 0) {
			originalWidth = super.width;
			super.width += 8 - super.width % 8;
		}

		super.img = new BufferedImage(super.width, super.height, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < originalWidth; i ++) {
			for (int j = 0; j < originalHeight; j ++) {
				int[] rgb = temp.poll();

				super.setPixel(i, j, rgb);
			}
		}

		for (int i = 0; i < super.width - originalWidth; i ++) {
			for (int j = 0; j < super.height - originalHeight; j ++) {
				super.setPixel(originalWidth + i, originalHeight + j, black);
			}
		}
	}

	/**
	 * Homework 3 Step i-4: Reconstruct the original image by removing extra pixels
	 */
	public void deResize() {
		super.width = originalWidth;
		super.height = originalHeight;

		ArrayDeque<int[]> temp = new ArrayDeque<int[]>();

		for (int i = 0; i < super.width; i ++) {
			for (int j = 0; j < super.height; j ++) {
				int[] rgb = new int[3];

				super.getPixel(i, j, rgb);

				temp.add(rgb);
			}
		}

		super.img = new BufferedImage(super.width, super.height, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < super.width; i ++) {
			for (int j = 0; j < super.height; j ++) {
				int[] rgb = temp.poll();

				super.setPixel(i, j, rgb);
			}
		}
	}

	/**
	 * Color transformation and subsampling
	 */
	public void colorTransAndSubsample() {
		colorTransform();
		subsamplingCbCr();
	}

	/**
	 * Homework 3 step f-2: Color space transformation
	 */
	public void colorTransform() {
		/** Transform rgb into YCbCr */
		for (int i = 0; i < super.getW(); i ++) {
			for (int j = 0; j < super.getH(); j ++) {
				int[] rgb = new int[3];
				
				super.getPixel(i, j, rgb);

				double[] yCbCr = new double[3];

				yCbCr[0] = 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2];
				yCbCr[1] = -0.1687 * rgb[0] - 0.3313 * rgb[1] + 0.5 * rgb[2] - 0.5;
				yCbCr[2] = 0.5 * rgb[0] - 0.4187 * rgb[1] - 0.0813 * rgb[2] - 0.5;

				yValues.put(new xyAxis(i, j), yCbCr[0]);
				cbValues.put(new xyAxis(i, j), yCbCr[1]);
				crValues.put(new xyAxis(i, j), yCbCr[2]);
			}
		}
	}

	/**
	 * Homework 3 step f-2: Subsampling 4:2:0
	 */
	public void subsamplingCbCr() {
		for (int x = 0; x < width; x += 2) {
			for (int y = 0; y < height; y += 2) {
				double averageCr = 0.0;
				double averageCb = 0.0;

				for (int xi = x; xi < (x + 2); xi ++) {
					for (int yi = y; yi < (y + 2); yi ++) {
						if (xi >= 0 && xi < width && yi >= 0 && yi < height) {
							double cr = getCr(xi, yi);
							double cb = getCb(xi, yi);

							averageCr += cr;
							averageCb += cb;
						}
					}
				}

				averageCr = averageCr/(2*2);
				averageCb = averageCb/(2*2);
				
				crValues.put(new xyAxis(x/2, y/2), averageCr);
				cbValues.put(new xyAxis(x/2, y/2), averageCb);
			}
		}
	}

	/**
	 * Homework 3 step i-3: Inverse color transformation and supersampling
	 */
	public void invColorTransAndSubSample() {
		superSamplingCbCr();
		invColorTransform();
	}

	/**
	 * Homework 3 step i-3: Super sampling against 4:2:0
	 */
	public void superSamplingCbCr() {
		Map<xyAxis, Double> tempAverageCr = new TreeMap<xyAxis, Double>();
		Map<xyAxis, Double> tempAverageCb = new TreeMap<xyAxis, Double>();

		for (int x = 0; x < width; x += 2) {
			for (int y = 0; y < height; y += 2) {
				double averageCr = crValues.get(new xyAxis(x/2, y/2));
				double averageCb = cbValues.get(new xyAxis(x/2, y/2));

				for (int xi = x; xi < (x + 2); xi ++) {
					for (int yi = y; yi < (y + 2); yi ++) {
						if (xi >= 0 && xi < width && yi >= 0 && yi < height) {
							tempAverageCr.put(new xyAxis(xi, yi), averageCr);
							tempAverageCb.put(new xyAxis(xi, yi), averageCb);
						}
					}
				}
			}
		}

		this.crValues = tempAverageCr;
		this.cbValues = tempAverageCb;
	}

	/**
	 * Inverse color Transformation
	 */
	public void invColorTransform() {
		// add 128 to y value and 0.5 to cr, cb value
		for (int i = 0; i < width; i ++) {
			for (int j = 0; j < height; j ++) {
				double y = yValues.get(new xyAxis(i, j)) + 128;
				double cr = crValues.get(new xyAxis(i, j)) + 0.5;
				double cb = cbValues.get(new xyAxis(i, j)) + 0.5;

				yValues.put(new xyAxis(i,j), y);
				crValues.put(new xyAxis(i,j), cr);
				cbValues.put(new xyAxis(i,j), cb);

				int[] rgb = new int[3];

				rgb[0] = (int) Math.round(1.0 * y + 0 * cb + 1.402 * cr);
				rgb[1] = (int) Math.round(1.0 * y - 0.3441 * cb - 0.7141 * cr);
				rgb[2] = (int) Math.round(1.0 * y + 1.772 * cb + 0 * cr);

				for (int k = 0; k < 3; k ++) {
					if (rgb[k] < 0) {
						rgb[k] = 0;
					} else if (rgb[k] > 255) {
						rgb[k] = 255;
					}
				}

				setPixel(i, j, rgb);
			}
		}		
	}
}