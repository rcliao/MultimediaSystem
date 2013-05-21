/**
 * Text Model class
 * Doing all the text convertion and storage work
 */
package models;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class TextModel {
	/**
	 * Assume the size per letter is 8
	 */
	public final int SIZE_PER_LETTER = 8;
	/**
	 * Max size of the dictionary should be 256
	 */
	public final int MAX_POWER_OF_2_DICTIONARY = 8;

	private File file;
	private String message;
	private int size;
	private int sizeAfterEncoded = 0;
	private int sizeOfDictonary = 256;

	private Map<Integer, String> lzwTable;
	
	public TextModel() {

	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}


	public String getMessage() {
		return message;
	}
	 
	public void setMessage(String message) {
		this.message = message;
	}


	public int getSize() {
		return size;
	}
	 
	public void setSize(int size) {
		this.size = size;
	}


	public int getSizeAfterEncoded() {
		return sizeAfterEncoded;
	}
	 
	public void setSizeAfterEncoded(int sizeAfterEncoded) {
		this.sizeAfterEncoded = sizeAfterEncoded;
	}


	public int getSizeOfDisctionary() {
		return sizeOfDictonary;
	}
	 
	public void setSizeOfDisctionary(int sizeOfDictonary) {
		this.sizeOfDictonary = sizeOfDictonary;
	}


	public Map<Integer, String> getLzwTable() {
		return lzwTable;
	}
	 
	public void setLzwTable(Map<Integer, String> lzwTable) {
		this.lzwTable = lzwTable;
	}

	/**
	 * read the text file and store the input string to the input message
	 * 
	 * @param file input text file
	 */
	public void readFile(File file) {
		this.file = file;

		try {
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			DataInputStream dis = new DataInputStream(inputStream);
			message = "";
			while (dis.available() != 0) {
				message = message + dis.readLine();
			}
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * LZW Pattern Substitution (Text Compression)
	 * 
	 * @param inputText         input message before encoded
	 * @param maxDictionarySize the dictionary size
	 */
	public String lzwEncoding(String inputText, int maxDictionarySize) {
		// initiate the dictionary
		Map<Integer, String> dictionary = initDictionary(inputText, maxDictionarySize);

		size = inputText.length() * SIZE_PER_LETTER;
		sizeAfterEncoded = 0;

		String result = lzwEncodingHelper(inputText, "", maxDictionarySize, dictionary);

		int indexSize = 8;

		for (int i = MAX_POWER_OF_2_DICTIONARY; i >= 0; i --) {
			if (Math.pow(2, i) <= dictionary.size()) {
				break;
			}
			indexSize = i;
		}

		sizeAfterEncoded *= indexSize;

		return result;
	}

	/**
	 * Recursive way of solving the lzw encoding problem
	 * 
	 * @param input       			input String
	 * @param maxSize     			dictionary size
	 * @param result      			final result
	 * @return String - Encoded Message
	 */
	public String lzwEncodingHelper(String input, String letter, int maxSize, Map<Integer,String> dictionary) {
		// base case when the input is empty
		if (input.isEmpty()) {
			// print the table to the textarea
			Iterator<Integer> iter = dictionary.keySet().iterator();

			String encodedString = "";

			// find the longest value and encoded the string
			while (iter.hasNext()) {
				Integer index = iter.next();
				String value = dictionary.get(index);
				if (value.equals(letter)) {
					encodedString = String.valueOf(index);
				}
			}

			lzwTable = dictionary;

			sizeAfterEncoded += 1;

			return encodedString;
		}
		// recursive case
		else {
			/** getting the first letter out of the original text */
			String firstLetter = input.substring(0, 1);
			/** conc with the letter */
			String newLetter = letter + firstLetter;
			/** if the dictionary contains this value, then keep recursive call */
			if (dictionary.containsValue(newLetter)) {
				return lzwEncodingHelper(input.substring(1, input.length()), newLetter, maxSize, dictionary);
			}
			/** else find the longest value from the  */
			else {
				// print the table to the textarea
				Iterator<Integer> iter = dictionary.keySet().iterator();

				String encodedString = "";

				// find the longest value and encoded the string
				while (iter.hasNext()) {
					Integer index = iter.next();
					String value = dictionary.get(index);
					if (value.equals(letter)) {
						encodedString = String.valueOf(index);
					}
				}

				if (dictionary.size() < maxSize) {
					// create the new entry
					String newEntry = letter + input.substring(0, 1);

					dictionary.put(dictionary.size(), newEntry);
				} else {

				}

				sizeAfterEncoded += 1;

				return encodedString + " " + lzwEncodingHelper(input, "", maxSize, dictionary);
			}
		}
	}

	/**
	 * LZW Decoding
	 * 
	 * @param  input             input String
	 * @param  maxDictionarySize the dictionary size
	 * @return                   decoded decoded string
	 */
	public String lzwDecoding(Map<Integer, String> dictionary, String input, int maxDictionarySize) {
		String[] indexes = input.split(" ");

		return lzwDecodingHelper(indexes, "", maxDictionarySize, dictionary);
	}

	public String lzwDecodingHelper(String[] indexes, String letter, int maxSize, Map<Integer, String> dictionary) {
		String result = "";

		for (int i = 0; i < indexes.length; i ++) {
			if (dictionary.containsKey(Integer.valueOf(indexes[i]))) {
				String entry = dictionary.get(Integer.valueOf(indexes[i]));
				result += entry;
				String nextEntry;
				if (i + 1 < indexes.length) {
					if (dictionary.containsKey(Integer.valueOf(indexes[i+1])))
						nextEntry = entry + dictionary.get(Integer.valueOf(indexes[i+1]));
					else
						nextEntry = entry + entry.substring(0, 1);
					dictionary.put(dictionary.size(), nextEntry);
				}
			}
		}

		return result;
	}

	/**
	 * initialize the dictionary to contain all the single symbles
	 * 
	 * @param  inputText         [input message]
	 * @param  maxDictionarySize [dictionary size]
	 * @return                   [return the dictionary]
	 */
	public Map<Integer, String> initDictionary(String inputText, int maxDictionarySize) {
		Map<Integer, String> result = new TreeMap<Integer, String>();

		for (int i = 0; i < inputText.length(); i ++) {
			String single = inputText.substring(i, i + 1);
			if (!result.containsValue(single)){
				result.put(result.size(), single);
			}
		}

		return result;
	}

	/**
	 * Given the root of huffman tree, translate the huffman tree into Array of String to get the binary code of the tree
	 */
	public String[] getCode(Tree.Node root) {
		if (root == null)
			return null;
		String[] codes = new String[2 * 128];
		assignCode(root, codes);
		return codes;
	}
	
	/**
	 * Given Huffman tree, assign the binary code to the nodes
	 * 
	 * @param root  The root of Huffman tree
	 * @param codes The code of the parent
	 */
	private void assignCode(Tree.Node root, String[] codes) {
		if (root.left != null) {
			root.left.code = root.code + "0";
			assignCode(root.left, codes);
			
			root.right.code = root.code + "1";
			assignCode(root.right, codes);
		}
		else {
			codes[(int)root.element] = root.code;
		}
	}
	
	/**
	 * Given the frequency of characters, create a huffman tree
	 * 
	 * @param  counts Frequency of character
	 * @return        Huffman tree
	 */
	public Tree getHuffmanTree(int[] counts) {
		Heap<Tree> heap = new Heap<Tree>();
		for (int i = 0; i < counts.length; i ++) {
			if (counts[i] > 0)
				heap.add(new Tree(counts[i], (char)i));
		}
		
		while (heap.getSize() > 1) {
			Tree t1 = heap.remove();
			Tree t2 = heap.remove();
			heap.add(new Tree(t1, t2));
		}
		
		return heap.remove();
	}
	
	/**
	 * Given the input sentence, get the frequency of the characters in the sentence
	 */
	public int[] getCharacterFrequency(String text) {
		int[] counts = new int[256];
		
		for (int i = 0; i < text.length(); i ++)
			counts[(int)text.charAt(i)]++;
		
		return counts;
	}

}