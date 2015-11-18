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
        proper.write2PPM("indexed.ppm");
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
        int index = 255;
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

    public void drawCircles(int M, int N, int K){

        changeToWhite();
        
        int thickness;
        int difRadii = 0;

        int originX = 256;
        int originY = 256;

        int[] black = new int[3];

        Arrays.fill(black, 0);

        while (difRadii < 240){

            difRadii += N;
            thickness = difRadii + M;
            //System.out.println("N: " + difRadii + ", M: " + thickness);

            for (int radius = difRadii; radius < thickness; radius++){

                for (double i = 0; i < 1; i += .0001){

                    double x = originX + radius * Math.cos(2 * Math.PI * i);
                    double y = originY + radius * Math.sin(2 * Math.PI * i);

                    setPixel((int) Math.round(x), (int) Math.round(y), black);
                }

            }


        }

        display("Circle");
        resizeNoFilter(K);
        resizeAvgFilter(K);
        resizeFilterOne(K);
        resizeFilterTwo(K);

    }

    public void changeToWhite(){
        int[] rgb = new int[3];
        Arrays.fill(rgb, 255);

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                setPixel(x, y, rgb);
            }
        }
    }

    public void resizeNoFilter(int K){
        Image noFilter = new Image(width / K, height / K);
        int[] rgb = new int[3];
        int noFilterX;
        int noFilterY = 0;

        for (int y = K; y <= height; y += K){

            noFilterX = 0;

            for (int x = K; x <= width; x += K){
                //System.out.println(x + " " + y);
                //System.out.println("Choosing: " + (x - K / 2) + " " + (y - K / 2));
                getPixel(x - K, y - K, rgb);
                noFilter.setPixel(noFilterX, noFilterY, rgb);
                noFilterX++;

            }
            noFilterY++;
        }

        noFilter.display("No Filter");
        //noFilter.write2PPM("noFilter.ppm");
    }

    public void resizeAvgFilter(int K){
        Image filterAvg = new Image(width / K, height / K);
        int[] rgb = new int[3];
        int filterX;
        int filterY = 0;

        for (int y = K; y <= height; y += K){

            filterX = 0;

            for (int x = K; x <= width; x += K){
                int total = 0;
                int avg;
                for (int blockY = y - K; blockY < y; blockY++){
                    for (int blockX = x - K; blockX < x; blockX++){

                        getPixel(blockX, blockY, rgb);
                        total += rgb[0];

                    }
                }

                avg = total / (K * K);
                Arrays.fill(rgb, avg);

                filterAvg.setPixel(filterX, filterY, rgb);
                filterX++;

            }
            filterY++;
        }

        filterAvg.display("Average Filter");
        //noFilter.write2PPM("noFilter.ppm");
    }

    public void resizeFilterOne(int K){
        //close, but need to modify blockX and Y to center at (x,y) and check to make sure the 3 are valid
        Image filterOne = new Image(width / K, height / K);
        int[] rgb = new int[3];
        int filterX;
        int filterY = 0;

        for (int y = 0; y < height; y += K){

            filterX = 0;

            for (int x = 0; x < width; x += K){
                double filteredVal = 0.0;

                if (x - 1 >= 0 && y - 1 >= 0){
                    getPixel(x - 1, y - 1, rgb);
                    filteredVal += (1.0 / 9.0) * rgb[0];
                }
                if (y - 1 >= 0){
                    getPixel(x, y - 1, rgb);
                    filteredVal += (1.0 / 9.0) * rgb[0];
                }
                if (x + 1 <= width && y - 1 >= 0){
                    getPixel(x + 1, y - 1, rgb);
                    filteredVal += (1.0 / 9.0) * rgb[0];
                }
                if (x - 1 >= 0){
                    getPixel(x - 1, y, rgb);
                    filteredVal += (1.0 / 9.0) * rgb[0];
                }

                getPixel(x, y, rgb);
                filteredVal += (1.0 / 9.0) * rgb[0];

                if (x + 1 <= width){
                    getPixel( x + 1, y, rgb);
                    filteredVal += (1.0 / 9.0) * rgb[0];
                }
                if (x - 1 >= 0 && y + 1 <= height){
                    getPixel( x - 1, y + 1 , rgb );
                    filteredVal += (1.0 / 9.0) * rgb[0];
                }
                if (y + 1 <= height){
                    getPixel(x, y + 1, rgb);
                    filteredVal += (1.0 / 9.0) * rgb[0];
                }
                if (x + 1 <= width && y + 1 <= height){
                    getPixel( x + 1, y + 1, rgb);
                    filteredVal += (1.0 / 9.0) * rgb[0];
                }

                filteredVal = Math.round(filteredVal);
                if (filteredVal > 255){
                    filteredVal = 255;
                }

                Arrays.fill(rgb, (int) filteredVal);

                filterOne.setPixel(filterX, filterY, rgb);
                filterX++;
            }
            filterY++;
        }

        filterOne.display("Filter One");

    }

    public void resizeFilterTwo(int K) {
        Image filterTwo = new Image(width / K, height / K);
        int[] rgb = new int[3];
        int filterX;
        int filterY = 0;

        for (int y = 0; y < height; y += K) {

            filterX = 0;

            for (int x = 0; x < width; x += K) {
                double filteredVal = 0.0;

                if (x - 1 >= 0 && y - 1 >= 0) {
                    getPixel(x - 1, y - 1, rgb);
                    filteredVal += (1.0 / 16.0) * rgb[0];
                }
                if (y - 1 >= 0) {
                    getPixel(x, y - 1, rgb);
                    filteredVal += (2.0 / 16.0) * rgb[0];
                }
                if (x + 1 <= width && y - 1 >= 0) {
                    getPixel(x + 1, y - 1, rgb);
                    filteredVal += (1.0 / 16.0) * rgb[0];
                }
                if (x - 1 >= 0) {
                    getPixel(x - 1, y, rgb);
                    filteredVal += (2.0 / 16.0) * rgb[0];
                }

                getPixel(x, y, rgb);
                filteredVal += (4.0 / 16.0) * rgb[0];

                if (x + 1 <= width) {
                    getPixel(x + 1, y, rgb);
                    filteredVal += (2.0 / 16.0) * rgb[0];
                }
                if (x - 1 >= 0 && y + 1 <= height) {
                    getPixel(x - 1, y + 1, rgb);
                    filteredVal += (1.0 / 16.0) * rgb[0];
                }
                if (y + 1 <= height) {
                    getPixel(x, y + 1, rgb);
                    filteredVal += (2.0 / 16.0) * rgb[0];
                }
                if (x + 1 <= width && y + 1 <= height) {
                    getPixel(x + 1, y + 1, rgb);
                    filteredVal += (1.0 / 16.0) * rgb[0];
                }

                filteredVal = Math.round(filteredVal);
                if (filteredVal > 255) {
                    filteredVal = 255;
                }

                Arrays.fill(rgb, (int) filteredVal);

                filterTwo.setPixel(filterX, filterY, rgb);
                filterX++;
            }
            filterY++;
        }

        filterTwo.display("Filter Two");
    }
    public void resizeMult8(){
        boolean resizeNeeded = false;
        int newWidth = width;
        int newHeight = height;

        if (width % 8 != 0){
            resizeNeeded = true;
            newWidth = (int) Math.ceil(width / 8.0) * 8;
        }
        if (height % 8 != 0){
            resizeNeeded = true;
            newHeight = (int) Math.ceil(height / 8.0) * 8;
        }
        if (resizeNeeded){
            int[] rgb = new int[3];
            int x = width;
            int y = height;
            Image temp_img = new Image(newWidth, newHeight);
            temp_img.copyImage(this, width, height);
            Arrays.fill(rgb, 0);
            for (; y < newHeight; y++){
                for (; x < newWidth; x++){
                    temp_img.setPixel(x, y, rgb);
                }
            }
            img = temp_img.img;
            width = newWidth;
            height = newHeight;
        }
    }
    public void decompress(int OldWidth, int OldHeight){
        Image temp_img = new Image(OldWidth, OldHeight);
        temp_img.copyImage(this, OldWidth, OldHeight);
        img = temp_img.img;
    }

    public void copyImage(Image ImgToBeCopied, int oldWidth, int oldHeight){
        int[] rgb = new int[3];
        for(int y = 0; y < oldHeight; y++){
            for (int x = 0; x < oldWidth; x++){
                ImgToBeCopied.getPixel(x, y, rgb);
                setPixel(x,y, rgb);
            }
        }
    }
    public void transformYCbCr(double[][] Y, double[][] Cb, double[][] Cr){
        int[] rgb = new int[3];
        int R, G, B;
        double Ypx, Cbpx, Crpx;
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                getPixel(x, y, rgb);

                R = rgb[0];
                G = rgb[1];
                B = rgb[2];

                Ypx = (R * 0.299) + (G * 0.587)  + (B * 0.144);
                Cbpx = (R * -0.168736) + (G * -0.331264) + (B * 0.5000);
                Crpx = (R * 0.5000) + (G * -0.4187) + (B * -0.0813);

                Ypx = limitCheck(Ypx, 0, 255);
                Cbpx = limitCheck(Cbpx, -127.5, 127.5);
                Crpx = limitCheck(Crpx, -127.5, 127.5);

                Ypx -= 128;
                Cbpx -= 0.5;
                Crpx -= 0.5;

                Y[x][y] = Ypx;
                Cb[x][y] = Cbpx;
                Cr[x][y] = Crpx;
            }
        }
    }

    public double limitCheck(double val, double min, double max){
        if (val < min){
            val = min;
        } else if (val > max){
            val = max;
        }
        return val;
    }

    public int limitCheck(double val, int min, int max){
        if (val < min){
            val = min;
        } else if (val > max){
            val = max;
        }
        return (int) val;
    }

    public void subSample(double[][] Cb, double[][] Cr, double[][] sampledCb, double[][] sampledCr){
        int newWidth = width / 2;
        int newHeight = height / 2;
        int i, j;
        i = j = 0;

        for (int x = 0; x < Cb.length - 1; x += 2) {
            j = 0;
            for (int y = 0; y < Cb[0].length - 1; y += 2) {
                double avg = (Cb[x][y] + Cb[x + 1][y] + Cb[x][y + 1] + Cb[x + 1][y + 1]) / 4.0;
                sampledCb[i][j] = avg;
                avg = (Cr[x][y] + Cr[x + 1][y] + Cr[x][y + 1] + Cr[x + 1][y + 1]) / 4.0;
                sampledCr[i][j] = avg;
                j++;
                //System.out.println("Left off at: " + x + ", " + y);
            }
            i++;
        }
        //System.out.println("Starting at: " + (i - 1) + ","+ j);
        for (int x = i - 1; x < sampledCb.length; x++){
            for (int y = j; y < sampledCb[0].length; y++){
                //System.out.println("Padding 0 at: " + x + "," + y);
                sampledCb[x][y] = 0;
                sampledCr[x][y] = 0;
            }
            j = 0;
        }

    }

    public void superSample(double[][] newCb, double[][] newCr, double[][] sampledCb, double[][] sampledCr){
        int i = 0;
        int j;
        for(int x = 0; x < newCr.length / 2; x++){
            j = 0;
            for (int y = 0; y < newCr[0].length / 2; y++){
                double avg_cb = sampledCb[x][y];
                double avg_cr = sampledCr[x][y];

                newCb[i][j] = avg_cb;
                newCb[i + 1][j] = avg_cb;
                newCb[i][j + 1] = avg_cb;
                newCb[i + 1][j + 1] = avg_cb;

                newCr[i][j] = avg_cr;
                newCr[i + 1][j] = avg_cr;
                newCr[i][j + 1] = avg_cr;
                newCr[i + 1][j + 1] = avg_cr;
                j += 2;
            }
            i += 2;
        }
    }

    public void transformRGB(double[][] Y, double[][] newCr, double[][] newCb){
        int[] rgb = new int[3];

        for (int x = 0; x < Y.length - 1; x++){
            for (int y = 0; y < Y[0].length - 1; y++){
                Y[x][y] += 128;
                newCr[x][y] += 0.5;
                newCb[x][y] += 0.5;
            }
        }

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                double Y_px = Y[x][y];
                double Cr_px = newCr[x][y];
                double Cb_px = newCb[x][y];

                double R = (Y_px * 1.000) + (Cr_px * 0)  + (Cb_px * 1.4020);
                double G = (Y_px * 1.000) + (Cr_px * -0.3441) + (Cb_px * -0.7141);
                double B = (Y_px * 1.000) + (Cr_px * 1.7720) + (Cb_px * 0);
                rgb[0] = limitCheck(R, 0, 255);
                rgb[1] = limitCheck(G, 0, 255);
                rgb[2] = limitCheck(B, 0, 255);
                setPixel(x, y, rgb);
            }
        }
    }
    public void dctTransform(double[][] Y, double[][] sampledCb, double[][] sampledCr, double[][] dctY, double[][] dctCb, double[][] dctCr){
        double Cu, Cv;
        for (int u = 0; u < Y.length; u += 8){
            for (int v = 0; v < Y[0].length; v += 8){
                for (int x = 0; x < 8; x++){
                    for (int y = 0; y < 8; y++){

                        double y_sum = 0;
                        for (int i = u; i < 8; i++){
                            for (int j = v; j < 8; j++){

                                double u_cos = Math.cos((((2 * i) + 1) * x * Math.PI) / 16.0);
                                double v_cos = Math.cos((((2 * j) + 1) * y * Math.PI) / 16.0);

                                y_sum += u_cos * v_cos * Y[i][j];
                                if (x == 0 && u == 0 && v == 0 && i == 0 && y == 0){
                                    System.out.println("DCT Y: " + Y[i][j]);
                                }

                            }
                        }

                        if (x == 0){
                            Cu = (1.0 / (Math.sqrt(2.0)));
                        } else {
                            Cu = 1.0;
                        }

                        if (y == 0){
                            Cv = (1.0 / (Math.sqrt(2.0)));
                        } else {
                            Cv = 1.0;
                        }
                        y_sum *= (Cu * Cv * (1.0 / 4.0));
                        dctY[x + u][y + v] = limitCheck(y_sum, -Math.pow(2.0, 10.0), Math.pow(2.0, 10.0));

                    }
                }


            }
        }

        for (int u = 0; u < sampledCb.length; u += 8){
            for (int v = 0; v < sampledCb[0].length; v += 8){

                for (int x = 0; x < 8; x++){
                    for (int y = 0; y < 8; y++){

                        double cb_sum = 0;
                        for (int i = u; i < 8; i++){
                            for (int j = v; j < 8; j++){

                                double u_cos = Math.cos((((2 * i) + 1) * x * Math.PI) / 16.0);
                                double v_cos = Math.cos((((2 * j) + 1) * y * Math.PI) / 16.0);

                                cb_sum += u_cos * v_cos * sampledCb[i][j];

                            }
                        }

                        if (x == 0){
                            Cu = (1.0 / (Math.sqrt(2.0)));
                        } else {
                            Cu = 1.0;
                        }

                        if (y == 0){
                            Cv = (1.0 / (Math.sqrt(2.0)));
                        } else {
                            Cv = 1.0;
                        }
                        cb_sum *= (Cu * Cv * (1.0 / 4.0));
                        dctCb[u + x][y + v] = limitCheck(cb_sum, -Math.pow(2.0, 10.0), Math.pow(2.0, 10.0));
                    }
                }


            }
        }

        for (int u = 0; u < sampledCr.length; u += 8){
            for (int v = 0; v < sampledCr[0].length; v += 8){
                for (int x = 0; x < 8; x++){
                    for (int y = 0; y < 8; y++){

                        double cr_sum = 0;
                        for (int i = u; i < 8; i++){
                            for (int j = v; j < 8; j++){

                                double u_cos = Math.cos((((2 * i) + 1) * x * Math.PI) / 16.0);
                                double v_cos = Math.cos((((2 * j) + 1) * y * Math.PI) / 16.0);

                                cr_sum += u_cos * v_cos * sampledCr[i][j];

                            }
                        }

                        if (x == 0){
                            Cu = (1.0 / (Math.sqrt(2.0)));
                        } else {
                            Cu = 1.0;
                        }

                        if (y == 0){
                            Cv = (1.0 / (Math.sqrt(2.0)));
                        } else {
                            Cv = 1.0;
                        }
                        cr_sum *= (Cu * Cv * (1.0 / 4.0));
                        dctCr[x + u][y + v] = limitCheck(cr_sum, -Math.pow(2.0, 10.0), Math.pow(2.0, 10.0));
                    }
                }


            }
        }
    }
    public void dctInverse(double[][]Y, double[][] Cb, double[][] Cr, double[][] inverseY, double[][] inverseCb, double[][] inverseCr){
        double Cv, Cu;
        for (int u = 0; u < Y.length; u += 8){
            for (int v = 0; v < Y[0].length; v += 8){
                for (int x = 0; x < 8; x++){
                    for (int y = 0; y < 8; y++){
                        double y_sum = 0;
                        for (int i = u; i < 8; i++){
                            for (int j = v; j < 8; j++){
                                if (x == 0){
                                    Cu = (1.0 / (Math.sqrt(2.0)));
                                } else {
                                    Cu = 1.0;
                                }

                                if (y == 0){
                                    Cv = (1.0 / (Math.sqrt(2.0)));
                                } else {
                                    Cv = 1.0;
                                }

                                double u_cos = Math.cos((((2 * i) + 1) * x * Math.PI) / 16.0);
                                double v_cos = Math.cos((((2 * j) + 1) * y * Math.PI) / 16.0);

                                y_sum += Cu * Cv * u_cos * v_cos * Y[i][j];

                            }
                        }
                        y_sum = (1.0 / 4.0) * y_sum;
                        inverseY[x + u][y + v] = y_sum;
                        if (y == 0 && u == 0 && v == 0){
                            System.out.println("Inverse DCT Y: " + y_sum);
                        }

                    }
                }


            }
        }
        for (int u = 0; u < Cr.length; u += 8){
            for (int v = 0; v < Cr[0].length; v += 8){
                for (int x = 0; x < 8; x++){
                    for (int y = 0; y < 8; y++){

                        if (x == 0){
                            Cu = (1.0 / (Math.sqrt(2.0)));
                        } else {
                            Cu = 1.0;
                        }

                        if (y == 0){
                            Cv = (1.0 / (Math.sqrt(2.0)));
                        } else {
                            Cv = 1.0;
                        }

                        double cr_sum = 0;
                        for (int i = u; i < 8; i++){
                            for (int j = v; j < 8; j++){

                                double u_cos = Math.cos((((2 * i) + 1) * x * Math.PI) / 16.0);
                                double v_cos = Math.cos((((2 * j) + 1) * y * Math.PI) / 16.0);

                                cr_sum += Cu * Cv * u_cos * v_cos * Cr[i][j];

                            }
                        }
                        cr_sum *= (1.0 / 4.0);
                        inverseCr[x + u][y + v] = cr_sum;

                    }
                }


            }
        }
        for (int u = 0; u < Cb.length; u += 8){
            for (int v = 0; v < Cb[0].length; v += 8){
                for (int x = 0; x < 8; x++){
                    for (int y = 0; y < 8; y++){

                        if (x == 0){
                            Cu = (1.0 / (Math.sqrt(2.0)));
                        } else {
                            Cu = 1.0;
                        }

                        if (y == 0){
                            Cv = (1.0 / (Math.sqrt(2.0)));
                        } else {
                            Cv = 1.0;
                        }

                        double cb_sum = 0;
                        for (int i = u; i < 8; i++){
                            for (int j = v; j < 8; j++){

                                double u_cos = Math.cos((((2 * i) + 1) * x * Math.PI) / 16.0);
                                double v_cos = Math.cos((((2 * j) + 1) * y * Math.PI) / 16.0);

                                cb_sum += Cu * Cv * u_cos * v_cos * Cb[i][j];

                            }
                        }
                        cb_sum *= (1.0 / 4.0);
                        inverseCb[x + u][y + v] = cb_sum;
                    }
                }


            }
        }
    }
} // Image class

