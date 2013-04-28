package test;

import org.junit.* ;
import static org.junit.Assert.* ;

public class UnitTest {

   @Test
   public void test_returnEuro() {
      System.out.println("Test if true works") ;
      assertTrue(1.0 == 1.0) ;
   }

   @Test
   public void test_roundUp() {
      System.out.println("Test if false works") ;
      assertFalse(1.0 == 0.67) ;
   }
}