/**
 * Motion Dection Class
 */

package models;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class Motion {
	private File target;

	private JPEGImage sourceImage;
	private JPEGImage targetImage;
	private JPEGImage image5th;

	private LinkedList<MacroBlock> blocks = new LinkedList<MacroBlock>();

	private LinkedList<MacroBlock> option1Blocks = new LinkedList<MacroBlock>();
	private LinkedList<MacroBlock> option2Blocks = new LinkedList<MacroBlock>();

	private LinkedList<double[]> mvs = new LinkedList<double[]>();

	private JPEGImage errorFrame;
	private JPEGImage option1Frame;
	private JPEGImage option2Frame;

	private int max;
	private int min;

	private double step;

	public Motion() {

	}

	public Motion(File source, File target) {
		this.target = target;
		sourceImage = new JPEGImage(source);
		targetImage = new JPEGImage(target);
	}

	public JPEGImage getSourceImage() {
		return sourceImage;
	}
	 
	public void setSourceImage(JPEGImage sourceImage) {
		this.sourceImage = sourceImage;
	}

	public JPEGImage getTargetImage() {
		return targetImage;
	}
	 
	public void setTargetImage(JPEGImage targetImage) {
		this.targetImage = targetImage;
	}

	public LinkedList<MacroBlock> getBlocks() {
		return blocks;
	}
	 
	public void setBlocks(LinkedList<MacroBlock> blocks) {
		this.blocks = blocks;
	}

	public JPEGImage getErrorFrame() {
		return errorFrame;
	}
	 
	public void setErrorFrame(JPEGImage errorFrame) {
		this.errorFrame = errorFrame;
	}

	public LinkedList<double[]> getMVs() {
		return mvs;
	}
	 
	public void setMVs(LinkedList<double[]> mvs) {
		this.mvs = mvs;
	}

	public JPEGImage getOption1Frame() {
		return option1Frame;
	}
	 
	public void setOption1Frame(JPEGImage option1Frame) {
		this.option1Frame = option1Frame;
	}

	public JPEGImage getOption2Frame() {
		return option2Frame;
	}
	 
	public void setOption2Frame(JPEGImage option2Frame) {
		this.option2Frame = option2Frame;
	}

	/**
	 * Homework 4 taks 1 - Block-based Motion Compensation
	 */
	public void findRoutine() {
		divideMacroBlocks();

		min = 255;
		max = -255;

		for (MacroBlock block : blocks) {
			findMatchBlock(block);
			calculateResidual(block);
		}

		calculateError();
		
		displayErrorImage();
	}

	/**
	 * divide image into 16*16 blocks
	 */
	public void divideMacroBlocks() {
		// for loop getting values
		for (double y = 0; y < targetImage.getH(); y += 16) {
			for (double x = 0; x < targetImage.getW(); x += 16) {
				// divide the block and put it into a list
				MacroBlock block = buildBlock(x, y, "target");

				blocks.add(block);
			}
		}
	}

	/**
	 * Build block according to the x-axis and y-axis with which image to get
	 * @param  x      x-axis
	 * @param  y      y-axis
	 * @param  source which image to read
	 * @return        block information
	 */
	public MacroBlock buildBlock(double x, double y, String source) {
		// creating a new block
		MacroBlock block = new MacroBlock(x, y);

		if (x % 1 == 0 && y % 1 == 0) {
			for (int j = 0; j < 16; j ++) {
				for (int i = 0; i < 16; i ++) {
					// check x+i and y+j is within range
					if (x+i < targetImage.getW() && y+j < targetImage.getH()) {
						int[] rgb = new int[3];

						if (source == "target")
							targetImage.getPixel((int)x+i, (int)y+j, rgb);
						else if (source == "reference")
							sourceImage.getPixel((int)x+i, (int)y+j, rgb);
						else if (source == "5th")
							image5th.getPixel((int)x+i, (int)y+j, rgb);

						int grey = (int) Math.round(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);

						block.greyValues[i][j] = grey;

						block.values.put(new xyAxis(i, j), rgb);
					}
				}
			}
		} else {
			if (x % 1 == 0 && y % 1 != 0) {
				for (int j = 0; j < 16; j ++) {
					for (int i = 0; i < 16; i ++) {
						// check x+i and y+j is within range
						if (x+i < targetImage.getW() && y+j+0.5 < targetImage.getH()) {
							int[] rgb = new int[3];
							int[] rgb2 = new int[3];

							if (source == "target") {
								targetImage.getPixel((int)x+i, (int) Math.round(y+j+0.5), rgb);
								targetImage.getPixel((int)x+i, (int) Math.round(y+j-0.5), rgb2);
							} else if (source == "reference") {
								sourceImage.getPixel((int)x+i, (int) Math.round(y+j+0.5), rgb);
								sourceImage.getPixel((int)x+i, (int) Math.round(y+j-0.5), rgb2);
							} else if (source == "5th") {
								image5th.getPixel((int)x+i, (int) Math.round(y+j+0.5), rgb);
								image5th.getPixel((int)x+i, (int) Math.round(y+j-0.5), rgb2);
							}

							int grey = (int) Math.round(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
							int grey2 = (int) Math.round(0.299 * rgb2[0] + 0.587 * rgb2[1] + 0.114 * rgb2[2]);

							int greyAvg = (int) Math.round((grey + grey2) / 2);

							int[] rgbAvg = new int[3];

							rgbAvg[0] = (int) Math.round((rgb[0] + rgb2[0]) / 2);
							rgbAvg[1] = (int) Math.round((rgb[1] + rgb2[1]) / 2);
							rgbAvg[2] = (int) Math.round((rgb[2] + rgb2[2]) / 2);

							block.greyValues[i][j] = greyAvg;

							block.values.put(new xyAxis(i, j), rgbAvg);
						} else if (x+i < targetImage.getW() && y+j-0.5 < targetImage.getH()) {
							int[] rgb2 = new int[3];

							if (source == "target") {
								targetImage.getPixel((int)x+i, (int) Math.round(y+j-0.5), rgb2);
							} else if (source == "reference") {
								sourceImage.getPixel((int)x+i, (int) Math.round(y+j-0.5), rgb2);
							} else if (source == "5th") {
								image5th.getPixel((int)x+i, (int) Math.round(y+j-0.5), rgb2);
							}

							int grey2 = (int) Math.round(0.299 * rgb2[0] + 0.587 * rgb2[1] + 0.114 * rgb2[2]);

							block.greyValues[i][j] = grey2;

							block.values.put(new xyAxis(i, j), rgb2);
						}
					}
				}
			} else if (y % 1 == 0 && x % 1 != 0) {
				for (int j = 0; j < 16; j ++) {
					for (int i = 0; i < 16; i ++) {
						// check x+i and y+j is within range
						if (x+i+0.5 < targetImage.getW() && y+j < targetImage.getH()) {
							int[] rgb = new int[3];
							int[] rgb2 = new int[3];

							if (source == "target") {
								targetImage.getPixel((int) Math.round(x+i+0.5), (int)y+j, rgb);
								targetImage.getPixel((int) Math.round(x+i-0.5), (int)y+j, rgb2);
							} else if (source == "reference") {
								sourceImage.getPixel((int) Math.round(x+i+0.5), (int)y+j, rgb);
								sourceImage.getPixel((int) Math.round(x+i-0.5), (int)y+j, rgb2);
							} else if (source == "5th") {
								image5th.getPixel((int) Math.round(x+i+0.5), (int)y+j, rgb);
								image5th.getPixel((int) Math.round(x+i-0.5), (int)y+j, rgb2);
							}

							int grey = (int) Math.round(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
							int grey2 = (int) Math.round(0.299 * rgb2[0] + 0.587 * rgb2[1] + 0.114 * rgb2[2]);

							int greyAvg = (int) Math.round((grey + grey2) / 2);

							int[] rgbAvg = new int[3];

							rgbAvg[0] = (int) Math.round((rgb[0] + rgb2[0]) / 2);
							rgbAvg[1] = (int) Math.round((rgb[1] + rgb2[1]) / 2);
							rgbAvg[2] = (int) Math.round((rgb[2] + rgb2[2]) / 2);

							block.greyValues[i][j] = greyAvg;

							block.values.put(new xyAxis(i, j), rgbAvg);
						} else if (x+i-0.5 < targetImage.getW() && y+j < targetImage.getH()) {
							int[] rgb2 = new int[3];

							if (source == "target") {
								targetImage.getPixel((int) Math.round(x+i-0.5), (int)y+j, rgb2);
							} else if (source == "reference") {
								sourceImage.getPixel((int) Math.round(x+i-0.5), (int)y+j, rgb2);
							} else if (source == "5th") {
								image5th.getPixel((int) Math.round(x+i-0.5), (int)y+j, rgb2);
							}

							int grey2 = (int) Math.round(0.299 * rgb2[0] + 0.587 * rgb2[1] + 0.114 * rgb2[2]);

							block.greyValues[i][j] = grey2;

							block.values.put(new xyAxis(i, j), rgb2);
						}
					}
				}
			} else {
				for (int j = 0; j < 16; j ++) {
					for (int i = 0; i < 16; i ++) {
						// check x+i and y+j is within range
						if (x+i+0.5 < targetImage.getW() && y+j+0.5 < targetImage.getH()) {
							int[] rgb = new int[3];
							int[] rgb2 = new int[3];
							int[] rgb3 = new int[3];
							int[] rgb4 = new int[4];

							if (source == "target") {
								targetImage.getPixel((int) Math.round(x+i+0.5), (int) Math.round(y+j+0.5), rgb);
								targetImage.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j-0.5), rgb2);
								targetImage.getPixel((int) Math.round(x+i+0.5), (int) Math.round(y+j-0.5), rgb3);
								targetImage.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j+0.5), rgb4);
							} else if (source == "reference") {
								sourceImage.getPixel((int) Math.round(x+i+0.5), (int) Math.round(y+j+0.5), rgb);
								sourceImage.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j-0.5), rgb2);
								sourceImage.getPixel((int) Math.round(x+i+0.5), (int) Math.round(y+j-0.5), rgb3);
								sourceImage.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j+0.5), rgb4);
							} else if (source == "5th") {
								image5th.getPixel((int) Math.round(x+i+0.5), (int) Math.round(y+j+0.5), rgb);
								image5th.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j-0.5), rgb2);
								image5th.getPixel((int) Math.round(x+i+0.5), (int) Math.round(y+j-0.5), rgb3);
								image5th.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j+0.5), rgb4);
							}

							int grey = (int) Math.round(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
							int grey2 = (int) Math.round(0.299 * rgb2[0] + 0.587 * rgb2[1] + 0.114 * rgb2[2]);
							int grey3 = (int) Math.round(0.299 * rgb3[0] + 0.587 * rgb3[1] + 0.114 * rgb3[2]);
							int grey4 = (int) Math.round(0.299 * rgb4[0] + 0.587 * rgb4[1] + 0.114 * rgb4[2]);

							int greyAvg = (int) Math.round((grey + grey2 + grey3 + grey4) / 4);

							int[] rgbAvg = new int[3];

							rgbAvg[0] = (int) Math.round((rgb[0] + rgb2[0] + rgb3[0] + rgb4[0]) / 4);
							rgbAvg[1] = (int) Math.round((rgb[1] + rgb2[1] + rgb3[1] + rgb4[1]) / 4);
							rgbAvg[2] = (int) Math.round((rgb[2] + rgb2[2] + rgb3[2] + rgb4[2]) / 4);

							block.greyValues[i][j] = greyAvg;

							block.values.put(new xyAxis(i, j), rgbAvg);
						} else if (x+i-0.5 < targetImage.getW() && y+j+0.5 < targetImage.getH()) {
							int[] rgb2 = new int[3];
							int[] rgb4 = new int[4];

							if (source == "target") {
								targetImage.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j-0.5), rgb2);
								targetImage.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j+0.5), rgb4);
							} else if (source == "reference") {
								sourceImage.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j-0.5), rgb2);
								sourceImage.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j+0.5), rgb4);
							} else if (source == "5th") {
								image5th.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j-0.5), rgb2);
								image5th.getPixel((int) Math.round(x+i-0.5), (int) Math.round(y+j+0.5), rgb4);
							}

							int grey2 = (int) Math.round(0.299 * rgb2[0] + 0.587 * rgb2[1] + 0.114 * rgb2[2]);
							int grey4 = (int) Math.round(0.299 * rgb4[0] + 0.587 * rgb4[1] + 0.114 * rgb4[2]);

							int greyAvg = (int) Math.round((grey2 + grey4) / 4);

							int[] rgbAvg = new int[3];

							rgbAvg[0] = (int) Math.round((rgb2[0] +  rgb4[0]) / 4);
							rgbAvg[1] = (int) Math.round((rgb2[1] +  rgb4[1]) / 4);
							rgbAvg[2] = (int) Math.round((rgb2[2] +  rgb4[2]) / 4);

							block.greyValues[i][j] = greyAvg;

							block.values.put(new xyAxis(i, j), rgbAvg);
						} else if (x+i+0.5 < targetImage.getW() && y+j-0.5 < targetImage.getH()) {
							int[] rgb2 = new int[3];
							int[] rgb3 = new int[3];

							if (source == "target") {
								targetImage.getPixel((int)Math.round(x+i-0.5), (int)Math.round(y+j-0.5), rgb2);
								targetImage.getPixel((int)Math.round(x+i+0.5), (int)Math.round(y+j-0.5), rgb3);
							} else if (source == "reference") {
								sourceImage.getPixel((int)Math.round(x+i-0.5), (int)Math.round(y+j-0.5), rgb2);
								sourceImage.getPixel((int)Math.round(x+i+0.5), (int)Math.round(y+j-0.5), rgb3);
							} else if (source == "5th") {
								image5th.getPixel((int)Math.round(x+i-0.5), (int)Math.round(y+j-0.5), rgb2);
								image5th.getPixel((int)Math.round(x+i+0.5), (int)Math.round(y+j-0.5), rgb3);
							}

							int grey2 = (int) Math.round(0.299 * rgb2[0] + 0.587 * rgb2[1] + 0.114 * rgb2[2]);
							int grey3 = (int) Math.round(0.299 * rgb3[0] + 0.587 * rgb3[1] + 0.114 * rgb3[2]);

							int greyAvg = (int) Math.round((grey2 + grey3) / 4);

							int[] rgbAvg = new int[3];

							rgbAvg[0] = (int) Math.round((rgb2[0] + rgb3[0]) / 4);
							rgbAvg[1] = (int) Math.round((rgb2[1] + rgb3[1]) / 4);
							rgbAvg[2] = (int) Math.round((rgb2[2] + rgb3[2]) / 4);

							block.greyValues[i][j] = greyAvg;

							block.values.put(new xyAxis(i, j), rgbAvg);
						} else if (x+i-0.5 < targetImage.getW() && y+j-0.5 < targetImage.getH()) {
							int[] rgb2 = new int[3];

							if (source == "target") {
								targetImage.getPixel((int)Math.round(x+i-0.5), (int)Math.round(y+j-0.5), rgb2);
							} else if (source == "reference") {
								sourceImage.getPixel((int)Math.round(x+i-0.5), (int)Math.round(y+j-0.5), rgb2);
							} else if (source == "5th") {
								image5th.getPixel((int)Math.round(x+i-0.5), (int)Math.round(y+j-0.5), rgb2);
							}

							int grey2 = (int) Math.round(0.299 * rgb2[0] + 0.587 * rgb2[1] + 0.114 * rgb2[2]);

							block.greyValues[i][j] = grey2;

							block.values.put(new xyAxis(i, j), rgb2);
						}
					}
				}
			}
		}

		return block;
	}

	/**
	 * find the best matching block searching using p = 12
	 * @param block original target block
	 */
	public void findMatchBlock(MacroBlock block) {
		double minDx = 0;
		double minDy = 0;
		double minMSD = 9999999;
		MacroBlock best = new MacroBlock(block.x, block.y);

		for (double p = -12; p <= 12; p += 0.5) {
			for (double q = -12; q <= 12; q += 0.5) {
				if (block.x + p < sourceImage.getW() && block.y + q < sourceImage.getH() && block.x + p >= 0 && block.y + q >= 0) {
					// build block from the source image
					MacroBlock sourceBlock = buildBlock(block.x + p, block.y + q, "reference");

					double msd = calculateMSD(block, sourceBlock);

					if (msd < minMSD) {
						minDx = p;
						minDy = q;
						minMSD = msd;
						best = sourceBlock;
					} else if (msd == minMSD) {
						if (Math.abs(p) + Math.abs(q) <= Math.abs(minDx) + Math.abs(minDy)) {
							minDx = p;
							minDy = q;
							best = sourceBlock;
						}
					}
				}
			}
		}

		block.dx = minDx;
		block.dy = minDy;
		block.bestMatchB = best;
	}

	/**
	 * Calculate the mean square difference from source block and target block
	 * @param  sourceBlock source block 
	 * @param  targetBlock target block
	 * @return             difference from those two blocks
	 */
	public double calculateMSD(MacroBlock sourceBlock, MacroBlock targetBlock) {
		double result = 0;
		int n = 0;
		for (int p = 0; p < 16; p ++) {
			for (int q = 0; q < 16; q ++) {
				result += Math.pow(sourceBlock.greyValues[p][q] - targetBlock.greyValues[p][q], 2);
			}
		}

		result /= 16*16;

		return result;
	}

	/**
	 * Calculate the residual error for the block
	 * @param block selected block to calculate
	 */
	public void calculateResidual(MacroBlock block) {
		for (int y = 0; y < 16; y ++) {
			for (int x = 0; x < 16; x ++) {
				block.residuals[x][y] = block.greyValues[x][y] - block.bestMatchB.greyValues[x][y];
				if (block.residuals[x][y] > max)
					max = block.residuals[x][y];
				if (block.residuals[x][y] < min)
					min = block.residuals[x][y];
			}
		}
	}

	/**
	 * Given the max and min, calculate the error range
	 */
	public void calculateError() {
		int range = max - min + 1;
		step = 256.0 / range;
	}

	/**
	 * Create an error image
	 */
	public void displayErrorImage() {
		errorFrame = new JPEGImage(target);

		for (int y = 0; y < targetImage.getH(); y += 16) {
			for (int x = 0; x < targetImage.getW(); x += 16) {
				MacroBlock block = blocks.poll();

				double[] mv = new double[2];
				mv[0] = block.dx;
				mv[1] = block.dy;

				mvs.add(mv);

				for (int j = 0; j < 16; j ++) {
					for (int i = 0; i < 16; i ++) {
						int[] rgb = new int[3];

						int afterShift = (int) ( ( block.residuals[i][j] - min ) * step );

						rgb = new int[]{afterShift, afterShift, afterShift};

						errorFrame.setPixel(x+i, y+j, rgb);
					}
				}
			}
		}
	}

	/**
	 * Homework 4 Task 2 - Application - Removing Moving Objects
	 */
	public void removingMovingObj() {
		divideMacroBlocks();

		for (MacroBlock block : blocks) {
			findMatchBlock(block);
			calculateResidual(block);
		}

		// replace the dynamic blocks
		for (MacroBlock block : blocks) {
			if (block.dx != 0 || block.dy != 0) {
				double minDist = 25565;

				MacroBlock closestB1 = new MacroBlock();
				MacroBlock closestB2 = new MacroBlock();

				for (MacroBlock staticBlock : blocks) {
					if (staticBlock.dx == 0 && staticBlock.dy == 0) {
						if (Math.abs(staticBlock.x - block.x) + Math.abs(staticBlock.y - block.y) < minDist) {
							minDist = Math.abs(staticBlock.x - block.x) + Math.abs(staticBlock.y - block.y);
							closestB1 = staticBlock;
						}
					}
				}

				File image5thFile = new File("IDB//Walk_005.ppm");

				image5th = new JPEGImage(image5thFile);

				closestB2 = buildBlock(block.x, block.y, "5th");

				option1Blocks.add(closestB1);
				option2Blocks.add(closestB2);
			} else {
				option1Blocks.add(block);
				option2Blocks.add(block);
			}
		}

		displayRemovedImages();
	}

	/**
	 * create the two images which removed the moving object(human)
	 */
	public void displayRemovedImages() {
		option1Frame = new JPEGImage(target);
		option2Frame = new JPEGImage(target);

		for (int y = 0; y < targetImage.getH(); y += 16) {
			for (int x = 0; x < targetImage.getW(); x += 16) {
				MacroBlock block1 = option1Blocks.poll();
				MacroBlock block2 = option2Blocks.poll();

				for (int j = 0; j < 16; j ++) {
					for (int i = 0; i < 16; i ++) {
						int[] rgb1 = block1.values.get(new xyAxis(i, j));
						int[] rgb2 = block2.values.get(new xyAxis(i, j));

						option1Frame.setPixel(x+i, y+j, rgb1);
						option2Frame.setPixel(x+i, y+j, rgb2);
					}
				}
			}
		}
	}

	/**
	 * Inner class to track each block
	 */
	class MacroBlock {
		double x;
		double y;

		double dx;
		double dy;

		MacroBlock bestMatchB;

		int[][] greyValues = new int[16][16];
		Map<xyAxis, int[]> values = new HashMap<xyAxis, int[]>();

		int[][] residuals = new int[16][16];

		public MacroBlock() {

		}

		public MacroBlock(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * Using xy axis to trace x and y position
	 */
	public class xyAxis implements Comparable<xyAxis> {
		Integer x;
		Integer y;

		public xyAxis(Integer x, Integer y) {
			this.x = x;
			this.y = y;
		}

		public int hashCode() {
		    return x.hashCode() * 3 + y.hashCode() * 5;
		}

	    public boolean equals(Object obj) {
	        if (obj == null)
	            return false;
	        if (obj == this)
	            return true;

	        xyAxis o2 = (xyAxis) obj;

	        if (x.equals(o2.x) && y.equals(o2.y))
	        	return true;
	        else
	        	return false;
	    }

		@Override
	    public int compareTo(xyAxis o2) {
	    	int output = x.compareTo(o2.x);

	    	if (output == 0) {
		    	output = y.compareTo(o2.y);
	    	}
	    	
	    	return output;
	    }
	}
}