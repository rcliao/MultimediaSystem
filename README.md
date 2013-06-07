CS 451 MultimediaSystem
=======================

## Current Schedule:

### Homework 4 - Block-based Motion Compensation 

1. Block-based Motion Compensation
2. Removing Moving Objects


## How to run this program

### Using executable jar file

Double click on dist\CS451_Liao.jar  
or  
in command prompt type in  
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

#### Motion Compensation

##### Block-Based motion Compensation

1. Read the two image file first (Reference image then target Image) by `Motion -> Read Motion Image`

2. do block-based motion compensation by `Motion -> Block-based Motion Compensation`

		This will display the error frame and mv.txt in the tab

###### I complete half pixel as extra credit as well, therefore, it might take a while to do motion compensation

##### Remove Moving Objects

###### Dependencies: Require to put IDB folder(containing all the Walk frames) under the same directory

1. Read the input image frame number by `Motion -> Removing moving object`

		This will ask for the input for the input frame number

2. Then the result will display all the required outputs(1 frame using n-2 as reference and another one using 5th frame as reference)

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
Motion	-> 	Read Motion Images
		-> 	Block-based Motion Compensation
		-> 	Removing moving objects
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
				Motion.java 		--> Motion detection class
			/test/
				ImageModelTest.java --> Test Image methods
				JPEGImageTest.java 	--> Test JPEG Image Compression
				ModelSuite.java		--> Test models
				MotionTest.java 	--> Test Motion class
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
