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
		
		
		
		LSegment segment = pimage.getLongestLSegment(list);
		// show longest distance points
		image.setRGB(segment.getA().getX(), segment.getA().getY(), 16711680);
		image.setRGB(segment.getB().getX(), segment.getB().getY(), 16711680);
		
		
		LSegment segment2 = new LSegment(new Coordinate(0,100), new Coordinate(100,0));
		//image.setRGB(segment2.getA().getX(), segment2.getA().getY(), 16711680);
		//image.setRGB(segment2.getB().getX(), segment2.getB().getY(), 16711680);
		
		Coordinate per = pimage.getPerpendicular(segment.getA(), 
				segment.getB(), new Coordinate(150, 10));
		image.setRGB(150, 10, 16711680);
		System.out.println(per.getX() + " " + per.getY());
		image.setRGB(per.getX(), per.getY(), 205);
		
		// show half of contour
		for (Coordinate co : pimage.getHalf(segment, list, 1)) {
			image.setRGB(co.getX(), co.getY(), 205);
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
		System.out.println(elapsedTime);
		System.exit(0);
	}

	public void openImage() throws MalformedURLException, IOException {
		System.out.println(FILENAME);
		BufferedImage img = ImageIO.read(new File(FILENAME));
		WIDTH = img.getWidth();
		HEIGHT = img.getHeight();
		pimage = new ProcessedImage(WIDTH, HEIGHT, img);
	}

	public void paint(Graphics gr) {
		gr.drawImage(img, 0, 0, this);
	}

}