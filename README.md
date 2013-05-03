CS 451 MultimediaSystem
=======================

## Current Schedule:

Homework 2:  
Homework 2 will perform the following methods: 1. Aliasing and 2. LZW Encoding

## How to run this program

### For professor

Homework 2 requirements can be performed as following flow:

##### Part 1 Aliasing:

1. Creating the circle using input M and N  
`Click on ImageTest -> Create Ciccle`  
System will pop out a __dialog__ to ask for input of M and N
2. Applying Aliasing  
`Click on ImageTest -> Aliasing`  
System will pop out a __dialog__ to ask for input of K
3. Clicking on __tabs__(Circle_M_N, No Filter, Average, Filter1, Filter2) to see each output(s)

###### Finding from part 1 Aliasing

* As result, average > filter2 > filter1 > no filter for K = 2.
* For K = 4, average > filter2 >= filter1 > no filter.
* As width of circle line grows, it becomes harder and harder to tell the artifact just from the resized image.
* Although average always performs the best result, the image still turns out to be blury after zoom back to 512*512 size.

##### Part 2 LZW Encoding:

1. Load the text file  
`Click on the File -> Load File` -> Choose a `.txt` extension file  
After loading the `.txt` file, the system will display the input text on both input and output panel and enable the text menu  
2. Applying LZW Encoding and Decoding
`Click on the Text -> LZW Encoding`  
The system will ask for input of max size of the dictionary  
After applying the LZW Encoding, the central panel will have 5 tabs(Input Text, LZW Encoding, LZW Table, Compression Ratio, Decoded Message)  
Please click on each panel to see the output

### Using Apache Ant

1. Download the Ant from (website)[http://ant.apache.org/]
2. Set up the environments for the apache ant as below
    1. Set up environment variable `ANT_HOME` to be **location of the ant folder**
    2. Include **%ANT_HOME%/bin/;** in the end of `PATH`
    3. Test ant has been installed by typing the following command in command prompt `ant -version`
3. Direct yourself to the location of this project
4. Type in `ant`　from the command prompt under the location of this project to deploy everything
5. Double click `dist\CS451_Liao.jar`

### Using command prompt

1. Compile the main class(CS451_Liao.java) by  
```
javac CS451_Liao.java
```
2. Run the main class(CS451_Liao), which will lead user to the GUI program by  
```
java CS451_Liao
```

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
				ImageModel.java 	--> Image model class (image convertions, methods)
				TextModel.java 		--> Text model class (Text compression)
			/test/
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

		README.md 				--> Read me file
