package test;

import java.io.*;
import org.junit.* ;
import static org.junit.Assert.* ;

import java.util.*;

import models.*;
import models.JPEGImage.*;
import models.ImageModel.*;

public class JPEGImageTest {

   private File duckyTestImage;
   private JPEGImage jpegImage;

   @Before
   public void initImage() {
   	duckyTestImage = new File("redblack.ppm");
   	jpegImage = new JPEGImage(duckyTestImage);
   }

   @Test
   public void initImageSetup() {
   	assertEquals(64, jpegImage.getW());
   	assertEquals(64, jpegImage.getH());
   }

   @Test
   public void testResize() {
   	jpegImage.resize();

   	assertEquals(64, jpegImage.getW());
   	assertEquals(64, jpegImage.getH());
   }

   @Test
   public void afterResize() {
   	jpegImage.resize();

   	assertEquals(64, jpegImage.getOriginalWidth());
   	assertEquals(64, jpegImage.getOriginalHeight());
   }

   @Test
   public void deResize() {
   	jpegImage.resize();
   	jpegImage.deResize();

   	assertEquals(jpegImage.getOriginalHeight(), jpegImage.getH());
   	assertEquals(jpegImage.getOriginalWidth(), jpegImage.getW());
   }

   @Test
   public void pixelValuesAfterResize() {
      boolean isPredicted = true;
      boolean extraBlack = true;

      LinkedList<int[]> originalRGBs = new LinkedList<int[]>();
      LinkedList<int[]> afterRGBs = new LinkedList<int[]>();

      for (int i = 0; i < jpegImage.getW(); i ++) {
         for (int j = 0; j < jpegImage.getH(); j++) {
            int[] rgb = new int[3];

            jpegImage.getPixel(i, j, rgb);

            originalRGBs.add(rgb);            
         }
      }

      jpegImage.resize();

      for (int i = 0; i < jpegImage.getOriginalWidth(); i ++) {
         for (int j = 0; j < jpegImage.getOriginalHeight(); j++) {
            int[] rgb = new int[3];

            jpegImage.getPixel(i, j, rgb);

            afterRGBs.add(rgb);            
         }
      }

      for (int i = jpegImage.getOriginalWidth(); i < jpegImage.getW(); i ++) {
         for (int j = jpegImage.getOriginalHeight(); j < jpegImage.getH(); j ++) {
            int[] rgb = new int[3];

            jpegImage.getPixel(i, j, rgb);

            afterRGBs.add(rgb); 
         }
      }

      int[] originalRGB = new int[3];
      int[] afterRGB = new int[3];

      while(originalRGB != null) {
         originalRGB = originalRGBs.poll();
         afterRGB = afterRGBs.poll();

         if (originalRGB == null) {
            break;
         }

         if (originalRGB[0] != afterRGB[0] || originalRGB[1] != afterRGB[1] || originalRGB[2] != afterRGB[2]) {
            isPredicted = false;
            break;
         }
      }

      while (afterRGB != null) {
         if (afterRGB == null) {
            break;
         }

         if (afterRGB[0] != 0 || afterRGB[1] != 0 || afterRGB[2] != 0) {
            extraBlack = false;
            break;
         }

         afterRGB = afterRGBs.poll();
      }

      assertTrue("Test image is identical", isPredicted);
      assertTrue("Test extra space are black", extraBlack);
   }

   @Test
   public void pixelValuesAfterDeResize() {
      boolean isPredicted = true;

      LinkedList<int[]> originalRGBs = new LinkedList<int[]>();
      LinkedList<int[]> afterRGBs = new LinkedList<int[]>();

      for (int i = 0; i < jpegImage.getW(); i ++) {
         for (int j = 0; j < jpegImage.getH(); j++) {
            int[] rgb = new int[3];

            jpegImage.getPixel(i, j, rgb);

            originalRGBs.add(rgb);            
         }
      }

      jpegImage.resize();
      jpegImage.deResize();

      for (int i = 0; i < jpegImage.getOriginalWidth(); i ++) {
         for (int j = 0; j < jpegImage.getOriginalHeight(); j++) {
            int[] rgb = new int[3];

            jpegImage.getPixel(i, j, rgb);

            afterRGBs.add(rgb);            
         }
      }

      int[] originalRGB = new int[3];
      int[] afterRGB = new int[3];

      while(originalRGB != null) {
         originalRGB = originalRGBs.poll();
         afterRGB = afterRGBs.poll();

         if (originalRGB == null) {
            break;
         }

         if (originalRGB[0] != afterRGB[0] || originalRGB[1] != afterRGB[1] || originalRGB[2] != afterRGB[2]) {
            isPredicted = false;
            break;
         }
      }

      assertTrue("Test image is identical", isPredicted);
   }

   @Test
   public void testColorTransform() {
      boolean isPredicted = true;

      jpegImage.resize();
   	jpegImage.colorTransform();

      assertEquals(-51.755, jpegImage.getY(1, 0), 0.01);
      assertEquals(-43.5185, jpegImage.getCb(1, 0), 0.01);
      assertEquals(127, jpegImage.getCr(1, 0), 0.01);
      assertEquals(-51.755, jpegImage.getY(0, 0), 0.01);
      assertEquals(-43.5185, jpegImage.getCb(0, 0), 0.01);
      assertEquals(127, jpegImage.getCr(0, 0), 0.01);
   }

   @Test
   public void testSubSampling() {
      jpegImage.resize();
      jpegImage.colorTransAndSubsample();

      assertEquals(64*64, jpegImage.getYValues().size());
      assertEquals(32*32, jpegImage.getCbValues().size());
      assertEquals(32*32, jpegImage.getCrValues().size());
   }

   @Test
   public void divideBlocks() {
      jpegImage.resize();
      jpegImage.colorTransAndSubsample();
      jpegImage.divideBlocks();

      assertEquals(8*8, jpegImage.getYBlocks().size());
      assertEquals(4*4, jpegImage.getCbBlocks().size());
      assertEquals(4*4, jpegImage.getCrBlocks().size());
      assertEquals(8*8, jpegImage.getCrBlock(3, 3).size());
   }

   @Test
   public void testCompressionRatio() {
      jpegImage.resize();
      jpegImage.colorTransAndSubsample();
      jpegImage.dctEncoding();
      jpegImage.quantization(0);
      jpegImage.calculateCompressionRatio();

      assertEquals(98304.0, jpegImage.getOriginalSize(), 0);
      assertEquals(9216.0, jpegImage.getDSizeY(), 0);
      assertEquals(2144.0, jpegImage.getDSizeCr(), 0);
      assertEquals(2144.0, jpegImage.getDSizeCb(), 0);
   }
}