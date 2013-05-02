/*******************************************************
 CS451 Multimedia Software Systems
 @ Author: Eric Liao

 This image class is for a 24bit RGB image only.
 *******************************************************/

package models;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import ImageUtils.*;

/**
 * Image class, dealing with all the image functions, such as convert image to gray scale, 2-bit scale ...
 */
public class ImageModel {
	private int width;  // number of columns
	private int height;  // number of rows
	private int pixelDepth=3;  // pixel depth in byte
	private BufferedImage img;  // image array to store rgb values, 8 bits per channel
	private String filename;  // store file name to save the index file
	private File file;

	/**
	 * Store index image to display separately from result image
	 */
	private BufferedImage indexImg;

	/**
	 * look up talbe for the uniform color quantization
	 */
	private Map<Integer, int[]> lookUpTable = new TreeMap<Integer, int[]>();

	/**
	 * look up table for the median cut algorithm
	 */
	private Map<Integer, ColorCube> lookUpTableMedian = new TreeMap<Integer, ColorCube>();

	/**
	 * Default Constructor
	 * 
	 * @return Basic constructor
	 */
	public ImageModel() {
	}

	/**
	 * Constructor creating empty image according to the width, and height as input
	 * 
	 * @param  w width
	 * @param  h height
	 * @return   Create ImageModel Class with preset height and width
	 */
	public ImageModel(int w, int h) {
		// create an empty image with width and height
		width = w;
		height = h;

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * Constructor according to the input file
	 * 
	 * @param  file image file
	 * @return      Create IamgeModel and read the input image file
	 */
	public ImageModel(File file) {
		// Create an image and read the data from the file
		readPPM(file);
		System.out.println("Created an image from " + file.getName()+ " with size "+width+"x"+height);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public BufferedImage getImg() {
		return img;
	}
	
	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public BufferedImage getIndexImg() {
		return indexImg;
	}

	public void setIndexImg(BufferedImage indexImg) {
		this.indexImg = indexImg;
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

	public Map<Integer, int[]> getLookUpTable() {
		return lookUpTable;
	}

	public void setLookUpTable(Map<Integer, int[]> lookUpTable) {
		this.lookUpTable = lookUpTable;
	}

	public Map<Integer, ColorCube> getLookUpTableMedian() {
		return lookUpTableMedian;
	}

	public void setLookUpTableMedian(Map<Integer, ColorCube> lookUpTableMedian) {
		this.lookUpTableMedian = lookUpTableMedian;
	}

	// display pixel value for testing purpose
	public void displayPixelValue(int x, int y) {
		int pix = img.getRGB(x,y);

		byte b = (byte) pix;
		byte g = (byte)(pix>>8);
		byte r = (byte)(pix>>16);

		System.out.println("RGB Pixel value at ("+x+","+y+"):"+(0xFF & r)+","+(0xFF & g)+","+(0xFF & b));
	}

	/**
	 * read the ppm file and set the current image to be input file
	 * 
	 * @param file read from file
	 */
	public void readPPM(File file) {
		// read a data from a PPM file
		FileInputStream fis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);

			this.file = file;
			this.filename = file.getName();

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

	/**
	 * write the image file to ppm format
	 * 
	 * @param file will store according to the file destination
	 */
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

	public void readPPM(String filename) {
		// read a data from a PPM file
		FileInputStream fis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(filename);
			dis = new DataInputStream(fis);

			this.filename = filename;

			System.out.println("Reading "+filename+"...");

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

			System.out.println("Read "+filename+" Successfully.");
			// try
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void write2PPM(String filename) {
		// wrrite the image data in img to a PPM file
		FileOutputStream fos = null;
		PrintWriter dos = null;

		try{
			fos = new FileOutputStream(filename);
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

	/**
	 * Create a circle image according to the input M(width) and N(radius)
	 */
	public void createCircleImage(int m, int n) {
		int[][] circlePoints = generateRadiuses(m, n);

		width = 512;
		height = 512;

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		int[] white = new int[3];
		int[] black = new int[3];

		for (int i = 0; i < 3; i ++) {
			white[i] = 255;
			black[i] = 0;
		}

		for (int w = 0; w < width; w ++) {
			for (int h = 0; h < height; h ++) {
				if (circlePoints[w][h] == 1) {
					setPixel(w, h, black);
				}
				else {
					setPixel(w, h, white);
				}
			}
		}
	}

	/**
	 * Homework 1 problem:
	 * This will convert image from 24 bit to gray scale according to the formula as below:
	 * gray = math.round(0.299 * r + 0.587 * g + 0.114 * b)
	 */
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

	/**
	 * Homework 1 part 2 convert to 1bit scale directly
	 * Step1 convert the image to gray value so that the average gray value can be calculated
	 * Then by comparing the pixel value to average gray value, the pixel can either assigned as
	 * white or black.
	 */
	public void convertToBiDirectly() {
		// ga = average of all pixel

		// task 1 convert img to grey scale from part 1
		convertToGray();

		int ga = calculateGrayAverage();

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);

				if (rgb[0] > ga) {
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

	/**
	 * Homework 1 part 2 Error diffusion
	 * For each pixel A[i,j],
	 * <p>
	 *	– Pick up the palette value that is nearest to the original pixel’s value. 
	 *	<p>
	 *	– Store this palette value in the destination B[i,j]. (Quantization)
	 *	<p>
	 *	– Calculate the quantization error e=A[i,j]-B[i,j] for the pixel A[i,j]. 
	 *	<p>
	 *	Error e can be negative.
	 *	<p>
	 *	– Distribute this error e to four of A[i,j]’s nearest neighbors that haven’t 
	 *	been scanned yet (the one on the right and the three ones centered 
	 *	below) according to a filter weight. Eg –Floyd-Steinberg
	 *	
	 * @param method take which error diffusion formula to apply
	 */
	public void convertToBiError(String method) {
		// step 1 convert to Gray Scale
		convertToGray();

		double[][] original = new double[width][height];

		// read ans store the original pixel values
		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];

				getPixel(x, y, rgb);

				original[x][y] = (double) rgb[0];
			}
		}

		int ga = calculateGrayAverage();

		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];

				double grayValue = original[x][y];
				double error = 0;
				
				if (grayValue < ga) {
					error = grayValue;

					for(int i=0;i<3;i++) {
						rgb[i] = 0;
					}
				} else {
					error = grayValue - 255;

					for(int i=0;i<3;i++) {
						rgb[i] = 255;
					}
				}

				// pass the value to the adacent pixel that haven't been done
				errorDiffusionFormula(x, y, method, original, error);

				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
	}

	/**
	 * Homework 1 part 3: 2-bit scale using error diffusion
	 * By default, this will use floyd error diffusion method
	 */
	public void convertToQuadError() {
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
				double error = 0;
				
				if (grayValue <= 42.5) {
					error = grayValue - 0;

					for(int i=0;i<3;i++) {
						rgb[i] = 0;
					}
				} else if (grayValue > 42.5 && grayValue <= 127.5) {
					error = grayValue - 85;

					for(int i=0;i<3;i++) {
						rgb[i] = 85;
					}
				} else if (grayValue > 127.5 && grayValue <= 212.5) {
					error = grayValue - 170;

					for(int i=0;i<3;i++) {
						rgb[i] = 170;
					}
				} else {
					error = grayValue - 255;

					for(int i=0;i<3;i++) {
						rgb[i] = 255;
					}
				}

				errorDiffusionFormula(x, y, "floyd", original, error);


				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
	}

	/**
	 * Homework 1 part 4: Uniform Color Quantization
	 * Step 1 create a uniform color look up table
	 * Step 2 using look up table to convert the 24 bit image to indexed iamge
	 * After convert to indexed image, store indexed image to indexImg to be able to display it later on
	 * Then convert the indexed image back to 8-bit color by usng same look up table again
	 */
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

					// finding the value to the correct cube and assign the index value to the image
					if (rgb[0] <= lookUpTable.get(index)[0] + 16 && rgb[0] >= lookUpTable.get(index)[0] - 16 &&
						rgb[1] <= lookUpTable.get(index)[1] + 16 && rgb[1] >= lookUpTable.get(index)[1] - 16 &&
						rgb[2] <= lookUpTable.get(index)[2] + 32 && rgb[2] >= lookUpTable.get(index)[2] - 32) {
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

		// store copy of the index image
		indexImg = deepCopy(img);

		// write to index.ppm
		write2PPM(filename + "-index.ppm");

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

	/**
	 * Homework 1 Extra Credit: Median Cut Algorithm
	 * Step 1 create a min color cube
	 * Step 2 Find the largest axis and divide the color cube by mean value into two sub color cube
	 * Step 3 continue doing step 1 and 2 until there is 256 color cube or there is no more color to divide
	 * Then using the result color cubes to produce a look up table for this specific image
	 * After having table, using table to create indexed image and 8-bit image using the indexed image
	 */
	public void convertTo8BitMCQ() {
		ArrayList<Integer> redValues = new ArrayList<Integer>();
		ArrayList<Integer> greenValues = new ArrayList<Integer>();
		ArrayList<Integer> blueValues = new ArrayList<Integer>();

		ArrayList<int[]> pixelValues = new ArrayList<int[]>();

		ColorCube entireCube = new ColorCube();

		// separate all the values into three axis (rgb)
		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);
				
				redValues.add(rgb[0]);
				greenValues.add(rgb[1]);
				blueValues.add(rgb[2]);

				// building histogram with key = rgb value, and value is the frequency of the rgb
				if (!entireCube.histogram.containsKey(rgb))
					entireCube.histogram.put(rgb, 1);
				else {
					Integer count = entireCube.histogram.get(rgb);
					entireCube.histogram.put(rgb, count + 1);
				}
			}
		}

		/* 
		 *	step 1 find the smallest box containing all the color
		 */
		// * Sort the list from small to big
		Collections.sort(redValues);
		Collections.sort(greenValues);
		Collections.sort(blueValues);

		entireCube.rmin = (Collections.min(redValues));
		entireCube.gmin = (Collections.min(greenValues));
		entireCube.bmin = (Collections.min(blueValues));

		entireCube.rmax = (Collections.max(redValues));
		entireCube.gmax = (Collections.max(greenValues));
		entireCube.bmax = (Collections.max(blueValues));

		entireCube.size = 256;

		createColorTableMCQ(entireCube);

		// generating index file
		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);
				
				Iterator<Integer> iter = lookUpTableMedian.keySet().iterator();
				int indexValue = 0;

				while (iter.hasNext()) {
					Integer index = iter.next();

					if (rgb[0] <= lookUpTableMedian.get(index).rmax && rgb[0] >= lookUpTableMedian.get(index).rmin &&
						rgb[1] <= lookUpTableMedian.get(index).gmax && rgb[1] >= lookUpTableMedian.get(index).gmin &&
						rgb[2] <= lookUpTableMedian.get(index).bmax && rgb[2] >= lookUpTableMedian.get(index).bmin) {
						indexValue = index;
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

		write2PPM(filename + "-index.ppm");

		// generating 8-bit file
		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);
				
				ColorCube indexValue = lookUpTableMedian.get(rgb[0]);

		    	rgb[0] = indexValue.rmean;
		    	rgb[1] = indexValue.gmean;
		    	rgb[2] = indexValue.bmean;

				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
	}

	/**
	 * Apply the error diffusion into median cut algorithm
	 * 
	 * @param method Which error diffusion formula to use
	 */
	public void convertTo8BitMCQError(String method) {
		ArrayList<Integer> redValues = new ArrayList<Integer>();
		ArrayList<Integer> greenValues = new ArrayList<Integer>();
		ArrayList<Integer> blueValues = new ArrayList<Integer>();
		ArrayList<int[]> pixelValues = new ArrayList<int[]>();

		ColorCube entireCube = new ColorCube();

		Map<xyAxis, int[]> original = new HashMap<xyAxis, int[]>();

		// separate all the values into three axis (rgb)
		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);
				
				redValues.add(rgb[0]);
				greenValues.add(rgb[1]);
				blueValues.add(rgb[2]);

				// building the histogram
				if (!entireCube.histogram.containsKey(rgb))
					entireCube.histogram.put(rgb, 1);
				else {
					Integer count = entireCube.histogram.get(rgb);
					entireCube.histogram.put(rgb, count + 1);
				}

				// store all the original pixel values to original Map
				xyAxis xy = new xyAxis(x, y);

				original.put(xy, rgb);
			}
		}

		/* 
		 *	step 1 find the smallest box containing all the color
		 */
		// * Sort the list from small to big
		Collections.sort(redValues);
		Collections.sort(greenValues);
		Collections.sort(blueValues);

		entireCube.rmin = (Collections.min(redValues));
		entireCube.gmin = (Collections.min(greenValues));
		entireCube.bmin = (Collections.min(blueValues));

		entireCube.rmax = (Collections.max(redValues));
		entireCube.gmax = (Collections.max(greenValues));
		entireCube.bmax = (Collections.max(blueValues));

		entireCube.size = 256;

		// building table using the color cube
		createColorTableMCQ(entireCube);

		// generating index file
		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				// store output
				int[] output = new int[3];

				// calculate the error
		    	int[] originalRGB = original.get(new xyAxis(x,y));

		    	int[] error = new int[3];
				
				Iterator<Integer> iter = lookUpTableMedian.keySet().iterator();

				int indexValue = 0;

				// init the distance to be the max distance as max distane you can get
				double distance = 255*255*255;

				// find the closest point after the error diffusion
				while (iter.hasNext()) {
					Integer index = iter.next();

					// calculate the current distance between table value to the corrent pixel value
					double diff = Math.sqrt((lookUpTableMedian.get(index).rmean - originalRGB[0]) * (lookUpTableMedian.get(index).rmean - originalRGB[0]) +
						(lookUpTableMedian.get(index).gmean - originalRGB[1]) * (lookUpTableMedian.get(index).gmean - originalRGB[1]) +
						(lookUpTableMedian.get(index).bmean - originalRGB[2]) * (lookUpTableMedian.get(index).bmean - originalRGB[2]));

					// if the distance is smaller than distance assign it!
					if (diff < distance) {
						distance = diff;
						indexValue = index;
					}
				}

				// change the output to be the index value
				for (int i = 0 ; i < 3; i ++) {
					output[i] = indexValue;
				}

				// calculage the error
		    	error[0] = originalRGB[0] - lookUpTableMedian.get(output[0]).rmean;
		    	error[1] = originalRGB[1] - lookUpTableMedian.get(output[0]).gmean;
		    	error[2] = originalRGB[2] - lookUpTableMedian.get(output[0]).bmean;

				// assign error to nearby tile
		    	errorDiffusionFormula(x, y, method, original, error);

				// write it to img (BufferedImage)
				setPixel(x, y, output);
			}
		}
		indexImg = deepCopy(img);

		// generating 8-bit file
		for (int y = 0; y < height; y ++) {
			for (int x = 0; x < width; x ++) {
				int[] rgb = new int[3];
				getPixel(x, y, rgb);
				
				ColorCube indexValue = lookUpTableMedian.get(rgb[0]);

		    	rgb[0] = indexValue.rmean;
		    	rgb[1] = indexValue.gmean;
		    	rgb[2] = indexValue.bmean;

				// write it to img (BufferedImage)
				setPixel(x, y, rgb);
			}
		}
	}

	/**
	 * Homework 2 Aliasing
	 * Resize the image to width/k and height/k by using filter to see the differences
	 * 
	 * @param k      Determine how small image will be
	 * @param option check which filter to apply
	 */
	public void subSampling(int k, String option) {
		Map<xyAxis, int[]> graph = new TreeMap<xyAxis, int[]>();

		// no filter
		if (option == "default") {
			for (int x = 0; x < width; x += k) {
				for (int y = 0; y < height; y += k) {
					int[] point = new int[3];
					getPixel(x, y, point);

					graph.put(new xyAxis(x/k, y/k), point);
				}
			}
		}
		// calculating average for the k
		else if (option == "average") {
			for (int x = 0; x < width; x += k) {
				for (int y = 0; y < height; y += k) {
					int[] averageRGB = new int[3];

					for (int xi = x; xi < (x + k); xi ++) {
						for (int yi = y; yi < (y + k); yi ++) {
							if (xi >= 0 && xi < width && yi >= 0 && yi < height) {
								int[] point = new int[3];
								getPixel(xi, yi, point);

								averageRGB[0] += point[0];
								averageRGB[1] += point[1];
								averageRGB[2] += point[2];
							}
						}
					}

					for (int i = 0; i < 3; i ++)
						averageRGB[i] = (int) Math.round(averageRGB[i]/(k*k));
					
					graph.put(new xyAxis(x/k, y/k), averageRGB);
				}
			}
		}
		// 3 x 3 filter case
		else if (option == "filter1" || option == "filter2") {
			for (int x = 0; x < width; x += k) {
				for (int y = 0; y < height; y += k) {
					int[] value = filter(x, y, option);

					graph.put(new xyAxis(x/k, y/k), value);
				}
			}
		}

		// required output
		width = width / k;
		height = height / k;

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < width; x ++) {
			for (int y = 0; y < height; y ++) {			
				int[] resultRGB = new int[3];
				resultRGB = graph.get(new xyAxis(x, y));
				setPixel(x, y, resultRGB);
			}
		}
	}

	/**
	 * Given the gray scale image, find the average gray value
	 * 
	 * @return average gray value of all pixels
	 */
	public int calculateGrayAverage() {
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

		return (int) sum / pixels;
	}

	/**
	 * Generating the look up table for uniform color quantization
	 */
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

	/**
	 * Generate the look up table for the median cut algorithm
	 * 
	 * @param cube Take original color cube as input
	 */
	public void createColorTableMCQ(ColorCube cube) {
		ArrayList<ColorCube> values = new ArrayList<ColorCube>();

		buildValues(cube, 0, 8, values);

		for (Integer index = 0; index < values.size(); index ++) {
			ColorCube c = values.get(index);

			c.rmean = 0;
			c.gmean = 0;
			c.bmean = 0;

			if (c.histogram.size() != 0) {
				c.size = c.histogram.size();

				for (Map.Entry<int[], Integer> entry : c.histogram.entrySet()) {
					c.rmean += entry.getKey()[0] * entry.getValue();
					c.gmean += entry.getKey()[1] * entry.getValue();
					c.bmean += entry.getKey()[2] * entry.getValue();
				}

				c.rmean /= c.size;
				c.gmean /= c.size;
				c.bmean /= c.size;
			}
		}

		Collections.sort(values);

		// putting table value as color cube
		for (Integer index = 0; index < values.size(); index ++) {
			lookUpTableMedian.put(index, values.get(index));
		}
	}

	/**
	 * Recursively dividing the color cube until there is 256 color cubes or no more color to divide
	 * 
	 * @param cube   Taking the original color cube as input
	 * @param count  trace how much cube have been created
	 * @param n      max cubes to create
	 * @param result return as the result
	 */
	public void buildValues(ColorCube cube, int count, int n, ArrayList<ColorCube> result) {
		if (count > n)
			// base case
			result.add(cube);
		else {

			/*
			 *  step 2 sort the axix (r, g, b)
			 */
			Integer redDiff = cube.rmax - cube.rmin;
			Integer greenDiff = cube.gmax - cube.gmin;
			Integer blueDiff = cube.bmax - cube.bmin;

			String largestAxis = "red";
			Integer largestDiff = redDiff;

			if (greenDiff.compareTo(largestDiff) > 0) {
				largestAxis = "green";
				largestDiff = greenDiff;
			}

			if (blueDiff.compareTo(largestDiff) > 0) {
				largestAxis = "blue";
				largestDiff = blueDiff;
			}

			if (cube.histogram.size() == 0) {
				return;
			} else if (largestDiff.equals(0)) {
				buildValues(cube, count + 1, n, result);
			} else {

				/* 
				 *  step 3 split the longest axis into 2 part, this also ends up spliting the box into two sub-boxes
		         */
				ColorCube cube1 = new ColorCube();
				cube1.size = 0;
				ColorCube cube2 = new ColorCube();
				cube2.size = 0;

				boolean isSame = false;

				if (largestAxis.equals("red")) {
					// copy old axis data
					cube1.gmin = cube.gmin;
					cube1.gmax = cube.gmax;
					cube1.bmin = cube.bmin;
					cube1.bmax = cube.bmax;
					cube2.gmin = cube.gmin;
					cube2.gmax = cube.gmax;
					cube2.bmin = cube.bmin;
					cube2.bmax = cube.bmax;

					if (cube.size > 1) {
						cube.rmean = 0;
						
						for (Map.Entry<int[], Integer> entry : cube.histogram.entrySet()) {
							cube.rmean += entry.getKey()[0] * entry.getValue();
						}

						cube.rmean = Math.round(cube.rmean / cube.size);

						Map.Entry<int[], Integer> firstEntry = cube.histogram.entrySet().iterator().next();
						cube1.rmin = 255;
						cube1.rmax = 0;
						cube2.rmin = 255;
						cube2.rmax = 0;

						for (Map.Entry<int[], Integer> entry : cube.histogram.entrySet()) {
							if (new Integer(entry.getKey()[0]).compareTo(cube.rmean) <= 0) {
								cube1.histogram.put(entry.getKey(), entry.getValue());
								cube1.size ++;
								if (entry.getKey()[0] < cube1.rmin) {
									cube1.rmin = entry.getKey()[0];
								}
								if (entry.getKey()[0] > cube1.rmax) {
									cube1.rmax = entry.getKey()[0];
								}
							} else {
								cube2.histogram.put(entry.getKey(), entry.getValue());
								cube2.size ++;
								if (entry.getKey()[0] < cube2.rmin) {
									cube2.rmin = entry.getKey()[0];
								}
								if (entry.getKey()[0] > cube2.rmax) {
									cube2.rmax = entry.getKey()[0];
								}
							}
						}
						if (cube2.size == 0) {
							isSame = true;
						}
					} else {
						isSame = true;
					}
				} else if (largestAxis.equals("green")) {
					// copy old axis data
					cube1.rmin = cube.rmin;
					cube1.rmax = cube.rmax;
					cube1.bmin = cube.bmin;
					cube1.bmax = cube.bmax;
					cube2.rmin = cube.rmin;
					cube2.rmax = cube.rmax;
					cube2.bmin = cube.bmin;
					cube2.bmax = cube.bmax;

					if (cube.size > 1) {
						cube.gmean = 0;
						
						for (Map.Entry<int[], Integer> entry : cube.histogram.entrySet()) {
							cube.gmean += entry.getKey()[1] * entry.getValue();
						}

						cube.gmean /= cube.size;

						Map.Entry<int[], Integer> firstEntry = cube.histogram.entrySet().iterator().next();
						cube1.gmin = 255;
						cube1.gmax = 0;
						cube2.gmin = 255;
						cube2.gmax = 0;

						for (Map.Entry<int[], Integer> entry : cube.histogram.entrySet()) {
							if (new Integer(entry.getKey()[1]).compareTo(cube.gmean) <= 0) {
								cube1.histogram.put(entry.getKey(), entry.getValue());
								cube1.size ++;
								if (entry.getKey()[1] < cube1.gmin) {
									cube1.gmin = entry.getKey()[1];
								}
								if (entry.getKey()[1] > cube1.gmax) {
									cube1.gmax = entry.getKey()[1];
								}
							} else {
								cube2.histogram.put(entry.getKey(), entry.getValue());
								cube2.size ++;
								if (entry.getKey()[1] < cube2.gmin) {
									cube2.gmin = entry.getKey()[1];
								}
								if (entry.getKey()[1] > cube2.gmax) {
									cube2.gmax = entry.getKey()[1];
								}
							}
						}
						if (cube2.size == 0) {
							isSame = true;
						}
					} else {
						isSame = true;
					}
				} else if (largestAxis.equals("blue")) {
					// copy old axis data
					cube1.rmin = cube.rmin;
					cube1.rmax = cube.rmax;
					cube1.gmin = cube.gmin;
					cube1.gmax = cube.gmax;
					cube2.rmin = cube.rmin;
					cube2.rmax = cube.rmax;
					cube2.gmin = cube.gmin;
					cube2.gmax = cube.gmax;

					if (cube.size > 1) {
						cube.bmean = 0;
						
						for (Map.Entry<int[], Integer> entry : cube.histogram.entrySet()) {
							cube.bmean += entry.getKey()[2] * entry.getValue();
						}

						cube.bmean /= cube.size;

						Map.Entry<int[], Integer> firstEntry = cube.histogram.entrySet().iterator().next();
						cube1.bmin = 255;
						cube1.bmax = 0;
						cube2.bmin = 255;
						cube2.bmax = 0;

						for (Map.Entry<int[], Integer> entry : cube.histogram.entrySet()) {
							if (new Integer(entry.getKey()[2]).compareTo(cube.bmean) <= 0) {
								cube1.histogram.put(entry.getKey(), entry.getValue());
								cube1.size ++;
								if (entry.getKey()[2] < cube1.bmin) {
									cube1.bmin = entry.getKey()[2];
								}
								if (entry.getKey()[2] > cube1.bmax) {
									cube1.bmax = entry.getKey()[2];
								}
							} else {
								cube2.histogram.put(entry.getKey(), entry.getValue());
								cube2.size ++;
								if (entry.getKey()[2] < cube2.bmin) {
									cube2.bmin = entry.getKey()[2];
								}
								if (entry.getKey()[2] > cube2.bmax) {
									cube2.bmax = entry.getKey()[2];
								}
							}
						}
						if (cube2.size == 0) {
							isSame = true;
						}
					} else {
						isSame = true;
					}
				}
			/*
			 *  step 4 check the box has been separated into 256 boxes
			 */
				// recurence step
				if (isSame) {
					buildValues(cube1, count + 1, n, result);
				} else {
					buildValues(cube1, count + 1, n, result);
					buildValues(cube2, count + 1, n, result);
				}
			}
		}
	}

	/**
	 * Apply the error difussion formula to the adjacent pixel values
	 * 
	 * @param x        X value of the current pixel
	 * @param y        Y value of the current pixel
	 * @param method   check which formula to apply
	 * @param original original arrays storing the pixel values
	 * @param error    the error value(ouput - original) from current pixel
	 */
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
		} else if (method.equals("stucki")) {
			if (x+1 < width)
				original[x+1][y] += 7*error/42;
			if (x+2 < width)
				original[x+2][y] += 5*error/42;
			if (y+1 < height) {
				if (x-2 >= 0)
					original[x-2][y+1] += 2*error/42;
				if (x-1 >= 0)
					original[x-1][y+1] += 4*error/42;
				original[x][y+1] += 8*error/42;
				if (x+1 < width)
					original[x+1][y+1] += 4*error/42;
				if (x+2 < width)
					original[x+2][y+1] += 2*error/42;
			}
			if (y+2 < height) {
				if (x-2 >= 0)
					original[x-2][y+2] += 1*error/42;
				if (x-1 >= 0)
					original[x-1][y+2] += 2*error/42;
				original[x][y+2] += 4*error/42;
				if (x+1 < width)
					original[x+1][y+2] += 2*error/42;
				if (x+2 < width)
					original[x+2][y+2] += 1*error/42;
			}
		}
	}

	/**
	 * Same as error difussion as above, except this time using rgb value
	 * 
	 * @param x          X value of current pixel
	 * @param y          Y value of current pixel
	 * @param method     check which formula to use
	 * @param original   original map storing all the pixel rgb values corresponding to the x and y
	 * @param error      error value from the current pixel
	 */
	public void errorDiffusionFormula(int x, int y, String method, Map<xyAxis, int[]> original, int[] error) {
		if (method.equals("floyd")) {
			if (x+1 < width) {
				original.get(new xyAxis(x+1, y))[0] += 7*error[0]/16;
				original.get(new xyAxis(x+1, y))[1] += 7*error[1]/16;
				original.get(new xyAxis(x+1, y))[2] += 7*error[2]/16;
			}
			if (x+1 < width && y+1 < height) {
				original.get(new xyAxis(x+1, y+1))[0] += error[0]/16;
				original.get(new xyAxis(x+1, y+1))[1] += error[1]/16;
				original.get(new xyAxis(x+1, y+1))[2] += error[2]/16;
			}
			if (x-1 >= 0 && y+1 < height) {
				original.get(new xyAxis(x-1, y+1))[0] += 3*error[0]/16;
				original.get(new xyAxis(x-1, y+1))[1] += 3*error[1]/16;
				original.get(new xyAxis(x-1, y+1))[2] += 3*error[2]/16;
			}
			if (y+1 < height) {
				original.get(new xyAxis(x, y+1))[0] += 5*error[0]/16;
				original.get(new xyAxis(x, y+1))[1] += 5*error[1]/16;
				original.get(new xyAxis(x, y+1))[2] += 5*error[2]/16;
			}
		}  else if (method.equals("bell")) {
			if (x+1 < width) {
				original.get(new xyAxis(x+1, y))[0] += 7*error[0]/48;
				original.get(new xyAxis(x+1, y))[1] += 7*error[1]/48;
				original.get(new xyAxis(x+1, y))[2] += 7*error[2]/48;
			}
			if (x+2 < width) {
				original.get(new xyAxis(x+2, y))[0] += 5*error[0]/48;
				original.get(new xyAxis(x+2, y))[1] += 5*error[1]/48;
				original.get(new xyAxis(x+2, y))[2] += 5*error[2]/48;
			}
			if (y+1 < height) {
				if (x-2 >= 0) {
					original.get(new xyAxis(x-2, y+1))[0] += 3*error[0]/48;
					original.get(new xyAxis(x-2, y+1))[1] += 3*error[1]/48;
					original.get(new xyAxis(x-2, y+1))[2] += 3*error[2]/48;
				}
				if (x-1 >= 0) {
					original.get(new xyAxis(x-1, y+1))[0] += 5*error[0]/48;
					original.get(new xyAxis(x-1, y+1))[1] += 5*error[1]/48;
					original.get(new xyAxis(x-1, y+1))[2] += 5*error[2]/48;
				}
				original.get(new xyAxis(x, y+1))[0] += 7*error[0]/48;
				original.get(new xyAxis(x, y+1))[1] += 7*error[1]/48;
				original.get(new xyAxis(x, y+1))[2] += 7*error[2]/48;
				if (x+1 < width) {
					original.get(new xyAxis(x+1, y+1))[0] += 5*error[0]/48;
					original.get(new xyAxis(x+1, y+1))[1] += 5*error[1]/48;
					original.get(new xyAxis(x+1, y+1))[2] += 5*error[2]/48;
				}
				if (x+2 < width) {
					original.get(new xyAxis(x+2, y+1))[0] += 3*error[0]/48;
					original.get(new xyAxis(x+2, y+1))[1] += 3*error[1]/48;
					original.get(new xyAxis(x+2, y+1))[2] += 3*error[2]/48;
				}
			}
			if (y+2 < height) {
				if (x-2 >= 0) {
					original.get(new xyAxis(x-2, y+2))[0] += 1*error[0]/48;
					original.get(new xyAxis(x-2, y+2))[1] += 1*error[1]/48;
					original.get(new xyAxis(x-2, y+2))[2] += 1*error[2]/48;
				}
				if (x-1 >= 0) {
					original.get(new xyAxis(x-1, y+2))[0] += 3*error[0]/48;
					original.get(new xyAxis(x-1, y+2))[1] += 3*error[1]/48;
					original.get(new xyAxis(x-1, y+2))[2] += 3*error[2]/48;
				}
				original.get(new xyAxis(x, y+2))[0] += 5*error[0]/48;
				original.get(new xyAxis(x, y+2))[1] += 5*error[1]/48;
				original.get(new xyAxis(x, y+2))[2] += 5*error[2]/48;
				if (x+1 < width) {
					original.get(new xyAxis(x+1, y+2))[0] += 3*error[0]/48;
					original.get(new xyAxis(x+1, y+2))[1] += 3*error[1]/48;
					original.get(new xyAxis(x+1, y+2))[2] += 3*error[2]/48;
				}
				if (x+2 < width) {
					original.get(new xyAxis(x+2, y+2))[0] += 1*error[0]/48;
					original.get(new xyAxis(x+2, y+2))[1] += 1*error[1]/48;
					original.get(new xyAxis(x+2, y+2))[2] += 1*error[2]/48;
				}
			}
		} else if (method.equals("stucki")) {
			if (x+1 < width) {
				original.get(new xyAxis(x+1, y))[0] += 7*error[0]/42;
				original.get(new xyAxis(x+1, y))[1] += 7*error[1]/42;
				original.get(new xyAxis(x+1, y))[2] += 7*error[2]/42;
			}
			if (x+2 < width) {
				original.get(new xyAxis(x+2, y))[0] += 5*error[0]/42;
				original.get(new xyAxis(x+2, y))[1] += 5*error[1]/42;
				original.get(new xyAxis(x+2, y))[2] += 5*error[2]/42;
			}
			if (y+1 < height) {
				if (x-2 >= 0) {
					original.get(new xyAxis(x-2, y+1))[0] += 2*error[0]/42;
					original.get(new xyAxis(x-2, y+1))[1] += 2*error[1]/42;
					original.get(new xyAxis(x-2, y+1))[2] += 2*error[2]/42;
				}
				if (x-1 >= 0) {
					original.get(new xyAxis(x-1, y+1))[0] += 4*error[0]/42;
					original.get(new xyAxis(x-1, y+1))[1] += 4*error[1]/42;
					original.get(new xyAxis(x-1, y+1))[2] += 4*error[2]/42;
				}
				original.get(new xyAxis(x, y+1))[0] += 8*error[0]/42;
				original.get(new xyAxis(x, y+1))[1] += 8*error[1]/42;
				original.get(new xyAxis(x, y+1))[2] += 8*error[2]/42;
				if (x+1 < width) {
					original.get(new xyAxis(x+1, y+1))[0] += 4*error[0]/42;
					original.get(new xyAxis(x+1, y+1))[1] += 4*error[1]/42;
					original.get(new xyAxis(x+1, y+1))[2] += 4*error[2]/42;
				}
				if (x+2 < width) {
					original.get(new xyAxis(x+2, y+1))[0] += 2*error[0]/42;
					original.get(new xyAxis(x+2, y+1))[1] += 2*error[1]/42;
					original.get(new xyAxis(x+2, y+1))[2] += 2*error[2]/42;
				}
			}
			if (y+2 < height) {
				if (x-2 >= 0) {
					original.get(new xyAxis(x-2, y+2))[0] += 1*error[0]/42;
					original.get(new xyAxis(x-2, y+2))[1] += 1*error[1]/42;
					original.get(new xyAxis(x-2, y+2))[2] += 1*error[2]/42;
				}
				if (x-1 >= 0) {
					original.get(new xyAxis(x-1, y+2))[0] += 2*error[0]/42;
					original.get(new xyAxis(x-1, y+2))[1] += 2*error[1]/42;
					original.get(new xyAxis(x-1, y+2))[2] += 2*error[2]/42;
				}
				original.get(new xyAxis(x, y+2))[0] += 4*error[0]/42;
				original.get(new xyAxis(x, y+2))[1] += 4*error[1]/42;
				original.get(new xyAxis(x, y+2))[2] += 4*error[2]/42;
				if (x+1 < width) {
					original.get(new xyAxis(x+1, y+2))[0] += 2*error[0]/42;
					original.get(new xyAxis(x+1, y+2))[1] += 2*error[1]/42;
					original.get(new xyAxis(x+1, y+2))[2] += 2*error[2]/42;
				}
				if (x+2 < width) {
					original.get(new xyAxis(x+2, y+2))[0] += 1*error[0]/42;
					original.get(new xyAxis(x+2, y+2))[1] += 1*error[1]/42;
					original.get(new xyAxis(x+2, y+2))[2] += 1*error[2]/42;
				}
			}
		}
	}

	/**
	 * clone the image since image class has no clone method
	 * 
	 * @param  bi input image
	 * @return    return the cloned input image
	 */
	public BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	/**
	 * write the look up table to file
	 * 
	 * @param filename Look up table txt name
	 */
	public void writeToFile(String filename) {
		try {
        	BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        	out.write("Look Up Table");
        	out.newLine();
        	out.write("index\tR\tG\tB\tFrequency");
        	out.newLine();
        	out.write("--------------------------------------------------------------------------");
        	out.newLine();

        	// write the table to the textarea
			Iterator<Integer> iter = lookUpTableMedian.keySet().iterator();

            while (iter.hasNext()) {
				Integer index = iter.next();
				out.write(index + "\t");

				out.write(lookUpTableMedian.get(index).rmean + "\t");
				out.write(lookUpTableMedian.get(index).gmean + "\t");
				out.write(lookUpTableMedian.get(index).bmean + "\t");

				out.write("" + lookUpTableMedian.get(index).histogram.size());

				out.newLine();
			}

            out.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}

	/**
	 * generate all the radiuses for all the circle according to input m and n
	 */
	public int[][] generateRadiuses(int m, int n) {
		int[][] result = new int[512][512];
		
		// calculate the radiuses according to the input m and n
		for (int r = n; r < 256; r += n) {
			for (int w = 0; w <= m-1; w ++) {
				if (m != 0)
					generateRadius(r+w, result);
			}
		}

		return result;
	}

	/**
	 * Trig formula to generate all the points on the xy axis for the circle radius r
	 * 
	 * @param r     radius
	 * @param input marker to mark which point should be black
	 */
	public void generateRadius(int r, int[][] input) {
		/**
		 * Formula to find all the points on the map for the circle
		 * x = cx + r * cos(a)
		 * y = cy + r * sin(a)
		 */
		
		int centerX = 255;
		int centerY = 255;

		for (double degree = 0; degree < 360; degree += 0.01) {
			int x  = (int) Math.round(centerX + r * Math.cos(Math.toRadians(degree)));
			int y  = (int) Math.round(centerY + r * Math.sin(Math.toRadians(degree)));

			if (x < 512 && y < 512 && x >= 0 && y >= 0)
				input[x][y] = 1;
		}
	}

	/**
	 * filter method, will resize the image according to the option
	 */
	public int[] filter(int x, int y, String option) {
		double[] result = new double[3];
		int[] resultInt = new int[3];

		int[] rgb = new int[3];


		for (int i = 0; i <= 2; i ++) {
			for (int j = 0; j <= 2; j ++) {
				if (x + i < width && x + i >= 0 && y + j < height && y + j >= 0) {
					getPixel(x+i, y+j, rgb);

					if (option == "filter1") {
						result[0] += rgb[0]/9;
						result[1] += rgb[1]/9;
						result[2] += rgb[2]/9;
					}

					else if (option == "filter2") { 
						if ((i + j)% 2 == 1) {
							result[0] += rgb[0] * 2/16;
							result[1] += rgb[1] * 2/16;
							result[2] += rgb[2] * 2/16;
						}
						else if (i == 1 && j == 1) {
							result[0] += rgb[0] * 4/16;
							result[1] += rgb[1] * 4/16;
							result[2] += rgb[2] * 4/16;
						}
						else {
							result[0] += rgb[0] * 1/16;
							result[1] += rgb[1] * 1/16;
							result[2] += rgb[2] * 1/16;
						}
					}
				}
			}
		}

		for (int i = 0; i < 3; i ++) {
			resultInt[i] = (int) Math.round(result[i]);
		}

		return resultInt;
	}

	/**
	 * Display the current image in a new java frame
	 * 
	 * @param title frame requires a title
	 */
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

	/**
	 * Color cube containing all the data that median cut algorithm needs
	 */
	public class ColorCube implements Comparable<ColorCube> {
		private Map<int[], Integer> histogram = new HashMap<int[], Integer>();

		private int rmin, rmax;
		private int gmin, gmax;
		private int bmin, bmax;
		private int size;

		private Integer rmean;
		private Integer gmean;
		private Integer bmean;

		/**
		 * contructor
		 */
		public ColorCube() {
		}

		/*
		  Getter/ Setters
		 */
		
		public Integer getRmean() {
			return rmean;
		}

		public void setRmean(Integer rmean) {
			this.rmean = rmean;
		}

		public Integer getGmean() {
			return gmean;
		}

		public void setGmean(Integer gmean) {
			this.gmean = gmean;
		}

		public Integer getBmean() {
			return bmean;
		}

		public void setBmean() {
			this.bmean = bmean;
		}

		public Map<int[], Integer> getHistogram() {
			return histogram;
		}

		public void setHistogram(Map<int[], Integer> histogram) {
			this.histogram = histogram;
		}

		@Override
	    public int compareTo(ColorCube o2) {
	    	int output;

	    	output = rmean.compareTo(o2.rmean);

	    	if (output == 0) {
	    		output = gmean.compareTo(o2.gmean);
	    	}
	    	if (output == 0) {
	    		output = bmean.compareTo(o2.bmean);
	    	}
	    	return output;
	    }
	}

	/**
	 * Using xy axis to trace x and y position
	 */
	class xyAxis implements Comparable<xyAxis> {
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

} // Image class 