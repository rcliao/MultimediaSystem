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
	 * Constants
	 */
	
	public final int SIZE_PER_LETTER = 8;
	public final int MAX_SIZE_OF_DICTIONARY = 256;

	private File file;
	private String message;
	private int size;
	private int sizeAfterEncoded = 0;
	private int sizeOfDictonary = 256;

	private Map<Integer, String> lzwTable;

	/**
	 *	Constructors
	 */
	
	public TextModel() {

	}

	/**
	 *	Getters/Setters
	 */
	
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
	 * 	Text Compression Methods
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
	 * @param inputText         input message before encoded
	 * @param maxDictionarySize the dictionary size
	 */
	public String lzwEncoding(String inputText, int maxDictionarySize) {
		// initiate the dictionary
		Map<Integer, String> dictionary = initDictionary(inputText, maxDictionarySize);

		size = inputText.length() * SIZE_PER_LETTER;
		sizeAfterEncoded = 0;

		// Recursive solving the lzw encoding
		return lzwEncodingHelper(inputText, "", maxDictionarySize, dictionary);
	}

	/**
	 * Recursive way of solving the lzw encoding problem
	 * @param input       			input String
	 * @param maxSize     			dictionary size
	 * @param result      			final result
	 * @param Map<Integer, String>	dictionary
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

			int indexSize = 0;

			for (int i = 0; i <= 8; i ++) {
				if (Math.pow(2, i) == maxSize) {
					indexSize = i;
				}
			}

			lzwTable = dictionary;

			sizeAfterEncoded += indexSize;

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

				int indexSize = 0;

				for (int i = 0; i <= 8; i ++) {
					if (Math.pow(2, i) == maxSize) {
						indexSize = i;
					}
				}

				sizeAfterEncoded += indexSize;

				return encodedString + " " + lzwEncodingHelper(input, "", maxSize, dictionary);
			}
		}
	}

	/**
	 * LZW Decoding
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
}