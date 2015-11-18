import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by esauceda on 11/4/15.
 */
public class homeworkThree {
    public static void start(String filename, Scanner input){
        //F1. Read and Resize the Input Image
        Image originalImg = new Image(filename);
        originalImg.display("original");

        int imgW = originalImg.getW();
        int imgH = originalImg.getH();

        originalImg.resizeMult8();
        originalImg.display("F1");

        int resW = originalImg.getW();
        int resH = originalImg.getH();

        //F2. Color space transformation and Subsampling
        double[][] Y, Cb, Cr;
        int newWidth = originalImg.getW() / 2;
        int newHeight = originalImg.getH() / 2;
        Y = new double[resW][resH];
        Cb =  new double[resW][resH];
        Cr = new double[resW][resH];

        //System.out.println(newHeight);
        originalImg.transformYCbCr(Y, Cb, Cr);
        if ((originalImg.getW() / 2.0) % 8 != 0){
            newWidth = (int) (Math.ceil(originalImg.getW() / 16.0) * 8);
        }

        if ((originalImg.getH() / 2.0) % 8 != 0) {
            newHeight = (int) Math.ceil(originalImg.getH() / 16.0) * 8;
        }
        //System.out.println(newWidth + ", " + newHeight);

        double[][] sampledCb = new double[newWidth][newHeight];
        double[][] sampledCr = new double[newWidth][newHeight];

        originalImg.subSample(Cb, Cr, sampledCb, sampledCr);

        //F3. Discrete Cosine Transform
        double[][] dctCb = new double[newWidth][newHeight];
        double[][] dctY = new double[resW][resH];
        double[][] dctCr = new double[newWidth][newHeight];
        originalImg.dctTransform(Y, sampledCb, sampledCr, dctY, dctCb, dctCr);

        //I2. Inverse Discrete Cosine Transform
        double[][] inverseCb = new double[newWidth][newHeight];
        double[][] inverseY = new double[resW][resH];
        double[][] inverseCr = new double[newWidth][newHeight];
        originalImg.dctInverse(dctY, dctCb, dctCr, inverseY, inverseCb, inverseCr);

        //I3. Inverse Color Space Transformation and Supersampling
        Image sampleImg = new Image(resW, resH);
        sampleImg.copyImage(originalImg, imgW, imgH);
        double[][] sampledY = new double[resW][resH];
        for (int a = 0; a < Y.length; a++){
            for (int b = 0; b < Y[0].length; b++){
                sampledY[a][b] = Y[a][b];
            }
        }
        double[][] newCb, newCr;
        newCb = new double[resW][resH];
        newCr = new double[resW][resH];
        sampleImg.superSample(newCb, newCr, inverseCb, inverseCr);
        sampleImg.transformRGB(sampledY, newCb, newCr);
        sampleImg.display("I3");

        //I4. Remove Padding and Display the Image
        originalImg.decompress(imgW, imgH);
        originalImg.display("I4");

        input.next();
    }
}
