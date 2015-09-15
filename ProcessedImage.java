import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class ProcessedImage {

	// To prevent out of bound
	public int WIDTH;
	public int HEIGHT;
	// Actually image
	public BufferedImage image;

	// White and black integer values
	int white = (255 << 16) | (255 << 8) | 255;
	int black = -16777216;
	
	public ArrayList<LSegment> lines = new ArrayList<>();
	boolean flag = true;

	public ProcessedImage(int width, int height, BufferedImage image) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.image = image;
		//lines.add(null); // pay attention! not good solution
	}

	public int getWIDTH() {
		return WIDTH;
	}

	public int getHEIGHT() {
		return HEIGHT;
	}

	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Convert image in binary.
	 * 
	 * @param level
	 *            - contrast level
	 * @return image converted in binary
	 */
	public BufferedImage convertInBinary(int level) {

		// This lines are commented because of using of colored pixels for
		// monitoring. Uncomment it in ready version
		// BufferedImage imageBin = new BufferedImage(WIDTH, HEIGHT,
		// BufferedImage.TYPE_BYTE_BINARY);
		BufferedImage imageBin = image;
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				Color c = new Color(image.getRGB(j, i));
				int red = (int) (c.getRed() * 0.299);
				int green = (int) (c.getGreen() * 0.587);
				int blue = (int) (c.getBlue() * 0.114);
				imageBin.setRGB(j, i, (red + green + blue < level) ? black : white);
			}
		}
		return imageBin;
	}
	/**
	 * Convert image in grey scale
	 * @return converted in grey scale image
	 */
	public BufferedImage toGreyScale() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color newColor = new Color(red + green + blue, red + green
                        + blue, red + green + blue);
                image.setRGB(j, i, newColor.getRGB());
            }
        }
        return image;
    }

	/**
	 * Find initial pixel - part of searching object on image
	 * 
	 * @return initial coordinate of this object
	 */
	public Coordinate findDot() {
		Coordinate c = new Coordinate(-1, -1);
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				if (image.getRGB(j, i) == black) {
					c = new Coordinate(j, i);
					return c;
				}
			}
		}
		System.err.println("No objects more!");
		return c;
	}

	/**
	 * This method is searching for the beginning of inner contour of object.
	 * 
	 * @param in
	 *            - initial pixel of object
	 * @return initial pixel NEAR the inner contour. So it is pixel from white
	 *         area.
	 */
	public Coordinate findInnerContour(Coordinate in) {
		Coordinate c = new Coordinate(-1, -1);
		// Takes initial pixel coordinates and goes right-bottom direction
		for (int j = in.getX(), i = in.getY(); j < WIDTH && i < HEIGHT; j++, i++) {
			if (image.getRGB(j, i) != black) {
				c = new Coordinate(j, i);
				return c;
			}
		}
		System.err.println("No inner contour!");
		return c;
	}

	/**
	 * This method select inner contour of object. It uses bug contour selection 
	 * algorithm.
	 * @param init - initial pixel near inner contour
	 * @return array with contour pixels
	 */
	public ArrayList<Coordinate> selectContour(Coordinate init) {
		// Start searching contour from top pixel
		Coordinate temp = new Coordinate(init.getX(), init.getY() - 1);
		// List of contour pixels
		ArrayList<Coordinate> contour = new ArrayList<Coordinate>();
		// Save first pixel
		contour.add(init);
		// Because of intro pixel is white and temp pixel is on top
		// set top direction
		int direction = 0;
		// Loop before we find and close entire contour
		while (temp.equals(init) == false) {
			if (image.getRGB(temp.getX(), temp.getY()) == black) {
				// Select black pixels for contour
				contour.add(new Coordinate(temp.getX(), temp.getY()));
				direction += 45;
			} else {
				direction -= 45;
			}
			switch (direction) {
			// top
			case 0:
				temp = new Coordinate(temp.getX(), temp.getY() - 1);
				break;
			// right
			case 45:
				temp = new Coordinate(temp.getX() + 1, temp.getY());
				break;
			// bottom
			case 90:
				temp = new Coordinate(temp.getX(), temp.getY() + 1);
				break;
			// left
			case 135:
				temp = new Coordinate(temp.getX() - 1, temp.getY());
				break;
			// left
			case -45: // same as 135
				direction = 135;
				temp = new Coordinate(temp.getX() - 1, temp.getY());
				break;
			// top
			case 180: // same as 0
				direction = 0;
				temp = new Coordinate(temp.getX(), temp.getY() - 1);
				break;
			default:
				System.err.println("Invalid direction value!");
				break;
			}

		}
		return contour;
	}

	/**
	 * This method calculate the angle between two crossing line parts. 
	 * @param a cross coordinate of two lines
	 * @param b coordinate of the first line
	 * @param c coordinate of the second line
	 * @return angle between lines
	 */
	public double getAngle(Coordinate a, Coordinate b, Coordinate c) {
		// Create two vectors from this coordinates to calculate it with formula
		Coordinate vector1 = new Coordinate(b.getX() - a.getX(), b.getY() - a.getY());
		Coordinate vector2 = new Coordinate(c.getX() - a.getX(), c.getY() - a.getY());
		double angle = (vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY())
				/ (Math.sqrt(Math.pow(vector1.getX(), 2) + Math.pow(vector1.getY(), 2))
						* Math.sqrt(Math.pow(vector2.getX(), 2) + Math.pow(vector2.getY(), 2)));
		return angle;
	}
	/**
	 * Takes coordinates array and searching for the two points
	 * that have the longest distance between each other.
	 * @param coordinates 
	 * @return longest line segment 
	 */
	public LSegment getLongestLSegment(ArrayList<Coordinate> coordinates) {
		// Array with line segments created between different coordinates
		ArrayList<LSegment> segments = new ArrayList<LSegment>();
		// Array of distances
		ArrayList<Integer> distances = new ArrayList<>();
		ArrayList<Coordinate> object = coordinates;
		// Take coordinate and calculate and fin another coordinate 
		// with the longest distance
		for (int i = 0; i < object.size(); i++) {
			Coordinate max1 = object.get(i);
			Coordinate max2 = object.get(i);
			int distance = 0;
			for (Coordinate obj : object) {
				int dist = (int) Math
						.sqrt((Math.pow(obj.getX() - max1.getX(), 2) + Math.pow(obj.getY() - max1.getY(), 2)));
				if (dist > distance) {
					max2 = obj;
					distance = dist; // Store maximal distance in this moment
				}
			}
			distances.add(distance);
			segments.add(
					new LSegment(new Coordinate(max1.getX(), max1.getY()), new Coordinate(max2.getX(), max2.getY())));

		}
		Collections.sort(segments); // Should be removed
		/*
		 * Collections.sort(segments, new Comparator<LSegment>() { public int
		 * compare(LSegment arg0, LSegment arg1) { return arg0.compareTo(arg1);
		 * } });
		 */
		return Collections.max(segments);
	}

	/**
	 * 
	 * @param line
	 *            - sides separator
	 * @param contour
	 *            of object
	 * @param side:
	 *            -1 if on bottom, 1 if on top side
	 * @return array of contour points from given side
	 */
	public ArrayList<Coordinate> getHalf(LSegment line, ArrayList<Coordinate> contour, int side) {
		ArrayList<Coordinate> result = new ArrayList<>();
		if (side != -1 && side != 1) {
			System.err.println("Invalid side argument " + side + ". Can be only -1 or 1");
		}
		for (Coordinate point : contour) {
			if (getPosition(line, new Coordinate(point.getX(), point.getY())) == side) {
				result.add(point);
			}
		}
		return result;

	}

	/**
	 * @param line
	 * @param c
	 *            - point coordinate
	 * @return 1 if on top, -1 if on bottom, 0 if on line
	 */
	public int getPosition(LSegment line, Coordinate c) {
		Coordinate vector1 = new Coordinate(line.getB().getX() - line.getA().getX(),
				line.getB().getY() - line.getA().getY());
		Coordinate vector2 = new Coordinate(c.getX() - line.getA().getX(), c.getY() - line.getA().getY());
		double result = vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX();
		if (result < 0)
			return 1;
		else if (result > 0)
			return -1;
		return 0;
	}
	
	/**
	 * This method select object on image by initial pixel of this object
	 * @param c - initial pixel of object
	 * @return array with all object pixels
	 */
	public ArrayList<Coordinate> selectObject(Coordinate c) {
        ArrayList<Coordinate> object = new ArrayList<Coordinate>();
        ArrayList<Coordinate> toCheck = new ArrayList<Coordinate>();
        Coordinate temp;
        toCheck.add(c);

        while (!toCheck.isEmpty()) {
            temp = toCheck.get(toCheck.size() - 1);
            if ((temp.getX() != 0 && temp.getX() != WIDTH - 1)
                    && (temp.getY() != 0 && temp.getY() != HEIGHT - 1)) {
                image.setRGB(temp.getX(), temp.getY(), 13158600);
                object.add(temp);
                /* | | | |
                 * | | | |
                 * | |X| |
                 */
                if (image.getRGB(temp.getX(), temp.getY() - 1) == black
                        && !toCheck.contains(new Coordinate(temp.getX(), temp
                                .getY() - 1))
                        && !object.contains(new Coordinate(temp.getX(), temp
                                .getY() - 1))) {
                    toCheck.add(new Coordinate(temp.getX(), temp.getY() - 1));
                }
                /* | |X| |
                 * | | | |
                 * | | | |
                 */
                if (image.getRGB(temp.getX(), temp.getY() + 1) == black
                        && !toCheck.contains(new Coordinate(temp.getX(), temp
                                .getY() + 1))
                        && !object.contains(new Coordinate(temp.getX(), temp
                                .getY() + 1))) {
                    toCheck.add(new Coordinate(temp.getX(), temp.getY() + 1));
                }
                /* | | | |
                 * | | | |
                 * | | |X|
                 */
                if (image.getRGB(temp.getX() + 1, temp.getY() - 1) == black
                        && !toCheck.contains(new Coordinate(temp.getX() + 1,
                                temp.getY() - 1))
                        && !object.contains(new Coordinate(temp.getX() + 1,
                                temp.getY() - 1))) {
                    toCheck.add(new Coordinate(temp.getX() + 1, temp.getY() - 1));
                }
                /* | | |X|
                 * | | | |
                 * | | | |
                 */
                if (image.getRGB(temp.getX() + 1, temp.getY() + 1) == black
                        && !toCheck.contains(new Coordinate(temp.getX() + 1,
                                temp.getY() + 1))
                        && !object.contains(new Coordinate(temp.getX() + 1,
                                temp.getY() + 1))) {
                    toCheck.add(new Coordinate(temp.getX() + 1, temp.getY() + 1));
                }
                /* |X| | |
                 * | | | |
                 * | | | |
                 */
                if (image.getRGB(temp.getX() - 1, temp.getY() - 1) == black
                        && !toCheck.contains(new Coordinate(temp.getX() - 1,
                                temp.getY() - 1))
                        && !object.contains(new Coordinate(temp.getX() - 1,
                                temp.getY() - 1))) {
                    toCheck.add(new Coordinate(temp.getX() - 1, temp.getY() - 1));
                }
                /* | | | |
                 * | | | |
                 * |X| | |
                 */
                if (image.getRGB(temp.getX() - 1, temp.getY() + 1) == black
                        && !toCheck.contains(new Coordinate(temp.getX() - 1,
                                temp.getY() + 1))
                        && !object.contains(new Coordinate(temp.getX() - 1,
                                temp.getY() + 1))) {
                    toCheck.add(new Coordinate(temp.getX() - 1, temp.getY() + 1));
                }
                /* | | | |
                 * |X| | |
                 * | | | |
                 */
                if (image.getRGB(temp.getX() - 1, temp.getY()) == black
                        && !toCheck.contains(new Coordinate(temp.getX() - 1,
                                temp.getY()))
                        && !object.contains(new Coordinate(temp.getX() - 1,
                                temp.getY()))) {
                    toCheck.add(new Coordinate(temp.getX() - 1, temp.getY()));
                }
                /* | | | |
                 * | | |X|
                 * | | | |
                 */
                if (image.getRGB(temp.getX() + 1, temp.getY()) == black
                        && !toCheck.contains(new Coordinate(temp.getX() + 1,
                                temp.getY()))
                        && !object.contains(new Coordinate(temp.getX() + 1,
                                temp.getY()))) {
                    toCheck.add(new Coordinate(temp.getX() + 1, temp.getY()));
                }
            }
            toCheck.remove(temp);
        }

        return object;

    }
	
	/**
	 * This method calculate coordinate of projection from point onto line segment 
	 * (builds a perpendicular). Formulas which are used in this method were 
	 * derived from examples from website (russian): 
	 * http://www.cleverstudents.ru/line_and_plane/projection_of_point_onto_line.html
	 * @param a - first coordinate of line  segment
	 * @param b - second coordinate of line segment
	 * @param m - coordinate of point, from which we building a projection on line segment
	 * @return coordinate of projection on line segment from point m
	 */
	public Coordinate getPerpendicular(Coordinate a, Coordinate b, Coordinate m ){
		// Angular coefficient of line segment
		double coefLS = (double)(b.getY() - a.getY()) / (double)(b.getX() - a.getX());
		// Angular coefficient of perpendicular to line segment
		double coefP = -Math.pow(coefLS, -1);
		// Calculate coordinates of projection on line segment
		int x = (int) ((coefLS*a.getX()-a.getY() - coefP*m.getX()+m.getY())/(coefLS-coefP));
		int y = (int) ((coefLS*(m.getX() - m.getY()/coefP -a.getX())+a.getY())/(1- coefLS/coefP));
		return new Coordinate(x, y);
	}
	
	public LSegment getLongestProjection(ArrayList<Coordinate> list, LSegment line){
		ArrayList<LSegment> perpendiculars = new ArrayList<>();
		for (Coordinate co : list) {
			perpendiculars.add(new LSegment(getPerpendicular(line.getA(), line.getB(), co), co));
		}
		//Collections.sort(perpendiculars);
		return new LSegment(Collections.max(perpendiculars).getA(), Collections.max(perpendiculars).getB());
		
	}
	
	int index = 5;
	public ArrayList<LSegment> approximate(ArrayList<Coordinate> list, LSegment line, int side) {
			
		LSegment maxProj = this.getLongestProjection(list, line);
		//if (lines.contains(line))
		lines.remove(line);
		lines.add(new LSegment(maxProj.getB(), line.getA()));
		lines.add(new LSegment(maxProj.getB(), line.getB()));
		ArrayList<Coordinate> listR = getHalf(maxProj, list, side);
		ArrayList<Coordinate> listL = getHalf(maxProj, list, side*-1);
		
		for (Coordinate co : listR) {
			//image.setRGB(co.getX(), co.getY(), 205);
		}
		//for (Coordinate co : listL) {
			//image.setRGB(co.getX(), co.getY(), 205);
		//}
		
		
		LSegment line1 = new LSegment(maxProj.getB(), line.getA());
		LSegment line2 = new LSegment(maxProj.getB(), line.getB());
		
		//if (index !=0){
			index--;
			if (maxProj.getLength() > 10 && listR.size()>0){
				approximate(listR, line1, side*-1 );
			}
			if (maxProj.getLength() > 10 && listL.size()>0){
				approximate(listL, line2, side  );
			}
		//}
		
		
		/*if (flag == true){
			flag = false;
			System.out.println("true");
			approximate(listR, new LSegment(maxR.getB(), line.getA()), side );
			approximate(listL, new LSegment(maxR.getB(), line.getB()), side );
		}*/
		System.out.println(lines.size());
		return lines;
	}
	
	public void clear(){
		lines.clear();
	}

}