CS 451 MultimediaSystem
=======================

## Current Schedule:

### Homework 3:  
Homework 3 will perform the following methods: Modified JPEG Compression as steps

1. Resize and De-resize
2. Color Transform and Sub-Sampling, inverse color transform and super-sampling
3. DCT and inverse DCT
4. Quantization and De-Quantization
5. Calculate Compression Ratio


## How to run this program

### Using exectubable jar file

Double click on dist\CS451_Liao.jar  
or  
in command promt type in  
`java -jar dist/CS451_Liao.jar`

### Using command prompt

1. Direct yourself to the location of source folder by  
```
cd src\
```
2. Compile the main class(CS451_Liao.java) by  
```
javac CS451_Liao.java
```
3. Run the main class(CS451_Liao), which will lead user to the GUI program by  
```
java CS451_Liao
```

### For professor

Homework 3 requirements can be performed as following flow:

#### JPEG Compression Ratio

1. Test resize and de-resize by using De-Resize by `JPEG Compression -> De Resize`  

		Explanation: This step will create identical image after de-resize

2. Color Transform and sub-sampling with inverse color transform and super-sampling by `JPEG Compression -> Color Transform`  

		Explanation: This inverse step also involve the resize and deresize, which at the end, it will return the identical image as well

3. DCT and inverse DCT by `JPEG Compression -> DCT Transform`  

		Explanation: This step will perform step 1 and 2 with step 3 in sequence to perform the correct compress and inverse.

4. Quantization and De-Quantization by `JPEG Compression -> Quantization`  

		Explanation: This step will do all the jpeg compression without calculating the compression ratio

5. JPEG Compression all in one by `JPEG Compression -> JPEG Compression`  

		By the end of compression, it will also calculate the compression ratio



## GUI Logic Flow

The gui will provide the menu bar for user to test the function. Here is the control flow to each function

```
File 	->	Load
		->	Save
		->	Quit

Image 	->	Gray Scale
		->	Bi-Scale(Black and White)	->	Directly
										->	Error-Diffusion(Floyd)
										->	Error-Diffusion(Bell)
										->	Error-Diffusion(Stucki)
		-> Quad-Level
		-> 8-Bits	->	Uniform Color Quantization
					->	Median Cut Color Quantization
					->	Median Cut Color Quantization(Error-Diffusion(Floyd))
					->	Median Cut Color Quantization(Error-Diffusion(Bell))
					->	Median Cut Color Quantization(Error-Diffusion(Stucki))
Text 	->	LZW Encoding
ImageTest	->	Create Circle
			->	Aliasing
JPEG Compression	-> 	Resize Image
					-> 	De Resize Image
					-> 	Color Transform
					-> 	DCT Transform
					-> 	Quantization
					-> 	JPEG Compression
```

## Dependencies

1. [Apache Ant](http://ant.apache.org/ "Apache Ant Official Website")
2. [Java JDK](http://www.java.com/en/ "Java official website")
3. [JUnit & hamcrest-core](https://github.com/junit-team/junit/wiki/Download-and-Install "JUnit official website")

## File Structure

	MultimediaSystem/			-->	Main folder
		/src/
			/ctrl/ 					--> controller package
				Controllers.java 	-->	Controller to bind view and model together
			/ImageUtils/			--> Utilities methods for image preview on file chooser
				ImageFilter.java 	--> Used to filter the image extension for file chooser
				ImagePreview.java 	--> Used to display the preview image from file chooser
				Utils.java 			--> Methods being used from ImageFilter.java and ImagePreview.java
			/models/
				Heap.java 			--> Used for Huffman Coding
				ImageModel.java 	--> Image model class (image convertions, methods)
				JPEGImage.java 		--> JPEG Compression methods
				TextModel.java 		--> Text model class (Text compression)
				Tree.java 			--> Used for Huffman coding
			/test/
				ImageModelTest.java --> Test Image methods
				JPEGImageTest.java 	--> Test JPEG Image Compression
				ModelSuite.java		--> Test models
				TextModelTest.java 	--> Test case for the text model
			/views/
				Views.java 			--> View class (GUI related)
			CS451_Liao.java 		--> Main class
			CS451_Test.java 		--> Main class for testing
		/lib/					--> extra library
			junit.jar 				--> JUnit library
			hamcrest-core.jar 		--> JUnit library
		/bin/ 					--> Binary Folder (where .class are)
			...						--> Classes
		/docs/					--> Java Docs Generated API Documents
			index.html 				--> Click here to check the api documents
		/testreport/			--> Report relating to unit tests

		README.md 				--> Read me file
