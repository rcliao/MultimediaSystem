package test;

import org.junit.* ;
import static org.junit.Assert.* ;

import java.util.*;

import models.*;

public class TextModelTest {

   private TextModel text = new TextModel();

   // Test the basic 1+1 operation
   @Test
   public void test_onePlusOne() {
      System.out.println("Test if true works on 1+1 == 2") ;
      assertTrue((1 + 1)== 2) ;
   }

   // more basic operation testing
   @Test
   public void test_equals() {
      System.out.println("Test if equals works") ;
      assertEquals(1+1, 2) ;
   }

   // test the initDictionary works as predicted
   @Test
   public void test_InitDictionary() {
      System.out.println("Test if init dictionary works on abababab");

      Map<Integer, String> expectedResult = new TreeMap<Integer, String>();
      expectedResult.put(0, "a");
      expectedResult.put(1, "b");

      assertEquals(text.initDictionary("abababab", 256), expectedResult);
   }

   @Test
   public void test_InitDictionary2() {
      System.out.println("Test if init dictionary works on eric");

      Map<Integer, String> secondResult = new TreeMap<Integer, String>();
      secondResult.put(0, "e");
      secondResult.put(1, "r");
      secondResult.put(2, "i");
      secondResult.put(3, "c");

      assertEquals(text.initDictionary("eric", 256), secondResult);
   }

   // test the recursive lzw encoding method
   @Test
   public void test_LZWEncodingHelper() {
      System.out.println("Test if the lzw encoding works on ericicic");

      assertEquals(text.lzwEncodingHelper("ericicic", "", 256, text.initDictionary("eric", 256)), "012366");
   }

   @Test
   public void test_LZWEncodingHelper2() {
      System.out.println("Test if the lzw encoding works on abcd");

      assertEquals(text.lzwEncodingHelper("abcd", "", 256, text.initDictionary("abcd", 256)), "0123");
   }

   @Test
   public void test_LZWEncodingHelper3() {
      System.out.println("Test if the lzw encoding works on abbaabbaababbaaaabaabba");

      assertEquals(text.lzwEncodingHelper("abbaabbaababbaaaabaabba", "", 256, text.initDictionary("abbaabbaababbaaaabaabba", 256)), "0110242655730");
   }

   @Test
   public void test_LZWEncodingHelper4() {
      System.out.println("Test if the lzw encoding works on xxyyxyxyxxyyyxyxxyxxyyx");

      assertEquals(text.lzwEncodingHelper("xxyyxyxyxxyyyxyxxyxxyyx", "", 256, text.initDictionary("xxyyxyxyxxyyyxyxxyxxyyx", 256)), "001136347580");
   }
}