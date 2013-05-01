package test;

import org.junit.* ;
import static org.junit.Assert.* ;

import java.util.*;

import models.*;

public class ImageModelTest {

   private ImageModel image = new ImageModel();

   @Test
   public void test_Radius() {
      assertEquals(image.generateRadiuses(1, 5)[230][255], 1);
   }

   @Test
   public void test_Radius2() {
      assertEquals(image.generateRadiuses(1, 5)[255][255], 0);
   }
}