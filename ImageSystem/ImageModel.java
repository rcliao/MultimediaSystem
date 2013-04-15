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

public class ImageModel
{
  private int width;        // number of columns
  private int height;       // number of rows
  private int pixelDepth=3;     // pixel depth in byte
  BufferedImage img;        // image array to store rgb values, 8 bits per channel

  public ImageModel() {

  }

  public ImageModel(int w, int h)
  // create an empty image with width and height
  {
  width = w;
  height = h;

  img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  System.out.println("Created an empty image with size " + width + "x" + height);
  }

  public ImageModel(File file)
  // Create an image and read the data from the file
  {
	readPPM(file);
	System.out.println("Created an image from " + file.getName()+ " with size "+width+"x"+height);
  }

  public BufferedImage getImg() {
	return img;
  }

  public void setImg(BufferedImage img) {
	this.img = img;
  }

  public int getW()
  {
  return width;
  }

  public int getH()
  {
  return height;
  }

  public int getSize()
  // return the image size in byte
  {
  return width*height*pixelDepth;
  }

  public void setPixel(int x, int y, byte[] rgb)
  // set rgb values at (x,y)
  {
  int pix = 0xff000000 | ((rgb[0] & 0xff) << 16) | ((rgb[1] & 0xff) << 8) | (rgb[2] & 0xff);
  img.setRGB(x,y,pix);
  }

  public void setPixel(int x, int y, int[] irgb)
  // set rgb values at (x,y)
  {
  byte[] rgb = new byte[3];

  for(int i=0;i<3;i++)
	rgb[i] = (byte) irgb[i];

  setPixel(x,y,rgb);
  }

  public void getPixel(int x, int y, byte[] rgb)
  // retreive rgb values at (x,y) and store in the array
  {
	int pix = img.getRGB(x,y);

	rgb[2] = (byte) pix;
	rgb[1] = (byte)(pix>>8);
	rgb[0] = (byte)(pix>>16);
  }


  public void getPixel(int x, int y, int[] rgb)
  // retreive rgb values at (x,y) and store in the array
  {
  int pix = img.getRGB(x,y);

  byte b = (byte) pix;
  byte g = (byte)(pix>>8);
  byte r = (byte)(pix>>16);

	// converts singed byte value (~128-127) to unsigned byte value (0~255)
  rgb[0]= (int) (0xFF & r);
  rgb[1]= (int) (0xFF & g);
  rgb[2]= (int) (0xFF & b);
  }

  public void displayPixelValue(int x, int y)
  // Display rgb pixel value at (x,y)
  {
  int pix = img.getRGB(x,y);

  byte b = (byte) pix;
  byte g = (byte)(pix>>8);
  byte r = (byte)(pix>>16);

	System.out.println("RGB Pixel value at ("+x+","+y+"):"+(0xFF & r)+","+(0xFF & g)+","+(0xFF & b));
   }

  public void readPPM(File file)
  // read a data from a PPM file
  {
  FileInputStream fis = null;
  DataInputStream dis = null;

  try{
	fis = new FileInputStream(file);
	dis = new DataInputStream(fis);

	System.out.println("Reading "+file.getName()+"...");

	// read Identifier
	if(!dis.readLine().equals("P6"))
	{
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

	if(maxVal != 255)
	{
	  System.err.println("Max val is not 255");
	  System.exit(0);
	}

	// read binary data byte by byte
	int x,y;
	//fBuffer = new Pixel[height][width];
	img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	byte[] rgb = new byte[3];
	int pix;

	for(y=0;y<height;y++)
	{
		for(x=0;x<width;x++)
	  {
		rgb[0] = dis.readByte();
		rgb[1] = dis.readByte();
		rgb[2] = dis.readByte();
		setPixel(x, y, rgb);
	  }
	}
	dis.close();
	fis.close();

	System.out.println("Read "+file.getName()+" Successfully.");

  } // try
  catch(Exception e)
  {
	System.err.println(e.getMessage());
  }
  }

  public void write2PPM(File file)
  // wrrite the image data in img to a PPM file
  {
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
	  for(x=0;x<width;x++)
	  {
		getPixel(x, y, rgb);
		fos.write(rgb[0]);
		fos.write(rgb[1]);
		fos.write(rgb[2]);
	  }
	  fos.flush();
	}
	dos.close();
	fos.close();

  } // try
  catch(Exception e)
  {
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

  public void convertToBiDirectly() {
	// ga = average of all pixel

	// task 1 convert img to grey scale from part 1
	convertToGray();

	// calculate sum of all pixel
	int sum = 0;
	// Formula: Gr(ay) = round(0.299 * R + 0.587 * G + 0.114 * B)
	for (int y = 0; y < height; y ++) {
	  for (int x = 0; x < width; x ++) {
		int[] rgb = new int[3];
		getPixel(x, y, rgb);
		
		sum += rgb[0];
	  }
	}

	int pixels = height * width;

	int ga = (int) sum / pixels;

	// Formula: Gr(ay) = round(0.299 * R + 0.587 * G + 0.114 * B)
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

  public void display(String title)
  // display the image on the screen
  {
	 // Use a label to display the image
	  JFrame frame = new JFrame();
	  JLabel label = new JLabel(new ImageIcon(img));
	  frame.add(label, BorderLayout.CENTER);
	  frame.setTitle(title);
	  frame.pack();
	  frame.setVisible(true);
  }

} // Image class 