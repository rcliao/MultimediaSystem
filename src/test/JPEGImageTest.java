package test;

import java.io.*;
import org.junit.* ;
import static org.junit.Assert.* ;

import java.util.*;

import models.*;

public class JPEGImageTest {

   private File duckyTestImage;
   private JPEGImage jpegImage;

   @Before
   public void initImage() {
   	duckyTestImage = new File("Ducky.ppm");
   	jpegImage = new JPEGImage(duckyTestImage);
   }

   @Test
   public void initImageSetup() {
   	assertEquals(250, jpegImage.getW());
   	assertEquals(273, jpegImage.getH());
   }

   @Test
   public void testResize() {
   	jpegImage.resize();

   	assertEquals(256, jpegImage.getW());
   	assertEquals(280, jpegImage.getH());
   }

   @Test
   public void afterResize() {
   	jpegImage.resize();

   	assertEquals(250, jpegImage.getOriginalWidth());
   	assertEquals(273, jpegImage.getOriginalHeight());
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

   	jpegImage.colorTransform();

      int[] rgb = new int[3];

      jpegImage.getPixel(30, 30, rgb);

      double y = 0.0;
      double cb = 0.0;
      double cr = 0.0;

      y = 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2];
      cb = -0.1687 * rgb[0] - 0.3313 * rgb[1] + 0.5 * rgb[2] - 0.5;
      cr = 0.5 * rgb[0] - 0.4187 * rgb[1] - 0.0813 * rgb[2] - 0.5;

      assertEquals(y, jpegImage.getY(30, 30), 0.01);
      assertEquals(cb, jpegImage.getCb(30, 30), 0.01);
      assertEquals(cr, jpegImage.getCr(30, 30), 0.01);
   }

   @Test
   public void testSubSampling() {
      jpegImage.resize();
      jpegImage.colorTransAndSubsample();

      assertEquals(256*280, jpegImage.getYValues().size());
      assertEquals(128*144, jpegImage.getCbValues().size());
      assertEquals(128*144, jpegImage.getCrValues().size());
   }

   @Test
   public void divideBlocks() {
      jpegImage.resize();
      jpegImage.colorTransAndSubsample();
      jpegImage.divideBlocks();

      assertEquals(32*35, jpegImage.getYBlocks().size());
      assertEquals(16*18, jpegImage.getCbBlocks().size());
      assertEquals(16*18, jpegImage.getCrBlocks().size());
      assertEquals(8*8, jpegImage.getCrBlock(15, 17).size());
   }

   @Test
   public void testDCTTransform() {

   }
}