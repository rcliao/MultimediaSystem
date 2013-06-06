package test;

import java.io.*;
import org.junit.* ;
import static org.junit.Assert.* ;

import java.util.*;

import models.*;
import models.JPEGImage.*;
import models.ImageModel.*;

public class MotionTest {
	
	private File targetImage;
	private File sourceImage;

	private Motion motion;

	@Before
	public void initMotion() {
		targetImage = new File("IDB//Walk_060.ppm");
		sourceImage = new File("IDB//Walk_057.ppm");
		motion = new Motion(sourceImage, targetImage);
	}

	@Test
	public void testDivide() {
		motion.divideMacroBlocks();

		assertEquals(12*9, motion.getBlocks().size());
	}
}