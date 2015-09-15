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

public class Driver extends Applet {

	// Location of image
	static String FILENAME = "circle.jpg";
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
		System.out.println("size "+linesL.size());
		
		ArrayList<LSegment> linesR = new ArrayList<>();
		linesR.addAll(pimage.approximate(listRHalf, segment, 1));

		Graphics2D g2d = image.createGraphics();

		// Draw on the buffered image
		g2d.setColor(Color.red);
		
	
		for (LSegment line : linesL) {
			g2d.setColor(Color.RED);
			g2d.drawLine(line.getA().getX(), line.getA().getY(), line.getB().getX(), line.getB().getY());
		}
		
		for (LSegment line : linesR) {
			g2d.setColor(Color.BLUE);
			g2d.drawLine(line.getA().getX(), line.getA().getY(), line.getB().getX(), line.getB().getY());
		}
		
		//
		int index = 1;
		g2d.setColor(Color.RED);
		//g2d.drawLine(linesR.get(index).getA().getX(), linesR.get(index).getA().getY(),linesR.get(index).getB().getX(), linesR.get(index).getB().getY());
		
		//ArrayList<Coordinate> listtestR = pimage.getHalf(linesR.get(index), list, -1);
		//for (Coordinate co : listtestR) { 
			//image.setRGB(co.getX(), co.getY(), Color.GREEN.getRGB()); 
		//}
		//LSegment maxProj = pimage.getLongestProjection(listtestR, linesR.get(index));
		//image.setRGB(maxProj.getA().getX(), maxProj.getA().getY(), Color.ORANGE.getRGB()); 
		//image.setRGB(maxProj.getB().getX(), maxProj.getB().getY(), Color.ORANGE.getRGB());
		//
		
		g2d.dispose();

		// get longest perpendicular
		// LSegment max = pimage.getLongestProjection(listHalf, segment);
		// get half side of this line

		// lines = pimage.approximate(list, segment);

		/*
		 * ArrayList<Coordinate> listHalf2 = pimage.getHalf(max, listHalf, 1);
		 * 
		 * LSegment segment2 = new LSegment(segment.getA(), max.getB());
		 * image.setRGB(segment2.getA().getX(), segment2.getA().getY(),
		 * 16711680); image.setRGB(segment2.getB().getX(),
		 * segment2.getB().getY(), 16711680);
		 * 
		 * LSegment max2 = pimage.getLongestProjection(listHalf2, segment2); //
		 * get half side of this line ArrayList<Coordinate> listHalf3 =
		 * pimage.getHalf(max2, listHalf2, 1);
		 * 
		 * for (Coordinate co : listHalf3) { image.setRGB(co.getX(), co.getY(),
		 * 16711680); }
		 */

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