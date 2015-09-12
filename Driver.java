import java.awt.*;
import java.applet.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;

public class Driver extends Applet {

	// Location of image
	static String FILENAME = "circle.jpg";
	public BufferedImage image;
	ProcessedImage pimage;
	Image myImage; //draw line
	Image img;
	static int WIDTH;
	static int HEIGHT;
	static int[] dataBuffInt;

	int white = (255 << 16) | (255 << 8) | 255;
	int black = -16777216;
	
	public static void main(String[] args) {
		Driver dr = new Driver();
		dr.init();
	}
	public void init() {

		long startTime = System.currentTimeMillis();
		try {
			openImage();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Convert In grey scale and change contrast
		image = pimage.convertInBinary(100);
		// Find pixel coordinate for inner contour inside of object
		Coordinate intro = pimage.findInnerContour(pimage.findDot());
		// Get all pixels of inner contour
		ArrayList<Coordinate> list = pimage.selectContour(intro);
		
		
		// get first longest line
		LSegment segment = pimage.getLongestLSegment(list);
		// get half side of this line
		ArrayList<Coordinate> listHalf = pimage.getHalf(segment, list, 1);
		// get longest perpendicular
		LSegment max = pimage.getLongestProjection(listHalf, segment);
		// get half side of this line
		ArrayList<Coordinate> listHalf2 = pimage.getHalf(max, listHalf, 1);
		
		LSegment segment2 = new LSegment(segment.getA(), max.getB());
		image.setRGB(segment2.getA().getX(), segment2.getA().getY(), 16711680);
		image.setRGB(segment2.getB().getX(), segment2.getB().getY(), 16711680);
		
		LSegment max2 = pimage.getLongestProjection(listHalf2, segment2);
		// get half side of this line
		ArrayList<Coordinate> listHalf3 = pimage.getHalf(max2, listHalf2, 1);
		
		for (Coordinate co : listHalf3) {
			image.setRGB(co.getX(), co.getY(), 16711680);
		}
	
		// Create and generate image
		dataBuffInt = image.getRGB(0, 0, WIDTH, HEIGHT, null, 0, WIDTH);
		img = createImage(new MemoryImageSource(WIDTH, HEIGHT, dataBuffInt, 0, WIDTH));
		// Save image locally
		File f = new File("MyFile.png");
		try {
			ImageIO.write((RenderedImage) image, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("Elapsed time: "+elapsedTime);
		//System.exit(0);
	}

	public void openImage() throws MalformedURLException, IOException {
		BufferedImage img = ImageIO.read(new File(FILENAME));
		WIDTH = img.getWidth();
		HEIGHT = img.getHeight();
		pimage = new ProcessedImage(WIDTH, HEIGHT, img);
	}

	public void paint(Graphics gr) {
		gr.drawImage(img, 0, 0, this);
		// Can be used for results demonstration
		// gr.setColor(Color.RED);
		// gr.drawLine(0, 0, 100, 100);
	}

}