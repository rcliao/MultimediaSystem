CS 451 MultimediaSystem
=======================

CS 451 Multi-Media system Homework 1 will be focusing on image conversion.

## Dependencies

1. (Apache Ant)[http://ant.apache.org/]
2. (Java)[http://www.java.com/en/]
3. (JUnit & hamcrest-core)[https://github.com/junit-team/junit/wiki/Download-and-Install]

## How to run this program

1. Compile the main class(CS451_Liao.java) by  
```
javac CS451_Liao.java
```
2. Run the main class(CS451_Liao), which will lead user to the GUI program by  
```
java CS451_Liao
```

3. The gui will provide the menu bar for user to test the function. Here is the control flow to each function

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
```

## How to run unit test

1. Download junit.jar and hamcrest-core.jar
2. Put *junit.jar* and *hamcrest-core.jar* to be under C:\JUnit\
3. Set `CLASSPATH` Environment Variable to be `C:\JUnit\junit.jar; C:\JUnit\hamcrest-core.jar`
4. Compile unit test main class by `javac CS451_Test.java`
5. Run unit test main class by `java CS451_Test`

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
		/lib/
			junit.jar 				--> JUnit library
			hamcrest-core.jar 		--> JUnit library
		/bin/
			
		README.md 				--> Read me file
