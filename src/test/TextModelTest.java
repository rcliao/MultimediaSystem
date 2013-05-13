package test;

import org.junit.* ;
import static org.junit.Assert.* ;

import java.util.*;

import models.*;

public class TextModelTest {

   private TextModel text = new TextModel();

   /**
    * Test the basic 1+1 operation
    */
   @Test
   public void test_onePlusOne() {
      assertTrue((1 + 1)== 2) ;
   }

   /**
    * more basic operation testing
    */
   @Test
   public void test_equals() {
      assertEquals(1+1, 2) ;
   }

   /**
    * test the initDictionary works as predicted
    */
   @Test
   public void test_InitDictionary() {
      Map<Integer, String> expectedResult = new TreeMap<Integer, String>();
      expectedResult.put(0, "a");
      expectedResult.put(1, "b");
      expectedResult.put(2, "c");

      assertEquals(expectedResult, text.initDictionary("abcababab", 256));
   }

   /**
    * test the initDictionary works as predicted
    */
   @Test
   public void test_InitDictionary2() {
      Map<Integer, String> secondResult = new TreeMap<Integer, String>();
      secondResult.put(0, "e");
      secondResult.put(1, "r");
      secondResult.put(2, "i");
      secondResult.put(3, "c");

      assertEquals(text.initDictionary("eric", 256), secondResult);
   }

   /**
    * test the recursive lzw encoding method
    */
   @Test
   public void test_LZWEncodingHelper() {
      assertEquals("0 1 2 3 6 6", text.lzwEncodingHelper("ericicic", "", 256, text.initDictionary("eric", 256)));
   }

   @Test
   public void test_LZWEncodingHelper2() {
      assertEquals(text.lzwEncodingHelper("abcd", "", 256, text.initDictionary("abcd", 256)), "0 1 2 3");
   }

   @Test
   public void test_LZWEncodingHelper3() {
      assertEquals(text.lzwEncodingHelper("abbaabbaababbaaaabaabba", "", 256, text.initDictionary("abbaabbaababbaaaabaabba", 256)), "0 1 1 0 2 4 2 6 5 5 7 3 0");
   }

   @Test
   public void test_LZWEncodingHelper4() {
      assertEquals(text.lzwEncodingHelper("xxyyxyxyxxyyyxyxxyxxyyx", "", 256, text.initDictionary("xxyyxyxyxxyyyxyxxyxxyyx", 256)), "0 0 1 1 3 6 3 4 7 5 8 0");
   }

   @Test
   public void test_LZWEncodingHelper5() {
      assertEquals(text.lzwEncoding("Multimedia is media and content that uses a combination of different content forms. The term can be used as a noun (a medium with multiple content forms) or as an  adjective describing a medium as having multiple content forms. The term is used in contrast to media which only use traditional forms of printed or hand-produced material. Multimedia includes a combination of text, audio, still images, animation, video, and interactivity content forms. Multimedia is usually recorded and played, displayed or accessed by information content processing devices, such as computerized and electronic devices, but can also be part of a live performance. Multimedia (as an adjective) also describes electronic media devices used to store and experience multimedia content. Multimedia is distinguished from mixed media in fine art; by including audio, for example, it has a broader scope. The term rich media is synonymous for interactive multimedia. Hypermedia can be considered one particular multimedia application.", 256), "0 1 2 3 4 5 6 7 4 8 9 4 10 9 39 41 43 8 11 7 9 12 13 11 3 6 57 9 3 14 8 3 9 1 10 6 46 43 55 5 15 4 11 64 4 56 9 13 16 9 41 16 16 6 17 59 65 55 57 89 9 16 13 17 5 10 18 9 19 14 6 61 87 5 54 51 9 15 104 67 40 9 8 70 9 11 13 1 11 9 20 43 48 4 1 107 21 4 62 47 35 37 22 2 104 91 58 60 95 97 10 23 80 17 115 70 122 115 7 24 6 12 37 25 104 7 69 12 17 4 74 11 26 115 47 40 127 107 116 9 63 25 75 166 5 134 4 136 138 56 140 65 142 98 100 102 104 58 97 44 46 113 53 75 54 183 17 116 65 3 13 168 49 9 21 14 4 12 14 80 11 2 27 66 68 61 200 41 37 56 8 2 94 96 98 80 82 22 162 92 53 96 173 51 7 28 231 13 7 1 12 114 5 64 87 42 2 100 34 36 38 169 43 75 12 2 1 159 117 72 164 77 79 81 105 29 3 30 115 1 41 13 30 9 10 37 2 225 38 8 26 69 30 115 11 38 77 56 30 9 175 159 13 30 115 52 44 92 200 155 4 175 3 216 139 93 186 99 9 252 37 126 43 45 217 1 224 215 9 88 55 17 159 53 237 9 136 8 27 40 30 83 45 136 8 27 114 235 8 12 244 10 68 53 15 216 75 186 77 79 139 93 240 244 10 10 176 83 6 175 244 10 30 9 10 243 212 172 72 22 1 191 4 31 114 237 9 6 137 155 17 56 210 83 6 175 244 10 30 110 1 90 109 224 10 204 111 9 22 8 17 65 81 167 2 4 157 9 22 87 186 51 244 251 179 126 43 124 149 151 153 155 4 157 145 224 10 204 159 10 161 163 69 9 6 137 155 17 56 210 205 42 83 6 175 244 194 68 53 203 9 10 203 88 115 52 9 6 29 22 248 59 244 133 253 126 71 183 89 251 179 126 43 45 83 45 37 165 1 45 103 53 16 17 13 107 5 4 29 245 255 44 122 16 75 104 8 17 3 32 110 216 75 12 2 1 41 165 115 1 41 13 30 226 147 6 29 8 5 181 30 44 65 63 117 15 17 13 8 159 147 10 55 22 6 188 103 105 192 162 211 205 42 193 9 10 27 119 11 27 5 120 46 142 44 92 200 155 4 157 133 253 126 8 100 33 27 22 106 255 108 122 111 198 11 10 4 159 88 234 11 104 22 8 17 37 12 35 8 147 178 253 126 50 22 136 210 77 56 18");
   }

   /**
    * Test the lzw size
    */
   @Test
   public void test_LZWSize() {
      text.lzwEncoding("abababab", 256);

      assertEquals(text.getSize(), 8*8);
   }

   /**
    * Test the lzw size after encoding
    */
   @Test
   public void test_LZWSizeAfter() {
      text.lzwEncoding("abababab", 256);

      assertEquals(text.getSizeAfterEncoded(), 3*5);
   }

   /**
    * Test Huffman coding(getting frequency of the input string)
    */
   @Test
   public void test_HuffmanFrequency() {
      int[] counts = text.getCharacterFrequency("Hello");

      assertEquals(2, counts[(int)'l']);
   }

   /**
    * Test Huffman Tree
    */
   @Test
   public void test_HuffmanTree() {
      int[] counts = text.getCharacterFrequency("go go gopher");

      Tree huffmanTree = text.getHuffmanTree(counts);

      String[] codes = text.getCode(huffmanTree.getRoot());

      assertEquals("01", codes[(int)'o']);
   }
}