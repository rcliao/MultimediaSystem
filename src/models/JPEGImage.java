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

	private Map<xyAxis, Double> yValues = new HashMap<xyAxis, Double>();
	private Map<xyAxis, Double> cbValues = new HashMap<xyAxis, Double>();
	private Map<xyAxis, Double> crValues = new HashMap<xyAxis, Double>();

	private Map<xyAxis, Map<xyAxis, Double>> yblocks = new HashMap<xyAxis, Map<xyAxis, Double>>();
	private Map<xyAxis, Map<xyAxis, Double>> cbblocks = new HashMap<xyAxis, Map<xyAxis, Double>>();
	private Map<xyAxis, Map<xyAxis, Double>> crblocks = new HashMap<xyAxis, Map<xyAxis, Double>>();

	/**
	 * Default constructor
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

	public Map<xyAxis, Double> getYValues() {
		return yValues;
	}
	 
	public void setYValues(Map<xyAxis, Double> yValues) {
		this.yValues = yValues;
	}

	public Map<xyAxis, Double> getCrValues() {
		return crValues;
	}
	 
	public void setCrValues(Map<xyAxis, Double> crValues) {
		this.crValues = crValues;
	}

	public Map<xyAxis, Double> getCbValues() {
		return cbValues;
	}
	 
	public void setCbValues(Map<xyAxis, Double> cbValues) {
		this.cbValues = cbValues;
	}

	public Map<xyAxis, Map<xyAxis, Double>> getYBlocks() {
		return yblocks;
	}

	public Map<xyAxis, Double> getYBlock(int x, int y) {
		return yblocks.get(new xyAxis(x, y));
	}
	 
	public void setYBlocks(Map<xyAxis, Map<xyAxis, Double>> yblocks) {
		this.yblocks = yblocks;
	}

	public Map<xyAxis, Map<xyAxis, Double>> getCrBlocks() {
		return crblocks;
	}

	public Map<xyAxis, Double> getCrBlock(int x, int y) {
		return crblocks.get(new xyAxis(x, y));
	}
	 
	public void setCrBlocks(Map<xyAxis, Map<xyAxis, Double>> crblocks) {
		this.crblocks = crblocks;
	}

	public Map<xyAxis, Map<xyAxis, Double>> getCbBlocks() {
		return cbblocks;
	}

	public Map<xyAxis, Double> getCbBlock(int x, int y) {
		return cbblocks.get(new xyAxis(x, y));
	}
	 
	public void setCbBlocks(Map<xyAxis, Map<xyAxis, Double>> cbblocks) {
		this.cbblocks = cbblocks;
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

		originalHeight = super.height;
		originalWidth = super.width;

		if (super.height % 8 != 0) {
			super.height += 8 - super.height % 8;
		}
		if (super.width % 8 != 0) {
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
		Map<xyAxis, Double> newCrValues = new HashMap<xyAxis, Double>();
		Map<xyAxis, Double> newCbValues = new HashMap<xyAxis, Double>();

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
				
				newCrValues.put(new xyAxis(x/2, y/2), averageCr);
				newCbValues.put(new xyAxis(x/2, y/2), averageCb);
			}
		}

		// if Cr,Cb is not dividable by 8, pad it with black pixel

		if ((width/2)%8 != 0) {
			for (int i = width/2; i < width/2+4; i ++) {
				for (int j = 0; j < height/2; j ++) {
					newCrValues.put(new xyAxis(i, j), 0.0);
					newCbValues.put(new xyAxis(i, j), 0.0);
				}
			}
		}

		if ((height/2)%8 != 0) {
			for (int i = 0; i < width/2; i ++) {
				for (int j = height/2; j < height/2+4; j ++) {
					newCrValues.put(new xyAxis(i, j), 0.0);
					newCbValues.put(new xyAxis(i, j), 0.0);
				}
			}
		}

		if ((height/2) %8 != 0 && (width/2)%8 != 0) {
			for (int i = width/2; i < width/2+4; i ++) {
				for (int j = height/2; j < height/2+4; j ++) {
					newCrValues.put(new xyAxis(i, j), 0.0);
					newCbValues.put(new xyAxis(i, j), 0.0);
				}
			}
		}

		crValues = newCrValues;
		cbValues = newCbValues;
	}

	/**
	 * Homework 3 step i-3: Inverse color transformation and supersampling
	 */
	public void invColorTransAndSuperSample() {
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
				double y = yValues.get(new xyAxis(i, j));
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

	/**
	 * Homework 3 step f-3: DCT Encoding
	 */
	public void dctEncoding() {
		divideBlocks();
		dctTransform();
	}

	/**
	 * Homework 3 step f-3: Divide image into 8*8 blocks
	 */
	public void divideBlocks() {
		for (int y = 0; y < height; y += 8) {
			for (int x = 0; x < width; x += 8) {
				Map<xyAxis, Double> yblock = new HashMap<xyAxis, Double>();
				Map<xyAxis, Double> crblock = new HashMap<xyAxis, Double>();
				Map<xyAxis, Double> cbblock = new HashMap<xyAxis, Double>();

				for (int yi = y; yi < (y + 8); yi ++) {
					for (int xi = x; xi < (x + 8); xi ++) {
						if ((width/2)%8 == 0 && (height/2)%8 == 0) {
							if (xi < width/2 && yi < height/2) {
								crblock.put(new xyAxis(xi - x, yi - y), crValues.get(new xyAxis(xi, yi)));
								cbblock.put(new xyAxis(xi - x, yi - y), cbValues.get(new xyAxis(xi, yi)));
							}
						} else if ((width/2)%8 != 0 && (height/2)%8 != 0) {
							if (xi < width/2+4 && yi < height/2+4) {
								crblock.put(new xyAxis(xi - x, yi - y), crValues.get(new xyAxis(xi, yi)));
								cbblock.put(new xyAxis(xi - x, yi - y), cbValues.get(new xyAxis(xi, yi)));
							}
						} else if ((width/2)%8 != 0) {
							if (xi < width/2+4 && yi < height/2) {
								crblock.put(new xyAxis(xi - x, yi - y), crValues.get(new xyAxis(xi, yi)));
								cbblock.put(new xyAxis(xi - x, yi - y), cbValues.get(new xyAxis(xi, yi)));
							}
						} else if ((height/2)%8 != 0) {
							if (xi < width/2 && yi < height/2+4) {
								crblock.put(new xyAxis(xi - x, yi - y), crValues.get(new xyAxis(xi, yi)));
								cbblock.put(new xyAxis(xi - x, yi - y), cbValues.get(new xyAxis(xi, yi)));
							}
						}
						if (xi < width && yi < height) {
							yblock.put(new xyAxis(xi - x, yi - y), yValues.get(new xyAxis(xi, yi)));
						}
					}
				}

				if (!crblock.isEmpty())
					crblocks.put(new xyAxis(x/8, y/8), crblock);
				if (!cbblock.isEmpty())
					cbblocks.put(new xyAxis(x/8, y/8), cbblock);

				yblocks.put(new xyAxis(x/8, y/8), yblock);
			}
		}
	}

	/**
	 * Homework 3 step f-3: DCT Transform
	 */
	public void dctTransform() {
		// calculating DCT transformation for y value
		for (int y = 0; y < height; y += 8) {
			for (int x = 0; x < width; x += 8) {
				Map<xyAxis, Double> yblock = yblocks.get(new xyAxis(x/8, y/8));
				Map<xyAxis, Double> newyBlock = new HashMap<xyAxis, Double>();

				for (int v = 0; v < 8; v ++) {
					for (int u = 0; u < 8; u ++) {
						double cu = 1.0;
						double cv = 1.0;

						if (u == 0)
							cu = 1 / Math.sqrt(2);
						if (v == 0)
							cv = 1 / Math.sqrt(2);

						double sum = 0.0;

						for (int xi = 0; xi < 8; xi ++) {
							for (int yi = 0; yi < 8; yi ++) {
								double yValue = yblock.get(new xyAxis(xi, yi));

								double h1 = Math.cos((2 * xi + 1) * u * Math.PI / 16);
								double h2 = Math.cos((2 * yi + 1) * v * Math.PI / 16);

								sum += yValue * h1 * h2;
							}
						}

						sum = sum * cu * cv / 4;

						newyBlock.put(new xyAxis(u, v), sum);
					}
				}

				yblocks.put(new xyAxis(x/8, y/8), newyBlock);
			}
		}

		// calculating cb,cr value for each block
		for (int y = 0; y < height/2 + height/2%8; y += 8) {
			for (int x = 0; x < width/2 + width/2%8; x += 8) {
				Map<xyAxis, Double> cbblock = cbblocks.get(new xyAxis(x/8, y/8));
				Map<xyAxis, Double> crblock = crblocks.get(new xyAxis(x/8, y/8));
				Map<xyAxis, Double> newcbBlock = new HashMap<xyAxis, Double>();
				Map<xyAxis, Double> newcrBlock = new HashMap<xyAxis, Double>();

				for (int v = 0; v < 8; v ++) {
					for (int u = 0; u < 8; u ++) {
						double cu = 1.0;
						double cv = 1.0;

						if (u == 0)
							cu = 1 / Math.sqrt(2);
						if (v == 0)
							cv = 1 / Math.sqrt(2);

						double sumcb = 0.0;
						double sumcr = 0.0;

						for (int xi = 0; xi < 8; xi ++) {
							for (int yi = 0; yi < 8; yi ++) {
								double cbValue = cbblock.get(new xyAxis(xi, yi));
								double crValue = crblock.get(new xyAxis(xi, yi));

								double h1 = Math.cos((2 * xi + 1) * u * Math.PI / 16);
								double h2 = Math.cos((2 * yi + 1) * v * Math.PI / 16);

								sumcb += cbValue * h1 * h2;
								sumcr += crValue * h1 * h2;
							}
						}

						sumcb = sumcb * cu * cv / 4;
						sumcr = sumcr * cu * cv / 4;

						newcbBlock.put(new xyAxis(u, v), sumcb);
						newcrBlock.put(new xyAxis(u, v), sumcr);
					}
				}

				cbblocks.put(new xyAxis(x/8, y/8), newcbBlock);
				crblocks.put(new xyAxis(x/8, y/8), newcrBlock);
			}
		}
	}

	/**
	 * Homework 3 step i-2: Inverse DCT Transform
	 */
	public void dctDecoding() {
		invDctTransform();
		combineBlocks();
	}

	/**
	 * Inverse DCT Formula
	 */
	public void invDctTransform() {
		// calculating DCT transformation for y value
		for (int y = 0; y < height; y += 8) {
			for (int x = 0; x < width; x += 8) {
				Map<xyAxis, Double> yblock = yblocks.get(new xyAxis(x/8, y/8));
				Map<xyAxis, Double> newyBlock = new HashMap<xyAxis, Double>();

				for (int xi = 0; xi < 8; xi ++) {
					for (int yi = 0; yi < 8; yi ++) {

						double sum = 0.0;

						for (int v = 0; v < 8; v ++) {
							for (int u = 0; u < 8; u ++) {
								double cu = 1.0;
								double cv = 1.0;

								if (u == 0)
									cu = 1 / Math.sqrt(2);
								if (v == 0)
									cv = 1 / Math.sqrt(2);

								double yValue = yblock.get(new xyAxis(u, v));

								double h1 = Math.cos((2 * xi + 1) * u * Math.PI / 16);
								double h2 = Math.cos((2 * yi + 1) * v * Math.PI / 16);

								sum += cu * cv * yValue * h1 * h2;
							}
						}

						sum = sum / 4;

						newyBlock.put(new xyAxis(xi, yi), sum);
					}
				}

				yblocks.put(new xyAxis(x/8, y/8), newyBlock);
			}
		}

		// calculating cb,cr value for each block
		for (int y = 0; y < height/2+height/2%8; y += 8) {
			for (int x = 0; x < width/2+width/2%8; x += 8) {
				Map<xyAxis, Double> cbblock = cbblocks.get(new xyAxis(x/8, y/8));
				Map<xyAxis, Double> crblock = crblocks.get(new xyAxis(x/8, y/8));
				Map<xyAxis, Double> newcbBlock = new HashMap<xyAxis, Double>();
				Map<xyAxis, Double> newcrBlock = new HashMap<xyAxis, Double>();

				for (int xi = 0; xi < 8; xi ++) {
					for (int yi = 0; yi < 8; yi ++) {

						double sumcb = 0.0;
						double sumcr = 0.0;

						for (int v = 0; v < 8; v ++) {
							for (int u = 0; u < 8; u ++) {
								double cu = 1.0;
								double cv = 1.0;

								if (u == 0)
									cu = 1 / Math.sqrt(2);
								if (v == 0)
									cv = 1 / Math.sqrt(2);

								double cbValue = cbblock.get(new xyAxis(u, v));
								double crValue = crblock.get(new xyAxis(u, v));

								double h1 = Math.cos((2 * xi + 1) * u * Math.PI / 16);
								double h2 = Math.cos((2 * yi + 1) * v * Math.PI / 16);

								sumcb += cu * cv * cbValue * h1 * h2;
								sumcr += cu * cv * crValue * h1 * h2;
							}
						}

						sumcb = sumcb / 4;
						sumcr = sumcr / 4;

						newcbBlock.put(new xyAxis(xi, yi), sumcb);
						newcrBlock.put(new xyAxis(xi, yi), sumcr);
					}
				}

				cbblocks.put(new xyAxis(x/8, y/8), newcbBlock);
				crblocks.put(new xyAxis(x/8, y/8), newcrBlock);
			}
		}
	}

	/**
	 * Combime blocks into one y, cb, cr values
	 */
	public void combineBlocks() {
		for (int y = 0; y < height; y += 8) {
			for (int x = 0; x < width; x += 8) {
				Map<xyAxis, Double> cbblock = cbblocks.get(new xyAxis(x/8, y/8));
				Map<xyAxis, Double> crblock = crblocks.get(new xyAxis(x/8, y/8));

				Map<xyAxis, Double> yblock = yblocks.get(new xyAxis(x/8, y/8));

				for (int yi = y; yi < (y + 8); yi ++) {
					for (int xi = x; xi < (x + 8); xi ++) {
						if (xi < width/2+width/2%8 && yi < height/2+height/2%8) {
							crValues.put(new xyAxis(xi, yi), crblock.get(new xyAxis(xi - x, yi - y)));
							cbValues.put(new xyAxis(xi, yi), cbblock.get(new xyAxis(xi - x, yi - y)));
						}
						if (xi < width && yi < height) {
							yValues.put(new xyAxis(xi, yi), yblock.get(new xyAxis(xi - x, yi - y)));
						}
					}
				}
			}
		}
	}
}