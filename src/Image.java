/*******************************************************
 * CS451 Multimedia Software Systems
 *
 * @ Author: Elaine Kang
 * <p/>
 * This image class is for a 24bit RGB image only.
 *******************************************************/

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class Image {
    private int width;                // number of columns
    private int height;                // number of rows
    private int pixelDepth = 3;            // pixel depth in byte
    BufferedImage img;                // image array to store rgb values, 8 bits per channel
    String title;

    public Image(int w, int h)
    // create an empty image with width and height
    {
        width = w;
        height = h;

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        System.out.println("Created an empty image with size " + width + "x" + height);
    }

    public Image(String fileName)
    // Create an image and read the data from the file
    {
        readPPM(fileName);
        System.out.println("Created an image from " + fileName + " with size " + width + "x" + height);
    }

    public int getW() {
        return width;
    }

    public int getH() {
        return height;
    }

    public int getSize()
    // return the image size in byte
    {
        return width * height * pixelDepth;
    }

    public void setPixel(int x, int y, byte[] rgb)
    // set rgb values at (x,y)
    {
        int pix = 0xff000000 | ((rgb[0] & 0xff) << 16) | ((rgb[1] & 0xff) << 8) | (rgb[2] & 0xff);
        img.setRGB(x, y, pix);
    }

    public void setPixel(int x, int y, int[] irgb)
    // set rgb values at (x,y)
    {
        byte[] rgb = new byte[3];

        for (int i = 0; i < 3; i++)
            rgb[i] = (byte) irgb[i];

        setPixel(x, y, rgb);
    }

    public void getPixel(int x, int y, byte[] rgb)
    // retrieve rgb values at (x,y) and store in the array
    {
        int pix = img.getRGB(x, y);

        rgb[2] = (byte) pix;
        rgb[1] = (byte) (pix >> 8);
        rgb[0] = (byte) (pix >> 16);
    }

    public int getAvgPixel() {
        int total = 0;
        int[] grgb = new int[3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                getPixel(x, y, grgb);
                total += grgb[0];
            }
        }
        return total / (width * height);
    }

    public void getPixel(int x, int y, int[] rgb)
    // retrieve rgb values at (x,y) and store in the array
    {
        int pix = img.getRGB(x, y);

        byte b = (byte) pix;
        byte g = (byte) (pix >> 8);
        byte r = (byte) (pix >> 16);

        // converts singed byte value (~128-127) to unsigned byte value (0~255)
        rgb[0] = (int) (0xFF & r);
        rgb[1] = (int) (0xFF & g);
        rgb[2] = (int) (0xFF & b);
    }

    public void displayPixelValue(int x, int y)
    // Display rgb pixel value at (x,y)
    {
        int pix = img.getRGB(x, y);

        byte b = (byte) pix;
        byte g = (byte) (pix >> 8);
        byte r = (byte) (pix >> 16);

        System.out.println("RGB Pixel value at (" + x + "," + y + "):" + (0xFF & r) + "," + (0xFF & g) + "," + (0xFF & b));
    }

    public void readPPM(String fileName)
    // read a data from a PPM file
    {
        FileInputStream fis = null;
        DataInputStream dis = null;
        try {
            fis = new FileInputStream(fileName);
            dis = new DataInputStream(fis);
            title = fileName.substring(0, fileName.length() - 4);

            System.out.println("Reading " + fileName + "...");

            // read Identifier
            if (!dis.readLine().equals("P6")) {
                System.err.println("This is NOT P6 PPM. Wrong Format.");
                System.exit(0);
            }

            // read Comment line
            String commentString = dis.readLine();

            // read width & height
            String[] WidthHeight = dis.readLine().split(" ");
            width = Integer.parseInt(WidthHeight[0]);
            height = Integer.parseInt(WidthHeight[1]);

            // read maximum value
            int maxVal = Integer.parseInt(dis.readLine());

            if (maxVal != 255) {
                System.err.println("Max val is not 255");
                System.exit(0);
            }

            // read binary data byte by byte
            int x, y;
            //fBuffer = new Pixel[height][width];
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            byte[] rgb = new byte[3];
            int pix;

            for (y = 0; y < height; y++) {
                for (x = 0; x < width; x++) {
                    rgb[0] = dis.readByte();
                    rgb[1] = dis.readByte();
                    rgb[2] = dis.readByte();
                    setPixel(x, y, rgb);
                }
            }
            dis.close();
            fis.close();

            System.out.println("Read " + fileName + " Successfully.");

        } // try
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void write2PPM(String fileName)
    // write the image data in img to a PPM file
    {
        FileOutputStream fos = null;
        PrintWriter dos = null;

        try {
            fos = new FileOutputStream(fileName);
            dos = new PrintWriter(fos);

            System.out.println("Writing the Image buffer into " + fileName + "...");

            // write header
            dos.print("P6" + "\n");
            dos.print("#CS451" + "\n");
            dos.print(width + " " + height + "\n");
            dos.print(255 + "\n");
            dos.flush();

            // write data
            int x, y;
            byte[] rgb = new byte[3];
            for (y = 0; y < height; y++) {
                for (x = 0; x < width; x++) {
                    getPixel(x, y, rgb);
                    fos.write(rgb[0]);
                    fos.write(rgb[1]);
                    fos.write(rgb[2]);

                }
                fos.flush();
            }
            dos.close();
            fos.close();

            System.out.println("Wrote into " + fileName + " Successfully.");

        } // try
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void display(String title)
    // display the image on the screen
    {
        // Use a label to display the image
        JFrame frame = new JFrame();
        JLabel label = new JLabel(new ImageIcon(img));
        frame.add(label, BorderLayout.CENTER);
        frame.setTitle(title);
        frame.pack();
        frame.setVisible(true);
    }

    public void displayIndexed(int[][] LUT) {
        Image proper = new Image(width, height);
        int[] RGBArray = new int[3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                getPixel(x, y, RGBArray);
                int index = RGBArray[0];
                RGBArray[0] = LUT[index][0];
                RGBArray[1] = LUT[index][1];
                RGBArray[2] = LUT[index][2];
                proper.setPixel(x, y, RGBArray);

            }
        }
        proper.display("Indexed Image using LUT");
    }

    public Image convertToGrayscale() {
        int[] RGBArray = new int[3];
        Image gray = new Image(width, height);
        gray.title = title;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                getPixel(x, y, RGBArray);

                int gr_val = (int) Math.round(0.299 * RGBArray[0] + 0.587 * RGBArray[1] + 0.114 * RGBArray[2]);
                if (gr_val > 255) gr_val = 255;
                if (gr_val < 0) gr_val = 0;

                Arrays.fill(RGBArray, gr_val);
                gray.setPixel(x, y, RGBArray);
            }
        }
        return gray;
    }

    public void biLevelConversion(){
        int[] RGBArray = new int[3];
        Image img = new Image(width, height);
        int new_val = 0;
        int ga = getAvgPixel();
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                getPixel(x, y, RGBArray);
                if (RGBArray[0] > ga){
                    new_val = 255;
                } else if (RGBArray[0] <= ga){
                    new_val = 0;
                }
                Arrays.fill(RGBArray, new_val);
                img.setPixel(x, y, RGBArray);

            }
        }
        img.display(title + "-bilevel-threshold.ppm");
        img.write2PPM(title + "-bilevel-threshold.ppm");
    }

    public void colorQuantization(){
        int[][] lookupTable = generateLookupTable();
        int[] RGBArray = new int[3];
        int index;
        Image indexedImage = new Image(width, height);

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                getPixel(x, y, RGBArray);
                index = findIndex(RGBArray[0], RGBArray[1], RGBArray[2], lookupTable);
                Arrays.fill(RGBArray, index);
                indexedImage.setPixel(x,y,RGBArray);
            }
        }
        indexedImage.display("Indexed Image");
        indexedImage.write2PPM(title + "-index.ppm");
        indexedImage.displayIndexed(lookupTable);

    }

    private static int[][] generateLookupTable(){
        int[][] lookupTable = new int[256][3];
        int index = 0;
        System.out.println("Index  R  G  B");
        System.out.println("--------------");
        for (int r = 0; r < 8; r++){
            for (int g = 0; g < 8; g++){
                for (int b = 0; b < 4; b++){
                    int rLUT = ((32 * r) + 16);
                    int gLUT = ((32 * g) + 16);
                    int bLUT = ((64 * b) + 32);

                    lookupTable[index][0] = rLUT;
                    lookupTable[index][1] = gLUT;
                    lookupTable[index][2] = bLUT;
                    System.out.println(index + "    " + rLUT + "    " + gLUT + "    " + bLUT);
                    index++;
                }
            }
        }
        return lookupTable;
    }

    public static int findIndex(int r, int g, int b, int[][] lookupTable){
        int index = -1;
        r = (int) Math.floor(r / 32) * 32 + 16;
        g = (int) Math.floor(g / 32) * 32 + 16;
        b = (int) Math.floor(b / 64) * 64 + 32;

        for (int i = 0; i < 255; i++) {
            boolean redMatch = r == lookupTable[i][0];
            boolean greenMatch = g == lookupTable[i][1];
            boolean blueMatch = b == lookupTable[i][2];

            if (redMatch && greenMatch && blueMatch) {
                index = i;
            }
        }
        return index;
    }

    public Image errorDiffusion(Image A, int N){
        int q;
        int[] RGBArray = new int[3];
        int[] gr = new int[3];
        int[] levels = getLevels(N);
        Image B = new Image(width, height);

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++) {
                int[] error = new int[3];
                int[] neighbor = new int[3];
                A.getPixel(x, y, RGBArray);
                if (RGBArray[0] < 0){
                    Arrays.fill(RGBArray, 0);
                }
                if (RGBArray[0] > 255){
                    Arrays.fill(RGBArray, 255);
                }
                //Choose the value that is nearest the original pixel's value
                q = closestPixel(levels, RGBArray[0]);
                //Assign to B
                Arrays.fill(gr, q);
                B.setPixel(x, y, gr);

                double er = (RGBArray[0] - gr[0]);

                if (x + 1 < width){
                    A.getPixel(x + 1, y, neighbor);
                    int e = (int) (neighbor[0] + 7.0 * er / 16.0);
                    if (e < 0){
                        e = 0;
                    } else if (e > 255){
                        e = 255;
                    }
                    Arrays.fill(error, e);
                    A.setPixel(x + 1, y, error);
                }

                if (y + 1 < height){
                    A.getPixel(x, y + 1, neighbor);
                    int e = (int) (neighbor[0] + 5.0 * er / 16.0);
                    if (e < 0){
                        e = 0;
                    } else if (e > 255){
                        e = 255;
                    }
                    Arrays.fill(error, e);
                    A.setPixel(x, y + 1, error);
                }

                if (x - 1 >= 0 && y + 1 < height){
                    A.getPixel(x - 1, y + 1, neighbor);
                    int e = (int) (neighbor[0] + 3.0 * er / 16.0);
                    if (e < 0){
                        e = 0;
                    } else if (e > 255){
                        e = 255;
                    }
                    Arrays.fill(error, e);
                    A.setPixel(x - 1, y + 1, error);
                }

                if (x + 1 < width && y + 1 < height){
                    A.getPixel(x + 1, y + 1, neighbor);
                    int e = (int) (neighbor[0] + 1.0 * er / 16.0);
                    if (e < 0){
                        e = 0;
                    } else if (e > 255){
                        e = 255;
                    }
                    Arrays.fill(error, e);
                    A.setPixel(x + 1, y + 1, error);
                }
            }
        }
        return B;
    }

    private static int[] getLevels(int N){
        int[] levels = new int[N];
        for (int i = 0; i < N; i++){
            double val = 255 * i / (N - 1.0);
            levels[i] =  (int) val;
        }
        return levels;
    }

    private static int closestPixel(int[] levels, int px){
        int distance = 999;
        int closestPixel = 0;
        for (int level : levels){
            int distanceFromLevel = Math.abs(level - px);
            if (distanceFromLevel < distance){
                closestPixel = level;
                distance = distanceFromLevel;
            }
        }
        return closestPixel;
    }
} // Image class

//TODO: Refactor to rewrite upon conversion
