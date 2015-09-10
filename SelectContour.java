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

public class SelectContour extends Applet {

    // Location of image
    static String FILENAME = "circle.jpg";
    public BufferedImage image;

    Image img;
    static int WIDTH;
    static int HEIGHT;
    static int[] dataBuffInt;

    int white = (255 << 16) | (255 << 8) | 255;
    int black = -16777216;

    public SelectContour() {
		// TODO Auto-generated constructor stub
	}
    public void analise(String filename) {
    	long startTime = System.currentTimeMillis();
        try {
            openImage();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Convert In grey scale and change contrast
        image = convertInBinary(image);
        // Find pixel coordinate for inner contour inside of object 
        Coordinate intro = findInnerContour(findDot(image), image);
        // Get all pixels of inner contour
        ArrayList<Coordinate> list = selectContour(intro, image);
        // Fill this contour with color
        for (Coordinate obj : list) {
            //image.setRGB(obj.getX(), obj.getY(), 16711680);
        }
        
        /*
         * Begin of monitoring block
         */
        //show longest distance points
        LSegment segment =getLongestLSegment(list);
        System.out.println(segment.toString());
        image.setRGB(segment.getA().getX(), segment.getA().getY(), 16711680);
        image.setRGB(segment.getB().getX(), segment.getB().getY(), 16711680);
        
        //getHalf(segment, list, 1);
        for (Coordinate co: getHalf(segment, list, 1) ){
            image.setRGB(co.getX(), co.getY(), 205);
        }
        
        //another test
        ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<Coordinate> object = list;
        for (int i = 0; i < object.size(); i++) {
            Coordinate max1 = object.get(i);
            Coordinate max2 = object.get(i);
            int distance = 0;
            for (Coordinate obj : object) {
                int dist = (int) Math.sqrt( (Math.pow(obj.getX() - max1.getX(), 2)
                        + Math.pow(obj.getY() - max1.getY(), 2)));
                if (dist > distance) {
                    max2 = obj;
                    distance = dist;
                }
            }
            //image.setRGB(max1.getX()+1, max1.getY(), -16776961);
            //image.setRGB(max2.getX()+1, max2.getY(), -16776961);
            distances.add(distance);
        }
        // Get angels
        ArrayList<Double> angles = new ArrayList<>();
        for (int i = 5; i < object.size()-5; i+=5) {
            //image.setRGB(object.get(i).getX(), object.get(i).getY(), 16711680);
            angles.add( getAngle(object.get(i), object.get(i-5), object.get(i+5)) );
            if(angles.get(angles.size()-1) < 0.8 && angles.get(angles.size()-1) > -0.8){
                //image.setRGB(object.get(i).getX(), object.get(i).getY(), 16711680);
            }
        }
        //Show results
        Collections.sort(distances); 
        /*System.out.println(distances.toString());
        System.out.println(Collections.max(distances) + " " + Collections.min(distances));
        System.out.println(angles.toString());*/
        /*
         * End of mon block 
         */
        
        // Create and generate image
        dataBuffInt = image.getRGB(0, 0, WIDTH, HEIGHT, null, 0, WIDTH);
        img = createImage(new MemoryImageSource(WIDTH, HEIGHT, dataBuffInt, 0,
                WIDTH));
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
        image = ImageIO.read(new File(FILENAME));
        WIDTH = image.getWidth();
        HEIGHT = image.getHeight();
    }

    public BufferedImage convertInBinary(BufferedImage image) {

        /*
         * BufferedImage imageBin = new BufferedImage(WIDTH, HEIGHT,
         * BufferedImage.TYPE_BYTE_BINARY);
         */
        BufferedImage imageBin = image;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                imageBin.setRGB(j, i, (red + green + blue < 100) ? black
                        : white);
            }
        }
        return imageBin;
    }

    public Coordinate findDot(BufferedImage img) {
        Coordinate c = new Coordinate(-1, -1);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (img.getRGB(j, i) == black) {
                    c = new Coordinate(j, i);
                    //System.out.println("Objects init: " + c.toString());
                    return c;
                }
            }
        }
        System.err.println("No objects more!");
        return c;
    }

    public Coordinate findInnerContour(Coordinate in, BufferedImage img) {
        Coordinate c = new Coordinate(-1, -1);
        for (int j = in.getX(), i = in.getY(); j < WIDTH && i < HEIGHT; j++, i++) {
            if (img.getRGB(j, i) != black) {
                c = new Coordinate(j, i);
                //First pixel of inner contour
                //image.setRGB(j, i, 16724016);
                //System.out.println("Inner init: " + c.toString());
                return c;
            }
        }
        System.err.println("No inner contour!");
        return c;
    }

    public ArrayList<Coordinate> selectContour(Coordinate init,
            BufferedImage img) {
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
            if (img.getRGB(temp.getX(), temp.getY()) == black) {
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
    
    public double getAngle (Coordinate a, Coordinate b, Coordinate c){
        Coordinate vector1 = new Coordinate(b.getX() - a.getX(), b.getY() - a.getY());
        Coordinate vector2 = new Coordinate(c.getX() - a.getX(), c.getY() - a.getY());
        double angle = ( vector1.getX()*vector2.getX() + vector1.getY()*vector2.getY() ) /
                (Math.sqrt( Math.pow(vector1.getX(),2)+Math.pow(vector1.getY(),2) )*
                Math.sqrt( Math.pow(vector2.getX(),2)+Math.pow(vector2.getY(),2) ));
        return angle;
    }
    
    public LSegment getLongestLSegment(ArrayList<Coordinate> coordinates){
        //ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<LSegment> segments = new ArrayList<LSegment>();
        ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<Coordinate> object = coordinates;
        for (int i = 0; i < object.size(); i++) {
            Coordinate max1 = object.get(i);
            Coordinate max2 = object.get(i);
            int distance = 0;
            for (Coordinate obj : object) {
                int dist = (int) Math.sqrt( (Math.pow(obj.getX() - max1.getX(), 2)
                        + Math.pow(obj.getY() - max1.getY(), 2)));
                if (dist > distance) {
                    max2 = obj;
                    distance = dist;
                }
            }
            //image.setRGB(max1.getX(), max1.getY(), 16711680);
            //image.setRGB(max2.getX(), max2.getY(), 16711680);
            distances.add(distance);
            segments.add(new LSegment(
                    new Coordinate(max1.getX(), max1.getY()), 
                    new Coordinate(max2.getX(), max2.getY())
                    ));
            
        }
        Collections.sort(segments);
        /*Collections.sort(segments, new Comparator<LSegment>() {
            public int compare(LSegment arg0, LSegment arg1) {
                return arg0.compareTo(arg1);
            }
        });*/
        return Collections.max(segments);
    }
    /**
     * 
     * @param line - sides separator
     * @param contour of object
     * @param side: -1 if on bottom, 1 if on top side
     * @return array of contour points from given side
     */
    public  ArrayList<Coordinate> getHalf(LSegment line, ArrayList<Coordinate> contour, int side){
        ArrayList<Coordinate> result = new ArrayList<>();
        if (side != -1 && side != 1){
            System.err.println("Invalid side argument "+ side+ ". Can be only -1 or 1");
        }
        for (Coordinate point: contour){
            if (getPosition(line, new Coordinate(point.getX(), point.getY())) == side){
                result.add(point);
            }
        }
        return result;
        
    }
    
    /**
     * @param line 
     * @param c - point coordinate
     * @return 1 if on top, -1 if on bottom, 0 if on line
     */
    public int getPosition (LSegment line, Coordinate c){
        Coordinate vector1 = new Coordinate(line.getB().getX() - line.getA().getX(), line.getB().getY() - line.getA().getY());
        Coordinate vector2 = new Coordinate(c.getX() - line.getA().getX(), c.getY() - line.getA().getY());
        double result  = vector1.getX()*vector2.getY() - vector1.getY()*vector2.getX();
        if (result < 0)
            return 1;
        else if(result > 0)
            return -1;
        return 0;
    }

    public void paint(Graphics gr) {
        gr.drawImage(img, 0, 0, this);
    }

}