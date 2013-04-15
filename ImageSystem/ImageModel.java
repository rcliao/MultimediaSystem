/*******************************************************
 CS451 Multimedia Software Systems
 @ Author: Elaine Kang

 This image class is for a 24bit RGB image only.
 *******************************************************/

package ImageSystem;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import ImageUtils.*;

public class ImageModel {
	private int width;        // number of columns
	private int height;       // number of rows
	private int pixelDepth=3;     // pixel depth in byte
	BufferedImage img;        // image array to store rgb values, 8 bits per channel

	BufferedImage indexImg;

	Map<Integer, int[]> lookUpTable = new HashMap<Integer, int[]>();

	public ImageModel() {

	}

	public ImageModel(int w, int h) {
		// create an empty image with width and height
		width = w;
		height = h;

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		System.out.println("Created an empty image with size " + width + "x" + height);
	}

	public ImageModel(File file) {
		// Create an image and read the data from the file
		readPPM(file);
		System.out.println("Created an image from " + file.getName()+ " with size "+width+"x"+height);
	}

	public BufferedImage getImg() {
		return img;
	}
	
	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public int getW() {
		return width;
	}

	public int getH() {
		return height;
	}

	public int getSize() {
		// return the image size in byte
		return width*height*pixelDepth;
	}

	public void setPixel(int x, int y, byte[] rgb) {
		// set rgb values at (x,y)
		int pix = 0xff000000 | ((rgb[0] & 0xff) << 16) | ((rgb[1] & 0xff) << 8) | (rgb[2] & 0xff);
		img.setRGB(x,y,pix);
	}

	public void setPixel(int x, int y, int[] irgb) {
		byte[] rgb = new byte[3];

		for(int i=0;i<3;i++)
			rgb[i] = (byte) irgb[i];

		setPixel(x,y,rgb);
	}

	public void getPixel(int x, int y, byte[] rgb) {
		// retreive rgb values at (x,y) and store in the array
		int pix = img.getRGB(x,y);

		rgb[2] = (byte) pix;
		rgb[1] = (byte)(pix>>8);
		rgb[0] = (byte)(pix>>16);
	}

	public void getPixel(int x, int y, int[] rgb) {
		int pix = img.getRGB(x,y);

		byte b = (byte) pix;
		byte g = (byte)(pix>>8);
		byte r = (byte)(pix>>16);

		// converts singed byte value (~128-127) to unsigned byte value (0~255)
		rgb[0]= (int) (0xFF & r);
		rgb[1]= (int) (0xFF & g);
		rgb[2]= (int) (0xFF & b);
	}

	public void displayPixelValue(int x, int y) {
		int pix = img.getRGB(x,y);

		byte b = (byte) pix;
		byte g = (byte)(pix>>8);
		byte r = (byte)(pix>>16);

		System.out.println("RGB Pixel value at ("+x+","+y+"):"+(0xFF & r)+","+(0xFF & g)+","+(0xFF & b));
	}

	public void readPPM(File file) {
		// read a data from a PPM file
		FileInputStream fis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);

			System.out.println("Reading "+file.getName()+"...");

			// read Identifier
			if(!dis.readLine().equals("P6")) {
				System.err.println("This is NOT P6 PPM. Wrong Format.");
				System.exit(0);
			}

			// read Comment line
			String commentString = dis.readLine();

			// read width & height
			String[] WidthHeight = dis.readLine().split(" ");
			width = Integer.parseInt(WidthHeight[0]);
			height = Integer.parseInt(WidthHeight[1]);

			// read maximum value
			int maxVal = Integer.parseInt(dis.readLine());

			if(maxVal != 255) {
				System.err.println("Max val is not 255");
				System.exit(0);
			}

			// read binary data byte by byte
			int x,y;
			//fBuffer = new Pixel[height][width];
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			byte[] rgb = new byte[3];
			int pix;

			for(y=0;y<height;y++) {
				for(x=0;x<width;x++) {
					rgb[0] = dis.readByte();
					rgb[1] = dis.readByte();
					rgb[2] = dis.readByte();
					setPixel(x, y, rgb);
				}
			}

			dis.close();
			fis.close();

			System.out.println("Read "+file.getName()+" Successfully.");
			// try
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void write2PPM(File file) {
		// wrrite the image data in img to a PPM file
		FileOutputStream fos = null;
		PrintWriter dos = null;

		try{
			fos = new FileOutputStream(file);
			dos = new PrintWriter(fos);

			// write header
			dos.print("P6"+"\n");
			dos.print("#CS451"+"\n");
			dos.print(width + " "+height +"\n");
			dos.print(255+"\n");
			dos.flush();

			// write data
			int x, y;
			byte[] rgb = new byte[3];
			for(y=0;y<height;y++)
			{
				for(x=0;x<width;x++) {
					getPixel(x, y, rgb);
					fos.write(rgb[0]);
					fos.write(rgb[1]);
					fos.write(rgb[2]);
				}
				fos.flush();
			}
			dos.close();
			fos.close();
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	// Homework Part 1:
	public void convertToGray() {
		// Formula: Gr(ay) = round(0.299 * R + 0.587 * G + 0.114 * B)
		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);
				
				// convertion from 24 bits to Gray scale
				int gray = (int) Math.round(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);

				for(int i=0;i<3;i++) {
					rgb[i] = gray;
				}

				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
	}

	// Homework 1 part 2: Bi-Scale (Directly)
	public void convertToBiDirectly() {
		// ga = average of all pixel

		// task 1 convert img to grey scale from part 1
		convertToGray();

		// calculate sum of all pixel
		int sum = 0;

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
			int[] rgb = new int[3];
			getPixel(x, y, rgb);

			sum += rgb[0];
			}
		}

		int pixels = height * width;

		int ga = (int) sum / pixels;

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);

				boolean isBlack;

				if (rgb[0] > ga) {
					isBlack = false;
				} else {
					isBlack = true;
				}

				if (isBlack) {
					for(int i=0;i<3;i++) {
						rgb[i] = 0;
					}
				} else {
					for(int i=0;i<3;i++) {
						rgb[i] = 255;
					}
				}

				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
	}

	// homework 1 part 2: Bi-Scale (Error diffusion)
	public void convertToBiError() {
		/* Error Diffusion

			For each pixel A[i,j],
		– Pick up the palette value that is nearest to the original pixel’s value. 
		– Store this palette value in the destination B[i,j]. (Quantization)
		– Calculate the quantization error e=A[i,j]-B[i,j] for the pixel A[i,j]. 
		Error e can be negative.
		– Distribute this error e to four of A[i,j]’s nearest neighbors that haven’t 
		been scanned yet (the one on the right and the three ones centered 
		below) according to a filter weight. Eg –Floyd-Steinberg

		A[i+1,j] = A[i+1,j] + 7*e/16
		 */

		// step 1 convert to Gray Scale
		convertToGray();

		boolean[][] checked = new boolean[width][height];
		double[][] original = new double[width][height];

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				checked[x][y] = false;
			}
		}

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];

				getPixel(x, y, rgb);

				original[x][y] = (double) rgb[0];
			}
		}

		// calculate sum of all pixel
		int sum = 0;

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);
				
				sum += rgb[0];
			}
		}

		int pixels = height * width;

		int ga = (int) sum / pixels;

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];

				boolean isBlack;

				double grayValue = original[x][y];

				if (grayValue > ga) {
					isBlack = false;
				} else {
					isBlack = true;
				}

				checked[x][y] = true;
				
				if (isBlack) {
					double error = grayValue - 0;

					// pass the value to the adacent pixel that haven't been done
					errorDiffusionFormula(x, y, "floyd", original, error);

					for(int i=0;i<3;i++) {
						rgb[i] = 0;
					}
				} else {
					double error = grayValue - 255;

					// pass the value to the adacent pixel that haven't been done
					errorDiffusionFormula(x, y, "floyd", original, error);

					for(int i=0;i<3;i++) {
						rgb[i] = 255;
					}
				}

				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
	}

	// convert to quad-level by Error Diffusion
	public void convertToQuadError() {
		/* Error Diffusion

			For each pixel A[i,j],
			– Pick up the palette value that is nearest to the original pixel’s value. 
			– Store this palette value in the destination B[i,j]. (Quantization)
			– Calculate the quantization error e=A[i,j]-B[i,j] for the pixel A[i,j]. 
			Error e can be negative.
			– Distribute this error e to four of A[i,j]’s nearest neighbors that haven’t 
			been scanned yet (the one on the right and the three ones centered 
			below) according to a filter weight. Eg –Floyd-Steinberg

			A[i+1,j] = A[i+1,j] + 7*e/16
		 */

		// step 1 convert to Gray Scale
		convertToGray();

		double[][] original = new double[width][height];

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];

				getPixel(x, y, rgb);

				original[x][y] = (double) rgb[0];
			}
		}

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];

				double grayValue = original[x][y];
				
				if (grayValue <= 42.5) {
					double error = grayValue - 0;

					// pass the value to the adacent pixel that haven't been done
					errorDiffusionFormula(x, y, "floyd", original, error);

					for(int i=0;i<3;i++) {
						rgb[i] = 0;
					}
				} else if (grayValue > 42.5 && grayValue <= 127.5) {
					double error = grayValue - 85;

					// pass the value to the adacent pixel that haven't been done
					errorDiffusionFormula(x, y, "floyd", original, error);

					for(int i=0;i<3;i++) {
						rgb[i] = 85;
					}
				} else if (grayValue > 127.5 && grayValue <= 212.5) {
					double error = grayValue - 170;

					// pass the value to the adacent pixel that haven't been done
					errorDiffusionFormula(x, y, "floyd", original, error);

					for(int i=0;i<3;i++) {
						rgb[i] = 170;
					}
				} else {
					double error = grayValue - 255;

					// pass the value to the adacent pixel that haven't been done
					errorDiffusionFormula(x, y, "floyd", original, error);

					for(int i=0;i<3;i++) {
						rgb[i] = 255;
					}
				}

				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
	}

	// convert to 8bit using Uniform Color Quantization
	public void convertTo8BitUCQ() {
		// building Look up table
		uniformColorTable();

		// generating index file
		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);
				
				Iterator<Integer> iter = lookUpTable.keySet().iterator();
				int indexValue = 0;

				while (iter.hasNext()) {
					Integer index = iter.next();

					if (rgb[0] < lookUpTable.get(index)[0] + 16 && rgb[0] >= lookUpTable.get(index)[0] - 16 &&
						rgb[1] < lookUpTable.get(index)[1] + 16 && rgb[1] >= lookUpTable.get(index)[1] - 16 &&
						rgb[2] < lookUpTable.get(index)[2] + 32 && rgb[2] >= lookUpTable.get(index)[2] - 32) {
						indexValue = index;
						break;
					}
				}

				for (int i = 0 ; i < 3; i ++) {
					rgb[i] = indexValue;
				}

				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
		indexImg = deepCopy(img);


		// generating 8-bit file
		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);
				
				int[] indexValue = lookUpTable.get(rgb[0]);

				for (int i = 0 ; i < 3; i ++) {
					rgb[i] = indexValue[i];
				}

				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
	}

	// homework 1 part 2: Bi-Scale (Error diffusion(Bell))
	public void convertToBiErrorBell() {
		/* Error Diffusion

			For each pixel A[i,j],
			– Pick up the palette value that is nearest to the original pixel’s value. 
			– Store this palette value in the destination B[i,j]. (Quantization)
			– Calculate the quantization error e=A[i,j]-B[i,j] for the pixel A[i,j]. 
			Error e can be negative.
			– Distribute this error e to four of A[i,j]’s nearest neighbors that haven’t 
			been scanned yet (the one on the right and the three ones centered 
			below) according to a filter weight. Eg –Floyd-Steinberg

			A[i+1,j] = A[i+1,j] + 7*e/16
		 */

		// step 1 convert to Gray Scale
		convertToGray();

		boolean[][] checked = new boolean[width][height];
		double[][] original = new double[width][height];

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				checked[x][y] = false;
			}
		}

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];

				getPixel(x, y, rgb);

				original[x][y] = (double) rgb[0];
			}
		}

		// calculate sum of all pixel
		int sum = 0;

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);
				
				sum += rgb[0];
			}
		}

		int pixels = height * width;

		int ga = (int) sum / pixels;

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];

				boolean isBlack;

				double grayValue = original[x][y];

				if (grayValue > ga) {
					isBlack = false;
				} else {
					isBlack = true;
				}

				checked[x][y] = true;
				
				if (isBlack) {
					double error = grayValue - 0;

					// pass the value to the adacent pixel that haven't been done
					errorDiffusionFormula(x, y, "bell", original, error);

					for(int i=0;i<3;i++) {
						rgb[i] = 0;
					}
				} else {
					double error = grayValue - 255;

					// pass the value to the adacent pixel that haven't been done
					errorDiffusionFormula(x, y, "bell", original, error);

					for(int i=0;i<3;i++) {
						rgb[i] = 255;
					}
				}

				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
	}

	// creating Look Up Table
	public void uniformColorTable() {

		for (int index = 0; index <= 255; index ++) {
			String binaryString = Integer.toBinaryString(index);
			while (binaryString.length() < 8) {
				binaryString = "0" + binaryString;
			}

			// build binary string for each color
			String blueBinaryString = binaryString.substring(6, 8);
			String greenBinaryString = binaryString.substring(3, 6);
			String redBinaryString = binaryString.substring(0, 3);

			// calculate binary string back to integer
			int blueIndex = Integer.valueOf(String.valueOf(blueBinaryString.charAt(1)))
						+ Integer.valueOf(String.valueOf(blueBinaryString.charAt(0))) * 2;
			int greenIndex = Integer.valueOf(String.valueOf(greenBinaryString.charAt(2)))
						+ Integer.valueOf(String.valueOf(greenBinaryString.charAt(1))) * 2
						+ Integer.valueOf(String.valueOf(greenBinaryString.charAt(0))) * 4;
			int redIndex = Integer.valueOf(String.valueOf(redBinaryString.charAt(2)))
						+ Integer.valueOf(String.valueOf(redBinaryString.charAt(1))) * 2
						+ Integer.valueOf(String.valueOf(redBinaryString.charAt(0))) * 4;

			// calculage the actual table value
			int blueValue = 32 + 64 * blueIndex;
			int greenValue = 16 + 32 * greenIndex;
			int redValue = 16 + 32 * redIndex;

			int[] values = new int[3];
			values[0] = redValue;
			values[1] = greenValue;
			values[2] = blueValue;

			lookUpTable.put(index, values);
		}
	}

	public void errorDiffusionFormula(int x, int y, String method, double[][] original, double error) {
		if (method.equals("floyd")) {
			if (x+1 < width)
				original[x+1][y] += 7*error/16;
			if (x+1 < width && y+1 < height)
				original[x+1][y+1] += error/16;
			if (x-1 >= 0 && y+1 < height)
				original[x-1][y+1] += 3*error/16;
			if (y+1 < height)
				original[x][y+1] += 5*error/16;
		} else if (method.equals("bell")) {
			if (x+1 < width)
				original[x+1][y] += 7*error/48;
			if (x+2 < width)
				original[x+2][y] += 5*error/48;
			if (y+1 < height) {
				if (x-2 >= 0)
					original[x-2][y+1] += 3*error/48;
				if (x-1 >= 0)
					original[x-1][y+1] += 5*error/48;
				original[x][y+1] += 7*error/48;
				if (x+1 < width)
					original[x+1][y+1] += 5*error/48;
				if (x+2 < width)
					original[x+2][y+1] += 3*error/48;
			}
			if (y+2 < height) {
				if (x-2 >= 0)
					original[x-2][y+2] += 1*error/48;
				if (x-1 >= 0)
					original[x-1][y+2] += 3*error/48;
				original[x][y+2] += 5*error/48;
				if (x+1 < width)
					original[x+1][y+2] += 3*error/48;
				if (x+2 < width)
					original[x+2][y+2] += 1*error/48;
			}
		}
	}

	public BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public void display(String title) {
	// display the image on the screen
		// Use a label to display the image
		JFrame frame = new JFrame();
		JLabel label = new JLabel(new ImageIcon(img));
		frame.add(label, BorderLayout.CENTER);
		frame.setTitle(title);
		frame.pack();
		frame.setVisible(true);
	}

} // Image class 