import java.awt.*;
import java.applet.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import javax.imageio.ImageIO;

public class fillObject extends Applet {

    // Location of image
    static String FILENAME = "circle.jpg";
    public BufferedImage image;
    
    Image img;
    static int WIDTH;
    static int HEIGHT;
    static int[] dataBuffInt;

    int white = (255 << 16) | (255 << 8) | 255;
    int black = -16777216;

    public void init() {

        try {
            openImage();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        image = convertInBinary(image);
        ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<Coordinate> object = selectObject(findDot(image));
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
        }
        
        Collections.sort(distances); 
        System.out.println(distances.toString());
        System.out.println(Collections.max(distances) + " " + Collections.min(distances));
        /*while (findDot(image).getX() != -1 ) {
            selectObject(findDot(image));
        }*/
        /*ArrayList<Coordinate> object = selectObject(findDot(image));
        int colors = 0;
        for (Coordinate obj : object){
            image.setRGB(obj.getX(), obj.getY(), colors);
            colors+=1;
            if  (colors> 1000)
                break;
        }
        int color = 0;
        for (int i = 0; i < WIDTH-1; i++) {
            image.setRGB(i, 0, color);
            color+=65536;
        }*/

        dataBuffInt = image.getRGB(0, 0, WIDTH, HEIGHT, null, 0, WIDTH);

        img = createImage(new MemoryImageSource(WIDTH, HEIGHT, dataBuffInt, 0,
                WIDTH));

        File f = new File("MyFile.png");
        try {
            ImageIO.write((RenderedImage) image, "PNG", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0); 
    }

    public void openImage() throws MalformedURLException, IOException {
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
                    System.out.println("Objects init: " + c.toString());
                    return c;
                }
            }
        }
        System.err.println("No objects more!");
        return c;
    }

    public ArrayList<Coordinate> selectObject(Coordinate c) {
        ArrayList<Coordinate> object = new ArrayList<Coordinate>();
        ArrayList<Coordinate> toCheck = new ArrayList<Coordinate>();
        Coordinate temp;
        toCheck.add(c);

        while (!toCheck.isEmpty()) {
            //System.out.println(c.toString());

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
            //System.out.println("iterate");
        }

        return object;

    }

    public void paint(Graphics gr) {
        gr.drawImage(img, 0, 0, this);
    }

}