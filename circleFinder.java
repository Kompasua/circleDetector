import java.awt.*;
import java.applet.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class circleFinder extends Applet {

    // Location of image
    static String FILENAME = "test4.jpg";
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
        //findDot(image);
        //image = removeRaw(image);
        dataBuffInt = image.getRGB(0, 0, WIDTH, HEIGHT, null, 0, WIDTH);
        int i =0;
        while (i<dataBuffInt.length){
           // System.out.println(dataBuffInt[i]);
            i++;
        }
        img = createImage(new MemoryImageSource(WIDTH, HEIGHT, dataBuffInt, 0,
                WIDTH));
        File f = new File("MyFile.png");
        try {
            ImageIO.write((RenderedImage) image, "PNG", f);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void openImage() throws MalformedURLException, IOException {
        image = ImageIO.read(new File(FILENAME));
        WIDTH = image.getWidth();
        HEIGHT = image.getHeight();
    }

    public BufferedImage toGreyScale(BufferedImage image) {
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

    public BufferedImage convertInBinary(BufferedImage image) {
        
        /*BufferedImage imageBin = new BufferedImage(WIDTH, HEIGHT,
                BufferedImage.TYPE_BYTE_BINARY);*/
        BufferedImage imageBin = image;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                imageBin.setRGB(j, i, (red + green + blue < 100) ? black : white);
            }
        }
        return imageBin;
    }
    
    public BufferedImage compress(BufferedImage image) {
        int black = -16777216;
        for (int i = 1; i < HEIGHT-1; i++) {
            for (int j = 1; j < WIDTH-1; j++) {
                Color c = new Color(image.getRGB(j, i));
                if (image.getRGB(j, i-1) == black && image.getRGB(j, i) == black && image.getRGB(j, i+1) == black &&
                        image.getRGB(j+1, i-1) == black && image.getRGB(j+1, i) == black && image.getRGB(j+1, i+1) == black){
                    image.setRGB(j, i+1, -1);
                    //image.setRGB(j, i, -1);
                    image.setRGB(j, i-1, -1);

                }
            }
        }
        return image;
        
    }
    
    public BufferedImage removeRaw(BufferedImage image) {
        int black = -16777216;
        for (int i = 1; i < HEIGHT-1; i++) {
            for (int j = 1; j < WIDTH-1; j++) {
                Color c = new Color(image.getRGB(j, i));
                if (image.getRGB(j, i) == -1 && image.getRGB(j+1, i) == black && (image.getRGB(j+1, i-1) == -1 || image.getRGB(j+1, i+1) == -1)){
                    while ((image.getRGB(j+1, i+1) == -1 || image.getRGB(j+1, i-1) == -1) && image.getRGB(j+1, i) == black && i <WIDTH && j<HEIGHT){
                        image.setRGB(j, i, 16399420);
                        i++; j++;
                        System.out.println("got it");
                    }
                    System.out.println("true");
                    //image.setRGB(j, i+1, -1);
                    
                   // image.setRGB(j, i-1, -1);

               }
            }
        }
        return image;
        
    }
    
    public BufferedImage clear(BufferedImage image) {
        int black = -16777216;
        for (int i = 1; i < HEIGHT-1; i++) {
            for (int j = 1; j < WIDTH-1; j++) {
                Color c = new Color(image.getRGB(j, i));
                if (image.getRGB(j, i-1) == -1 && image.getRGB(j, i) == black && image.getRGB(j, i+1) == -1 && 
                        (
                        (image.getRGB(j+1, i-1) == black && image.getRGB(j+1, i) == black) ||
                        (image.getRGB(j+1, i) == black && image.getRGB(j+1, i+1) == black)
                        )
                        ){
                    image.setRGB(j, i, -1);
                    System.out.println("true");
                }
            }
        }
        return image;
        
    }
    
    public void findDot (BufferedImage image){
        int x =0; int y = 0;
        while (image.getRGB(x+1, y) != black && x+1 < WIDTH) {
            image.setRGB(x, y, 16399420);
            x++;
        }
        System.out.println(x);
        image.setRGB(x+1, y, -16711936);
        getDirection(x+1, y);
    }
    
    public double getDirection(int x, int y) {
        int black = -16777216;
        final int X = x;
        final int Y = y;
        int width = 0;
        int height = 0;
           while (image.getRGB(x+1, y) == black && x+1 < WIDTH) {
                    image.setRGB(x, y,  -65281 );
                    System.out.println("right");
                    x++;
                    width++;
                }
           x= X; 
           while (image.getRGB(x-1, y) == black && x-1 < 0) {
               image.setRGB(x, y,  -65281 );
               System.out.println("left");
               x--;
               width++;
           }
           y =Y;
           while (image.getRGB(x, y+1) == black && y+1 < HEIGHT) {
               image.setRGB(x, y, -16776961);
               System.out.println("down");
               y++;
               height++;
           }
            y =Y;
           while ( y-1 > 0 && image.getRGB(x, y-1) == black) {
               image.setRGB(x, y, -16776961);
               System.out.println("top");
               y--;
               height++;
           }
           System.out.println(width);
           System.out.println(height);
           image.setRGB(21, 0, -256);
        return 0.0;
        
    }
    
    
    public void paint(Graphics gr) {
        gr.drawImage(img, 0, 0, this);
    }

}