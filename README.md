CS 451 MultimediaSystem
=======================

CS 451 Multi-Media system Homework 1 will be focusing on image conversion.

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
File 	->	Load	=>	This will open a file chooser to let user to pick which image to load
		->	Save	=>	This will save the current image from the file chooser
		->	Quit	=>	This will quit the program

Image 	->	Gray Scale	=>	This will transform the current image to the gray Scale
		->	Bi-Scale(Black and White)	->	Directly				=>	This will convert to the 1-bit image directly
										->	Error-Diffusion(Floyd)	=>	Applying Floyd formula to the error diffusion
										->	Error-Diffusion(Bell)	=>	Applying Bell formula to the error diffusion
										->	Error-Diffusion(Stucki)	=>	Applying Stucki formula to the error diffusion
		-> Quad-Level	=>	This will convert the image to the 2-bit scale image by applying error diffusion (Floyd)
		-> 8-Bits	->	Uniform Color Quantization										=>	Create a Look Up Table(static) and use table to convert the image to index value only and display it, 
																	and save index file as [filename]-index.ppm and continue converting index.ppm to 8-bit image
					->	Median Cut Color Quantization									=>	Applying Median Cut Algorithm to the image
					->	Median Cut Color Quantization(Error-Diffusion(Floyd))			=>	Applying Median Cut Algorithm with error diffusion(Floyd)
					->	Median Cut Color Quantization(Error-Diffusion(Bell))			=>	Applying Median Cut Algorithm with error difufsion(Bell)
					->	Median Cut Color Quantization(Error-Diffusion(Stucki))			=>	Applying Median Cut Algorithm with error difufsion(Stucki)
```
