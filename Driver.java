import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.applet.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.plaf.SliderUI;

public class Driver extends Applet {

	// Location of image
	static String FILENAME = "test_circle.jpg";
	public BufferedImage image;
	ProcessedImage pimage;
	Image myImage; // draw line
	Image img;
	static int WIDTH;
	static int HEIGHT;
	static int[] dataBuffInt;
	boolean flag = true;

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
		ArrayList<Coordinate> listRHalf = pimage.getHalf(segment, list, 1);
		ArrayList<Coordinate> listLHalf = pimage.getHalf(segment, list, -1);

		ArrayList<LSegment> linesL = new ArrayList<>();
		linesL.addAll(pimage.approximate(listLHalf, segment, -1));
		pimage.clear();

		ArrayList<LSegment> linesR = new ArrayList<>();
		linesR.addAll(pimage.approximate(listRHalf, segment, 1));

		Graphics2D g2d = image.createGraphics();

		// Draw on the buffered image
		g2d.setColor(Color.red);

		ArrayList<LSegment> segments = new ArrayList<>();

		segments.addAll(linesR);
		segments.addAll(linesL);
		ArrayList<LSegment> segmentsSorted = new ArrayList<>();
		segmentsSorted.addAll(segment.sortLines(segments));
		// segmentsSorted.add(segments.get(segments.size()-1));
		System.out.println(segments);
		segmentsSorted.remove(0);
		System.out.println(segmentsSorted);
		ArrayList<Color> colors = new ArrayList<>();

		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		colors.add(Color.YELLOW);
		colors.add(Color.CYAN);
		colors.add(Color.ORANGE);
		colors.add(Color.RED);
		colors.add(Color.DARK_GRAY);
		colors.add(Color.PINK);
		colors.add(Color.MAGENTA);
		colors.add(Color.GRAY);
		colors.add(Color.LIGHT_GRAY);
		
		for (int j = 0; j < segmentsSorted.size()-1; j++) {
			if (!segmentsSorted.get(j).getA().equals(segmentsSorted.get(j+1).getB())) {
				segmentsSorted.set(j+1, new LSegment(
						segmentsSorted.get(j+1).getB(), segmentsSorted.get(j+1).getA()));
			}else
			if (!segmentsSorted.get(j).getB().equals(segmentsSorted.get(j+1).getA())) {
				segmentsSorted.set(j+1, new LSegment(
						segmentsSorted.get(j+1).getA(), segmentsSorted.get(j+1).getB()));
			}
		}
		
		for (int j = 0; j < segmentsSorted.size(); j++) {
			if (segmentsSorted.get(j).getLength() < 20) {
				segmentsSorted.remove(j);
				j--;
			}
		}
				
		int i = 0;
		for (LSegment line : segmentsSorted) {
			System.out.println(line);
			g2d.setColor(Color.RED);
			g2d.setColor(colors.get(i));
			g2d.drawLine(line.getA().getX(), line.getA().getY(), line.getB().getX(), line.getB().getY());
			i++;
			
		}

		ArrayList<Double> angles = new ArrayList<>();
		//int j = 0;
		for (int j = 0; j < segmentsSorted.size() - 1; j++) {
			int num = j;
			LSegment line1 = segmentsSorted.get(num);
			LSegment line2 = segmentsSorted.get(num + 1);
			// Jump over too little line segments
			angles.add(180-Math.toDegrees(Math.acos(pimage.getAngleLine(line1, line2))));
		}
		
		angles.add(180-Math.toDegrees(Math.acos(pimage.getAngleLine(segmentsSorted.get(0), 
				segmentsSorted.get(segmentsSorted.size() - 1)))));

		for (double num : angles) {
			System.out.println(num);
		}
		if (Collections.max(angles) - Collections.min(angles) < 10){
			System.out.println("Yes");
		}else{
			System.out.println("No!");
		}

		g2d.dispose();

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
		System.out.println("Elapsed time: " + elapsedTime);
		System.exit(0);
	}

	public void openImage() throws MalformedURLException, IOException {
		BufferedImage img = ImageIO.read(new File(FILENAME));
		WIDTH = img.getWidth();
		HEIGHT = img.getHeight();
		pimage = new ProcessedImage(WIDTH, HEIGHT, img);
	}

	public void paint(Graphics gr) {
		// gr.drawImage(img, 0, 0, this);
		// Can be used for results demonstration
		// gr.setColor(Color.RED);
		// gr.drawLine(0, 0, 100, 100);
		gr.setColor(Color.RED);

		// for (LSegment co : lines) {
		// drawLineSegment(co);
		// }
	}

	public void drawLineSegment(LSegment line) {
		this.getGraphics().setColor(Color.RED);
		this.getGraphics().drawLine(line.getA().getX(), line.getA().getY(), line.getB().getX(), line.getB().getY());
		this.repaint();
	}

}