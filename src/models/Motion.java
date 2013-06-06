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

	private LinkedList<MacroBlock> blocks = new LinkedList<MacroBlock>();

	private LinkedList<int[]> mvs = new LinkedList<int[]>();

	private JPEGImage errorFrame;

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

	public LinkedList<int[]> getMVs() {
		return mvs;
	}
	 
	public void setMVs(LinkedList<int[]> mvs) {
		this.mvs = mvs;
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
		for (int y = 0; y < targetImage.getH(); y += 16) {
			for (int x = 0; x < targetImage.getW(); x += 16) {
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
	public MacroBlock buildBlock(int x, int y, String source) {
		// creating a new block
		MacroBlock block = new MacroBlock(x, y);

		for (int j = 0; j < 16; j ++) {
			for (int i = 0; i < 16; i ++) {
				// check x+i and y+j is within range
				if (x+i < targetImage.getW() && y+j < targetImage.getH()) {
					int[] rgb = new int[3];

					if (source == "target")
						targetImage.getPixel(x+i, y+j, rgb);
					else if (source == "reference")
						sourceImage.getPixel(x+i, y+j, rgb);

					int grey = (int) Math.round(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);

					block.greyValues[i][j] = grey;

					block.values.put(new xyAxis(i, j), rgb);
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
		int minDx = 0;
		int minDy = 0;
		double minMSD = 9999999;
		MacroBlock best = new MacroBlock(block.x, block.y);

		for (int p = -12; p <= 12; p ++) {
			for (int q = -12; q <= 12; q ++) {
				if (block.x + p < sourceImage.getW() && block.y + q < sourceImage.getH() && block.x + p >= 0 && block.y + q >= 0) {
					// build block from the source image
					MacroBlock sourceBlock = buildBlock(block.x + p, block.y + q, "reference");

					double msd = calculateMSD(block, sourceBlock);

					if (msd < minMSD) {
						minDx = p;
						minDy = q;
						minMSD = msd;
						best = sourceBlock;
					}

					if (msd == minMSD) {
						if (Math.abs(p) + Math.abs(q) < Math.abs(minDx) + Math.abs(minDy)) {
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

		System.out.println(range);
		System.out.println(step);
	}

	/**
	 * Create an error image
	 */
	public void displayErrorImage() {
		errorFrame = new JPEGImage(target);

		for (int y = 0; y < targetImage.getH(); y += 16) {
			for (int x = 0; x < targetImage.getW(); x += 16) {
				MacroBlock block = blocks.poll();

				int[] mv = new int[2];
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

		for (MacroBlock block : blocks) {
			if (block.dx != 0 && block.dy != 0) {

			}
		}
	}

	/**
	 * Inner class to track each block
	 */
	class MacroBlock {
		int x;
		int y;

		int dx;
		int dy;

		MacroBlock bestMatchB;

		int[][] greyValues = new int[16][16];
		Map<xyAxis, int[]> values = new HashMap<xyAxis, int[]>();

		int[][] residuals = new int[16][16];

		public MacroBlock(int x, int y) {
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